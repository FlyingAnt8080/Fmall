package com.suse.fmall.order.feign;

import com.suse.common.utils.R;
import com.suse.fmall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/15/ 22:54
 * @Description
 */
@FeignClient("fmall-ware")
@Service
public interface WareFeignService {

    /**
     * 根据收货地址Id查询快递费
     * @param addrId
     * @return
     */
    @RequestMapping("/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    /**
     * 锁定订单库存
     * @param lockVo
     * @return
     */
    @PostMapping("/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo lockVo);

    /**
     * 根据skuId查询商品是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
    R getSkusStock(@RequestBody List<Long> skuIds);
}
