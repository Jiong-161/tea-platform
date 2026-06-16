package com.tea.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import com.tea.user.entity.User;
import com.tea.user.Mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理 - 用户管理接口
 * 路径 /admin/user/**，仅管理员可访问（网关校验 role=1）
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
@Tag(name = "后台-用户管理")
public class AdminUserController {

    private final UserMapper userMapper;

    /** 用户分页列表 */
    @Operation(summary = "用户分页列表")

    @GetMapping("/list")
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Page<User> userPage = new Page<>(page, size);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 关键词搜索：用户名或昵称模糊匹配
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> result = userMapper.selectPage(userPage, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null)); // 脱敏
        return Result.success(result);
    }

    /** 启用/禁用用户 */
    @Operation(summary = "启用/禁用用户")
    @PutMapping("/status/{id}")
    public Result<String> toggleStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getRole() != null && user.getRole() == 1) {
            throw new BusinessException("不能禁用管理员账户");
        }

        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success(status == 1 ? "已启用" : "已禁用");
    }

    /** 删除用户 */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getRole() != null && user.getRole() == 1) {
            throw new BusinessException("不能删除管理员账户");
        }

        userMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /** 用户详情 */
    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public Result<User> detail(@PathVariable Long id) {

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }
}