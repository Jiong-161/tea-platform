package com.tea.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.product.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}