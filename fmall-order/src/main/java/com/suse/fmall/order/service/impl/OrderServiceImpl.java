package com.suse.fmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.suse.common.constant.OrderConstant;
import com.suse.common.constant.OrderStatusEnum;
import com.suse.common.exception.NoStockException;
import com.suse.common.to.OrderTo;
import com.suse.common.to.mq.SeckillOrderTo;
import com.suse.common.utils.R;
import com.suse.common.vo.MemberRespVo;
import com.suse.fmall.order.entity.OrderItemEntity;
import com.suse.fmall.order.entity.PaymentInfoEntity;
import com.suse.fmall.order.feign.CartFeignService;
import com.suse.fmall.order.feign.MemberFeignService;
import com.suse.fmall.order.feign.ProductFeignService;
import com.suse.fmall.order.feign.WareFeignService;
import com.suse.fmall.order.interceptor.LoginUserInterceptor;
import com.suse.fmall.order.service.OrderItemService;
import com.suse.fmall.order.service.PaymentInfoService;
import com.suse.fmall.order.to.OrderCreateTo;
import com.suse.fmall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;
import com.suse.fmall.order.dao.OrderDao;
import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor poolExecutor;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }
        if (!StringUtils.isEmpty(key)){
            queryWrapper.eq("order_sn",key).or().eq("member_username",key)
            .or().eq("receiver_phone",key).or().eq("receiver_name",key);
        }
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUserThreadLocal.get();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //1.远程查询收货地址列表
        CompletableFuture<Void> getAddressTask = CompletableFuture.runAsync(() -> {
            //开启新线程防止请求头数据丢失
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> addresses = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(addresses);
        }, poolExecutor);

        //2.远程查询购物车所有选中的购物项
        CompletableFuture<Void> getCartTask = CompletableFuture.runAsync(() -> {
            //开启新线程防止请求头数据丢失
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, poolExecutor).thenRunAsync(()->{
            //获取商品是否有库存
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R res = wareFeignService.getSkusStock(skuIds);
            List<SkuStockVo> skuStockVos = null;
            if (res.getCode() == 0){
                skuStockVos = res.getData("data", new TypeReference<List<SkuStockVo>>() {});
            }
            if (skuStockVos != null){
                Map<Long, Boolean> skuStockMap = skuStockVos.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(skuStockMap);
            }
        },poolExecutor);

        //3.查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);
        //4.其他数据自动计算
        //5.TODO 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);
        //等待异步任务都执行完成
        CompletableFuture.allOf(getAddressTask,getCartTask).get();
        return confirmVo;
    }

    /**
     * 下单方法
     * @param orderSubmitVo
     * @return
     */
