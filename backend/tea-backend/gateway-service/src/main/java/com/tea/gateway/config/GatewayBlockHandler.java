package com.tea.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Sentinel 网关层自定义降级处理
 * 当请求被限流或熔断时，返回统一的 JSON 响应而非默认的 429 页面
 *
 * 优化点：
 * 1. 使用 static ObjectMapper 避免重复实例化，降低 GC 压力
 * 2. 统一 UTF-8 编码输出，解决中文乱码问题
 * 3. 区分限流(429)和熔断(503)两种场景，便于前端差异化处理
 */
public class GatewayBlockHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayBlockHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sentinel BlockRequestHandler 入口 — 匹配 Sentinel 1.8.9 的
     * {@code BlockRequestHandler.handleRequest(ServerWebExchange, Throwable)} 签名
     */
    public static Mono<ServerResponse> handleBlockRequest(ServerWebExchange exchange, Throwable ex) {
        // 默认按限流处理；可通过 ex 类型进一步区分限流/熔断
        return writeBlockedResponse(exchange, HttpStatus.TOO_MANY_REQUESTS,
                "请求过于频繁，请稍后再试");
    }

    /**
     * 被限流时的处理（QPS超阈值）
     */
    public static Mono<Void> blockHandler(ServerWebExchange exchange) {
        return writeBlockedResponseVoid(exchange, HttpStatus.TOO_MANY_REQUESTS,
                "请求过于频繁，请稍后再试");
    }

    /**
     * 被熔断时的处理（下游服务故障）
     */
    public static Mono<Void> fallbackHandler(ServerWebExchange exchange) {
        return writeBlockedResponseVoid(exchange, HttpStatus.SERVICE_UNAVAILABLE,
                "服务暂时不可用，请稍后再试");
    }

    /**
     * 写入被拦截响应 — 返回 {@code Mono<ServerResponse>}，适配
     * Sentinel 1.8.9 {@code BlockRequestHandler} 接口
     */
    private static Mono<ServerResponse> writeBlockedResponse(ServerWebExchange exchange,
                                                             HttpStatus status,
                                                             String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        // Content-Type 中指定 charset=UTF-8，防止中文乱码（不要设置 Content-Encoding，那是压缩算法头）
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("code", status.value());
        body.put("message", message);
        body.put("data", null);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            log.warn("[Sentinel网关拦截] status={}, path={}", status.value(),
                    exchange.getRequest().getPath().value());
            return response.writeWith(Mono.just(buffer)).then(Mono.empty());
        } catch (JsonProcessingException e) {
            log.error("[Sentinel降级] JSON序列化异常", e);
            byte[] fallbackBytes = "{\"code\":500,\"message\":\"内部错误\"}".getBytes(
                    StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(fallbackBytes)))
                    .then(Mono.empty());
        }
    }

    /**
     * 写入被拦截响应 — 返回 {@code Mono<Void>}，用于其他需要 Void 返回的场景
     */
    private static Mono<Void> writeBlockedResponseVoid(ServerWebExchange exchange,
                                                       HttpStatus status,
                                                       String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("code", status.value());
        body.put("message", message);
        body.put("data", null);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            log.warn("[Sentinel网关拦截] status={}, path={}", status.value(),
                    exchange.getRequest().getPath().value());
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("[Sentinel降级] JSON序列化异常", e);
            byte[] fallbackBytes = "{\"code\":500,\"message\":\"内部错误\"}".getBytes(
                    StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(fallbackBytes)));
        }
    }
}