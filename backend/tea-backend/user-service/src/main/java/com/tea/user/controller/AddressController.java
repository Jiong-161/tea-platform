package com.tea.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import com.tea.user.dto.AddressDTO;
import com.tea.user.entity.Address;
import com.tea.user.Mapper.AddressMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址管理接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/address")
@Tag(name = "收货地址接口")
public class AddressController {

    private final AddressMapper addressMapper;

    /**
     * 新增收货地址
     * 若设为默认地址，自动清除其他默认地址
     */
    @Operation(summary = "新增收货地址")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> add(
            @Valid @RequestBody AddressDTO dto,
            HttpServletRequest request) {

        Long userId = getUserId(request);

        // 若设为默认地址，先清除该用户其他默认地址
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(userId);
        }

        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(dto.getReceiverName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetail(dto.getDetail());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);

        addressMapper.insert(address);
        return Result.success("新增地址成功");
    }

    /**
     * 修改收货地址
     */
    @Operation(summary = "修改收货地址")
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> update(
            @Valid @RequestBody AddressDTO dto,
            @RequestParam Long id,
            HttpServletRequest request) {

        Long userId = getUserId(request);

        Address address = addressMapper.selectById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        // 若改为默认地址，先清除其他默认
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(userId);
        }

        address.setReceiverName(dto.getReceiverName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetail(dto.getDetail());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);

        addressMapper.updateById(address);
        return Result.success("修改成功");
    }

    /**
     * 删除收货地址
     */
    @Operation(summary = "删除收货地址")
    @DeleteMapping("/{id}")
    public Result<String> delete(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long userId = getUserId(request);

        Address address = addressMapper.selectById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        addressMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 查询收货地址列表
     * 默认地址排在最前
     */
    @Operation(summary = "收货地址列表")
    @GetMapping("/list")
    public Result<List<Address>> list(HttpServletRequest request) {

        Long userId = getUserId(request);

        List<Address> list = addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreateTime)
        );

        return Result.success(list);
    }

    /**
     * 设置默认地址
     */
    @Operation(summary = "设置默认地址")
    @PutMapping("/default/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> setDefault(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long userId = getUserId(request);

        Address address = addressMapper.selectById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        // 清除该用户所有默认地址，再将当前地址设为默认
        clearDefault(userId);

        address.setIsDefault(1);
        addressMapper.updateById(address);

        return Result.success("设置默认地址成功");
    }

    /**
     * 清除指定用户的所有默认地址标记
     */
    private void clearDefault(Long userId) {
        addressMapper.update(null,
                new LambdaUpdateWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .set(Address::getIsDefault, 0)
        );
    }

    /**
     * 从网关请求头提取当前登录用户ID
     */
    private Long getUserId(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new BusinessException(401, "未登录");
        }
        return Long.valueOf(userIdStr);
    }
}