package com.suse.fmall.member.controller;

import com.suse.common.constant.AuthServerConstant;
import com.suse.common.exception.BizCodeEnume;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.R;
import com.suse.common.vo.MemberRespVo;
import com.suse.fmall.member.entity.MemberReceiveAddressEntity;
import com.suse.fmall.member.service.MemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 会员收货地址
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:29:19
 */
@RestController
@RequestMapping("/member/memberreceiveaddress/")
public class MemberReceiveAddressController {
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;

    @GetMapping("/addresses/{memberId}")
    public List<MemberReceiveAddressEntity> getAddress(@PathVariable("memberId") Long memberId){
        return  memberReceiveAddressService.getAddresses(memberId);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberReceiveAddressService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);
        return R.ok().put("memberReceiveAddress", memberReceiveAddress);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress, HttpSession session){
        MemberRespVo loginUser = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (loginUser!=null){
            memberReceiveAddress.setMemberId(loginUser.getId());
            memberReceiveAddressService.save(memberReceiveAddress);
            return R.ok();
        }
        return R.error(BizCodeEnume.NOT_LOGIN.getCode(),BizCodeEnume.NOT_LOGIN.getMsg());
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){

		memberReceiveAddressService.updateById(memberReceiveAddress);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberReceiveAddressService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    @PostMapping("/setDefaultAddr")
    public R setDefaultAddress(@RequestBody Long id,HttpSession session){
        MemberRespVo loginUser = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (loginUser!=null){
            memberReceiveAddressService.setDefaultAddress(id,loginUser.getId());
            return R.ok();
        }
        return R.error(BizCodeEnume.NOT_LOGIN.getCode(),BizCodeEnume.NOT_LOGIN.getMsg());
    }
}
