package com.tea.content.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import com.tea.content.dto.ArticlePublishDTO;
import com.tea.content.entity.Article;
import com.tea.content.entity.ArticleCategory;
import com.tea.content.Mapper.ArticleMapper;
import com.tea.content.Mapper.ArticleCategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理 - 茶文化内容管理接口
 * 路径 /admin/content/**，仅管理员可访问（网关校验 role=1）
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/content")
@Tag(name = "后台-内容管理")
public class AdminContentController {

    private final ArticleMapper articleMapper;
    private final ArticleCategoryMapper categoryMapper;

    /** 文章分页列表 */
    @Operation(summary = "文章分页列表")
    @GetMapping("/article/list")
    public Result<Page<Article>> articleList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        Page<Article> articlePage = new Page<>(page, size);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Article::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }
        wrapper.orderByDesc(Article::getCreateTime);

        return Result.success(articleMapper.selectPage(articlePage, wrapper));
    }

    /** 新增/编辑文章（id为空则新增，有值则编辑） */
    @Operation(summary = "新增/编辑文章")
    @PostMapping("/article/save")
    public Result<String> save(@Valid @RequestBody ArticlePublishDTO dto,
                               @RequestParam(required = false) Long id) {

        Article article;
        if (id != null) {
            article = articleMapper.selectById(id);
            if (article == null) {
                throw new BusinessException("文章不存在");
            }
        } else {
            article = new Article();
            article.setViewCount(0);
        }

        BeanUtils.copyProperties(dto, article);
        // 管理员发布的文章默认已审核通过
        article.setStatus(1);

        if (id != null) {
            articleMapper.updateById(article);
        } else {
            articleMapper.insert(article);
        }

        return Result.success(id != null ? "编辑成功" : "发布成功");
    }

    /** 删除文章 */
    @Operation(summary = "删除文章")
    @DeleteMapping("/article/{id}")
    public Result<String> deleteArticle(@PathVariable Long id) {
        if (articleMapper.selectById(id) == null) {
            throw new BusinessException("文章不存在");
        }
        articleMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /** 审核文章（设置状态：1=通过 2=驳回） */
    @Operation(summary = "审核文章")
    @PutMapping("/article/audit/{id}")
    public Result<String> audit(
            @PathVariable Long id,
            @RequestParam Integer status) {

        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        article.setStatus(status);
        articleMapper.updateById(article);
        return Result.success(status == 1 ? "审核通过" : "已驳回");
    }

    /** 分类列表（供管理员选择） */
    @Operation(summary = "分类列表")
    @GetMapping("/category/list")
    public Result<java.util.List<ArticleCategory>> categoryList() {
        return Result.success(categoryMapper.selectList(
                new LambdaQueryWrapper<ArticleCategory>()
                        .orderByAsc(ArticleCategory::getSort)));
    }
}