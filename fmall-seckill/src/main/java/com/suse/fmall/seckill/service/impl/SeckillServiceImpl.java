package com.suse.fmall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.suse.common.to.mq.SeckillOrderTo;
import com.suse.common.utils.R;
import com.suse.common.vo.MemberRespVo;
import com.suse.fmall.seckill.feign.CouponFeignService;
import com.suse.fmall.seckill.feign.ProductFeignService;
import com.suse.fmall.seckill.interceptor.LoginUserInterceptor;
import com.suse.fmall.seckill.service.SeckillService;
import com.suse.fmall.seckill.to.SeckillSKuRedisTo;
import com.suse.fmall.seckill.vo.SeckillSessionsWithSkusVo;
import com.suse.fmall.seckill.vo.SkuInfoVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author LiuJing
 * @Date: 2021/04/20/ 23:01
 * @Description
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE_PREFIX = "seckill:stock:";//+商品随机码
    private final String SEPARATOR_UNDERLINE = "_";

    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void uploadSeckillSkuLatest3Days() {
        R res = couponFeignService.getLatest3DaySession();
        if (res.getCode() == 0) {
            //上架秒杀商品
            List<SeckillSessionsWithSkusVo> sessions = res.getData(new TypeReference<List<SeckillSessionsWithSkusVo>>() {});
            //缓存到Redis
            //1.缓存活动信息
            saveSessionInfos(sessions);
            //2.缓存活动的关联信息
            saveSessionSkuInfos(sessions);
        }
    }

    /**
     * 查询当前时间可以参与的秒杀商品信息
     *
     * @return
     */
    @Override
    public List<SeckillSKuRedisTo> getCurrentSeckillSkus() {
        //确定当前时间属于哪个场次
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        for (String key : keys) {
            String[] times = key.replace(SESSIONS_CACHE_PREFIX, "").split(SEPARATOR_UNDERLINE);
            long startTime = Long.parseLong(times[0]);
            long endTime = Long.parseLong(times[1]);
            if (time >= startTime && time <= endTime) {
                //2.获取当前秒杀场次的所有商品信息
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<String> list = hashOps.multiGet(range);
                if (list != null) {
                    List<SeckillSKuRedisTo> collect = list.stream().map(item -> {
                        SeckillSKuRedisTo skuRedisTo = JSON.parseObject(item, SeckillSKuRedisTo.class);
                        return skuRedisTo;
                    }).collect(Collectors.toList());
                    return collect;
                }
                break;
            }
        }
        return null;
    }


    @Override
    public SeckillSKuRedisTo getSkuSeckillInfo(Long skuId) {
        //1.找到所有需要秒杀的商品信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (!CollectionUtils.isEmpty(keys)) {
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    String jsonStr = hashOps.get(key);
                    SeckillSKuRedisTo skuRedisTo = JSON.parseObject(jsonStr, SeckillSKuRedisTo.class);
                    //随机码
                    long currentTime = new Date().getTime();
                    if (!(currentTime >= skuRedisTo.getStartTime() && currentTime <= skuRedisTo.getEndTime())) {
                        skuRedisTo.setRandomCode(null);
                    }
                    return skuRedisTo;
                }
            }
        }
        return null;
    }
    //TODO 上架秒杀商品的时候，每一个数据都有过期时间
    //TODO 秒杀后的流程，简化了收货地址等信息
    //TODO 上架要锁库存
    @Override
    public String kill(String killId, String key, Integer num) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUserThreadLocal.get();
        //1.获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String productJson = hashOps.get(killId);
        if (!StringUtils.isEmpty(productJson)) {
            SeckillSKuRedisTo redisTo = JSON.parseObject(productJson, SeckillSKuRedisTo.class);
            //效验合法性
            Long startTime = redisTo.getStartTime();
            Long endTime = redisTo.getEndTime();
            long time = new Date().getTime();
            long ttl = endTime - time;
            //1.效验时间的合法性
            if (time >= startTime && time <= endTime) {
                //2.校验随机码是否正确
                String code = redisTo.getRandomCode();
                System.out.println(code);
                String redisKillId = redisTo.getPromotionSessionId() + SEPARATOR_UNDERLINE + redisTo.getSkuId();
                System.out.println("killId："+redisKillId.equals(killId));
                if (code.equals(key) && redisKillId.equals(killId)) {
                    System.out.println("校验随机码比对成功");
                    //3.验证购物数量是否合理
                    if (num <= redisTo.getSeckillLimit()) {
                        //4.验证是否已经购买过
                        //setnx
                        String redisKey = memberRespVo.getId() + redisKillId;
                        //自动过期
                        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                        if (ifAbsent) {
                            //成功说明从来没有买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE_PREFIX + code);
                            boolean tryAcquire = semaphore.tryAcquire(num);
                            if (tryAcquire) {
                                //秒杀成功!
                                //快速下单，发送MQ消息 10ms
                                String timeId = IdWorker.getTimeId();
                                SeckillOrderTo orderTo = new SeckillOrderTo();
                                orderTo.setOrderSn(timeId);
                                orderTo.setMemberId(memberRespVo.getId());
                                orderTo.setNum(num);
                                orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                                orderTo.setSkuId(redisTo.getSkuId());
                                orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", orderTo);
                                return timeId;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 缓存活动信息
     *
     * @param sessions
     */
    private void saveSessionInfos(List<SeckillSessionsWithSkusVo> sessions) {
        if (sessions != null) {
            sessions.stream().forEach(session -> {
                long startTime = session.getStartTime().getTime();
                long endTime = session.getEndTime().getTime();
                String key = SESSIONS_CACHE_PREFIX + startTime + SEPARATOR_UNDERLINE + endTime;
                //缓存活动信息
                Boolean hasKey = redisTemplate.hasKey(key);
                if (!hasKey) {
                    List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "_" + item.getSkuId()).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(collect)) {
                        redisTemplate.opsForList().leftPushAll(key, collect);
                    }
                }
            });
        }

    }

    /**
     * 缓存活动关联的商品sku信息
     *
     * @param sessions
     */
    private void saveSessionSkuInfos(List<SeckillSessionsWithSkusVo> sessions) {
        if (sessions != null) {
            sessions.stream().forEach(session -> {
                //准备Hash操作
                BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                session.getRelationSkus().forEach((seckillSkuVo -> {
                    String key = seckillSkuVo.getPromotionSessionId() + SEPARATOR_UNDERLINE + seckillSkuVo.getSkuId();
                    //没有上架才上架
                    if (!hashOps.hasKey(key)) {
                        //缓存商品
                        SeckillSKuRedisTo sKuRedisTo = new SeckillSKuRedisTo();
                        //1.sku的基本信息
                        R res = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                        if (res.getCode() == 0) {
                            SkuInfoVo skuInfo = res.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
                            sKuRedisTo.setSkuInfo(skuInfo);
                        }
                        //2.sku的秒杀信息
                        BeanUtils.copyProperties(seckillSkuVo, sKuRedisTo);
                        //3.设置当前商品秒杀时间信息
                        sKuRedisTo.setStartTime(session.getStartTime().getTime());
                        sKuRedisTo.setEndTime(session.getEndTime().getTime());
                        //4.随机码
                        String token = UUID.randomUUID().toString().replace("-", "");
                        sKuRedisTo.setRandomCode(token);
                        //6.保存到Redis中
                        String sKuRedisToJson = JSON.toJSONString(sKuRedisTo);
                        hashOps.put(key, sKuRedisToJson);
                        //使用库存作为分布式信号量(商品可以秒杀的数量)  限流
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE_PREFIX + token);
                        semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                    }
                }));
            });
        }
    }
}
