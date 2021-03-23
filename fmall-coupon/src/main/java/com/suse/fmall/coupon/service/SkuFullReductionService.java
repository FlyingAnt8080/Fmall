package com.suse.fmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.to.SkuReductionTo;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 22:56:58
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo reductionTo);

}

