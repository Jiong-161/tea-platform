package com.tea.exhibition.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 展览/茶博会报名记录实体类
 * 对应数据库表 tea_exhibition_signup
 * 用于存储用户报名展览的记录信息
 */
@Data // Lombok注解，自动生成getter/setter、toString、equals/hashCode等方法
@TableName("tea_exhibition_signup") // MyBatis-Plus注解，指定对应的数据库表名
public class ExhibitionSignup {

    // 报名记录主键ID
    private Long id;

    // 关联的展览ID（外键，对应tea_exhibition表的id字段）
    private Long exhibitionId;

    // 报名用户ID（外键，关联用户表的用户ID）
    private Long userId;

    // 报名用户的用户名（冗余存储，方便直接展示报名人信息，无需额外联表查询）
    private String username;

    // 报名时间
    private LocalDateTime createTime;
}