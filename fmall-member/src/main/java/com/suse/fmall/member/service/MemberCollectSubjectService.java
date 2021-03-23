package com.suse.fmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.member.entity.MemberCollectSubjectEntity;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:29:19
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

