package com.tea.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建订单的请求参数DTO
 */
@Data
public class CreateOrderDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;         // 要购买的商品ID

    @NotNull(message = "购买数量不能为空")
    private Integer quantity;       // 购买数量
}