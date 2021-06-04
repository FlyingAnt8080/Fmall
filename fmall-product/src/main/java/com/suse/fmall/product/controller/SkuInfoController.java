package com.suse.fmall.product.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.suse.fmall.product.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.suse.fmall.product.entity.SkuInfoEntity;
import com.suse.fmall.product.service.SkuInfoService;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.R;



/**
 * sku信息
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:39:49
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SpuInfoService spuInfoService;
    /**
     * 根据skuId查询商品价格
     * @param skuId
     * @return
     */
    @GetMapping("/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId){
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        return skuInfoEntity.getPrice();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
        skuInfoService.removeSkuInfoByIds(skuIds,true);
        return R.ok();
    }

    /**
     * 查询skuIds中被删除或下架的skuId
     * @param skuIds
     * @return
     */
    @PostMapping("/delOrDownSkuIds")
    List<Long> selectDelOrDownSkuIds(@RequestBody List<Long> skuIds){
       return skuInfoService.selectDelOrDownSkuIds(skuIds);
    }
}
