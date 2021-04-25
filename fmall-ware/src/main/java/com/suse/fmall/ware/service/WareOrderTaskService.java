package com.suse.fmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-18 09:24:41
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);


    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

