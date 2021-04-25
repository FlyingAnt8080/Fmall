package com.suse.fmall.order.service.impl;

import com.rabbitmq.client.Channel;
import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;
import com.suse.fmall.order.dao.OrderItemDao;
import com.suse.fmall.order.entity.OrderItemEntity;
import com.suse.fmall.order.service.OrderItemService;

//@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues:声明需要监听的所有队列
     * 参数可以是以下类型
     * 1、Message ：原生消息详细信息。头+体
     * 2、T<发送的消息类型> OrderReturnReasonEntity
     * Queue:可以很多人都来监听，只要收到消息，队列删除消息，而且只能有一个收到消息
     * 场景：
     *  1)订单服务启动多个，同一个消息，只能有一个客户端收到
     *  2)只有一个消息完全处理完，方法运行结束，我们就可以接收到下一个消息
     */
//    @RabbitHandler
    public void receiveMessage(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel) {
        System.out.println("接收消息...内容："+"===>"+content);
        //通道内按顺序自增
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag:"+deliveryTag);
        //非批量模式
        try {
            if (deliveryTag % 2 == 0){
                channel.basicAck(deliveryTag,false);
                System.out.println("签收货物："+deliveryTag);
            }else {
                //long deliveryTag,boolean multiple,boolean requeue
                //requeue=false 丢弃,requeue=true 发回服务器，服务器重新入队
                channel.basicNack(deliveryTag,false,true);
            }

        } catch (IOException e) {
            //网络中断
            e.printStackTrace();
        }
    }

//    @RabbitHandler
    public void receiveMessage(Message message,
                               OrderEntity content,
                               Channel channel){
        System.out.println("接收消息...内容："+message+"===>"+content);
    }
}