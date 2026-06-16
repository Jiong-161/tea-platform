package com.tea.user.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.user.entity.Address;
import org.apache.ibatis.annotations.Mapper;
// MyBatis-Plus Mapper 接口，对应表 tea_address
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}