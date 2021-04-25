package com.suse.fmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.ware.entity.WareInfoEntity;
import com.suse.fmall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-18 09:24:41
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户收货地址计算运费
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);

}

