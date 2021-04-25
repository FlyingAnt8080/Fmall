package com.suse.fmall.seckill.config;

import com.suse.fmall.seckill.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author LiuJing
 * @Date: 2021/04/23/ 0:02
 * @Description
 */
@Configuration
public class SeckillWebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginUserInterceptor loginUserInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
