package com.suse.fmall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author LiuJing
 * @Date: 2021/04/16/ 23:40
 * @Description 封装订单提交的数据
 */
@Data
@ToString
public class OrderSubmitVo {
    //收货地址Id
    private Long addrId;
    //支付方式
    private Integer payType;
    //无需提交要购买的商品，去购物车在获取一遍
    //优惠、发票..

    //防重令牌
    private String token;
    //应付价格(验价)
    private BigDecimal payPrice;
    //用户信息从session中获取
    //订单备注
    private String node;
}
