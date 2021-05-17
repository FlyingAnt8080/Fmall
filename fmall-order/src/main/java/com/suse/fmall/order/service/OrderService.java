package com.suse.fmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.to.mq.SeckillOrderTo;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.order.entity.OrderEntity;
import com.suse.fmall.order.vo.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 23:39:30
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 订单确认页返回需要的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 下单
     * @param orderSubmitVo
     * @return
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    /**
     * 根据订单号查询订单
     * @param orderSn
     * @return
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 关闭订单
     * @param orderEntity
     */
    void closeOrder(OrderEntity orderEntity);

    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    List<OrderEntity> queryOrderItems(String status);

    /**
     * 支付宝支付成功
     * @param payVo
     * @return
     */
    String handlePayResult(PayAsyncVo payVo);

    /**
     * 创建秒杀订单
     * @param seckillOrder
     */
    void createSeckillOrder(SeckillOrderTo seckillOrder);
}

