package com.suse.fmall.order.listener;

import com.rabbitmq.client.Channel;
import com.suse.common.to.mq.SeckillOrderTo;
import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author LiuJing
 * @Date: 2021/04/24/ 14:29
 * @Description
 */
@Slf4j
@Component
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSeckillListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTo seckillOrder, Channel channel, Message message){
        log.info("创建秒杀单的详细信息：{}",seckillOrder.toString());
        orderService.createSeckillOrder(seckillOrder);
    }
}
