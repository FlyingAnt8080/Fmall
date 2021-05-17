package com.suse.fmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.member.entity.MemberEntity;
import com.suse.fmall.member.exception.PhoneExistException;
import com.suse.fmall.member.exception.UsernameExistException;
import com.suse.fmall.member.vo.MemberLoginVo;
import com.suse.fmall.member.vo.MemberQuery;
import com.suse.fmall.member.vo.MemberRegisterVo;
import com.suse.fmall.member.vo.SocialUser;

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

    /**
     * 注册
     * @param vo
     */
    void register(MemberRegisterVo vo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUsernameUnique(String username) throws UsernameExistException;

    MemberEntity login(MemberLoginVo loginVo);

    MemberEntity login(SocialUser socialUser);

    PageUtils queryPageByCondition(MemberQuery query);
}

