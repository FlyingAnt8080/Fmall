package com.suse.fmall.product.dao;

import com.suse.fmall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:02:28
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
