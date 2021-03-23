package com.suse.fmall.product.vo;

import lombok.Data;

/**
 * @Author LiuJing
 * @Date: 2021/03/07/ 14:26
 * @Description
 */
@Data
public class AttrRespVo extends AttrVo{
    /**
     * 分类名字
     */
    private String catelogName;

    /**
     * 分组名字
     */
    private String groupName;



    private Long[] catelogPath;
}
