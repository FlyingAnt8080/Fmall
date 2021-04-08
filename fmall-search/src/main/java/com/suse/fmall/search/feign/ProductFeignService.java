package com.suse.fmall.search.feign;

import com.suse.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/06/ 22:38
 * @Description
 */
@FeignClient("fmall-product")
public interface ProductFeignService {
    @GetMapping("/product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);

    /**
     * 根据品牌ids查询品牌信息
     */
    @RequestMapping("/product/brand/infos")
    R brandInfo(@RequestParam("brandIds") List<Long> brandIds);
}
