package com.suse.fmall.order.interceptor;

import com.suse.common.constant.AuthServerConstant;
import com.suse.common.vo.MemberRespVo;
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
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> loginUserThreadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //将服务之间的调用放行
        AntPathMatcher matcher = new AntPathMatcher();
        boolean match1 = matcher.match("/order/order/status/**", request.getRequestURI());
        boolean match2 = matcher.match("/payed/notify", request.getRequestURI());
        if (match1 || match2)return true;
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
