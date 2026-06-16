package com.tea.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    /**
     * JWT 签名密钥 —— 优先从系统属性/环境变量读取，支持不同环境使用不同密钥
     * 读取顺序：系统属性 jwt.secret → 环境变量 JWT_SECRET → 默认值
     * 生产环境务必通过环境变量设置强密钥，避免使用默认值
     */
    private static final String SECRET_KEY = getSecretKey();

    /** Token 过期时间：24 小时，可通过系统属性 jwt.expiration 自定义 */
    private static final long EXPIRE_TIME = getExpireTime();

    /** 从系统属性或环境变量获取密钥 */
    private static String getSecretKey() {
        String key = System.getProperty("jwt.secret");
        if (key != null && !key.isBlank()) return key;
        key = System.getenv("JWT_SECRET");
        if (key != null && !key.isBlank()) return key;
        // 默认密钥（仅用于开发环境，生产环境务必覆盖）
        return "tea_platform_secret_key_2026_login_jwt_safe_key";
    }

    /** 从系统属性获取过期时间（毫秒），默认 24 小时 */
    private static long getExpireTime() {
        String val = System.getProperty("jwt.expiration");
        if (val != null && !val.isBlank()) {
            try { return Long.parseLong(val); } catch (NumberFormatException ignored) {}
        }
        return 1000L * 60 * 60 * 24; // 24 小时
    }

    public static String createToken(Long userId, String username) {
        return createToken(userId, username, 0);
    }

    /** 签发JWT，携带用户角色信息 */
    public static String createToken(Long userId, String username, Integer role) {

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", role != null ? role : 0)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8)
                ))
                .compact();
    }

    public static Claims parseToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8)
                ))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}