package com.suse.fmall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.suse.common.exception.BizCodeEnume;
import com.suse.fmall.member.exception.PhoneExistException;
import com.suse.fmall.member.exception.UsernameExistException;
import com.suse.fmall.member.feign.CouponFeignService;
import com.suse.fmall.member.vo.MemberLoginVo;
import com.suse.fmall.member.vo.MemberQuery;
import com.suse.fmall.member.vo.MemberRegisterVo;
import com.suse.fmall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.suse.fmall.member.entity.MemberEntity;
import com.suse.fmall.member.service.MemberService;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.R;



/**
 * 会员
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:29:19
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeignService couponFeignService;


    @RequestMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("小冲");
        R result = couponFeignService.memberCoupons();
        return R.ok().put("member",memberEntity).put("coupons",result.get("coupons"));
    }

    /**
     * 注册
     * @param vo
     * @return
     */
    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo vo){
        try{
            memberService.register(vo);
        }catch (UsernameExistException e){
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(),BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }catch (PhoneExistException e){
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(),BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 登录
     * @param loginVo
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo loginVo){
       MemberEntity member =  memberService.login(loginVo);
       if (member != null){
           return R.ok().setData(member);
       }else {
           return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVALID__EXCEPTION.getCode(),
                   BizCodeEnume.LOGINACCT_PASSWORD_INVALID__EXCEPTION.getMsg());
       }
    }

    /**
     * 社交登录
     * @param socialUser
     * @return
     */
    @PostMapping("/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser){
        MemberEntity member =  memberService.login(socialUser);
        if (member != null){
            return R.ok().setData(member);
        }else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVALID__EXCEPTION.getCode(),
                    BizCodeEnume.LOGINACCT_PASSWORD_INVALID__EXCEPTION.getMsg());
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(MemberQuery query){
        PageUtils page = memberService.queryPageByCondition(query);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }
    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    public R updateStatus(@RequestBody MemberEntity member){
        memberService.updateById(member);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
