package com.suse.fmall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1.远程调用服务步骤
 * ①引入openfeign
 * ②编写一个接口,告诉SpringCloud这个接口需要调用远程服务
 * ③开启远程调用功能
 */
//开启远程服务调用功能
@EnableFeignClients(basePackages = "com.suse.fmall.member.feign")
@EnableRedisHttpSession
@EnableDiscoveryClient
@SpringBootApplication
public class FmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallMemberApplication.class, args);
    }

}
