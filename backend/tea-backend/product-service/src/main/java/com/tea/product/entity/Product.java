package com.tea.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体，对应表 tea_product
 */
@Data
@TableName("tea_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;                // 主键ID
    private String title;           // 商品名称
    private String cover;            // 商品封面图
    private String description;     // 商品描述
    private BigDecimal price;       // 商品价格
    private Integer stock;          // 库存数量
    private Integer status;         // 商品状态（1=上架，0=下架）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; // 更新时间
}