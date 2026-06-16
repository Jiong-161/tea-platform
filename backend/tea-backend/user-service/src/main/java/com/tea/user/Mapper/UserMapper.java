package com.tea.user.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}