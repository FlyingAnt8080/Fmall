package com.suse.fmall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.suse.common.exception.BizCodeEnume;
import com.suse.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author LiuJing
 * @Date: 2021/04/25/ 15:20
 * @Description
 */
@Configuration
public class SentinelGatewayConfig {
    public SentinelGatewayConfig(){
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                //网关限流的请求就会调用此回调
                R error = R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg());
                String errorJson = JSON.toJSONString(error);
                Mono<ServerResponse> mono = ServerResponse.ok().body(Mono.just(errorJson), String.class);
                return mono;
            }
        });
    }
}
