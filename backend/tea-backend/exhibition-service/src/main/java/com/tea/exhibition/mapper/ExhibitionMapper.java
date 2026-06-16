package com.tea.exhibition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.exhibition.entity.Exhibition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 展览数据访问层接口
 * 用于操作 tea_exhibition 数据库表
 */
@Mapper
public interface ExhibitionMapper extends BaseMapper<Exhibition> {
    // 继承 BaseMapper 后，自动拥有 CRUD、分页等基础数据库操作方法
}