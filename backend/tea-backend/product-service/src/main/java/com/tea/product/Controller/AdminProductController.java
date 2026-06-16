package com.tea.product.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import com.tea.product.dto.ProductPublishDTO;
import com.tea.product.entity.Order;
import com.tea.product.entity.Product;
import com.tea.product.mapper.OrderMapper;
import com.tea.product.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理 - 茶商品与订单管理接口
 * 路径 /admin/**，仅管理员可访问（网关校验 role=1）
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "后台-商品管理")
public class AdminProductController {

    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    /** 获取单个商品详情（管理端，不受status过滤） */
    @Operation(summary = "获取单个商品")
    @GetMapping("/product/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return Result.success(product);
    }

    /** 商品分页列表 */
    @Operation(summary = "商品分页列表")
    @GetMapping("/product/list")
    public Result<Page<Product>> productList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        Page<Product> productPage = new Page<>(page, size);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getCreateTime);

        return Result.success(productMapper.selectPage(productPage, wrapper));
    }

    /** 新增商品 */
    @Operation(summary = "新增商品")
    @PostMapping("/product")
    public Result<String> addProduct(@Valid @RequestBody ProductPublishDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        productMapper.insert(product);
        return Result.success("新增成功");
    }

    /** 编辑商品 */
    @Operation(summary = "编辑商品")
    @PutMapping("/product/{id}")
    public Result<String> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductPublishDTO dto) {

        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 逐字段更新，避免null覆盖已有值
        product.setTitle(dto.getTitle());
        product.setCover(dto.getCover());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }
        productMapper.updateById(product);
        return Result.success("编辑成功");
    }

    /** 上架/下架商品 */
    @Operation(summary = "上架/下架商品")
    @PutMapping("/product/status/{id}")
    public Result<String> toggleProductStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {

        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(status);
        productMapper.updateById(product);
        return Result.success(status == 1 ? "已上架" : "已下架");
    }

    /** 删除商品 */
    @Operation(summary = "删除商品")
    @DeleteMapping("/product/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        if (productMapper.selectById(id) == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /** 订单分页列表 */
    @Operation(summary = "订单分页列表")
    @GetMapping("/order/list")
    public Result<Page<Order>> orderList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {

        Page<Order> orderPage = new Page<>(page, size);

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Order::getOrderNo, keyword);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        return Result.success(orderMapper.selectPage(orderPage, wrapper));
    }

    /** 订单状态变更（如发货、完成等） */
    @Operation(summary = "修改订单状态")
    @PutMapping("/order/status/{id}")
    public Result<String> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        order.setStatus(status);
        orderMapper.updateById(order);
        return Result.success("状态已更新");
    }
}