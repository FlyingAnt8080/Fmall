package com.suse.fmall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/14/ 11:50
 * @Description 完成采购vo
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;//采购单id

    private List<PurchaseItemDoneVo> items;//采购项

}
