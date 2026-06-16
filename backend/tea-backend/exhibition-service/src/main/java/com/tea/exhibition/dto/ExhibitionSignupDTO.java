package com.tea.exhibition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 展览报名请求数据传输对象（DTO）
 * 用于接收前端提交的展览报名请求参数
 */
@Data // Lombok注解，自动生成getter/setter、toString、equals/hashCode等方法
public class ExhibitionSignupDTO {

    /**
     * 要报名的展览ID
     * @NotNull 校验注解：确保该字段不为null，否则返回错误信息“展览ID不能为空”
     */
    @NotNull(message = "展览ID不能为空")
    private Long exhibitionId;
}