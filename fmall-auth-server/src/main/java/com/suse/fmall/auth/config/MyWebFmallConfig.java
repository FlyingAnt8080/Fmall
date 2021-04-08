package com.suse.fmall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author LiuJing
 * @Date: 2021/04/07/ 22:56
 * @Description
 */
@Configuration
public class MyWebFmallConfig implements WebMvcConfigurer {
    /**
     * 视图映射
     * 直接渲染页面
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
