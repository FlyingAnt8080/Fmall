package com.suse.fmall.search.controller;

import com.suse.common.exception.BizCodeEnume;
import com.suse.common.to.es.SkuEsModel;
import com.suse.common.utils.R;
import com.suse.fmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/21/ 21:57
 * @Description
 */
@RestController
@RequestMapping("/search/save")
@Slf4j
public class ElasticSaveController {
    @Autowired
    private ProductSaveService productSaveService;
    /**
     * 上架商品
     * @param skuEsModels
     * @return
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){
        boolean flag;
        try {
            flag = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            log.error("ElasticSaveController商品上架异常：{}",e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (flag){
            return R.ok();
        }else {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }
}
