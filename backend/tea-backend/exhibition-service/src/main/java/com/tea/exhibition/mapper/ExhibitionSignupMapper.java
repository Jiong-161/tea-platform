package com.tea.exhibition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.exhibition.entity.ExhibitionSignup;
import org.apache.ibatis.annotations.Mapper;

/**
 * 展览报名记录数据访问层接口
 * 用于操作 tea_exhibition_signup 数据库表
 */
@Mapper
public interface ExhibitionSignupMapper extends BaseMapper<ExhibitionSignup> {
    // 继承 BaseMapper 后，自动拥有 CRUD、分页等基础数据库操作方法
}