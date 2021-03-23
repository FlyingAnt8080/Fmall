package com.suse.fmall.product.service.impl;

import com.suse.fmall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;

import com.suse.fmall.product.dao.CategoryDao;
import com.suse.fmall.product.entity.CategoryEntity;
import com.suse.fmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> findWithTree() {
        //1.查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2.组装成树形结构
        List<CategoryEntity> level1Menus = entities.stream().
                filter((categoryEntity) -> categoryEntity.getParentCid() == 0)
                .map((menu)->{
                    menu.setChildren(getChildren(menu,entities));
                    return menu;
                })
                .sorted((menu1,menu2)->((menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort())))
                .collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return paths.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0){
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;
    }

    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){
       List<CategoryEntity> children =  all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .map(categoryEntity ->{
                    categoryEntity.setChildren(getChildren(categoryEntity,all));
                    return categoryEntity;
                })
                .sorted((menu1,menu2)->((menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort())))
                .collect(Collectors.toList());
       return children;

    }
}