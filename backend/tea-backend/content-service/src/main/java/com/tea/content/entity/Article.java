package com.tea.content.entity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
// 文章实体类
@Data
@TableName("tea_article")
public class Article {

    private Long id;

    private Long categoryId;

    private String title;

    private String summary;

    private String cover;

    private String content;

    private String author;

    private Integer viewCount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}