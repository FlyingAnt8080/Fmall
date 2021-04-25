package com.suse.fmall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 本地事务失效问题
 * 同一个对象内事务方法互调默认失效，原因：绕过了代理对象，事务底层使用代理对象控制的
 * 解决：使用代理对象调用事务方法
 *  1) 引入spring-boot-starter-aop;使用aspectJ做代理
 *  2) @EnableAspectJAutoProxy(exposeProxy = true) 开启AspectJ动态代理功能，以后所有动态代理都使用aspectJ(即使没有接口也可以创建动态代理)
 *      exposeProxy = true 表示对外暴露代理对象
 *  3) 在同一个类中方法之间调动
 *     OrderServiceImpl orderService =  AopContext.currentProxy();
 *      orderService.b();
 *      orderService.c();
 */
@EnableRabbit
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class FmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallOrderApplication.class, args);
    }
}
