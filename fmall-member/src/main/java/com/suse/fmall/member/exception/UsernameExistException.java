package com.suse.fmall.member.exception;

/**
 * @Author LiuJing
 * @Date: 2021/04/09/ 23:23
 * @Description 用户名已经存在
 */
public class UsernameExistException extends  RuntimeException{
    public UsernameExistException(){
        super("用户名已经存在");
    }
}
