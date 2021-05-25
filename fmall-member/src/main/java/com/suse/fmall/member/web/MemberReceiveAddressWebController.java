package com.suse.fmall.member.web;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suse.common.constant.AuthServerConstant;
import com.suse.common.vo.MemberRespVo;
import com.suse.fmall.member.entity.MemberReceiveAddressEntity;
import com.suse.fmall.member.service.MemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 收货地址页面
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:29:19
 */
@Controller
public class MemberReceiveAddressWebController {
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;


    @GetMapping("/receiveAddress.html")
    public String receiveAddressPage(HttpSession session, Model model){
        MemberRespVo loginUser = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (loginUser != null){
            List<MemberReceiveAddressEntity> addresses = memberReceiveAddressService.getAddresses(loginUser.getId());
            model.addAttribute("data",addresses);
            return "address";
        }
        return "redirect:http://auth.fmall.com/login.html";
    }

    @GetMapping("/add.html")
    public String receiveAddressAdd(){
        return "add";
    }

    @GetMapping("/edit.html/{addressId}")
    public String receiveAddressEdit(@PathVariable("addressId") Long addressId, Model model,HttpSession session){
        MemberReceiveAddressEntity address = memberReceiveAddressService.getById(addressId);
        model.addAttribute("address",address);
        return "edit";
    }
}
