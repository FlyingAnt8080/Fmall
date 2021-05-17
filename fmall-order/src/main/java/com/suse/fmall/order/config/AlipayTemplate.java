package com.suse.fmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.suse.fmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016101900720622";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCo1mfKUY9DqmvtTk9atXzox/3YmiNBNTeZStQiSKqZx3jyAyvZO3c92OOhUwITnZK3sbDWIJFvlQ05PmaxM4Fq62iZoBLmfUXwI436Gx5I1AKB9gvG9XPm0HZrgXhYOqPnUpR9poWv1YC9mXTrF750mQ2xE6WD0b616PtfNwVCSrtEXBLZNnq18Q6+/ri2/IbD0u4/pNfdqMiLVPbY0OTkk1yyEDs5Q/qQml09D26uRzJj6uEWeSgYwui/nKgdYF81gns/8DgS178lx0izercCWxMiIc5yhHh0wBtyCivqxqf8QZx/DXakyCup/HB+zXbW9f+2q6+zPAKPoIMCqq2HAgMBAAECggEAGheIUqvoB/z+UN4ZRVtmwlKl2CN570naHcaCogm406sP9danoBqggt5Rz0yUtZNUaS78tRqzXxsZwA70r83V4kAfGh6yzQvvEv1ro4stUvPwr0Tm7QhhsPnmUxbfEkN9sPICIKjrYBEOpD5i4zH4LOy6QPNYUNbJMjOfJUfLeELzjOONEKN3dAt4LHbH4lE1yw/VsGwqdMCskApf5zd3rPaQ4EaoQWdqOA/uTKQ2fqmbT2WSWOFwGJ+DF2HH82N49osVJCayqTIvFcbzC72A42Hgo8PYBWgjUNyZ30+fN9/WCLDxBjV/SYNLFfxDhzzKNxFgp+HNSlZZbvYI8YOYAQKBgQDrXlReztIDKW47GegtGv+Ku/ZL9yF6L/OrRqH8+pMtN56FXLykSuK5NkCiczr56Ht/nIAw+YUIHK9lyBaTgy2jljpounO7y/Xxor2WTUz+Pr4Jt5TgreSegXENsueGjrLQqEEPEQkOY2zEwnf9aYbXDbc8cdIXHx0gVzuugTY2QQKBgQC3ox7ogn4lQtUFMDxzhLTKyQiydCTcPWafeYDUN26uQwq8EGJWbJF+B5dyFC4oqxnnh+ZlvQJ1EE6qbRZ5+2Ae23A4JvTU2SktKLLtYIC2uDg705ZWR6dQn5hjLuuueNDCfKwv08Rekvw/CU2cpnSxFCzF3ajvOQiLihnP3cBBxwKBgAhnYdPQxqbeP9VUY3nY0O7/LxgfRs66D5U5/Gav+7lNlxdj7EhhdB7w1PX3708lAePYjqsw/ZMAkzKrJkM2F9cTchpzLdayvaFXEjXfpLQfQWHPcD1leBhAvuyiSqn/Rls05r3G+e/NlQChgO9HaU0cbEJ4PewdbEjPVI4yhuEBAoGAZMC9s4ntFhp977PQZvz3iI8WE3r/wkp55KNwWH71XezwldBTc+FrTf9ySyfhT3TY6Jw6f7VdBMdmscDaZSDTUvrIrjZJfAKNwfYjfXPGCvo1+DUVtc7ocKavoDNdIcOtnhdzDWadxdnVReyfxYPaYFGWYxVDm8Tp+E1T7iVXV38CgYAJOn4LyjyICvcDQQ2mSnoObZPjtc/6PJaIO2F5qUXcSSMfvOx47lvcEHrgZRKydjDRF8MoWt7NxmQJAEk7B4hsWhyjTCsMn/afpePGc4WmcCYxSNH6z7iPw66yfqr0PNpT7JhtxF5V4dKnSVYgLMK+fy9if1rMbt6xH9scPJuPLg==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvb3pYC4VIbttaIcZOOIsXj3WFouuMG+0OyzTa9cdDh42WpogyRnPRLEpmD7TGlGIgSvI3MkU2PrqtmcG/ab5ONYXTDNkXOSmTw4DdEteG2sHvtzb472PJIq+FKGcUcs9mm6EekNKvZEbSyv+tOY0zuvaaGwLouX/l5nirVwrersRXG6uNqgLYS6Zsg98gO6z/DIRaKVB/gxZmeacmXKZIth221hWB3pEmYTeaHd+eteJSlEgZy95parNvyG+n6kJCtfU2AeCHvr9UC7Xx/TXWe8th45W+lv11gxSdi2bcvZBCP9qrWqw2BID+tKIuAB+ihPwxetbAguRT4c3pGRVdQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://k5f2tyryxb.bjhttp.cn/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url ="http://member.fmall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    private String timeout = "1m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                //设置订单时间，超时支付宝关闭支付功能
                + "\"timeout_express\":\""+timeout+"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
