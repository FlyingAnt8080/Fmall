package com.suse.fmall.seckill.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.suse.common.exception.BizCodeEnume;
import com.suse.common.utils.R;
import org.springframework.context.annotation.Configuration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author LiuJing
 * @Date: 2021/04/24/ 18:07
 * @Description
 */
@Configuration
public class FmallSentinelConfig{

    public FmallSentinelConfig(){
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
            @Override
            public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException e) throws IOException {
                R error = R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg());
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().write(JSON.toJSONString(error));
            }
        });
    }
}
