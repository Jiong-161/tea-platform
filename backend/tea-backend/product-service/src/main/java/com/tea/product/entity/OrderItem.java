package com.tea.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单商品明细实体，对应表 tea_order_item
 */
@Data
@TableName("tea_order_item")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;                // 主键ID
    private Long orderId;           // 关联订单ID
    private Long productId;         // 关联商品ID
    private String productName;     // 商品名称（冗余存储）
    private String productCover;    // 商品封面（冗余存储）
    private BigDecimal price;       // 下单时的商品单价
    private Integer quantity;       // 购买数量
}