package com.suse.fmall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author LiuJing
 * @Date: 2021/04/07/ 21:16
 * @Description
 */
@Configuration
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties poolProperties){
       return new ThreadPoolExecutor(poolProperties.getCoreSize(),
               poolProperties.getMaxSize(),
               poolProperties.getKeepAliveTime(),
               TimeUnit.SECONDS,
               new LinkedBlockingDeque<>(100000),
               Executors.defaultThreadFactory(),
               new ThreadPoolExecutor.AbortPolicy());
    }
}
