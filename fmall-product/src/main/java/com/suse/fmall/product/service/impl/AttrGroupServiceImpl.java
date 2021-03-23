package com.suse.fmall.product.service.impl;

import com.suse.fmall.product.entity.AttrEntity;
import com.suse.fmall.product.service.AttrService;
import com.suse.fmall.product.vo.AttrGroupWithAttrsVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;

import com.suse.fmall.product.dao.AttrGroupDao;
import com.suse.fmall.product.entity.AttrGroupEntity;
import com.suse.fmall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and((obj)->
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key)
            );
        }
        if (catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), queryWrapper);
            return new PageUtils(page);
        }else {
            queryWrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), queryWrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据分类id查出所有的分组以及这些组里面的属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatlogId(Long catelogId) {
        //查询分组信息
        List<AttrGroupEntity> attrGroups = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //查询所有属性
        List<AttrGroupWithAttrsVo> attrVos =  attrGroups.stream().map(group -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrsVo);
            List<AttrEntity> attrEntities = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrEntities);
            return attrsVo;
        }).collect(Collectors.toList());
        return attrVos;
    }
}