package com.suse.fmall.seckill.service;


import com.suse.fmall.seckill.to.SeckillSKuRedisTo;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/20/ 23:00
 * @Description
 */

public interface SeckillService {
    /**
     * 上架最近三天需要秒杀的商品
     */
    void uploadSeckillSkuLatest3Days();

    List<SeckillSKuRedisTo> getCurrentSeckillSkus();

    SeckillSKuRedisTo getSkuSeckillInfo(Long skuId);

    String kill(String killId, String key, Integer num);
}
