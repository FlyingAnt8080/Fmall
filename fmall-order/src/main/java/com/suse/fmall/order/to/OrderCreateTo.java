package com.suse.fmall.order.to;

import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/17/ 9:23
 * @Description
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;
}
