package com.suse.fmall.order.exception;

import com.suse.common.exception.BizCodeEnume;
import com.suse.common.exception.NoStockException;
import com.suse.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @Author LiuJing
 * @Date: 2021/05/24/ 21:03
 * @Description
 */
@Slf4j
@ControllerAdvice
public class FmallExceptionControllerAdvice {

    @ExceptionHandler(value = NoStockException.class)
    public String handleNoStockException(NoStockException e,RedirectAttributes redirectAttributes){
        log.error(e.getMessage());
        redirectAttributes.addFlashAttribute("msg",e.getMessage());
        return "redirect:http://order.fmall.com/toTrade";
    }
}
