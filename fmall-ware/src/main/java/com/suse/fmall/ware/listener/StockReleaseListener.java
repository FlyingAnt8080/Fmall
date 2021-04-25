package com.suse.fmall.ware.listener;

import com.rabbitmq.client.Channel;
import com.suse.common.to.OrderTo;
import com.suse.common.to.mq.StockLockedTo;
import com.suse.fmall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * @Author LiuJing
 * @Date: 2021/04/18/ 11:19
 * @Description
 */
@Slf4j
@Component
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {
    @Autowired
    private WareSkuService wareSkuService;
    /**
     * 1)、下订单成功,订单过期没有支付被系统自动取消、被用户手动取消。都要解锁库存
     * 2)、下单成功，库存锁定成功，但业务调用失败，导致订单回滚。之前锁定的库存就要自动解锁
     * @param lockedTo
     * @param message
     */
    @RabbitHandler
    public void  handleStockLockedRelease(StockLockedTo lockedTo, Message message, Channel channel) throws IOException {
        try{
            wareSkuService.unlockStock(lockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //失败重新放回队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo,Message message, Channel channel) throws IOException {
        log.info("订单关闭准备解锁库存...");
        try{
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //失败重新放回队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
