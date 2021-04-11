package com.suse.fmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisHttpSession
//排除数据源的配置(common中引入了数据源导致的)
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class FmallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallSearchApplication.class, args);
    }

}
