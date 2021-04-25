package com.suse.fmall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiuJing
 * @Date: 2021/04/17/ 23:45
 * @Description
 */
@Configuration
public class MyRabbitConfig {
    /**
     * 配置发送的消息采用JSON进行序列化
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建普通交换机
     * @return
     */
    @Bean
    public Exchange stockEventExchange(){
      return new TopicExchange("stock-event-exchange",true,false);
    }

    /**
     * 创建普通队列
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue(){
        return new Queue("stock.release.stock.queue",true,false,false);
    }

    /**
     * 创建延迟队列
     * @return
     */
    @Bean
    public Queue stockDelayQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","stock-event-exchange");
        arguments.put("x-dead-letter-routing-key","stock.release");
        arguments.put("x-message-ttl",120000);
        return new Queue("stock.delay.queue",true,false,false,arguments);
    }

    /**
     * 和普通队列绑定
     * @return
     */
    @Bean
    public Binding stockReleaseBinding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }

    /**
     * 和延时队列绑定
     * @return
     */
    @Bean
    public Binding stockLockedBinding(){
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                 null);
    }

    //要监听队列的组件第一次连接MQ时才会创建所有的Exchange、Queue、Binding等组件
    /*@RabbitListener(queues = {"stock.release.stock.queue"})
    public void handle(Message message){

    }*/
}
