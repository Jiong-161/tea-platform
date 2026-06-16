package com.tea.gateway;

import com.tea.gateway.service.HomeAggregationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = "spring.cloud.nacos.discovery.enabled=false")
class ContentServiceGatewayTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private HomeAggregationService homeAggregationService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("首页聚合 - 所有下游服务正常响应")
    void testHomeIndexSuccess() {
        when(homeAggregationService.fetchArticles())
                .thenReturn(Mono.just(List.of(Map.of("id", 1, "title", "文章1"))));
        when(homeAggregationService.fetchExhibitions())
                .thenReturn(Mono.just(List.of(Map.of("id", 1, "name", "展览1"))));
        when(homeAggregationService.fetchProducts())
                .thenReturn(Mono.just(List.of(Map.of("id", 1, "name", "商品1"))));

        webTestClient.get()
                .uri("/home/index")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data.articles.length()").isEqualTo(1)
                .jsonPath("$.data.articles[0].title").isEqualTo("文章1")
                .jsonPath("$.data.exhibitions.length()").isEqualTo(1)
                .jsonPath("$.data.products.length()").isEqualTo(1);
    }

    @Test
    @DisplayName("首页聚合 - 部分服务返回空数据时仍正常响应")
    void testHomeIndexWithPartialEmpty() {
        // 模拟：文章服务正常返回数据，展览和商品服务返回空列表
        when(homeAggregationService.fetchArticles())
                .thenReturn(Mono.just(List.of(Map.of("id", 1, "title", "文章1"))));
        when(homeAggregationService.fetchExhibitions())
                .thenReturn(Mono.just(List.of()));
        when(homeAggregationService.fetchProducts())
                .thenReturn(Mono.just(List.of()));

        webTestClient.get()
                .uri("/home/index")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data.articles.length()").isEqualTo(1)
                .jsonPath("$.data.exhibitions.length()").isEqualTo(0)
                .jsonPath("$.data.products.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("首页聚合 - 下游服务返回空数据")
    void testHomeIndexWithEmptyData() {
        when(homeAggregationService.fetchArticles())
                .thenReturn(Mono.just(Collections.emptyList()));
        when(homeAggregationService.fetchExhibitions())
                .thenReturn(Mono.just(Collections.emptyList()));
        when(homeAggregationService.fetchProducts())
                .thenReturn(Mono.just(Collections.emptyList()));

        webTestClient.get()
                .uri("/home/index")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data.articles.length()").isEqualTo(0)
                .jsonPath("$.data.exhibitions.length()").isEqualTo(0)
                .jsonPath("$.data.products.length()").isEqualTo(0);
    }
}