package com.suse.fmall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.suse.common.exception.BizCodeEnume;
import com.suse.common.exception.NoStockException;
import com.suse.fmall.ware.vo.SkuHasStockVo;
import com.suse.fmall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.suse.fmall.ware.entity.WareSkuEntity;
import com.suse.fmall.ware.service.WareSkuService;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.R;



/**
 * 商品库存
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-18 09:24:41
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    //查询sku是否有库存
    @PostMapping("/hasstock")
    public R getSkusStock(@RequestBody List<Long> skuIds){
       List<SkuHasStockVo> vos =  wareSkuService.getSkuHasStock(skuIds);
       return R.ok().setData(vos);
    }

    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo lockVo){
        try{
           wareSkuService.orderLockStock(lockVo);
            return R.ok();
        }catch (NoStockException e){
            return R.error(BizCodeEnume.NO_STOCK_EXCEPTION.getCode(),BizCodeEnume.NO_STOCK_EXCEPTION.getMsg());
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);
        return R.ok().put("page",page);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
