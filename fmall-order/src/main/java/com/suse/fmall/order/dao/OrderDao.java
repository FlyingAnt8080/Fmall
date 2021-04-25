package com.suse.fmall.order.dao;

import com.suse.fmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:39:30
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("code") Integer code);

}
