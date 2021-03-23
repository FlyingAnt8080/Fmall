package com.suse.fmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.suse.fmall.product.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.suse.fmall.product.feign")
@SpringBootApplication
public class FmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallProductApplication.class, args);
    }

}
