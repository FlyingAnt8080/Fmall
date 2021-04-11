package com.suse.fmall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.suse.common.constant.AuthServerConstant;
import com.suse.common.utils.HttpUtils;
import com.suse.common.utils.R;
import com.suse.fmall.auth.feign.MemberFeignService;
import com.suse.common.vo.MemberRespVo;
import com.suse.fmall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiuJing
 * @Date: 2021/04/10/ 13:37
 * @Description 处理社交登录
 */
@Slf4j
@Controller
public class OAuth2Controller {
    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        //以下配置参考微博公众平台官方文档
        Map<String,String> map = new HashMap<>();
        map.put("client_id","微博公众平台你的应用申请的id");
        map.put("client_secret","微博公众平台你的应用申请的secret");
        map.put("grant_type","authorization_code");
        //微博公众平台上你的应用申请的回调地址
        map.put("redirect_uri","http://fmall.com/oauth2.0/weibo/success");
        map.put("code",code);
        HttpResponse response = HttpUtils.doPost("api.weibo.com", "/oauth2/access_token", "post", null, null, map);
        if (response.getStatusLine().getStatusCode() == 200){
            //获取到了accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            //知道当前是哪个社交用户
            //1)、如果用户第一次进网站，自动注册进来(为当前社交用户生成一个会员账号)
            //登录或注册社交用户
            R res = memberFeignService.oauthLogin(socialUser);
            if(res.getCode() == 0){
                MemberRespVo data = res.getData("data", new TypeReference<MemberRespVo>() {});
                //1.第一次使用session；命令浏览器保存JSESSIONID这个cookie
                //以后浏览器访问那个网站就会带上这个网站的coolie
                //指定域名为父域名
                //TODO 默认发的令牌，session=xxxxx,作用域：当前域(解决子域session共享问题)
                //TODO 使用JSON序列化方式来序列化对象数据到redis中
                session.setAttribute(AuthServerConstant.LOGIN_USER,data);
                log.info("登录成功：用户信息：{}",data.toString());
                return "redirect:http://fmall.com";
            }
        }
        return "redirect:http://auth.fmall.com/login.html";
    }
}
