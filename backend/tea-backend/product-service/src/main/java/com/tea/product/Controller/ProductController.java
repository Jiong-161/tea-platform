package com.tea.product.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import com.tea.product.dto.ProductPublishDTO;
import com.tea.product.entity.Product;
import com.tea.product.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
@Tag(name = "商品接口")
public class ProductController {

    private final ProductMapper productMapper;

    /**
     * 商品列表（仅返回未下架的商品：status=1或NULL均视为上架，仅明确status=0视为下架）
     * 修复：MySQL中 status<>0 无法匹配NULL行，改用 status=1 OR status IS NULL
     */
    @Operation(summary = "商品列表")
    @GetMapping("/list")
    public Result<List<Product>> list() {
        List<Product> list = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .and(w -> w.eq(Product::getStatus, 1).or().isNull(Product::getStatus))
                        .orderByDesc(Product::getCreateTime)
        );
        return Result.success(list);
    }

    /**
     * 商品详情（仅返回未下架的商品：status=1或NULL视为上架）
     * 修复：MySQL中 status<>0 无法匹配NULL行，改用 status=1 OR status IS NULL
     */
    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productMapper.selectOne(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getId, id)
                        .and(w -> w.eq(Product::getStatus, 1).or().isNull(Product::getStatus))
        );
        if (product == null) {
            throw new BusinessException("商品不存在或已下架");
        }
        return Result.success(product);
    }

    /**
     * 发布/新增商品
     */
    @Operation(summary = "发布商品")
    @PostMapping("/publish")
    public Result<String> publish(@Valid @RequestBody ProductPublishDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setStatus(1); // 默认设置为上架状态
        productMapper.insert(product);
        return Result.success("发布成功");
    }
}