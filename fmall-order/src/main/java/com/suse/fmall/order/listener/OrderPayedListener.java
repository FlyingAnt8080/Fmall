package com.suse.fmall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.suse.fmall.order.config.AlipayTemplate;
import com.suse.fmall.order.service.OrderService;
import com.suse.fmall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author LiuJing
 * @Date: 2021/04/19/ 22:15
 * @Description
 */
@Slf4j
@RestController
public class OrderPayedListener {
    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

    @PostMapping("/payed/notify")
    public String handleAliPayed(PayAsyncVo payVo, HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
        //只要收到支付宝给我们的异步通知，告诉我们订单支付成功。返回success，支付宝就不在通知
        // 验证签名
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getCharset(),
                alipayTemplate.getSign_type()); //调用SDK验证签名
        if (signVerified){
            //签名验证成功
            log.info("签名验证成功!");
            return orderService.handlePayResult(payVo);
        }else{
            log.error("签名验证失败!");
            return "fail";
        }
    }
}
