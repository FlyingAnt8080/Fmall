package com.suse.fmall.product.exception;

import com.suse.common.exception.BizCodeEnume;
import com.suse.common.exception.NoStockException;
import com.suse.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiuJing
 * @Date: 2021/03/03/ 23:44
 * @Description
 * 统一异常处理
 * 返回json数据
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.suse.fmall.product.controller")
public class FmallExceptionControllerAdvice {

    /**
     * 处理MethodArgumentNotValidException类型的异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据效验出现问题：{},异常类型：{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((item)->{
            String message = item.getDefaultMessage();
            String field = item.getField();
            errorMap.put(field,message);
        });
        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(),BizCodeEnume.VALID_EXCEPTION.getMsg()).put("data",errorMap);
    }

    @ExceptionHandler(value = SpuUpException.class)
    public R handleSpuUpException(SpuUpException e){
        log.error(e.getMessage());
        return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误：",throwable);
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
