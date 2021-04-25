package com.suse.fmall.seckill.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author LiuJing
 * @Date: 2021/04/21/ 20:28
 * @Description
 */
@Data
@ToString
public class SeckillSkuVo {
    private Long id;
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer seckillCount;
    /**
     * 每人限购数量
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;
}
