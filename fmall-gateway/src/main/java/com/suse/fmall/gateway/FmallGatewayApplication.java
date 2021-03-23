package com.suse.fmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
//排除与数据库有关的配置
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FmallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallGatewayApplication.class, args);
    }

}
