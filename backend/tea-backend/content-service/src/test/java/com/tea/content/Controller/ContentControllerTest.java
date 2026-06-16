package com.tea.content.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tea.content.Mapper.ArticleCategoryMapper;
import com.tea.content.Mapper.ArticleMapper;
import com.tea.content.entity.Article;
import com.tea.content.entity.ArticleCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.sentinel.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=true",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=never"
})
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleMapper articleMapper;

    @MockBean
    private ArticleCategoryMapper categoryMapper;

    @Test
    @DisplayName("文章分类列表 - 成功")
    void testCategoryList() throws Exception {
        ArticleCategory cat1 = new ArticleCategory();
        cat1.setId(1L);
        cat1.setName("茶文化");
        cat1.setSort(1);

        ArticleCategory cat2 = new ArticleCategory();
        cat2.setId(2L);
        cat2.setName("茶具");
        cat2.setSort(2);

        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(cat1, cat2));

        mockMvc.perform(get("/content/category/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("茶文化"))
                .andExpect(jsonPath("$.data[1].name").value("茶具"));
    }

    @Test
    @DisplayName("文章分类列表 - 空列表")
    void testCategoryListEmpty() throws Exception {
        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/content/category/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("文章列表 - 分页查询")
    void testArticleList() throws Exception {
        Article a1 = new Article();
        a1.setId(1L);
        a1.setTitle("文章标题1");
        a1.setSummary("摘要1");
        a1.setAuthor("作者1");

        Article a2 = new Article();
        a2.setId(2L);
        a2.setTitle("文章标题2");
        a2.setSummary("摘要2");
        a2.setAuthor("作者2");

        Page<Article> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(a1, a2));
        mockPage.setTotal(2);

        when(articleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/content/article/list?current=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records.length()").value(2))
                .andExpect(jsonPath("$.data.records[0].title").value("文章标题1"))
                .andExpect(jsonPath("$.data.total").value(2));
    }

    @Test
    @DisplayName("文章列表 - 空结果")
    void testArticleListEmpty() throws Exception {
        Page<Article> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of());
        mockPage.setTotal(0);

        when(articleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/content/article/list?current=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records.length()").value(0))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("文章详情 - 成功")
    void testArticleDetail() throws Exception {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("文章标题");
        article.setSummary("文章摘要");
        article.setContent("详细内容");
        article.setAuthor("作者");
        article.setViewCount(100);

        when(articleMapper.selectById(1L)).thenReturn(article);

        mockMvc.perform(get("/content/article/detail/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("文章标题"))
                .andExpect(jsonPath("$.data.content").value("详细内容"));
    }

    @Test
    @DisplayName("文章详情 - 不存在")
    void testArticleDetailNotFound() throws Exception {
        when(articleMapper.selectById(999L)).thenReturn(null);

        mockMvc.perform(get("/content/article/detail/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("发布文章 - 成功")
    void testArticlePublish() throws Exception {
        when(articleMapper.insert(any(Article.class))).thenReturn(1);

        mockMvc.perform(post("/content/article/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":1,\"title\":\"新文章\",\"summary\":\"摘要\",\"content\":\"内容\",\"author\":\"作者\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("发布成功"));
    }

    @Test
    @DisplayName("发布文章 - 参数校验失败")
    void testArticlePublishValidationFail() throws Exception {
        // 缺少必填字段 title, content, author
        mockMvc.perform(post("/content/article/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":1,\"summary\":\"只有摘要\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}