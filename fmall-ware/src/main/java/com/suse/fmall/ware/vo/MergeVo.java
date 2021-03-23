package com.suse.fmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/14/ 10:16
 * @Description 合并采购单vo
 */
@Data
public class MergeVo {
    //整单id
   private Long purchaseId;
   //合并项集合
   private List<Long> items;
}
