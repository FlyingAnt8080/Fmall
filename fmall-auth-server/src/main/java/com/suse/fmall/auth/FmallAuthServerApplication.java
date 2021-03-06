package com.suse.fmall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 核心原理
 * 1) @EnableRedisHttpSession 导入了RedisHttpSessionConfiguration配置
 *    1、给容器添加了一个组件
 *    SessionRepository-->【RedisIndexedSessionRepository】：redis操作session，session的增删改查封装类
 *    2、SessionRepositoryFilter：session存储过滤器，每个请求过来都必须经过filter
 *      1、创建的时候，就自动从容器中获取SessionRepository
 *      2、原始的request、response都被包装。SessionRepositoryRequestWrapper、SessionRepositoryResponseWrapper
 *      3、以后获取session。request.getSession()是调用的wrapper.getSession()
 *      4、wrapperRequest.getSession();=======>SessionRepository中获取到的
 *装饰者模式
 *
 * session自动延期
 */
@EnableRedisHttpSession //整合redis作为session存储
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FmallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallAuthServerApplication.class, args);
    }

}
