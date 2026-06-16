package com.tea.exhibition.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import com.tea.exhibition.dto.ExhibitionSaveDTO;
import com.tea.exhibition.entity.Exhibition;
import com.tea.exhibition.entity.ExhibitionSignup;
import com.tea.exhibition.mapper.ExhibitionMapper;
import com.tea.exhibition.mapper.ExhibitionSignupMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理 - 茶展览活动管理接口
 * 路径 /admin/exhibition/**，仅管理员可访问（网关校验 role=1）
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/exhibition")
@Tag(name = "后台-展览管理")
public class AdminExhibitionController {

    private final ExhibitionMapper exhibitionMapper;
    private final ExhibitionSignupMapper signupMapper;

    /** 展览分页列表 */
    @Operation(summary = "展览分页列表")
    @GetMapping("/list")
    public Result<Page<Exhibition>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Page<Exhibition> exhibitionPage = new Page<>(page, size);

        LambdaQueryWrapper<Exhibition> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Exhibition::getTitle, keyword);
        }
        wrapper.orderByDesc(Exhibition::getCreateTime);

        return Result.success(exhibitionMapper.selectPage(exhibitionPage, wrapper));
    }

    /** 新增/编辑展览（id为空则新增，有值则编辑） */
    @Operation(summary = "新增/编辑展览")
    @PostMapping("/save")
    public Result<String> save(@Valid @RequestBody ExhibitionSaveDTO dto,
                               @RequestParam(required = false) Long id) {

        Exhibition exhibition;
        if (id != null) {
            exhibition = exhibitionMapper.selectById(id);
            if (exhibition == null) {
                throw new BusinessException("展览不存在");
            }
        } else {
            exhibition = new Exhibition();
            exhibition.setStatus(0); // 默认未开始
        }

        // 只拷贝 DTO 中定义的字段，防止客户端注入内部字段
        BeanUtils.copyProperties(dto, exhibition);

        if (id != null) {
            exhibitionMapper.updateById(exhibition);
            return Result.success("编辑成功");
        } else {
            exhibitionMapper.insert(exhibition);
            return Result.success("新增成功");
        }
    }

    /** 删除展览 */
    @Operation(summary = "删除展览")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (exhibitionMapper.selectById(id) == null) {
            throw new BusinessException("展览不存在");
        }
        exhibitionMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /** 查看展览报名列表（支持按展览ID筛选） */
    @Operation(summary = "报名列表")
    @GetMapping("/signup/list")
    public Result<List<ExhibitionSignup>> signupList(
            @RequestParam(required = false) Long exhibitionId) {

        LambdaQueryWrapper<ExhibitionSignup> wrapper = new LambdaQueryWrapper<>();
        if (exhibitionId != null) {
            wrapper.eq(ExhibitionSignup::getExhibitionId, exhibitionId);
        }
        wrapper.orderByDesc(ExhibitionSignup::getCreateTime);

        return Result.success(signupMapper.selectList(wrapper));
    }
}