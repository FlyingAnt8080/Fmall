package com.suse.common.to.mq;

import com.suse.common.to.mq.StockDetailTo;
import lombok.Data;
/**
 * @Author LiuJing
 * @Date: 2021/04/18/ 9:25
 * @Description 库存锁定成功信息
 */
@Data
public class StockLockedTo {
    private Long id;//库存工作单id
    private StockDetailTo detail;//工作单详情
}
