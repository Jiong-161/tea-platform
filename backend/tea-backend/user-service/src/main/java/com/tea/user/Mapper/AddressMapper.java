package com.tea.user.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.user.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址数据访问接口
 * MyBatis-Plus Mapper，对应表 tea_address
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}