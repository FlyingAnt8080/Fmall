package com.suse.fmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.ware.entity.PurchaseEntity;
import com.suse.fmall.ware.vo.MergeVo;
import com.suse.fmall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-03-14 09:36:10
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void receivedPurchase(List<Long> ids);


    void finishPurchase(PurchaseDoneVo doneVo);

}

