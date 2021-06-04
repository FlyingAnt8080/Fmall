package com.suse.fmall.order.controller;

import com.suse.fmall.order.entity.OrderEntity;
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
}
