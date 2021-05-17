package com.suse.fmall.order.interceptor;

import com.suse.common.constant.AuthServerConstant;
import com.suse.common.vo.MemberRespVo;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
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
        String uri = request.getRequestURI();
        System.out.println("uri:"+uri);
        boolean match1 = matcher.match("/order/order/status/**", uri);
        boolean match2 = matcher.match("/payed/notify", uri);
        boolean match3 = matcher.match("/order/*/delete",uri);
        boolean match4 = matcher.match("/order/order/update",uri);
        boolean match5 = matcher.match("/order/order/delivery",uri);
        if (match1 || match2 ||match3 || match4 || match5) return true;
        //TODO 前台系统和后台系统登录不一样问题
        if (!StringUtils.isEmpty(request.getParameter("t"))){
            //后台登录了
            return true;
        }
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
