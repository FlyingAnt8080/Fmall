package com.suse.fmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.suse.fmall.product.entity.ProductAttrValueEntity;
import com.suse.fmall.product.service.AttrAttrgroupRelationService;
import com.suse.fmall.product.service.ProductAttrValueService;
import com.suse.fmall.product.vo.AttrRespVo;
import com.suse.fmall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.suse.fmall.product.entity.AttrEntity;
import com.suse.fmall.product.service.AttrService;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.R;



/**
 * 商品属性
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:39:49
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 根据spu的id查询该商品对应的规格参数信息
     * @param spuId
     * @return
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data",entities);
    }


    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String type){
        PageUtils page = attrService.queryBaseAttrPage(params,catelogId,type);
        return R.ok().put("page",page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
        AttrRespVo respVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(spuId,entities);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));
		//TODO 是否需要删除关联表
        return R.ok();
    }
}
