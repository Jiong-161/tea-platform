package com.tea.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体，对应表 tea_order
 */
@Data
@TableName("tea_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;                // 主键ID
    private String orderNo;         // 订单号
    private Long userId;            // 下单用户ID
    private String username;        // 下单用户名（冗余存储）
    private BigDecimal totalAmount; // 订单总金额
    private Integer status;         // 订单状态（1=已支付，0=待支付）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 订单创建时间
}