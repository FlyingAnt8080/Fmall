package com.suse.common.exception;

/**
 * @Author LiuJing
 * @Date: 2021/04/17/ 14:59
 * @Description
 */
public class NoStockException extends RuntimeException {
    private Long skuId;
    public NoStockException(String msg){
        super(msg);
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
