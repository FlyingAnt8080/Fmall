package com.suse.fmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-03-14 09:36:10
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);


}

