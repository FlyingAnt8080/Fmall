package com.suse.fmall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.suse.fmall.ware.vo.MergeVo;
import com.suse.fmall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.suse.fmall.ware.entity.PurchaseEntity;
import com.suse.fmall.ware.service.PurchaseService;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.R;



/**
 * 采购信息
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-03-14 09:36:10
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 领取采购单
     * @param doneVo
     * @return
     */
    @PostMapping("/done")
    public R finishPurchase(@RequestBody PurchaseDoneVo doneVo){
        purchaseService.finishPurchase(doneVo);
        return R.ok();
    }

    /**
     * 领取采购单
     * @param ids
     * @return
     */
    @PostMapping("/received")
    public R receivedPurchase(@RequestBody List<Long> ids){
        purchaseService.receivedPurchase(ids);
        return R.ok();
    }

    /**
     * 查询所有未领取的采购单
     */
    @GetMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceivePurchase(params);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }
}
