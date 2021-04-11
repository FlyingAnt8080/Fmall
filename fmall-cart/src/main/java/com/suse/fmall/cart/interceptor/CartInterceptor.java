package com.suse.fmall.cart.interceptor;

import com.suse.common.constant.AuthServerConstant;
import com.suse.common.constant.CartConstant;
import com.suse.common.vo.MemberRespVo;
import com.suse.fmall.cart.to.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @Author LiuJing
 * @Date: 2021/04/11/ 10:38
 * @Description
 * 在执行目标方法之前判断用户登录状态。并封装传递给controller目标请求
 */

public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
    /**
     * 目标方法执行之前执行
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        MemberRespVo loginUser = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        UserInfoTo userInfo = new UserInfoTo();
        if (loginUser != null){
            //用户登录
            userInfo.setUserId(loginUser.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0){
            for (Cookie cookie:cookies){
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfo.setUserKey(cookie.getValue());
                    userInfo.setTempUser(true);
                    break;
                }
            }
        }
        //如果没有临时用户，分配零时用户
        if (StringUtils.isEmpty(userInfo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfo.setUserKey(uuid);
        }
        //目标方法执行之前
        threadLocal.set(userInfo);
        return true;
    }

    /**
     * 目标方法执行之后执行
     * 分配临时用户，让浏览器保存
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        UserInfoTo userInfoTo = threadLocal.get();
        //如果没有临时用户一定保存一个临时用户
        if (!userInfoTo.isTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, threadLocal.get().getUserKey());
            cookie.setDomain("fmall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
