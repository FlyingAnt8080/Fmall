package com.suse.fmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.suse.common.constant.OrderStatusEnum;
import com.suse.common.exception.NoStockException;
import com.suse.common.to.OrderTo;
import com.suse.common.to.mq.StockDetailTo;
import com.suse.common.to.mq.StockLockedTo;
import com.suse.common.utils.R;
import com.suse.fmall.ware.entity.WareOrderTaskDetailEntity;
import com.suse.fmall.ware.entity.WareOrderTaskEntity;
import com.suse.fmall.ware.feign.OrderFeignService;
import com.suse.fmall.ware.feign.ProductFeignService;
import com.suse.fmall.ware.service.WareOrderTaskDetailService;
import com.suse.fmall.ware.service.WareOrderTaskService;
import com.suse.fmall.ware.vo.OrderItemVo;
import com.suse.fmall.ware.vo.OrderVo;
import com.suse.fmall.ware.vo.SkuHasStockVo;
import com.suse.fmall.ware.vo.WareSkuLockVo;
import lombok.Data;
import lombok.ToString;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;
import com.suse.fmall.ware.dao.WareSkuDao;
import com.suse.fmall.ware.entity.WareSkuEntity;
import com.suse.fmall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private OrderFeignService orderFeignService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService orderTaskDetailService;

    /**
     * 给商品解锁库存
     *
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    private void unLockedStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        wareSkuDao.unlockStock(skuId, wareId, num);
        //更新库存工单的状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLockStatus(2);//变为已解锁
        orderTaskDetailService.updateById(entity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.判断如果没有该库存记录就新增,存在就修改
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>()
                .eq("sku_id", skuId)
                .eq("ware_id", wareId));
        if (CollectionUtils.isEmpty(entities)) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku名字,如果失败，整个事务无需回滚
            //TODO 高级部分处理
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            //查询当前sku的总库存量
            //SELECT SUM(stock - stock_locked) FROM `wms_ware_sku` WHERE sku_id = 1
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count == null ? false : count > 0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 为某个订单锁定库存
     *
     * @param lockVo
     * @return 默认运行时异常都会回滚
     * 库存解锁的场景
     * 1)、下订单成功,订单过期没有支付被系统自动取消、被用户手动取消。都要解锁库存
     * 2)、下单成功，库存锁定成功，但业务调用失败，导致订单回滚。之前锁定的库存就要自动解锁
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo lockVo) {
        //保存库存工单的详情
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(lockVo.getOrderSn());
        wareOrderTaskService.save(taskEntity);
        //按照下单的收货地址，找到就近仓库，锁定库存
        //1.找到每个商品哪个仓库有库存
        List<OrderItemVo> locks = lockVo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock wareHasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            wareHasStock.setSkuId(skuId);
            wareHasStock.setNum(item.getCount());
            List<Long> wareIds = this.baseMapper.listWareIdHashSkuStock(skuId);
            wareHasStock.setWareId(wareIds);
            return wareHasStock;
        }).collect(Collectors.toList());
        //锁库存
        for (SkuWareHasStock wareHasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = wareHasStock.getSkuId();
            List<Long> wareIds = wareHasStock.getWareId();
            if (CollectionUtils.isEmpty(wareIds)) {
                //没有任何仓库有这个商品的库存
                throw new NoStockException("商品"+skuId+"无库存！");
            }
            //1.如果每个商品都锁定成功，将当前商品锁定了几件的工作单记录发给MQ
            //2.如果有一个商品锁定失败，前面的工作单信息就回滚了。发送出去的消息，即使要解锁记录，由于数据库查不到对应id，所以就不用解锁

            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, wareHasStock.getNum());
                if (count == 1) {
                    //成功
                    skuStocked = true;
                    //通知MQ库存锁定成功
                    WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", wareHasStock.getNum(), taskEntity.getId(), wareId, 1);
                    orderTaskDetailService.save(taskDetailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(taskDetailEntity, stockDetailTo);
                    //只发id不行，防止回滚后找不到数据
                    stockLockedTo.setDetail(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);
                    break;
                }
            }
            if (!skuStocked) {
                //当前商品所有仓库库都没有锁住
                throw new NoStockException("库存锁定失败!");
            }
        }
        return true;
    }

    @Override
    public void unlockStock(StockLockedTo lockedTo) {
        System.out.println("收到解锁库存的消息");
        StockDetailTo detail = lockedTo.getDetail();
        Long detailId = detail.getId();
        /**
         * 解锁
         * 查询数据库关于这个订单的锁定库存信息
         * 1)有：库存锁定成功
         *      解锁订单情况
         *      1) 没有这个订单。必须解锁
         *      2) 有这个订单
         *          订单状态： 已取消：解锁库存
         *                    没取消：不解锁库存
         * 2)没有：库存锁定失败了，库存回滚了这种情况无需解锁
         */
        WareOrderTaskDetailEntity taskDetailEntity = orderTaskDetailService.getById(detailId);
        if (taskDetailEntity != null) {
            //解锁
            Long id = lockedTo.getId();
            WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getById(id);
            String orderSn = orderTaskEntity.getOrderSn();
            R res = orderFeignService.getOrderStatus(orderSn);
            if (res.getCode() == 0) {
                OrderVo data = res.getData(new TypeReference<OrderVo>(){});
                //订单不存在 或者 订单被取消了
                // 解锁库存
                if (data == null || data.getStatus() == OrderStatusEnum.CANCLED.getCode()) {
                    if(taskDetailEntity.getLockStatus() == 1){
                        //当前库存工作单详情为已锁定(状态1),才解锁
                        unLockedStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                //消息拒绝后重新放回队列,让别人重新解锁
                throw new RuntimeException("远程服务失败");
            }
        } else {
            //无需解锁
        }
    }

    /**
     * 防止订单服务卡顿导致订单状态改不了，库存消息优先到期。查询订单状态是新建状态，什么都不做，拥有无法解锁库存
     * @param orderTo
     */
    @Transactional
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查询最新库存的状态，防止重复解锁库存
        WareOrderTaskEntity task =  wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = task.getId();
        List<WareOrderTaskDetailEntity> entities = orderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id)
                .eq("lock_status", 1));
        for (WareOrderTaskDetailEntity entity : entities) {
            unLockedStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }
    }

    @Data
    @ToString
    class SkuWareHasStock {
        private Long skuId;
        private List<Long> wareId;
        private Integer num;
    }
}