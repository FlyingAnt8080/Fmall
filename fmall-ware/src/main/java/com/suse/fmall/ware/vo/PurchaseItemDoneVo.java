package com.suse.fmall.ware.vo;

import lombok.Data;

/**
 * @Author LiuJing
 * @Date: 2021/03/14/ 11:52
 * @Description
 */
@Data
public class PurchaseItemDoneVo {
    //采购项id
    private Long itemId;
    //采购完成状态
    private Integer status;
    //采购失败原因
    private String reason;
}
