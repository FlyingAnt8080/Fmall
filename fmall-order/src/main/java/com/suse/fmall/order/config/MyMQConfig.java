package com.suse.fmall.order.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiuJing
 * @Date: 2021/04/17/ 22:43
 * @Description
 */
@Configuration
public class MyMQConfig {
    //@Bean Binding、Queue、Exchange

    /**
     * 容器中的Binding、Queue、Exchange会自动创建(前提：RabbitMQ中没有的情况下)
     * RabbitMQ中只要有对应的组件。@Bean声明属性发生变化不会覆盖
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-ttl",60000);
        return new Queue("order.delay.queue", true, false, false,arguments);
    }

    @Bean
    public Queue orderReleaseOrderQueue(){
        return new Queue("order.release.order.queue", true, false, false);
    }

    /**
     * 秒杀订单队列
     * @return
     */
    @Bean
    public Queue orderSeckillOrderQueue(){
        return new Queue("order.seckill.order.queue",true,false,false);
    }



    @Bean
    public Binding orderCreateOrderBinding(){
       return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    @Bean
    public Binding orderReleaseOrderBinding(){
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

    @Bean
    public Binding orderSeckillOrderQueueBinding(){
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null
                );
    }

    /**
     * 订单释放直接和库存释放绑定
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }

    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange", true, false);
    }
}
