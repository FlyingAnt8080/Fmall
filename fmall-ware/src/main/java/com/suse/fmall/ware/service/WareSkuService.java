package com.suse.fmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.to.OrderTo;
import com.suse.common.to.mq.StockLockedTo;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.ware.entity.WareSkuEntity;
import com.suse.fmall.ware.vo.SkuHasStockVo;
import com.suse.fmall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-18 09:24:41
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * 锁库存
     * @param lockVo
     * @return
     */
    Boolean orderLockStock(WareSkuLockVo lockVo);

    void unlockStock(StockLockedTo lockedTo);

    void unlockStock(OrderTo orderTo);

    void deleteStock(String orderSn);

}

