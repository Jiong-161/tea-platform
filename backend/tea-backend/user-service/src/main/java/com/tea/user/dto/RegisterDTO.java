package com.tea.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用户注册请求DTO
 */
@Data
public class RegisterDTO {

    /** 用户名（4-20位） */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 4, max = 20, message = "用户名长度必须是4到20位")
    private String username;

    /** 密码（6-20位） */
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须是6到20位")
    private String password;

    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    private String nickname;
}