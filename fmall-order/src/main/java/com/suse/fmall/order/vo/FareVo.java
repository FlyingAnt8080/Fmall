package com.suse.fmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author LiuJing
 * @Date: 2021/04/16/ 22:25
 * @Description
 */
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
