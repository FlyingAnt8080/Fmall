package com.suse.fmall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author LiuJing
 * @Date: 2021/04/12/ 22:15
 * @Description
 */
@Configuration
public class MyRabbitConfig {
    private RabbitTemplate rabbitTemplate;

    // TODO
    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(messageConverter());
        initRabbitTemplate();
        return rabbitTemplate;
    }
    /**
     * 配置发送的消息采用JSON进行序列化
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * @PostConstruct：构造器创建完之后调用该方法(MyRabbitConfig对象创建完成以后，执行这个方法)
     * 发送端确认
     * 定制RabbitTemplate
     * 1.服务器收到消息就回调
     *      1)、spring.rabbitmq.publisher-confirm-type=correlated
     *      2)、设置确认回调ConfirmCallback
     * 2.消息抵达队列回调
     *      1)、spring.rabbitmq.publisher-returns=true
     *          spring.rabbitmq.template.mandatory=true
     *      2)、设置确认回调ReturnCallback
     * 3、消费端确认(保证每一个消息被正确消费，此时才可以broker删除这个消息)
     *    1、默认自动确认，只要消息接收到了，客户端自动确认，服务端就会移除这个消息
     *      (问题：服务器宕机消息会丢失)
     *    手动确认模式：spring.rabbitmq.listener.direct.acknowledge-mode=manual
     *    只要没有ACK，消息就一直是unacked状态，即使Consumer宕机，消息不会丢失，会重新变为Ready状态。下次有新的consumer连接进来就发送给它
     *    2、如何签收：
     *    channel.basicAck(deliveryTag,false);签收；业务成功完成后就应该签收
     *    channel.basicNack(deliveryTag,false,false);拒签；业务失败，拒签
     *
     */

    public void initRabbitTemplate(){
        //设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息抵达Broker就ack=true
             * @param correlationData   当前消息的唯一关联数据(这是消息的唯一id)
             * @param ack 消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                /**
                 * 做好消息确认机制(publisher、consumer[手动ack])
                 * 每一个发送的消息都在数库做好记录。定期将失败的消息再次发送一遍
                 */
                System.out.println("confirm...correlationData["+correlationData+"]===>ack["+ack+"]===>cause["+cause+"]");
            }
        });
        //设置消息抵达队列的确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定的队列，就触发这个失败回调
             * private final Message message; 投递失败消息详细信息
             * private final int replyCode; 回复的状态码
             * private final String replyText;  回复的文本内容
             * private final String exchange; 当时这个消息发送给哪个交换机
             * private final String routingKey; 当时这个消息用的哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("Fail Message");

            }
        });
    }
}
