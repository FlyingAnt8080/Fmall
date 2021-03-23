package com.suse.fmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.suse.fmall.product.entity.AttrEntity;
import com.suse.fmall.product.service.AttrAttrgroupRelationService;
import com.suse.fmall.product.service.AttrService;
import com.suse.fmall.product.service.CategoryService;
import com.suse.fmall.product.vo.AttrGroupRelationVo;
import com.suse.fmall.product.vo.AttrGroupWithAttrsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.suse.fmall.product.entity.AttrGroupEntity;
import com.suse.fmall.product.service.AttrGroupService;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.R;



/**
 * 属性分组
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:39:49
 */
@Slf4j
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){
        relationService.saveBatch(vos);
        return R.ok();
    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        //1.查询当前分类下的所有属性分组
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatlogId(catelogId);
        //2.查出每个属性分组的所有属性

        return R.ok().put("data",vos);
    }
    /**
     * 获取属性组关联的所有分属性
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",entities);
    }

    /**
     * 获取该属性组没有关联的所有属性
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                            @PathVariable("attrgroupId") Long attrgroupId){
        PageUtils page = attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page",page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        log.debug("catelogId:{}",catelogId);
        Long[] path = categoryService.findCatelogPath(catelogId);
        log.debug("path:{}",path);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelattion(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }

}
