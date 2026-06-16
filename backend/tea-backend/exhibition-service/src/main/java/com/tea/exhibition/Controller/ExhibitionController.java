package com.tea.exhibition.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import com.tea.exhibition.dto.ExhibitionSignupDTO;
import com.tea.exhibition.entity.Exhibition;
import com.tea.exhibition.entity.ExhibitionSignup;
import com.tea.exhibition.mapper.ExhibitionMapper;
import com.tea.exhibition.mapper.ExhibitionSignupMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 茶文化展览模块控制器
 * 提供展览列表、详情、报名、我的报名等业务接口
 */
@Tag(name = "茶文化展览接口") // Swagger接口文档标签
@RestController // 标记为REST风格控制器，自动返回JSON响应
@RequiredArgsConstructor // Lombok注解，自动生成final字段的构造方法，实现依赖注入
@Slf4j
@RequestMapping("/exhibition") // 接口基础路径
public class ExhibitionController {

    // MyBatis-Plus Mapper，用于数据库操作
    private final ExhibitionMapper exhibitionMapper;
    private final ExhibitionSignupMapper signupMapper;

    /**
     * 展览列表接口
     * @return 所有展览列表，按创建时间倒序排列
     */
    @Operation(summary = "展览列表") // Swagger接口说明
    @GetMapping("/list")
    public Result<List<Exhibition>> list() {
        // 使用LambdaQueryWrapper构建查询条件，按创建时间倒序
        List<Exhibition> list = exhibitionMapper.selectList(
                new LambdaQueryWrapper<Exhibition>()
                        .orderByDesc(Exhibition::getCreateTime)
        );
        return Result.success(list);
    }

    /**
     * 展览详情接口
     * @param id 展览ID（路径参数）
     * @return 单个展览的详细信息
     */
    @Operation(summary = "展览详情")
    @GetMapping("/{id}")
    public Result<Exhibition> detail(@PathVariable Long id) {
        // 根据ID查询展览详情
        Exhibition exhibition = exhibitionMapper.selectById(id);
        if (exhibition == null) {
            throw new BusinessException("展览不存在");
        }
        return Result.success(exhibition);
    }

    /**
     * 展览报名接口
     * Sentinel资源: exhibitionSignup — 防重复提交/高频请求
     */
    @Operation(summary = "展览报名")
    @PostMapping("/signup")
    @SentinelResource(value = "exhibitionSignup",
            blockHandler = "signupBlockHandler")
    public Result<String> signup(
            @Valid @RequestBody ExhibitionSignupDTO dto,
            HttpServletRequest request) {

        // 从网关注入的请求头获取用户信息，无需重复解析JWT
        String userIdStr = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");

        if (userIdStr == null || username == null) {
            throw new BusinessException(401, "未登录");
        }
        Long userId = Long.valueOf(userIdStr);

        // 校验展览是否存在
        Exhibition exhibition = exhibitionMapper.selectById(dto.getExhibitionId());
        if (exhibition == null) {
            throw new BusinessException("展览不存在");
        }

        // 校验是否重复报名：同一用户+同一展览不可重复报名
        Long count = signupMapper.selectCount(
                new LambdaQueryWrapper<ExhibitionSignup>()
                        .eq(ExhibitionSignup::getUserId, userId)
                        .eq(ExhibitionSignup::getExhibitionId, dto.getExhibitionId())
        );
        if (count > 0) {
            throw new BusinessException("您已报名该展览，无需重复报名");
        }

        ExhibitionSignup signup = new ExhibitionSignup();
        signup.setExhibitionId(dto.getExhibitionId());
        signup.setUserId(userId);
        signup.setUsername(username);

        signupMapper.insert(signup);

        return Result.success("报名成功");
    }

    @Operation(summary = "我的报名")
    @GetMapping("/my")
    public Result<List<ExhibitionSignup>> my(HttpServletRequest request) {

        // 从网关注入的请求头获取用户ID，无需重复解析JWT
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new BusinessException(401, "未登录");
        }
        Long userId = Long.valueOf(userIdStr);

        List<ExhibitionSignup> list = signupMapper.selectList(
                new LambdaQueryWrapper<ExhibitionSignup>()
                        .eq(ExhibitionSignup::getUserId, userId)
                        .orderByDesc(ExhibitionSignup::getCreateTime)
        );

        return Result.success(list);
    }

    /**
     * 展览报名被限流时的降级方法
     */
    public Result<String> signupBlockHandler(ExhibitionSignupDTO dto,
                                              HttpServletRequest request,
                                              BlockException e) {
        log.warn("[Sentinel降级] 展览报名被拦截: type={}", e.getClass().getSimpleName());
        return Result.error(429, "报名操作过于频繁，请稍后再试");
    }
}