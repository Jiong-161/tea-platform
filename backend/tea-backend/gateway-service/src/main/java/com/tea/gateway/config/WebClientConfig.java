package com.tea.gateway.config;


import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient 全局配置
 * - 负载均衡（@LoadBalanced，支持通过服务名调用微服务）
 * - 连接超时 5 秒，读超时 15 秒，写超时 10 秒
 * - 响应内存缓冲区 10MB（应对大列表响应）
 */
@Configuration
public class WebClientConfig {

    /** 连接超时（秒） */
    private static final int CONNECT_TIMEOUT = 5;
    /** 读超时（秒）— 下游聚合接口最长等待时间 */
    private static final int READ_TIMEOUT = 15;
    /** 写超时（秒） */
    private static final int WRITE_TIMEOUT = 10;

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {

        // Reactor Netty HttpClient：配置 TCP 层超时
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT * 1000)
                .responseTimeout(Duration.ofSeconds(READ_TIMEOUT))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT, TimeUnit.SECONDS)));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs()
                                .maxInMemorySize(10 * 1024 * 1024)
                )
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies);
    }
}
