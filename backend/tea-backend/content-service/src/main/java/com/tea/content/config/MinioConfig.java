package com.tea.content.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// MinIO 配置类
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig { // MinIO 配置类

    private String endpoint; // MinIO 服务器地址

    private String accessKey; // MinIO 访问密钥

    private String secretKey; // MinIO 密钥

    @Bean
    public MinioClient minioClient() { // 创建 MinIO 客户端实例

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}