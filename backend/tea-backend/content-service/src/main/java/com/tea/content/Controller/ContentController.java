package com.tea.content.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.common.result.Result;
import com.tea.content.entity.Article;
import com.tea.content.entity.ArticleCategory;
import com.tea.content.Mapper.ArticleCategoryMapper;
import com.tea.content.Mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import jakarta.validation.Valid;
import com.tea.content.dto.ArticlePublishDTO;
@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
public class ContentController {

    private final ArticleCategoryMapper categoryMapper;

    private final ArticleMapper articleMapper;

    /**
     * 分类列表
     */
    @Operation(summary = "文章分类列表")
    @Tag(name = "茶文化内容接口")
    @GetMapping("/category/list")
    public Result<List<ArticleCategory>> categoryList() {

        List<ArticleCategory> list =
                categoryMapper.selectList(
                        new LambdaQueryWrapper<ArticleCategory>()
                                .orderByAsc(ArticleCategory::getSort)
                );

        return Result.success(list);
    }

    /**
     * 文章列表
     * 改动1：新增分页配置类Page
     */
    @GetMapping("/article/list")
    public Result<Page<Article>> articleList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {

        Page<Article> page = new Page<>(current, size);

        Page<Article> resultPage =
                articleMapper.selectPage(
                        page,
                        new LambdaQueryWrapper<Article>()
                                .orderByDesc(Article::getCreateTime)
                );

        return Result.success(resultPage);
    }

    /**
     * 文章详情
     */
    @GetMapping("/article/detail/{id}")
    public Result<Article> detail(@PathVariable Long id) {

        Article article = articleMapper.selectById(id);

        return Result.success(article);
    }

    /**
     * 发布文章
     * 修改publish方法:新增DTO
     */
    @PostMapping("/article/publish")
    public Result<String> publish(
            // @Valid：开启DTO参数校验，自动验证@NotBlank等注解
            // @RequestBody：接收JSON格式请求体
            @Valid @RequestBody ArticlePublishDTO dto
    ) {
        // 1. 创建数据库实体对象
        Article article = new Article();
        // 2. 将前端传入的DTO数据拷贝到实体对象中
        BeanUtils.copyProperties(dto, article);
        // 3. 设置业务默认值（前端不传入，后端自动生成）
        article.setViewCount(0);   // 初始阅读数为0
        article.setStatus(1);      // 文章状态：1=已发布
        // 4. 调用Mapper插入数据库
        articleMapper.insert(article);
        // 5. 返回统一响应结果
        return Result.success("发布成功");
    }
}