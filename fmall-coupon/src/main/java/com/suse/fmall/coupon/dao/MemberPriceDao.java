package com.suse.fmall.coupon.dao;

import com.suse.fmall.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 22:56:58
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
