package com.tea.product.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import com.tea.product.dto.CartItemDTO;
import com.tea.product.entity.Cart;
import com.tea.product.entity.Product;
import com.tea.product.mapper.CartMapper;
import com.tea.product.mapper.ProductMapper;
import com.tea.product.ov.CartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车管理接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/product/cart")
@Tag(name = "购物车接口")
public class CartController {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    /**
     * 加入购物车
     * 若商品已在购物车中则累加数量，否则新增记录
     */
    @Operation(summary = "加入购物车")
    @PostMapping("/add")
    public Result<String> add(
            @Valid @RequestBody CartItemDTO dto,
            HttpServletRequest request) {

        Long userId = getUserId(request);

        // 校验商品是否存在且未下架（status=1或NULL视为上架）
        Product product = productMapper.selectOne(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getId, dto.getProductId())
                        .and(w -> w.eq(Product::getStatus, 1).or().isNull(Product::getStatus))
        );
        if (product == null) {
            throw new BusinessException("商品不存在或已下架");
        }

        // 校验库存（避免加入购物车时数量超出库存）
        if (product.getStock() != null && dto.getQuantity() > product.getStock()) {
            throw new BusinessException("库存不足，当前库存为 " + product.getStock());
        }

        // 查询是否已在购物车中
        Cart exist = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .eq(Cart::getProductId, dto.getProductId())
        );

        if (exist != null) {
            // 已存在：累加数量（安全处理null）
            int oldQty = exist.getQuantity() != null ? exist.getQuantity() : 0;
            int newQty = oldQty + (dto.getQuantity() != null ? dto.getQuantity() : 1);
            exist.setQuantity(newQty);
            cartMapper.updateById(exist);
        } else {
            // 不存在：新增记录
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(dto.getProductId());
            cart.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
            cartMapper.insert(cart);
        }

        return Result.success("加入购物车成功");
    }

    /**
     * 查看购物车列表
     * 返回商品详情、数量、小计金额
     */
    @Operation(summary = "购物车列表")
    @GetMapping("/list")
    public Result<List<CartVO>> list(HttpServletRequest request) {

        Long userId = getUserId(request);

        // 查询当前用户的购物车记录
        List<Cart> cartItems = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .orderByDesc(Cart::getCreateTime)
        );

        if (cartItems.isEmpty()) {
            return Result.success(List.of());
        }

        // 收集关联的商品ID，批量查询商品信息
        List<Long> productIds = cartItems.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productMapper.selectBatchIds(productIds);

        // productId → Product 映射，便于快速查找
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 组装CartVO列表
        List<CartVO> voList = cartItems.stream().map(cart -> {
            Product p = productMap.get(cart.getProductId());

            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setProductId(cart.getProductId());
            // 安全获取数量
            int qty = cart.getQuantity() != null ? cart.getQuantity() : 1;
            vo.setQuantity(qty);

            if (p != null) {
                vo.setProductName(p.getTitle() != null ? p.getTitle() : "未知商品");
                vo.setProductCover(p.getCover());
                vo.setPrice(p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO);
                vo.setSubtotal(vo.getPrice().multiply(BigDecimal.valueOf(qty)));
            } else {
                // 商品已删除的降级处理
                vo.setProductName("商品已下架");
                vo.setProductCover("");
                vo.setPrice(BigDecimal.ZERO);
                vo.setSubtotal(BigDecimal.ZERO);
            }

            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    /**
     * 删除购物车中的商品
     * @param id 购物车记录ID
     */
    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{id}")
    public Result<String> delete(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long userId = getUserId(request);

        Cart cart = cartMapper.selectById(id);
        if (cart == null) {
            throw new BusinessException("购物车记录不存在");
        }
        // 校验数据归属，防止越权删除他人购物车
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        cartMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 修改购物车商品数量
     */
    @Operation(summary = "修改购物车商品数量")
    @PutMapping("/update")
    public Result<String> update(
            @Valid @RequestBody CartItemDTO dto,
            HttpServletRequest request) {

        Long userId = getUserId(request);

        // 根据 userId + productId 定位购物车记录
        Cart cart = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .eq(Cart::getProductId, dto.getProductId())
        );

        if (cart == null) {
            throw new BusinessException("购物车中无该商品");
        }

        // 安全设置数量
        cart.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
        cartMapper.updateById(cart);

        return Result.success("修改成功");
    }

    /**
     * 从网关请求头中提取登录用户ID
     */
    private Long getUserId(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new BusinessException(401, "未登录");
        }
        return Long.valueOf(userIdStr);
    }
}