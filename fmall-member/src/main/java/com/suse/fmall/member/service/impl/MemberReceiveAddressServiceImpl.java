package com.suse.fmall.member.service.impl;

import com.suse.common.vo.MemberRespVo;
import com.suse.fmall.member.interceptor.LoginUserInterceptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;

import com.suse.fmall.member.dao.MemberReceiveAddressDao;
import com.suse.fmall.member.entity.MemberReceiveAddressEntity;
import com.suse.fmall.member.service.MemberReceiveAddressService;
import org.springframework.transaction.annotation.Transactional;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberReceiveAddressEntity> getAddresses(Long memberId) {
        return this.list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id",memberId));
    }

    @Transactional
    @Override
    public void setDefaultAddress(Long id,Long userId) {
        //查询出当前默认地址的id
        MemberReceiveAddressEntity oldDefaultAddress = this.baseMapper.selectOne(new QueryWrapper<MemberReceiveAddressEntity>()
                .eq("member_id", userId).eq("default_status", 1));
        oldDefaultAddress.setDefaultStatus(0);
        this.update(oldDefaultAddress,new QueryWrapper<MemberReceiveAddressEntity>()
                .eq("id",oldDefaultAddress.getId()));
        MemberReceiveAddressEntity newDefaultAddress = new MemberReceiveAddressEntity();
        newDefaultAddress.setDefaultStatus(1);
        this.update(newDefaultAddress,new QueryWrapper<MemberReceiveAddressEntity>()
                .eq("id",id));
    }

}