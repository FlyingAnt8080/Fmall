package com.suse.fmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:39:30
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

