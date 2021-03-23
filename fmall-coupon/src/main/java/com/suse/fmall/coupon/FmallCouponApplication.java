package com.suse.fmall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallCouponApplication.class, args);
    }

}
