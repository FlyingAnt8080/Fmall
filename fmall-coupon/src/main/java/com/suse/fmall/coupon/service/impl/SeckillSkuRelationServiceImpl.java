package com.suse.fmall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;

import com.suse.fmall.coupon.dao.SeckillSkuRelationDao;
import com.suse.fmall.coupon.entity.SeckillSkuRelationEntity;
import com.suse.fmall.coupon.service.SeckillSkuRelationService;
import org.springframework.util.StringUtils;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String promotionSessionId = (String) params.get("promotionSessionId");
        String key = (String) params.get("key");
        QueryWrapper<SeckillSkuRelationEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(promotionSessionId)){
            queryWrapper.eq("promotion_session_id",promotionSessionId);
        }
        if (!StringUtils.isEmpty(key)){
          //TODO 条件查询
        }
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
               queryWrapper
        );
        return new PageUtils(page);
    }
}