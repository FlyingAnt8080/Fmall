package com.suse.fmall.product.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suse.fmall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suse.fmall.product.vo.SpuInfoQuery;
import com.suse.fmall.product.vo.SpuInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:02:27
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);

    IPage<SpuInfoVo> getSpuInfoByPage(Page<SpuInfoVo> page, @Param("query") SpuInfoQuery query);
}
