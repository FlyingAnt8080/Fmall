package com.suse.fmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.suse.fmall.product.service.CategoryBrandRelationService;
import com.suse.fmall.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redisson;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
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
     * @CahePut:双写模式
     * @CacheEvict：失效模式
     * 1、同时进行多种缓存操作 @Caching
     * 2、删除某个分区下的所有数据 @CacheEvict(value = "category",allEntries = true)
     * 3、存储同一类型数据，可以指定成同一个分区
     * @param category
     */
//    @Caching(evict = {
//            @CacheEvict(value = "category",key = "'getLevel1Categories'"),
//            @CacheEvict(value = "category",key = "'getCatalogJson'")
//    })
    @CacheEvict(value = "category",allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    /**
     * 查询所有的一级分类
     * 每个缓存的数据需要指定缓存的数据放到哪个名字的缓存下【缓存分区(按照业务分)】
     * 自定义:
     *  ①指定生成缓存时使用的key（#root.menthod.name 表示用方法名作key）
     *      https://docs.spring.io/spring-framework/docs/5.2.13.RELEASE/spring-framework-reference/integration.html#cache-spel-context
     *  ②指定缓存数据的存活时间
     *      在配置文件中配置ttl: spring.cache.redis.time-to-live: 3600000
     *  ③将数据保存为json格式
     *  CacheAutoConfiguration会导入RedisCacheConfiguration
     *
     *
     * @return
     */
    //sync 表示是否加锁
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)//当前方法的结果需要缓存，如果缓存中有方法不用调用。如果缓存中没有，调用方法最后将方法的结果放入缓存
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        System.out.println("getLevel1Categories...");
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Cacheable(value = "category",key = "#root.methodName",sync = true)
    @Override
    public Map<String, List<Catalog2Vo>> getCateLogJson() {
        System.out.println("查询数据库.....");
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //1.查出所有1级分类
        List<CategoryEntity> level1Categories = getCategoryByParentCid(categoryEntities, 0L);
        Map<String, List<Catalog2Vo>> catalogVos = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //2.查出每个一级分类下的二级分类封装成vo
            List<CategoryEntity> level2Categories = getCategoryByParentCid(categoryEntities, v.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (level2Categories != null) {
                catalog2Vos = level2Categories.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //3.找到每个二级分类下的三级分类封装成vo
                    List<CategoryEntity> level3Categories = getCategoryByParentCid(categoryEntities, l2.getCatId());
                    if (level3Categories != null) {
                        List<Catalog2Vo.Catalog3Vo> catalog3Vos = level3Categories.stream().map(l3 -> {
                            Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(catalog3Vos);
                    }
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));
        return catalogVos;
    }

    /**
     * TODO 堆外内存溢出 OutOfDirectMemoryError
     * SpringBoot 2.2.2 集成的lettuce是5.2.1版本，不会出现该异常
     * @return
     */
    //@Override
    /*public Map<String, List<Catalog2Vo>> getCateLogJson2() {

     *//**
     * 1.空结果缓存：解决缓存穿透问题
     * 2.设置过期时间(加随机值)：解决缓存雪崩问题
     * 3.加锁：解决缓存击穿问题
     *//*

        //加入缓存
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)){
            //缓存中没有，查询数据
            System.out.println("缓存不命中.....将要查询数据库....");
            Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedissonLock();
            return catalogJsonFromDb;
        }
        System.out.println("缓存命中....直接返回....");
        //转换成我们存的对象
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>(){});
        return result;
    }
*/
    /**
     * 使用Redisson做分布式锁
     * 缓存数据一致性问题
     * ①双写模式
     * ②失效模式
     * @return
     */
//    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
//        //锁名相同就是同一把锁
//        RLock lock = redisson.getLock("catalog-json-lock");
//        Map<String, List<Catalog2Vo>> dataFromDb;
//        lock.lock();
//
//        try{
//            dataFromDb = getDataFromDB();
//        }finally {
//            lock.unlock();
//        }
//        return dataFromDb;
//    }
    /**
     * 使用Redis做分布式锁
     * @return
     */
//    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
//        //1.占分布式锁
//        //2.设置lock过期时间,必须和加锁是原子操作
//        String token =  UUID.randomUUID().toString();
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", token,300,TimeUnit.SECONDS);
//        //加锁失败....重试
//        //自旋锁
//        while (!lock){
//            //休眠100ms
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            token = UUID.randomUUID().toString();
//            lock = redisTemplate.opsForValue().setIfAbsent("lock", token,300,TimeUnit.SECONDS);
//        }
//
//        System.out.println("获取分布式锁成功...");
//        //加锁成功...执行业务
//        Map<String, List<Catalog2Vo>> dataFromDB;
//        try{
//            dataFromDB = getDataFromDB();
//        }finally {
//            //解锁
//            //获取值对比，对比成功删除锁要是原子操作：用lua脚本解锁
//            String script = " if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
//            //删除成功发回1，删除失败0
//            redisTemplate.execute(new DefaultRedisScript<>(script,Long.class),Arrays.asList("lock"),token);
//        }
//        return dataFromDB;
//    }

//    private Map<String, List<Catalog2Vo>> getDataFromDB() {
//        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
//        if (!StringUtils.isEmpty(catalogJson)){
//            //缓存不为null直接返回
//            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>(){});
//            return result;
//        }
//        System.out.println("查询数据库.....");
//        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
//        //1.查出所有1级分类
//        List<CategoryEntity> level1Categories = getCategoryByParentCid(categoryEntities, 0L);
//        Map<String, List<Catalog2Vo>> catalogVos = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//            //2.查出每个一级分类下的二级分类封装成vo
//            List<CategoryEntity> level2Categories = getCategoryByParentCid(categoryEntities, v.getCatId());
//            List<Catalog2Vo> catalog2Vos = null;
//            if (level2Categories != null) {
//                catalog2Vos = level2Categories.stream().map(l2 -> {
//                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
//                    //3.找到每个二级分类下的三级分类封装成vo
//                    List<CategoryEntity> level3Categories = getCategoryByParentCid(categoryEntities, l2.getCatId());
//                    if (level3Categories != null) {
//                        List<Catalog2Vo.Catalog3Vo> catalog3Vos = level3Categories.stream().map(l3 -> {
//                            Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                            return catalog3Vo;
//                        }).collect(Collectors.toList());
//                        catalog2Vo.setCatalog3List(catalog3Vos);
//                    }
//                    return catalog2Vo;
//                }).collect(Collectors.toList());
//            }
//            return catalog2Vos;
//        }));
//        //将查询数据转换成json存入redis
//        String json = JSON.toJSONString(catalogVos);
//        redisTemplate.opsForValue().set("catalogJson", json, 1, TimeUnit.DAYS);
//        return catalogVos;
//    }


    /**
     * 过滤parentCid为指定值的分类
     * @param categoryEntities
     * @param parentCid
     * @return
     */
    public List<CategoryEntity> getCategoryByParentCid(List<CategoryEntity> categoryEntities,Long parentCid){
        List<CategoryEntity> collect = categoryEntities.stream().filter(item -> item.getParentCid() == parentCid).collect(Collectors.toList());
        return collect;
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