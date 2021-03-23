package com.suse.fmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:29:19
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

