package com.suse.fmall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author LiuJing
 * @Date: 2021/04/20/ 22:55
 * @Description
 */
@EnableAsync//开启异步任务
@EnableScheduling//开启定时任务功能
@Configuration
public class ScheduledConfig {

}
