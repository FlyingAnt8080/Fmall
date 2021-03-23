package com.suse.fmall.ware.service.impl;

import com.suse.common.constant.WareConstant;
import com.suse.fmall.ware.entity.PurchaseDetailEntity;
import com.suse.fmall.ware.service.PurchaseDetailService;
import com.suse.fmall.ware.service.WareSkuService;
import com.suse.fmall.ware.vo.MergeVo;
import com.suse.fmall.ware.vo.PurchaseDoneVo;
import com.suse.fmall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;

import com.suse.fmall.ware.dao.PurchaseDao;
import com.suse.fmall.ware.entity.PurchaseEntity;
import com.suse.fmall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    private PurchaseDetailService detailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null){
            //采购单为空先创建一个采购单，然后在合并
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //TODO 确认采购单状态是0或1
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        detailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void receivedPurchase(List<Long> ids) {
        //1.确认当前采购单是新建或已分配状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity entity = this.getById(id);
            return entity;
        }).filter(item ->
            item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()
                    || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()
        ).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //2.改变采购单的状态
        if (!CollectionUtils.isEmpty(collect)){
            this.updateBatchById(collect);
        }
        //3.改变采购项的状态
        collect.forEach(item -> {
           List<PurchaseDetailEntity> detailEntities =  detailService.listDetailByPurchaseId(item.getId());
           List<PurchaseDetailEntity> pdEntities = detailEntities.stream().map(entity -> {
               PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
               detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
               detailEntity.setId(entity.getId());
               return detailEntity;
           }).collect(Collectors.toList());
           detailService.updateBatchById(pdEntities);
        });
    }

    @Override
    public void finishPurchase(PurchaseDoneVo doneVo) {

        //1.改变采购项状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> updateItems = new ArrayList<>();
        for (PurchaseItemDoneVo item : items){
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HAS_ERROR.getCode()){
                flag = false;
            }else{
                //2.将成功采购进行入库
                PurchaseDetailEntity entity = detailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            detailEntity.setStatus(item.getStatus());
            detailEntity.setId(item.getItemId());
            updateItems.add(detailEntity);
        }
        detailService.updateBatchById(updateItems);

        //3.改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(doneVo.getId());
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseDetailStatusEnum.FINISH.getCode() : WareConstant.PurchaseDetailStatusEnum.HAS_ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }
}