//    @GlobalTransactional AT模式不适合高并发场景
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) throws NoStockException{
        confirmVoThreadLocal.set(orderSubmitVo);
        SubmitOrderResponseVo response  = new SubmitOrderResponseVo();
        response.setCode(0);
        //获取当前用户信息
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUserThreadLocal.get();
        //验令牌、创订单、验价格、锁库存...
        //1.验证令牌
        //0:删除失败;1:删除成功
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        String orderToken = orderSubmitVo.getToken();
        //通过LUA脚本原子验证令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()),
                orderToken);
        if (result == 1L){
            //验证通过
            //1.创建订单
            OrderCreateTo order = createOrder();
            //2.验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = orderSubmitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
                //金额对比成功
                //3.保存订单
                saveOrder(order);
                //4.锁库存,只要有异常回滚订单数据
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                //TODO 远程锁库存
                //为保证高并发。库存服务自己回滚。可以发消息给库存服务。
                //库存服务本身可以使用自动解锁模式，消息队列
                R res = wareFeignService.orderLockStock(lockVo);
                if(res.getCode() == 0){
                    //锁定成功
                    response.setOrder(order.getOrder());

                    //TODO 订单创建成功发送消息给MQ
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());
                    return response;
                }else {
                    //锁定失败
                    String msg = (String) res.get("msg");
                    throw new NoStockException(msg);
                }
            }else{
                //金额对比失败
                response.setCode(2);
            }
        }else {
            response.setCode(1);
        }
        return response;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn",orderSn));
    }

    @Override
    public void closeOrder(OrderEntity order) {
        //查询当前订单的最新状态
        OrderEntity orderEntity = this.getById(order.getId());
        if (orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){
            //关单
            OrderEntity update = new OrderEntity();
            update.setId(orderEntity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            //给MQ发送消息,解锁库存
            try{
                //保证消息一定会发送出去，每个消息做好日志记录(给数据库保存每一个消息的详细信息)
                //定期扫描数据库将失败的消息再发送一遍
                rabbitTemplate.convertAndSend("order-event-exchange","order.release.other",orderTo);
            }catch (Exception e){
                //将没有发送成功的消息进行重试
            }
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity orderEntity = this.getOrderByOrderSn(orderSn);
        BigDecimal payAmount = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(payAmount.toString());
        payVo.setOut_trade_no(orderSn);
        List<OrderItemEntity> orderItems = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        payVo.setSubject(orderItems.get(0).getSkuName());
        payVo.setBody(orderItems.get(0).getSkuAttrsVals());
        return payVo;
    }

    /**
     * 查询当前登录用户的所有订单信息
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUserThreadLocal.get();
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        String status = (String) params.get("status");
        if (status != null && !status.equals("-1")){
            queryWrapper.eq("status",status);
        }
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                queryWrapper.eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );
        List<OrderEntity> orders = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> itemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(itemEntities);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(orders);
        return new PageUtils(page);
    }

    /**
     * 查询当前登录用户的所有订单信息
     * @param status
     * @return
     */
    public List<OrderEntity> queryOrderItems(String status){
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUserThreadLocal.get();
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.equals("-1")){
            queryWrapper.eq("status",status);
        }
        queryWrapper.eq("member_id",memberRespVo.getId()).orderByDesc("id");
        List<OrderEntity> orderEntities = this.list(queryWrapper);
        List<OrderEntity> orders = orderEntities.stream().map(order -> {
            List<OrderItemEntity> itemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(itemEntities);
            return order;
        }).collect(Collectors.toList());
        return orders;
    }

    /**
     * 处理支付宝支付成功的回调结果
     * @param payVo
     * @return
     */
    //TODO 分布式事务
    @Transactional
    @Override
    public String handlePayResult(PayAsyncVo payVo) {
        log.info("支付宝回调数据：{}",payVo);
        //1.保存交易流水
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setAlipayTradeNo(payVo.getTrade_no());
        paymentInfo.setOrderSn(payVo.getOut_trade_no());
        paymentInfo.setTotalAmount(new BigDecimal(payVo.getTotal_amount()));
        paymentInfo.setSubject(payVo.getSubject());
        paymentInfo.setPaymentStatus(payVo.getTrade_status());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            paymentInfo.setCallbackTime(format.parse(payVo.getNotify_time()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        paymentInfoService.save(paymentInfo);
        //2.修改订单状态信息
        String tradeStatus = payVo.getTrade_status();
        if (tradeStatus != null && (tradeStatus.equals("TRADE_SUCCESS")|| tradeStatus.equals("TRADE_FINISHED"))){
            //支付成功
            String orderSn = payVo.getOut_trade_no();
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setPaymentTime(new Date());
            orderEntity.setStatus(OrderStatusEnum.PAYED.getCode());
            //修改订单状态
            this.baseMapper.update(orderEntity,new QueryWrapper<OrderEntity>().eq("order_sn",orderSn));
            //扣库库存
            R res = wareFeignService.deleteStock(orderSn);
            if (res.getCode() != 0){
                return "fail";
            }
        }
        return "success";
    }

    /**
     * 创建秒杀订单
     * @param seckillOrder
     */
    @Override
    public void createSeckillOrder(SeckillOrderTo seckillOrder) {
        //TODO 待修改
        //保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrder.getOrderSn());
        orderEntity.setMemberId(seckillOrder.getMemberId());
        //orderEntity.setReceiverRegion(); 收货地址待设置
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal payAmount = seckillOrder.getSeckillPrice().multiply(new BigDecimal(seckillOrder.getNum().toString()));
        orderEntity.setPayAmount(payAmount);
        save(orderEntity);
        //TODO 保存订单项信息
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(seckillOrder.getOrderSn());
        //TODO 还要加运费
        orderItemEntity.setRealAmount(payAmount);
        orderItemEntity.setSkuQuantity(seckillOrder.getNum());
        //TODO 远程查询sku详细信息
        orderItemService.save(orderItemEntity);
    }

    /**
     * 保存订单所有数据
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        List<OrderItemEntity> orderItemEntities = order.getOrderItems();
        orderEntity.setCreateTime(new Date());
        this.save(orderEntity);
        orderItemService.saveBatch(orderItemEntities);
    }

    private OrderCreateTo createOrder(){
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        OrderCreateTo createTo = new OrderCreateTo();
        //1.生成订单号
        String orderSn = IdWorker.getTimeId();
        OrderEntity orderEntity = buildOrder(orderSn);
        orderEntity.setPayType(orderSubmitVo.getPayType());
        createTo.setOrder(orderEntity);
        //2.获取所有的订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        createTo.setOrderItems(orderItemEntities);
        //3、计算价格、积分等信息
        computePrice(orderEntity,orderItemEntities);
        return createTo;
    }

    /**
     * 计算价格
     * @param orderEntity
     * @param orderItemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        BigDecimal totalPrice = new BigDecimal("0.0");
        BigDecimal couponPrice = new BigDecimal("0.0");
        BigDecimal integrationPrice = new BigDecimal("0.0");
        BigDecimal promotionPrice = new BigDecimal("0.0");
        Integer totalGrowth = new Integer(0);
        Integer totalIntegration = new Integer(0);

        //订单总额
        for (OrderItemEntity entity : orderItemEntities) {
            totalPrice =  totalPrice.add(entity.getRealAmount());
            promotionPrice = promotionPrice.add(entity.getPromotionAmount());
            integrationPrice = integrationPrice.add(entity.getIntegrationAmount());
            couponPrice  = couponPrice.add(entity.getCouponAmount());
            totalGrowth += entity.getGiftGrowth();
            totalIntegration += entity.getGiftIntegration();
        }
        orderEntity.setTotalAmount(totalPrice);
        orderEntity.setPromotionAmount(promotionPrice);
        orderEntity.setIntegrationAmount(integrationPrice);
        orderEntity.setCouponAmount(couponPrice);
        //应付总额
        orderEntity.setPayAmount(totalPrice.add(orderEntity.getFreightAmount()));
        //设置积分等信息
        orderEntity.setGrowth(totalGrowth);
        orderEntity.setIntegration(totalIntegration);
    }

    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUserThreadLocal.get();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(memberRespVo.getId());
        entity.setMemberUsername(memberRespVo.getUsername());
        //远程调用获取收货地址信息
        OrderSubmitVo submitVo = confirmVoThreadLocal.get();
        R res = wareFeignService.getFare(submitVo.getAddrId());
        FareVo fareVo = res.getData(new TypeReference<FareVo>() {});
        //设置运费信息
        entity.setFreightAmount(fareVo.getFare());
        //设置收货人信息
        entity.setReceiverName(fareVo.getAddress().getName());
        entity.setReceiverProvince(fareVo.getAddress().getProvince());
        entity.setReceiverCity(fareVo.getAddress().getCity());
        entity.setReceiverRegion(fareVo.getAddress().getRegion());
        entity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
        entity.setReceiverPhone(fareVo.getAddress().getPhone());
        entity.setReceiverPostCode(fareVo.getAddress().getPostCode());
        //设置订单状态
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(7);
        entity.setDeleteStatus(0);//未删除
        return entity;
    }

    /**
     * 构建所有的订单项
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (!CollectionUtils.isEmpty(currentUserCartItems)){
            List<OrderItemEntity> orderItemEntities = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = buildOrderItem(cartItem);
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            }).collect(Collectors.toList());
            return orderItemEntities;
        }
        return null;
    }

    /**
     * 构建某一个订单项
     * @param cartItem
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity orderItem = new OrderItemEntity();
        Long skuId = cartItem.getSkuId();
        R res = productFeignService.getSpuInfoBySkuId(skuId);
        //封装商品spu信息
        SpuInfoVo spuInfoVo = res.getData(new TypeReference<SpuInfoVo>(){});
        orderItem.setSpuId(spuInfoVo.getId());
        orderItem.setSpuBrand(spuInfoVo.getBrandId().toString());
        orderItem.setSpuName(spuInfoVo.getSpuName());
        orderItem.setCategoryId(spuInfoVo.getCatalogId());
        //商品的sku信息
        orderItem.setSkuId(skuId);
        orderItem.setSkuName(cartItem.getTitle());
        orderItem.setSkuPic(cartItem.getImage());
        orderItem.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ",");
        orderItem.setSkuAttrsVals(skuAttr);
        orderItem.setSkuQuantity(cartItem.getCount());
        //积分信息
        orderItem.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        orderItem.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        //订单项的价格
        orderItem.setPromotionAmount(new BigDecimal("0"));
        orderItem.setCouponAmount(new BigDecimal("0"));
        orderItem.setIntegrationAmount(new BigDecimal("0"));
        BigDecimal originPrice = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuQuantity().toString()));
        BigDecimal realPrice = originPrice.subtract(orderItem.getPromotionAmount())
                .subtract(orderItem.getCouponAmount())
                .subtract(orderItem.getIntegrationAmount());
        //当前订单项的实际金额
        orderItem.setRealAmount(realPrice);
        return orderItem;
    }
}