package com.suse.fmall.order.vo;

import com.suse.fmall.order.entity.OrderEntity;
import lombok.Data;
import lombok.ToString;

/**
 * @Author LiuJing
 * @Date: 2021/04/16/ 23:59
 * @Description 下单操作返回数据信息
 */
@Data
@ToString
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;//错误状态码 0:成功
    private String msg;
}
