package com.suse.fmall.order.controller;

import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @Author LiuJing
 * @Date: 2021/04/13/ 20:22
 * @Description
 */
@RestController
public class RabbitController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMQ")
    public String sendMessage(@RequestParam(value = "num",defaultValue = "10") Integer num){
        /*for (int i=0;i<num;i++){
            if (i % 2 == 0){
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1l);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("Hello");
                //如果发送的消息是对象，会使用序列化机制，将对象写出去。要求对象对应的类实现Serializable接口
                rabbitTemplate.convertAndSend("hello-java-exchange",
                        "hello-java",
                        reasonEntity,
                        new CorrelationData(UUID.randomUUID().toString()));
            }else{
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange",
                        "hello-java",
                        orderEntity,
                        new CorrelationData(UUID.randomUUID().toString()));
            }

        }*/
        for (int i=0;i<5;i++){
            OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
            reasonEntity.setId(1l);
            reasonEntity.setCreateTime(new Date());
            reasonEntity.setName("Hello");
            //如果发送的消息是对象，会使用序列化机制，将对象写出去。要求对象对应的类实现Serializable接口
            rabbitTemplate.convertAndSend("hello-java-exchange",
                    "hello-java",
                    reasonEntity,
                    new CorrelationData(UUID.randomUUID().toString()));
        }
        return "ok";
    }
}
