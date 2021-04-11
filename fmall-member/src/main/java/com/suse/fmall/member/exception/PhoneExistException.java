package com.suse.fmall.member.exception;

/**
 * @Author LiuJing
 * @Date: 2021/04/09/ 23:24
 * @Description
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException(){
        super("手机号已经存在!");
    }
}
