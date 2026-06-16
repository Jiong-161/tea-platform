package com.tea.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发布/编辑商品的请求参数DTO
 * 配合@Valid使用，实现参数校验
 */
@Data
public class ProductPublishDTO {
    @NotBlank(message = "商品名称不能为空")
    private String title;           // 商品名称

    private String cover;            // 商品封面图（非必填）

    @NotBlank(message = "商品描述不能为空")
    private String description;     // 商品描述

    @NotNull(message = "价格不能为空")
    private BigDecimal price;       // 商品价格

    @NotNull(message = "库存不能为空")
    private Integer stock;          // 库存数量

    private Integer status;         // 商品状态（1=上架，0=下架，管理端使用）
}