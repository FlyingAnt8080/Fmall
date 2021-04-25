package com.suse.fmall.order.feign;

import com.suse.fmall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/14/ 23:08
 * @Description
 */
@FeignClient("fmall-cart")
@Service
public interface CartFeignService {

    @GetMapping("/currentUserItems")
    List<OrderItemVo> getCurrentUserCartItems();
}
