package com.tea.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Sentinel Gateway 集成配置
 *
 * 优化点：
 * 1. SentinelGatewayFilter 优先级设为 Ordered.HIGHEST_PRECEDENCE + 1，
 *    确保在 AuthGlobalFilter（认证）之后执行（先认证后限流）
 * 2. 通过构造函数注册自定义 BlockRequestHandler，避免 @PostInit 等延迟初始化
 * 3. 支持通过 Dashboard 动态推送规则变更
 */
@Configuration
public class SentinelGatewayConfig {

    private static final Logger log = LoggerFactory.getLogger(SentinelGatewayConfig.class);

    /**
     * 注册 Sentinel 网关过滤器到 Spring Cloud Gateway 过滤器链
     *
     * 优先级说明：
     * - AuthGlobalFilter: HIGHEST_PRECENTENCE (认证鉴权)
     * - SentinelGatewayFilter: HIGHEST_PRECEDENCE + 1 (流量控制/熔断降级)
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public SentinelGatewayFilter sentinelGatewayFilter() {
        log.info("[Sentinel] Gateway Filter 已注册");
        return new SentinelGatewayFilter();
    }

    /**
     * 注册全局 BlockRequestHandler
     * 当请求被 Sentinel 拦截时返回 JSON 格式响应而非默认的 HTML 页面
     */
    public SentinelGatewayConfig() {
        GatewayCallbackManager.setBlockHandler((exchange, ex) ->
                GatewayBlockHandler.handleBlockRequest(exchange, ex));
        log.info("[Sentinel] 自定义 BlockRequestHandler 已注册");
    }
}
