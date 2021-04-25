package com.suse.fmall.order;

import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class FmallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    /**
     * 1.如何创建Exchange、Queue、Binding
            1)、使用AmqpAdmin进行创建
     * 2.如何发送消息
     *
     */
    @Test
    public void createExchange(){
        DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功","hello-java-exchange");
    }

    /**
     * 创建队列
     */
    @Test
    public void createQueue(){
        Queue queue = new Queue("hello-java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功","hello-java-queue");
    }

    /**
     * 创建绑定
     */
    @Test
    public void createBinding(){

        Binding binding = new Binding("hello-java-queue", //目的地
                Binding.DestinationType.QUEUE,//目的地类型
                "hello-java-exchange",//交换机
                "hello-java",//路由键
                null);//自定义参数
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功","hello-java-binding");
    }

    /**
     * 发送消息
     */
    @Test
    public void SendMessage(){
       for (int i=0;i<10;i++){
           if (i % 2 == 0){
               OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
               reasonEntity.setId(1l);
               reasonEntity.setCreateTime(new Date());
               reasonEntity.setName("Hello");
               //如果发送的消息是对象，会使用序列化机制，将对象写出去。要求对象对应的类实现Serializable接口
               rabbitTemplate.convertAndSend("hello-java-exchange","hello-java",reasonEntity);
           }else{
               OrderEntity orderEntity = new OrderEntity();
               orderEntity.setOrderSn(UUID.randomUUID().toString());
               rabbitTemplate.convertAndSend("hello-java-exchange","hello-java",orderEntity);
           }
           log.info("消息发送完成：{}","hello world");
       }
    }
}
