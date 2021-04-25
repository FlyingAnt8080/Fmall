package com.suse.fmall.ware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@EnableRabbit
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallWareApplication.class, args);
    }

}
