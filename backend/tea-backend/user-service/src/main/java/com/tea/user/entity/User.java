package com.tea.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tea_user")
public class User {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private String avatar;

    private Integer role;   // 角色：0=普通用户 1=管理员

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}