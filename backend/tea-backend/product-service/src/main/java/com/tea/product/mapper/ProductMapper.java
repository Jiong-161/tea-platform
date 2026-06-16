package com.tea.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 继承BaseMapper后，自动拥有CRUD、分页等基础数据库操作
}