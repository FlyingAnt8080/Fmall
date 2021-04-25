package com.suse.fmall.coupon.service.impl;

import com.suse.fmall.coupon.entity.SeckillSkuRelationEntity;
import com.suse.fmall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;

import com.suse.fmall.coupon.dao.SeckillSessionDao;
import com.suse.fmall.coupon.entity.SeckillSessionEntity;
import com.suse.fmall.coupon.service.SeckillSessionService;
import org.springframework.util.CollectionUtils;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLatest3DaySession() {
        List<SeckillSessionEntity> sessionEntities = this.list(new QueryWrapper<SeckillSessionEntity>()
                .between("start_time", startDateTime(), endDateTime()));
        if (!CollectionUtils.isEmpty(sessionEntities)){
            List<SeckillSessionEntity> collect = sessionEntities.stream().map(session -> {
                Long id = session.getId();
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>()
                        .eq("promotion_session_id", id));
                session.setRelationSkus(relationEntities);
                return session;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    private String startDateTime(){
        LocalDate  now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        LocalDateTime startDateTime = LocalDateTime.of(now, min);
        String format = startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

    private String endDateTime(){
        LocalDate  twoDayLater = LocalDate.now().plusDays(2);
        LocalTime max = LocalTime.MAX;
        LocalDateTime endDateTime = LocalDateTime.of(twoDayLater,max);
        String format = endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }
}