package com.suse.fmall.member.web;

import com.alibaba.fastjson.JSON;
import com.suse.common.utils.R;
import com.suse.fmall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiuJing
 * @Date: 2021/04/18/ 22:05
 * @Description
 */
@Controller
public class MemberWebController {
    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "status",required = false) String status, Model model){
        //查出当前登录用户的所有订单列表数据
        //R res = orderFeignService.listWithItems(params);
        R res = orderFeignService.listOrderItems(status);
        model.addAttribute("orders",res);
        if (status != null && !status.equals("-1")){
            model.addAttribute("status",Integer.parseInt(status));
        }
        return "orderList";
    }
}
