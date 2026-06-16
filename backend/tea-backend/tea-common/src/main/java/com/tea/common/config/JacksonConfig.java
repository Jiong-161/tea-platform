package com.tea.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Jackson 全局序列化配置
 *
 * 核心功能：
 *   1. Long → String（根治 JavaScript 大数精度丢失）
 *   2. LocalDateTime 灵活反序列化（支持 ISO-8601 和空格分隔等多种格式）
 *   3. 统一日期时间输出格式
 */
@Configuration
public class JacksonConfig {

    /**
     * 自定义 Jackson ObjectMapper：
     *   1. Long → String（解决 JS 大数精度丢失）
     *   2. 统一日期时间序列化格式
     *   3. 多格式日期时间反序列化（兼容前端不同传参格式）
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // ====================================================================
            // 一、Long 序列化为 String — 根治 JS 大数精度丢失
            // ====================================================================
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);

            // ====================================================================
            // 二、日期时间：输出格式统一，输入格式灵活兼容
            // ====================================================================
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            // 序列化（输出给前端）：统一使用易读的空格分隔格式
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(dateFormatter));
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(timeFormatter));
            builder.serializerByType(LocalDateTime.class,
                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // 反序列化（从请求中解析）：日期和时间使用标准格式
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(dateFormatter));
            builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

            // ★ 核心修复：LocalDateTime 反序列化使用多格式兼容解析器
            builder.deserializerByType(LocalDateTime.class, new MultiFormatLocalDateTimeDeserializer());
        };
    }

    /**
     * LocalDateTime 多格式兼容反序列化器
     *
     * 问题背景：
     *   前端传参格式多样（ISO-8601 的 T 分隔、空格分隔、带毫秒等），
     *   单一 DateTimeFormatter 无法覆盖所有情况，导致 DateTimeParseException。
     *
     * 解决方式：
     *   按优先级依次尝试多种格式，任一成功即返回，全部失败才抛出异常。
     *
     * 支持的格式（按优先级）：
     *   1. yyyy-MM-dd'T'HH:mm:ss       — 前端标准 ISO-8601 格式（如 2026-06-01T00:00:00）
     *   2. yyyy-MM-dd HH:mm:ss         — 后端常见格式（如 2026-06-01 00:00:00）
     *   3. yyyy-MM-dd'T'HH:mm:ss.SSS   — 带毫秒的 ISO 格式
     *   4. yyyy-MM-dd HH:mm:ss.SSS     — 带毫秒的空格格式
     *   5. ISO_LOCAL_DATE_TIME          — Jackson 默认 ISO 解析器（兜底）
     */
    public static class MultiFormatLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        // 按优先级排列的格式列表
        private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        };

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getText();
            if (text == null || text.isEmpty()) {
                return null;
            }

            // 去除可能的尾部 Z（UTC 标记），按本地时间解析
            String cleaned = text.trim();
            if (cleaned.endsWith("Z")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
            // 去除可能的时区偏移（如 +08:00）
            if (cleaned.contains("+") && (cleaned.indexOf('+') > 10)) {
                cleaned = cleaned.substring(0, cleaned.indexOf('+'));
            }

            // 依次尝试每种格式
            for (DateTimeFormatter fmt : FORMATTERS) {
                try {
                    return LocalDateTime.parse(cleaned, fmt);
                } catch (DateTimeParseException ignored) {
                    // 当前格式不匹配，继续尝试下一个
                }
            }

            // 所有格式均失败
            throw new IOException(
                "无法解析日期时间字符串 '" + text + "'，" +
                "支持的格式: yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd HH:mm:ss 等"
            );
        }
    }
}