package com.suse.fmall.order.web;

import com.suse.common.constant.OrderStatusEnum;
import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.service.OrderService;
import com.suse.fmall.order.vo.OrderConfirmVo;
import com.suse.fmall.order.vo.OrderSubmitVo;
import com.suse.fmall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @Author LiuJing
 * @Date: 2021/04/14/ 22:01
 * @Description
 */
@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;

    /**
     *结算页面
     * @param model
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("confirmData",confirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes){
        //创订单、验令牌、验价格、锁库存
        SubmitOrderResponseVo responseVo = orderService.submitOrder(orderSubmitVo);
        if (responseVo.getCode() == 0){
            //下单成功
            model.addAttribute("submitOrderResp",responseVo);
            return "pay";
        }else {
            String msg = "";
            switch (responseVo.getCode()){
                case 1:
                    msg += "订单信息过期，请刷新再次提交";
                    break;
                case 2:
                    msg += "订单商品价格发送变化，请确认后再次提交";
                    break;
            }
            redirectAttributes.addFlashAttribute("msg",msg);
            return "redirect:http://order.fmall.com/toTrade";
        }
    }

    /**
     * 立即支付
     * @return
     */
    @PostMapping("/payNow")
    public String payNow(Long orderId,Model model,RedirectAttributes redirectAttributest){
        OrderEntity order = orderService.getById(orderId);
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setOrder(order);
        if (order.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){
            model.addAttribute("submitOrderResp",responseVo);
            return "pay";
        }else if (order.getStatus() == OrderStatusEnum.CANCLED.getCode()){
            redirectAttributest.addFlashAttribute("msg","订单已取消，请刷新！");
            return "redirect:http://member.fmall.com/memberOrder.html";
        }
        redirectAttributest.addFlashAttribute("msg","订单已取消，请刷新！");
        return "http://member.fmall.com/memberOrder.html";
    }
}
