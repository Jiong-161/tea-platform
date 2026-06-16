package com.tea.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 商品服务启动类
 * scanBasePackages = "com.tea" 扫描全项目的Bean（包括common模块）
 */
@SpringBootApplication(scanBasePackages = "com.tea")
@ConfigurationPropertiesScan
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}