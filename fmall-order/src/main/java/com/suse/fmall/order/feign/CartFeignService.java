package com.suse.fmall.order.feign;

import com.suse.common.utils.R;
import com.suse.fmall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    /**
     * 删除购物车中已购买的商品项
     * @param ids
     * @return
     */
    @PostMapping("/clearPurchasedCartItems")
    R clearPurchasedCartItems(@RequestBody List<Long> ids);
}
