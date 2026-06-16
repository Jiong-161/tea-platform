package com.tea.exhibition.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 展览/茶博会实体类
 * 对应数据库表 tea_exhibition
 */
@Data // Lombok注解，自动生成getter/setter、toString、equals/hashCode等方法
@TableName("tea_exhibition") // MyBatis-Plus注解，指定对应的数据库表名
public class Exhibition {

    // 主键ID
    private Long id;
    // 展览标题
    private String title;

    // 展览封面图片地址
    private String cover;

    // 展览举办地点
    private String location;

    // 展览开始时间
    private LocalDateTime startTime;

    // 展览结束时间
    private LocalDateTime endTime;

    // 展览详细描述/介绍
    private String description;

    // 展览状态（如：0=未开始，1=进行中，2=已结束，3=已取消）
    private Integer status;

    // 记录创建时间
    private LocalDateTime createTime;

    // 记录更新时间
    private LocalDateTime updateTime;
}