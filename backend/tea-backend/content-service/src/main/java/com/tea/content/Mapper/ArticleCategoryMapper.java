package com.tea.content.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.content.entity.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleCategoryMapper
        extends BaseMapper<ArticleCategory> {
}