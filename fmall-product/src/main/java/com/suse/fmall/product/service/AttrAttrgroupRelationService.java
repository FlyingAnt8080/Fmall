package com.suse.fmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suse.common.utils.PageUtils;
import com.suse.fmall.product.entity.AttrAttrgroupRelationEntity;
import com.suse.fmall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:02:27
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatch(List<AttrGroupRelationVo> vos);


}

