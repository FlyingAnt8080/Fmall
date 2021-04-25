package com.suse.fmall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * @Author LiuJing
 * @Date: 2021/04/20/ 22:25
 * @Description
 * 定时任务
 *      1.@EnableScheduling 开启定时任务功能
 *      2.@Scheduled 开启一个定时任务
 *      3.自动配置类TaskSchedulingAutoConfiguration
 * 异步任务
 *      1.@EnableAsync 开启异步任务功能
 *      2.@Async 给希望异步执行的方法上标注
 *      2.自动配置类TaskExecutionAutoConfiguration 属性绑定在TaskExecutionProperties
 */

@Slf4j
@Component
public class HelloSchedule {
    /**
     * 1 Spring自带的任务调度cron表达式只有6位，不允许第7位的年
     * 2 Spring自带的任务调度cron表达式中用1-7表示周一到周日或MON-SUN
     * 3 定时任务不应该阻塞。默认是阻塞的
     *      1) 可以让业务运行以异步的方式，自己提交到线程池
     *          CompletableFuture.runAsync(()->{
     *             xxxService.hello();
     *         });
     *      2) 支持定时任务线程池：设置spring.task.scheduling.pool.size=5
     *          spring.task.scheduling.pool.size=5
     *      3) 让定时任务异步执行
     *          @Async//让定时任务以异步方式运行
     *      解决：使用异步+定时任务来完成定时任务不阻塞的功能
     */

    @Async//开启一个异步任务(让定时任务以异步方式运行)
    @Scheduled(cron = "*/5 * * ? * 2")//开启一个定时任务
    public void hello(){
        log.info("hello...'");
    }
}
