package com.suse.fmall.product.feign;

import com.suse.common.to.es.SkuEsModel;
import com.suse.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/21/ 22:21
 * @Description
 */
@FeignClient("fmall-search")
@Service
public interface SearchFeginService {

    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
