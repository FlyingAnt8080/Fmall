package com.suse.fmall.order.web;


import com.alipay.api.AlipayApiException;
import com.suse.fmall.order.config.AlipayTemplate;
import com.suse.fmall.order.service.OrderService;
import com.suse.fmall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author LiuJing
 * @Date: 2021/04/18/ 21:04
 * @Description
 */
@Controller
public class PayWebController {
    @Autowired
    private AlipayTemplate alipayTemplate;
    @Autowired
    private OrderService orderService;

    /**
     * 1.将支付页让浏览器展示
     * 2.支付成功以后，跳到用户的订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @GetMapping(value = "/payOrder",produces = "text/html")
    @ResponseBody
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo payVo =  orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }
}
