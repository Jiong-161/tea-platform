package com.tea.gateway.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tea.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 首页数据聚合服务
 * <p>
 * 并行调用三个下游微服务，聚合文章/展览/商品数据。
 *
 * 优化后的容错架构（与Sentinel协同）：
 * 第一道防线：Sentinel Gateway Filter（路由级QPS限流）
 * 第二道防线：@SentinelResource（方法级资源保护 + 熔断降级）
 * 第三道防线：WebClient retryWhen（网络瞬时可恢复重试）
 * 第四道防线：onErrorResume（最终兜底降级）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeAggregationService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    /** 重试次数（优化后从2次减少为1次，避免过度重试加剧下游压力） */
    private static final int MAX_RETRY = 1;
    /** 重试基础间隔（秒） */
    private static final Duration RETRY_BASE_DURATION = Duration.ofSeconds(2);

    /** 聚合请求计数器（用于监控指标采集） */
    private static final AtomicLong totalRequests = new AtomicLong(0);
    private static final AtomicLong successCount = new AtomicLong(0);
    private static final AtomicLong degradeCount = new AtomicLong(0);

    // ==================== 公开方法（Sentinel资源定义）====================

    /**
     * 获取茶文化文章列表（首页展示用）
     * Sentinel资源: fetchArticles — 可在Dashboard配置独立流控/熔断规则
     */
    @SentinelResource(value = "fetchArticles",
            blockHandler = "fetchArticlesBlockHandler",
            fallback = "fetchArticlesFallback")
    public Mono<List<Object>> fetchArticles() {
        return callDownstream(
                "content-service",
                "http://content-service/content/article/list?current=1&size=6",
                this::extractArticleRecords
        );
    }

    /**
     * 获取展览活动列表（首页展示用）
     * Sentinel资源: fetchExhibitions — 可在Dashboard配置独立流控/熔断规则
     */
    @SentinelResource(value = "fetchExhibitions",
            blockHandler = "fetchExhibitionsBlockHandler",
            fallback = "fetchExhibitionsFallback")
    public Mono<List<Object>> fetchExhibitions() {
        return callDownstream(
                "exhibition-service",
                "http://exhibition-service/exhibition/list",
                this::extractListData
        );
    }

    /**
     * 获取精选商品列表（首页展示用）
     * Sentinel资源: fetchProducts — 可在Dashboard配置独立流控/熔断规则
     */
    @SentinelResource(value = "fetchProducts",
            blockHandler = "fetchProductsBlockHandler",
            fallback = "fetchProductsFallback")
    public Mono<List<Object>> fetchProducts() {
        return callDownstream(
                "product-service",
                "http://product-service/product/list",
                this::extractListData
        );
    }

    // ==================== 通用调用方法 ====================

    /**
     * 通用的下游服务调用方法
     * 包含：重试、超时、详细日志、错误处理、数据提取
     *
     * 优化点：
     * - 重试次数从2次减至1次（避免雪崩效应下加重下游负载）
     * - 基础退避间隔从1s增至2s（给下游更多恢复时间）
     * - 新增聚合指标统计（total/success/degrade）
     */
    private Mono<List<Object>> callDownstream(
            String serviceName,
            String uri,
            DataExtractor extractor) {

        long reqId = totalRequests.incrementAndGet();
        log.info("[首页聚合 #{}] 开始调用 {} → {}", reqId, serviceName, uri);

        return webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Result<Object>>() {})
                // ====== 优化后重试策略：最多1次重试，基础间隔2s ======
                .retryWhen(Retry.backoff(MAX_RETRY, RETRY_BASE_DURATION)
                        .maxBackoff(Duration.ofSeconds(5))
                        .filter(throwable -> isRetryable(throwable))
                        .doBeforeRetry(signal ->
                                log.warn("[首页聚合 #{}] {} 第{}次重试, 原因: {}",
                                        reqId,
                                        serviceName,
                                        signal.totalRetries() + 1,
                                        signal.failure().getMessage())))
                // ====== 数据提取 + 日志 ======
                .map(result -> {
                    log.info("[首页聚合 #{}] {} 响应状态: code={}, data类型={}",
                            reqId, serviceName,
                            result.getCode(),
                            result.getData() != null ? result.getData().getClass().getSimpleName() : "null");

                    List<Object> extracted = extractor.extract(result);
                    successCount.incrementAndGet();
                    log.info("[首页聚合 #{}] {} 提取到 {} 条数据", reqId, serviceName, extracted.size());
                    return extracted;
                })
                // ====== 错误处理：单点故障降级 ======
                .onErrorResume(e -> {
                    degradeCount.incrementAndGet();
                    return handleError(reqId, serviceName, uri, e);
                });
    }

    // ==================== Sentinel 降级/限流处理方法 ====================

    /**
     * fetchArticles 被Sentinel限流时的处理
     */
    public Mono<List<Object>> fetchArticlesBlockHandler(BlockException e) {
        log.warn("[Sentinel] fetchArticles 被拦截: type={}", e.getClass().getSimpleName());
        return Mono.just(Collections.emptyList());
    }

    /**
     * fetchArticles 业务异常降级
     */
    public Mono<List<Object>> fetchArticlesFallback(Throwable t) {
        log.error("[Sentinel降级] fetchArticles 业务异常: {}", t.getMessage());
        return Mono.just(Collections.emptyList());
    }

    /**
     * fetchExhibitions 被Sentinel限流时的处理
     */
    public Mono<List<Object>> fetchExhibitionsBlockHandler(BlockException e) {
        log.warn("[Sentinel] fetchExhibitions 被拦截: type={}", e.getClass().getSimpleName());
        return Mono.just(Collections.emptyList());
    }

    /**
     * fetchExhibitions 业务异常降级
     */
    public Mono<List<Object>> fetchExhibitionsFallback(Throwable t) {
        log.error("[Sentinel降级] fetchExhibitions 业务异常: {}", t.getMessage());
        return Mono.just(Collections.emptyList());
    }

    /**
     * fetchProducts 被Sentinel限流时的处理
     */
    public Mono<List<Object>> fetchProductsBlockHandler(BlockException e) {
        log.warn("[Sentinel] fetchProducts 被拦截: type={}", e.getClass().getSimpleName());
        return Mono.just(Collections.emptyList());
    }

    /**
     * fetchProducts 业务异常降级
     */
    public Mono<List<Object>> fetchProductsFallback(Throwable t) {
        log.error("[Sentinel降级] fetchProducts 业务异常: {}", t.getMessage());
        return Mono.just(Collections.emptyList());
    }

    // ==================== 监控指标查询接口 ====================

    /**
     * 获取聚合服务运行指标（可用于健康检查或监控面板展示）
     */
    public Map<String, Long> getMetrics() {
        return Map.of(
                "totalRequests", totalRequests.get(),
                "successCount", successCount.get(),
                "degradeCount", degradeCount.get()
        );
    }

    // ==================== 数据提取器 ====================

    /**
     * 从文章分页接口的响应中提取 records 列表
     * content-service 返回 Result<Page<Article>>，序列化后：
     * { code:200, data:{ records:[...], total:N, ... } }
     */
    private List<Object> extractArticleRecords(Result<Object> result) {
        if (result.getCode() == null || result.getCode() != 200) {
            log.warn("[文章提取] 非成功响应: code={}, message={}", result.getCode(), result.getMessage());
            return Collections.emptyList();
        }

        Object data = result.getData();
        if (data == null) {
            log.warn("[文章提取] data 为 null");
            return Collections.emptyList();
        }

        // 方式1：标准 Map 结构（Jackson 反序列化 Page 对象的结果）
        if (data instanceof Map<?, ?> map) {
            Object records = map.get("records");
            if (records instanceof List<?> list) {
                return castList(list);
            }
            // records 不存在但 map 非空 — 可能是直接返回了 list 在其他 key 下
            log.debug("[文章提取] Map 结构但无 records 字段, keys={}", map.keySet());
        }

        // 方式2：data 本身就是 List（某些情况下可能直接返回列表）
        if (data instanceof List<?> list) {
            return castList(list);
        }

        // 方式3：兜底 — 尝试用 Jackson 重新解析为 List
        try {
            String json = objectMapper.writeValueAsString(data);
            List<Map<String, Object>> parsed = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, Object>>>() {});
            log.info("[文章提取] Jackson 二次解析成功, 获取 {} 条记录", parsed.size());
            return new ArrayList<>(parsed);
        } catch (Exception e) {
            log.error("[文章提取] 无法从 data 中提取列表, data类型={}, data={}",
                    data.getClass().getName(), data);
        }

        return Collections.emptyList();
    }

    /**
     * 从展览/商品列表接口的响应中提取 data 列表
     * exhibition-service / product-service 返回 Result<List<T>>，序列化后：
     * { code:200, data:[...] }
     */
    private List<Object> extractListData(Result<Object> result) {
        if (result.getCode() == null || result.getCode() != 200) {
            log.warn("[列表提取] 非成功响应: code={}, message={}", result.getCode(), result.getMessage());
            return Collections.emptyList();
        }

        Object data = result.getData();
        if (data instanceof List<?> list) {
            return castList(list);
        }

        if (data == null) {
            log.warn("[列表提取] data 为 null");
            return Collections.emptyList();
        }

        // 兜底：如果 data 不是 List 而是 Map（如被包装了一层），尝试取值
        if (data instanceof Map<?, ?> map) {
            // 尝试常见字段名
            for (String key : new String[]{"list", "records", "items", "content", "data"}) {
                Object val = map.get(key);
                if (val instanceof List<?> list) {
                    log.info("[列表提取] 从 Map.{} 字段中提取到 {} 条记录", key, list.size());
                    return castList(list);
                }
            }
            log.warn("[列表提取] data 是 Map 但未找到列表字段, keys={}", map.keySet());
        }

        log.error("[列表提取] 无法提取列表, data类型={}, data={}",
                data.getClass().getName(), data);
        return Collections.emptyList();
    }

    // ==================== 工具方法 ====================

    /**
     * 统一错误处理：记录详细日志后降级返回空列表
     */
    private Mono<List<Object>> handleError(long reqId, String serviceName, String uri, Throwable e) {
        if (e instanceof WebClientResponseException responseEx) {
            log.error("[首页聚合 #{}] {} 调用失败! HTTP状态码={}, 响应体={}, URI={}",
                    reqId, serviceName,
                    responseEx.getStatusCode().value(),
                    responseEx.getResponseBodyAsString(),
                    uri,
                    e);
        } else {
            log.error("[首页聚合 #{}] {} 调用失败! 类型={}, 消息={}, URI={}",
                    reqId, serviceName,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    uri,
                    e);
        }
        return Mono.just(Collections.emptyList());
    }

    /**
     * 判断异常是否可重试（网络异常/超时/5xx 服务端错误可重试；4xx 客户端错误不重试）
     */
    private boolean isRetryable(Throwable throwable) {
        if (throwable instanceof WebClientResponseException ex) {
            // 4xx 客户端错误不重试（参数错误等），5xx 服务端错误可重试
            return ex.getStatusCode().is5xxServerError();
        }
        // 网络异常、超时、连接拒绝等都可重试
        return true;
    }

    @SuppressWarnings("unchecked")
    private List<Object> castList(List<?> list) {
        return (List<Object>) list;
    }

    // ==================== 函数式接口 ====================

    @FunctionalInterface
    interface DataExtractor {
        List<Object> extract(Result<Object> result);
    }
}
