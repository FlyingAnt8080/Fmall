package com.suse.fmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.coupon.entity.SeckillPromotionEntity;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 22:56:58
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

