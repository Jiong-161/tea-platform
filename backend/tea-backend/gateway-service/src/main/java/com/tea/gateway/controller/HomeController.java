package com.tea.gateway.controller;

import com.tea.common.result.Result;
import com.tea.gateway.ov.HomeVO;
import com.tea.gateway.service.HomeAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * 首页数据聚合接口
 * <p>
 * GET /home/index
 * 并行调用 content-service / exhibition-service / product-service，
 * 聚合返回茶文化文章、展览活动、精选商品三类核心数据。
 * <p>
 * 错误处理策略：
 * - 单个下游服务失败 → 降级为空列表，不影响其他数据的返回
 * - 所有服务均失败 → 返回空 HomeVO（code=200），由前端展示空状态
 * - 详细日志记录每个下游调用的成功/失败状态，便于排查
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeAggregationService homeAggregationService;

    @GetMapping("/home/index")
    public Mono<Result<HomeVO>> index() {
        long startTime = System.currentTimeMillis();

        return Mono.zip(
                        homeAggregationService.fetchArticles(),
                        homeAggregationService.fetchExhibitions(),
                        homeAggregationService.fetchProducts()
                )
                .map(tuple -> {
                    List<Object> articles = tuple.getT1();
                    List<Object> exhibitions = tuple.getT2();
                    List<Object> products = tuple.getT3();

                    // ====== 聚合结果日志（关键排查入口）======
                    long costMs = System.currentTimeMillis() - startTime;
                    log.info("[首页聚合] 全部完成! 耗时={}ms | 文章={}条 | 展览={}条 | 商品={}条",
                            costMs, articles.size(), exhibitions.size(), products.size());

                    if (articles.isEmpty() && exhibitions.isEmpty() && products.isEmpty()) {
                        log.warn("[首页聚合] ⚠ 三类数据均为空! 请检查：1) 下游微服务是否启动 2) 数据库是否有数据 3) 服务注册中心是否正常");
                    }

                    HomeVO vo = new HomeVO();
                    vo.setArticles(articles);
                    vo.setExhibitions(exhibitions);
                    vo.setProducts(products);
                    return Result.success(vo);
                });
    }
}
