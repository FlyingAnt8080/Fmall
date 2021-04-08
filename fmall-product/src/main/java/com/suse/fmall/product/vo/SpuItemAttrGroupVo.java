package com.suse.fmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/07/ 15:59
 * @Description
 */
//属性组
@Data
@ToString
public class SpuItemAttrGroupVo{
    private String groupName;
    private List<Attr> attrs;
}