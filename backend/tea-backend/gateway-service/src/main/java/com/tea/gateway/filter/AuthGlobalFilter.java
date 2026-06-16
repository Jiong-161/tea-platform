package com.tea.gateway.filter;

import com.tea.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        boolean isAdmin = path.startsWith("/admin/");

        // /admin/** 接口强制要求管理员权限
        if (isAdmin) {
            return handleAdminPath(exchange, chain);
        }

        boolean isPublic = isPublicPath(path, method);

        String token = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");
        String jwt = extractJwt(token);

        // 尝试解析Token（公开路径可选携带，受保护路径强制要求）
        if (jwt != null) {
            try {
                Claims claims = JwtUtil.parseToken(jwt);
                // 验证Redis中的登录状态（Redis不可用时信任JWT本身）
                boolean redisOk = true;
                try {
                    String redisToken = redisTemplate.opsForValue().get("login:" + jwt);
                    if (redisToken == null) {
                        redisOk = false;
                    }
                } catch (Exception redisEx) {
                    // Redis不可用，信任JWT本身
                    System.err.println("[WARN] Redis不可用，信任JWT: " + redisEx.getMessage());
                }

                if (redisOk) {
                    return chain.filter(injectUserHeaders(exchange, claims, jwt));
                } else if (!isPublic) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);

            } catch (Exception e) {
                // JWT解析失败
                if (!isPublic) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            }
        }

        if (!isPublic) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    /** 处理 /admin/** 路径的管理员权限校验 */
    private Mono<Void> handleAdminPath(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String jwt = extractJwt(token);

        if (jwt == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Claims claims = JwtUtil.parseToken(jwt);

            // 校验Redis中的登录状态（Redis不可用时信任JWT）
            try {
                String redisToken = redisTemplate.opsForValue().get("login:" + jwt);
                if (redisToken == null) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            } catch (Exception redisEx) {
                System.err.println("[WARN] Redis不可用，信任JWT进行管理员校验");
            }

            // 校验是否为管理员角色
            Integer role = claims.get("role", Integer.class);
            if (role == null || role != 1) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(injectUserHeaders(exchange, claims, jwt));

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /** 将用户信息注入请求头，传递给下游微服务 */
    private ServerWebExchange injectUserHeaders(ServerWebExchange exchange, Claims claims, String jwt) {
        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        Integer role = claims.get("role", Integer.class);

        return exchange.mutate()
                .request(r -> {
                    r.header("X-User-Id", String.valueOf(userId));
                    r.header("X-Username", username);
                    r.header("X-Auth-Token", jwt);
                    r.header("X-User-Role", String.valueOf(role != null ? role : 0));
                })
                .build();
    }

    /**
     * 提取并清洗JWT（去掉Bearer前缀）
     */
    private String extractJwt(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }

    /**
     * 判断是否为公开路径（无需登录即可访问）
     * 注意：/product/cart/** 和 /product/order/** 不属于公开路径，需要登录
     */
    private boolean isPublicPath(String path, String method) {
        boolean isArticleRead = "GET".equalsIgnoreCase(method)
                && path.startsWith("/content/article");

        return "/user/login".equals(path)
                || "/user/register".equals(path)
                || "/user/test".equals(path)
                || path.startsWith("/content/category")
                || isArticleRead
                || path.startsWith("/content/upload")
                || path.startsWith("/exhibition/")
                || (path.startsWith("/product/")
                    && !path.startsWith("/product/cart")
                    && !path.startsWith("/product/order"))
                || path.startsWith("/home/");
    }

    @Override
    public int getOrder() {
        return -1;
    }
}