package com.tea.exhibition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 展览/茶博会模块启动类
 * @SpringBootApplication：Spring Boot项目的核心启动注解
 * scanBasePackages = "com.tea"：指定组件扫描的根包路径
 */
@SpringBootApplication(scanBasePackages = "com.tea")
public class ExhibitionApplication {

    /**
     * 主方法，Spring Boot项目的入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 启动Spring Boot应用，加载所有配置和组件
        SpringApplication.run(ExhibitionApplication.class, args);
    }
}