package com.tea.content.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
// 文章分类实体类
@Data
@TableName("tea_article_category")
public class ArticleCategory {

    private Long id;

    private String name;

    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}