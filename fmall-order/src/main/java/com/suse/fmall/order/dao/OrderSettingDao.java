package com.suse.fmall.order.dao;

import com.suse.fmall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:39:29
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
