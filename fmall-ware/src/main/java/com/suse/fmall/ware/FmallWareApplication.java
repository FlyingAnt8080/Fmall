package com.suse.fmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@EnableFeignClients
@SpringBootApplication
public class FmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallWareApplication.class, args);
    }

}
