package com.suse.fmall.order.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.suse.common.constant.OrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.service.OrderService;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.R;



/**
 * 订单
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:39:30
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn){
       OrderEntity orderEntity =  orderService.getOrderByOrderSn(orderSn);
       return R.ok().setData(orderEntity);
    }
    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPageByCondition(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询当前登录用户的所有订单信息
     * @return
     */
    /*@PostMapping("/listWithItems")
    public R listWithItems(@RequestBody Map<String, Object> params){
        PageUtils page = orderService.queryPageWithItem(params);
        return R.ok().put("page", page);
    }*/

    @GetMapping("/listOrderItems")
    public R listOrderItems(@RequestParam(value = "status",required = false) String status){
        List<OrderEntity> orderEntities = orderService.queryOrderItems(status);
        return R.ok().put("orders",orderEntities);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order){
        order.setModifyTime(new Date());
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 确认发货
     * @return
     */
    @PostMapping("/delivery")
    public R confirmDelivery(@RequestBody Long id){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(id);
        orderEntity.setDeliveryTime(new Date());
        orderEntity.setStatus(OrderStatusEnum.SENDED.getCode());
        orderService.updateById(orderEntity);
        return R.ok();
    }

    /**
     * 确认收货
     * @param orderId
     * @return
     */
    @PostMapping("/receive")
    public R confirmReceive(@RequestBody Long orderId){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setReceiveTime(new Date());
        orderEntity.setStatus(OrderStatusEnum.RECIEVED.getCode());
        orderService.updateById(orderEntity);
        return R.ok();
    }

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @PostMapping("/cancelOrder")
    public R cancelOrder(@RequestBody Long orderId){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderService.closeOrder(orderEntity);
        return R.ok();
    }
}
