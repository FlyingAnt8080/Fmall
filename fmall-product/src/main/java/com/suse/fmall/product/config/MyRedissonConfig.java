package com.suse.fmall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;

/**
 * @Author LiuJing
 * @Date: 2021/03/28/ 10:46
 * @Description
 */
@Configuration
public class MyRedissonConfig {
    /**
     * 所有对Redisson的使用都是通过RedissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson(){
        //Redis单节点模式配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://169.254.122.40:6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;

        /*Redis集群模式配置
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");
        return Redisson.create(config);
        */
    }
}
