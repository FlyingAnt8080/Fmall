package com.suse.fmall.member.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suse.fmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suse.fmall.member.vo.MemberQuery;
import com.suse.fmall.member.vo.MemberVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员
 * 
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:29:19
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    IPage<MemberVo> getMemberByPage(@Param("page") Page<Object> page, @Param("query") MemberQuery query);
}
