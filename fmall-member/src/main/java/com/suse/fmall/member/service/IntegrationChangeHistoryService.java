package com.suse.fmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:29:19
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

