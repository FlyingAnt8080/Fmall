package com.suse.fmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.product.entity.SpuInfoDescEntity;
import com.suse.fmall.product.entity.SpuInfoEntity;
import com.suse.fmall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:02:27
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 商品上架
     * @param spuId
     */
    void supUp(Long spuId);
}

