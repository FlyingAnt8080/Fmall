package com.suse.fmall.member.interceptor;

import com.suse.common.constant.AuthServerConstant;
import com.suse.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Author LiuJing
 * @Date: 2021/04/14/ 22:05
 * @Description
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> loginUserThreadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //将服务之间的调用放行
        boolean match = new AntPathMatcher().match("/member/**", request.getRequestURI());
        if (match)return true;
        HttpSession session = request.getSession();
        MemberRespVo loginUser = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (loginUser != null){
            loginUserThreadLocal.set(loginUser);
            return true;
        }else {
            //没登录
            session.setAttribute("msg","请先进行登录");
            response.sendRedirect("http://auth.fmall.com/login.html");
            return false;
        }
    }
}
