package com.suse.fmall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author LiuJing
 * @Date: 2021/04/14/ 23:51
 * @Description
 */
@Configuration
public class FmallFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attribute != null){
                    HttpServletRequest request = attribute.getRequest();//老请求
                    //同步请求头数据，主要是Cookie
                    requestTemplate.header("Cookie",request.getHeader("Cookie"));
                }
            }
        };
    }
}
