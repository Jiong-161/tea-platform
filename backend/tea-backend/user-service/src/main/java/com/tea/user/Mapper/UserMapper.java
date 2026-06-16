package com.tea.user.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 * MyBatis-Plus Mapper，对应表 tea_user
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}