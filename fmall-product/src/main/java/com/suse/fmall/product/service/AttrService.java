package com.suse.fmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.product.entity.AttrEntity;
import com.suse.fmall.product.entity.ProductAttrValueEntity;
import com.suse.fmall.product.vo.AttrGroupRelationVo;
import com.suse.fmall.product.vo.AttrRespVo;
import com.suse.fmall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:02:27
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);


    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);


    void deleteRelation(AttrGroupRelationVo[] vos);


    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 在指定的所有属性里面挑出检索属性
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}


