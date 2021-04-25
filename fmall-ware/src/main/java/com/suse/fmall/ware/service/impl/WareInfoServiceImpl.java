package com.suse.fmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suse.common.utils.R;
import com.suse.fmall.ware.feign.MemberFeignService;
import com.suse.fmall.ware.vo.FareVo;
import com.suse.fmall.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;
import com.suse.fmall.ware.dao.WareInfoDao;
import com.suse.fmall.ware.entity.WareInfoEntity;
import com.suse.fmall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {
    @Autowired
    private MemberFeignService memberFeignService;

    public PageUtils queryPage(Map<String,Object> params) {
        QueryWrapper<WareInfoEntity> wareInfoEntityQueryWrapper = new QueryWrapper<>();
        //TODO 有个bug待解决
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wareInfoEntityQueryWrapper.eq("id",key).or().like("name",key).or().like("address",key).or().like("areacode",key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoEntityQueryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R res = memberFeignService.addrInfo(addrId);
        MemberAddressVo addressVo = res.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {});
        if (addressVo != null){
            //TODO 运费计算实际掉物流系统(此处模拟计算)
           String phone = addressVo.getPhone();
           String subStr = phone.substring(phone.length()-1);
           BigDecimal fare = new BigDecimal(subStr);
           fareVo.setFare(fare);
           fareVo.setAddress(addressVo);
           return fareVo;
        }   
        return null;
    }
}