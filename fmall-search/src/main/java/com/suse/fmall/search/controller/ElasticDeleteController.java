package com.suse.fmall.search.controller;

import com.suse.common.exception.BizCodeEnume;
import com.suse.common.utils.R;
import com.suse.fmall.search.service.ProductDeleteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author LiuJing
 * @Date: 2021/05/04/ 16:01
 * @Description
 */
@Slf4j
@RestController
@RequestMapping("/search/del")
public class ElasticDeleteController {
    @Autowired
    private ProductDeleteService productDeleteService;

    @PostMapping("/product/{spuId}")
    public R productStatusDown(@PathVariable("spuId") Long spuId){
        boolean flag;
        try {
            flag = productDeleteService.productStatusDown(spuId);
        } catch (IOException e) {
            log.error("ElasticDeleteController商品下架异常,{}",e);
           return R.error(BizCodeEnume.PRODUCT_DOWN_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_DOWN_EXCEPTION.getMsg());
        }
        if (flag){
            return R.ok();
        }else {
           return R.error(BizCodeEnume.PRODUCT_DOWN_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_DOWN_EXCEPTION.getMsg());
        }
    }
}
