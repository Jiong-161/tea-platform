package com.tea.content.dto;
// 文章发布DTO
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArticlePublishDTO {

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "摘要不能为空")
    private String summary;

    private String cover;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "作者不能为空")
    private String author;
}