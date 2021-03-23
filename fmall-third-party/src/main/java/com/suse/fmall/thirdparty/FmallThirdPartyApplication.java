package com.suse.fmall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FmallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmallThirdPartyApplication.class, args);
    }

}
