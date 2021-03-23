package com.suse.common.constant;

/**
 * @Author LiuJing
 * @Date: 2021/03/07/ 16:54
 * @Description
 */
public class ProductConstant {
    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),ATTR_TYPE_SALE(0,"销售属性");
        private int code;
        private String msg;
        AttrEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum SpuStatusEnum{
        SPU_NEW(0,"新建"),SPU_UP(1,"商品上架"),SPU_DOWN(2,"商品下架");
        private int code;
        private String msg;
        SpuStatusEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
