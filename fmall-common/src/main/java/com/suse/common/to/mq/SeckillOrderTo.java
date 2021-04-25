package com.suse.common.to.mq;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author LiuJing
 * @Date: 2021/04/24/ 11:59
 * @Description
 */
@Data
@ToString
public class SeckillOrderTo {
    /**
     * 订单号
     */
    private String orderSn;
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
    private Integer num;
    /**
     * 会员id
     */
    private Long memberId;

}
