package com.suse.fmall.ware.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/17/ 14:19
 * @Description
 */
@Data
@ToString
public class WareSkuLockVo {
    private String orderSn;//订单号
    private List<OrderItemVo> locks;//需要锁住所有库存信息
}
