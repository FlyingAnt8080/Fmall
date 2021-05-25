package com.suse.fmall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.suse.common.constant.AuthServerConstant;
import com.suse.common.exception.BizCodeEnume;
import com.suse.common.utils.R;
import com.suse.common.vo.MemberRespVo;
import com.suse.fmall.auth.feign.MemberFeignService;
import com.suse.fmall.auth.feign.ThirdPartFeignService;
import com.suse.fmall.auth.vo.UserLoginVo;
import com.suse.fmall.auth.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author LiuJing
 * @Date: 2021/04/08/ 23:47
 * @Description
 */
@Controller
public class LoginController {
    @Autowired
    private ThirdPartFeignService thirdPartFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone){
        //TODO 1、接口防刷
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)){
            Long time = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - time < 60000){
                //60秒内不能再发
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(),BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        //2.验证码再次校验
        String code = UUID.randomUUID().toString().substring(0,5);
        String saveCode = code + "_" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, saveCode ,10, TimeUnit.MINUTES);
        thirdPartFeignService.sendCode(phone,code);
        return R.ok();
    }

    /**
     * RedirectAttributes 重定向携带数据,利用session原理。将数据放到session。
     * 只要跳到下一个页面取出数据后，session里面的数据就会删除掉
     * //TODO 分布式session问题
     * @param userVo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/register")
    public String register(@Validated UserRegisterVo userVo, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            Map<String,String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",errors);
            //校验出错转发到注册页
            return "redirect:http://auth.fmall.com/reg.html";
        }
        String code = userVo.getCode();
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userVo.getPhone());
        if (!StringUtils.isEmpty(redisCode)){
            if (code.equals(redisCode.split("_")[0])){
                //验证码对比成功
                //删除验证码
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userVo.getPhone());
                R r = memberFeignService.register(userVo);
                if (r.getCode() == 0){
                    //注册成功回登录页
                    return "redirect:http://auth.fmall.com/login.html";
                }else {
                    //注册失败
                    Map<String,String> errors = new HashMap<>();
                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.fmall.com/reg.html";
                }
            }else {
                Map<String,String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",errors);
                //校验出错转发到注册页
                return "redirect:http://auth.fmall.com/reg.html";
            }
        }else{
            Map<String,String> errors = new HashMap<>();
            errors.put("code","验证码过期");
            redirectAttributes.addFlashAttribute("errors",errors);
            //校验出错转发到注册页
            return "redirect:http://auth.fmall.com/reg.html";
        }
    }

    /**
     * 账号密码登录
     * @param loginVo
     * @param redirectAttributes
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String login(UserLoginVo loginVo, RedirectAttributes redirectAttributes, HttpSession session){
        R res = memberFeignService.login(loginVo);
        if (res.getCode() == 0){
            MemberRespVo data = res.getData("data", new TypeReference<MemberRespVo>() {});
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            //成功
            return "redirect:http://fmall.com";
        }else{
            //失败
            Map<String,String> errrors = new HashMap<>();
            errrors.put("msg",res.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errrors);
            return "redirect:http://auth.fmall.com/login.html";
        }
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:http://fmall.com";
    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object user = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(user == null){
            //没有登录
            return "login";
        }else{
            return "redirect:http://fmall.com";
        }
    }
}
