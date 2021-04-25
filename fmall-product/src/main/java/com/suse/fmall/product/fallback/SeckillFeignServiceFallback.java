package com.suse.fmall.product.fallback;

import com.suse.common.exception.BizCodeEnume;
import com.suse.common.utils.R;
import com.suse.fmall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author LiuJing
 * @Date: 2021/04/25/ 9:40
 * @Description
 */
@Slf4j
@Component
public class SeckillFeignServiceFallback implements SeckillFeignService {

    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.error("getSkuSeckillInfo 熔断保护");
        return R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(),BizCodeEnume.TOO_MANY_REQUEST.getMsg());
    }
}
