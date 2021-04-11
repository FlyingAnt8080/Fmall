package com.suse.fmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 模板引擎Thymeleaf
 * ①静态资源放在static文件夹下可以按照路径访问
 * ②页面放在templates下，可以直接访问
 * ③SpringBoot访问项目时，默认会找index页面
 * WebMvcAutoConfiguration
 */

/**
 * 整合redis
 * ①引入starter spring-boot-starter-data-redis
 * ②简单配置Redis的host信息
 * ③使用SpringBoot自动配置好的RedisTemplate、StringRedisTemplate操作Redis
 */
@MapperScan("com.suse.fmall.product.dao")
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.suse.fmall.product.feign")
@SpringBootApplication
public class FmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallProductApplication.class, args);
    }

}
