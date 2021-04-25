package com.suse.common.exception;

/**
 * @Author LiuJing
 * @Date: 2021/03/04/ 0:00
 * @Description
 * 错误码列表：
 * 10：通用
 *  001：参数格式效验
 *  002：短信验证码评率太高
 * 11：商品
 * 12：订单
 * 13：购物车
 * 14：物流
 * 15: 用户
 * 16: 库存
 */
public enum BizCodeEnume {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    TOO_MANY_REQUEST(10002,"请求流量过大"),
    SMS_CODE_EXCEPTION(10001,"验证码获取频率太高，稍后再试"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    USER_EXIST_EXCEPTION(15001,"用户存在"),
    PHONE_EXIST_EXCEPTION(15002,"手机号已存在"),
    LOGINACCT_PASSWORD_INVALID__EXCEPTION(15003,"账号或密码错误"),
    NO_STOCK_EXCEPTION(16000,"商品库存不足");

    private int code;
    private String msg;

    BizCodeEnume(int code,String msg){
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
