package com.suse.fmall.order.listener;

import com.rabbitmq.client.Channel;
import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author LiuJing
 * @Date: 2021/04/18/ 14:36
 * @Description
 */
@Slf4j
@Component
@RabbitListener(queues = "order.release.order.queue")
public class OrderCloseListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        log.info("收到过期订单信息，准备关闭订单：{}",orderEntity.getOrderSn());
        try {
            orderService.closeOrder(orderEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            //手动调用支付宝收单

        } catch (IOException e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
