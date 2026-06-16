package com.tea.exhibition.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 展览新增/编辑请求DTO
 * 替代直接使用 Exhibition Entity 接收请求体，防止客户端注入内部字段
 */
@Data
public class ExhibitionSaveDTO {

    @NotBlank(message = "展览标题不能为空")
    private String title;

    private String cover;

    @NotBlank(message = "展览地点不能为空")
    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private Integer status;
}