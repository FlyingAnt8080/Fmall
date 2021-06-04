package com.suse.fmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.product.entity.SpuInfoEntity;
import com.suse.fmall.product.exception.SpuDownException;
import com.suse.fmall.product.exception.SpuUpException;
import com.suse.fmall.product.vo.SpuInfoQuery;
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

    PageUtils queryPageByCondition(SpuInfoQuery query);

    /**
     * 商品上架
     * @param spuId
     */
    void spuUp(Long spuId) throws SpuUpException;

    /**
     *
     * @param skuId
     * @return
     */
    SpuInfoEntity getSpuInfoBySkuId(Long skuId);

    /**
     * 商品下架
     * @param spuId
     */
    void spuDown(Long spuId) throws SpuDownException;

    /**
     * 根据SpuId商品
     * @param id
     */
    void removeBySpuId(Long id);
}

