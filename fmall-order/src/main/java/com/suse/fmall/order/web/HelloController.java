package com.suse.fmall.order.web;

import com.suse.common.utils.R;
import com.suse.fmall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

/**
 * @Author LiuJing
 * @Date: 2021/04/13/ 21:53
 * @Description
 */
@Controller
public class HelloController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page){
        return page;
    }

    @GetMapping("/testCreateOrder")
    @ResponseBody
    public String createOrderTest(){
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(UUID.randomUUID().toString());
        entity.setModifyTime(new Date());
        //给MQ发送消息
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",entity);
        return "ok";
    }
}
