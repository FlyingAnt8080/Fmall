package com.suse.fmall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.suse.common.exception.BizCodeEnume;
import com.suse.fmall.product.entity.SkuInfoEntity;
import com.suse.fmall.product.exception.SpuDownException;
import com.suse.fmall.product.exception.SpuUpException;
import com.suse.fmall.product.feign.SearchFeignService;
import com.suse.fmall.product.vo.SpuInfoQuery;
import com.suse.fmall.product.vo.SpuInfoVo;
import com.suse.fmall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.suse.fmall.product.entity.SpuInfoEntity;
import com.suse.fmall.product.service.SpuInfoService;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.R;



/**
 * spu信息
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:39:49
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;


    /**
     * 商品上架接口
     * @param spuId
     * @return
     */
    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId) throws SpuUpException {
        spuInfoService.spuUp(spuId);
        return R.ok();
    }

    /**
     * 商品下架接口
     * @param spuId
     * @return
     */
    @PostMapping("/{spuId}/down")
    public R spuDown(@PathVariable("spuId") Long spuId) throws SpuDownException {
        spuInfoService.spuDown(spuId);
        return R.ok();
    }

    /**
     * 根据skuId查询SpuInfo
     * @param skuId
     * @return
     */
    @GetMapping("/spuinfo/{skuId}")
    public R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId){
        SpuInfoEntity entity = spuInfoService.getSpuInfoBySkuId(skuId);
        return R.ok().setData(entity);
    }

    /**
     * 多条件查询
     */
    @RequestMapping("/list")
    public R list(SpuInfoQuery query){
        PageUtils page = spuInfoService.queryPageByCondition(query);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSaveVo vo){
		spuInfoService.saveSpuInfo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
