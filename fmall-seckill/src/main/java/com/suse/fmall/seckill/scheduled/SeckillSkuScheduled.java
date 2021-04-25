package com.suse.fmall.seckill.scheduled;

import com.suse.fmall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author LiuJing
 * @Date: 2021/04/20/ 22:54
 * @Description
 *  秒杀商品的定时上架功能
 *      每天晚上3点上架最近三天需要秒杀的商品
 *      当天00:00:00 - 23:59:59
 *      明天00:00:00 - 23:59:59
 *      后天00:00:00 - 23:59:59
 */
@Slf4j
@Service
public class SeckillSkuScheduled {
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedissonClient redissonClient;
    private final String UPLOAD_LOCK = "seckill:upload:lock";
    /**
     * 每天晚上3点执行
     */
    @Scheduled(cron = "0 * * * * ?")
//    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days(){
        log.info("上架秒杀的商品信息....");
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        try{
            lock.lock(10, TimeUnit.SECONDS);
            seckillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }
}
