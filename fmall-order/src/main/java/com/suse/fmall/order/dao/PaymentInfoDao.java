package com.suse.fmall.order.dao;

import com.suse.fmall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:39:30
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
