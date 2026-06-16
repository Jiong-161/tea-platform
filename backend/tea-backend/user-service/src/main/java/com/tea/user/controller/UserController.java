package com.tea.user.controller;

import com.tea.common.exception.BusinessException;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tea.common.result.Result;
import com.tea.common.util.JwtUtil;
import com.tea.user.dto.LoginDTO;
import com.tea.user.dto.PasswordDTO;
import com.tea.user.dto.RegisterDTO;
import com.tea.user.entity.User;
import com.tea.user.Mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * 用户管理接口
 * 提供用户登录、注册、密码修改、查询等功能
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;

    /**
     * 服务健康检查接口
     */
    @GetMapping("/user/test")
    public Result<String> test() {
        return Result.success("user-service success");
    }

    /**
     * 用户列表（分页）
     * 仅管理员可访问，返回用户列表并脱敏密码字段
     */
    @GetMapping("/user/list")
    public Result<List<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> userPage = new Page<>(page, size);
        Page<User> result = userMapper.selectPage(userPage, null);

        result.getRecords().forEach(u -> u.setPassword(null));

        return Result.success(result.getRecords());
    }
    // 密码加密
//    @GetMapping("/user/password")
//    public Result<String> password() {
//
//        BCryptPasswordEncoder encoder =
//                new BCryptPasswordEncoder();
//
//        String encode = encoder.encode("123456");
//
//        return Result.success(encode);
//    }
    /**
     * 修改密码接口
     * Sentinel资源: userUpdatePassword — 保护密码修改操作
     */
    @PostMapping("/user/password")
    @SentinelResource(value = "userUpdatePassword",
            blockHandler = "updatePasswordBlockHandler")
    public Result<String> updatePassword(
            @RequestBody PasswordDTO dto,
            HttpServletRequest request) {

        // 从网关注入的请求头获取用户信息和token
        String userIdStr = request.getHeader("X-User-Id");
        String token = request.getHeader("X-Auth-Token");

        if (userIdStr == null || token == null) {
            throw new BusinessException(401, "未登录");
        }
        Long userId = Long.valueOf(userIdStr);

        // 校验Redis中token是否仍有效（Redis不可用时跳过校验）
        try {
            String redisVal = redisTemplate.opsForValue().get("login:" + token);
            if (redisVal == null) {
                throw new BusinessException(401, "登录已失效");
            }
        } catch (Exception e) {
            System.err.println("[WARN] Redis不可用，跳过Token校验: " + e.getMessage());
        }

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验旧密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(encoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);

        // 清除Redis token，使所有设备重新登录（Redis不可用时忽略）
        try {
            redisTemplate.delete("login:" + token);
        } catch (Exception e) {
            System.err.println("[WARN] Redis不可用，Token未清除: " + e.getMessage());
        }

        return Result.success("密码修改成功，请重新登录");
    }




    /**
     * 用户登录接口
     * Sentinel资源: userLogin — 防暴力破解/高频请求
     */
    @PostMapping("/user/login")
    @SentinelResource(value = "userLogin",
            blockHandler = "loginBlockHandler",
            fallback = "loginFallback")
    public Result<String> login(@Valid @RequestBody LoginDTO loginDTO) {

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, loginDTO.getUsername())
        );

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账户已被禁用，请联系管理员");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        boolean match = encoder.matches(
                loginDTO.getPassword(),
                user.getPassword()
        );

        if (!match) {
            throw new BusinessException("密码错误");
        }

        String token = JwtUtil.createToken(
                user.getId(), user.getUsername(),
                user.getRole() != null ? user.getRole() : 0
        );
        // Redis缓存Token（Redis不可用时仅记录日志，不影响登录流程）
        try {
            redisTemplate.opsForValue().set(
                    "login:" + token,
                    user.getId().toString(),
                    24,
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            System.err.println("[WARN] Redis不可用，Token未缓存: " + e.getMessage());
        }
        return Result.success(token);
    }
    /**
     * 获取当前登录用户信息
     * 从网关注入的请求头获取用户ID，返回用户详情并脱敏密码字段
     */
    @GetMapping("/user/current")
    public Result<User> current(HttpServletRequest request) {

        // 从网关注入的请求头获取用户ID，无需重复解析JWT
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new BusinessException(401, "未登录");
        }
        Long userId = Long.valueOf(userIdStr);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(null);
        return Result.success(user);
    }
    /**
     * 用户注册接口
     * Sentinel资源: userRegister — 防恶意注册
     */
    @PostMapping("/user/register")
    @SentinelResource(value = "userRegister",
            blockHandler = "registerBlockHandler")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO) {

        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, registerDTO.getUsername())
        );

        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        User user = new User();

        user.setUsername(registerDTO.getUsername());

        user.setPassword(
                encoder.encode(registerDTO.getPassword())
        );

        user.setNickname(registerDTO.getNickname());

        user.setStatus(1);
        user.setRole(0);   // 默认为普通用户角色

        userMapper.insert(user);

        return Result.success("注册成功");
    }
    /**
     * 用户退出登录
     * 清除Redis中的Token，使当前登录状态失效
     */
    @PostMapping("/user/logout")
    public Result<String> logout(HttpServletRequest request) {

        // 从网关注入的请求头获取token，无需从Authorization头解析
        String token = request.getHeader("X-Auth-Token");
        if (token != null) {
            // Redis不可用时忽略删除操作
            try {
                redisTemplate.delete("login:" + token);
            } catch (Exception e) {
                System.err.println("[WARN] Redis不可用，Token未删除: " + e.getMessage());
            }
        }

        return Result.success("退出成功");
    }

    // ==================== Sentinel 降级/限流处理方法 ====================

    /**
     * 登录接口被限流或熔断时的降级方法
     */
    public Result<String> loginBlockHandler(LoginDTO dto, BlockException e) {
        log.warn("[Sentinel降级] 登录接口被拦截: type={}", e.getClass().getSimpleName());
        return Result.error(429, "登录请求过于频繁，请稍后再试");
    }

    /**
     * 登录接口业务异常时的降级方法
     */
    public Result<String> loginFallback(LoginDTO dto, Throwable t) {
        log.error("[Sentinel降级] 登录服务异常: {}", t.getMessage());
        return Result.error(503, "登录服务暂不可用，请稍后重试");
    }

    /**
     * 密码修改接口被限流时的降级方法
     */
    public Result<String> updatePasswordBlockHandler(PasswordDTO dto,
                                                      HttpServletRequest request,
                                                      BlockException e) {
        log.warn("[Sentinel降级] 密码修改被拦截: type={}", e.getClass().getSimpleName());
        return Result.error(429, "操作过于频繁，请稍后再试");
    }

    /**
     * 注册接口被限流时的降级方法
     */
    public Result<String> registerBlockHandler(RegisterDTO dto, BlockException e) {
        log.warn("[Sentinel降级] 注册接口被拦截: type={}", e.getClass().getSimpleName());
        return Result.error(429, "注册请求过于频繁，请稍后再试");
    }

}