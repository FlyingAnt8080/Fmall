package com.suse.fmall.member.web;

import com.alibaba.fastjson.JSON;
import com.suse.common.utils.R;
import com.suse.fmall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, Model model){
        //查出当前登录用户的所有订单列表数据
        Map<String,Object> page = new HashMap<>();
        page.put("page",pageNum.toString());
        R res = orderFeignService.listWithItems(page);
        System.out.println(JSON.toJSONString(res));
        model.addAttribute("orders",res);
        return "orderList";
    }
}
