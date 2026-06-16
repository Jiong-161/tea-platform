package com.tea.product.ov;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车列表项VO，含商品详情和金额小计
 */
@Data
public class CartVO {
    private Long id;                // 购物车记录ID
    private Long productId;         // 商品ID
    private String productName;     // 商品名称
    private String productCover;    // 商品封面图
    private BigDecimal price;       // 单价
    private Integer quantity;       // 数量
    private BigDecimal subtotal;    // 小计（单价 × 数量）
}