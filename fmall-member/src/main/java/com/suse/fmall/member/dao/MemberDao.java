package com.suse.fmall.member.dao;

import com.suse.fmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:29:19
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
