package com.suse.fmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 22:56:58
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

