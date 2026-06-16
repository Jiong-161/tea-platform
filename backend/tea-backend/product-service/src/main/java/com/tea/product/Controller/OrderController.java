package com.tea.product.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import com.tea.product.dto.CreateOrderDTO;
import com.tea.product.entity.Cart;
import com.tea.product.entity.Order;
import com.tea.product.entity.OrderItem;
import com.tea.product.entity.Product;
import com.tea.product.mapper.CartMapper;
import com.tea.product.mapper.OrderItemMapper;
import com.tea.product.mapper.OrderMapper;
import com.tea.product.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单管理接口
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product/order")
@Tag(name = "订单接口")
public class OrderController {

    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;

    /**
     * 生成16位唯一订单号（时间戳+随机数，比UUID截取更可靠）
     */
    private String generateOrderNo() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 取UUID前16位，保证唯一性
        return uuid.length() >= 16 ? uuid.substring(0, 16) : uuid + "0000000000000000".substring(uuid.length());
    }

    /**
     * 创建订单（单商品直接下单）
     * Sentinel资源: orderCreate — 保护订单创建操作
     */
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    @SentinelResource(value = "orderCreate",
            blockHandler = "orderCreateBlockHandler",
            fallback = "orderCreateFallback")
    public Result<String> create(
            @Valid @RequestBody CreateOrderDTO dto,
            HttpServletRequest request) {

        // 从网关注入的请求头获取用户信息，无需重复解析JWT
        String userIdStr = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        if (userIdStr == null || username == null) {
            throw new BusinessException(401, "未登录");
        }
        Long userId = Long.valueOf(userIdStr);

        // 校验商品是否存在且未下架（status=1或NULL视为可售）
        Product product = productMapper.selectOne(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getId, dto.getProductId())
                        .and(w -> w.eq(Product::getStatus, 1).or().isNull(Product::getStatus))
        );
        if (product == null) {
            throw new BusinessException("商品不存在或已下架");
        }

        // 安全的购买数量
        int qty = dto.getQuantity() != null ? dto.getQuantity() : 1;
        int stock = product.getStock() != null ? product.getStock() : 0;

        // 校验库存
        if (stock < qty) {
            throw new BusinessException("库存不足，当前库存为 " + stock);
        }

        // 计算订单总金额（安全处理null price）
        BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
        BigDecimal amount = price.multiply(BigDecimal.valueOf(qty));

        // 创建订单主记录
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setUsername(username);
        order.setTotalAmount(amount);
        order.setStatus(1);
        orderMapper.insert(order);

        // 创建订单商品明细
        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(product.getId());
        item.setProductName(product.getTitle() != null ? product.getTitle() : "未知商品");
        item.setProductCover(product.getCover());
        item.setPrice(price);
        item.setQuantity(qty);
        orderItemMapper.insert(item);

        // 扣减库存
        product.setStock(stock - qty);
        productMapper.updateById(product);

        return Result.success("下单成功");
    }

    /**
     * 从购物车批量下单（将用户购物车中所有商品合并为一个订单）
     * Sentinel资源: orderCreateFromCart — 保护批量下单操作
     */
    @Operation(summary = "购物车批量下单")
    @PostMapping("/create-from-cart")
    @Transactional(rollbackFor = Exception.class)
    @SentinelResource(value = "orderCreateFromCart",
            blockHandler = "orderCreateFromCartBlockHandler",
            fallback = "orderCreateFromCartFallback")
    public Result<String> createFromCart(HttpServletRequest request) {

        String userIdStr = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        if (userIdStr == null || username == null) {
            throw new BusinessException(401, "未登录");
        }
        Long userId = Long.valueOf(userIdStr);

        // 查询用户购物车
        List<Cart> cartItems = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
        );

        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException("购物车为空，无法下单");
        }

        // 批量查询商品信息
        List<Long> productIds = cartItems.stream()
                .map(Cart::getProductId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 计算总金额并校验（安全处理null值）
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : cartItems) {
            Product p = productMap.get(cart.getProductId());
            if (p == null || (p.getStatus() != null && p.getStatus() == 0)) {
                String title = p != null ? p.getTitle() : String.valueOf(cart.getProductId());
                throw new BusinessException("商品【" + title + "】不存在或已下架，请从购物车移除");
            }
            int cartQty = cart.getQuantity() != null ? cart.getQuantity() : 1;
            int stock = p.getStock() != null ? p.getStock() : 0;
            if (stock < cartQty) {
                throw new BusinessException("商品【" + p.getTitle() + "】库存不足，当前库存为 " + stock);
            }
            BigDecimal price = p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO;
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(cartQty)));
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setUsername(username);
        order.setTotalAmount(totalAmount);
        order.setStatus(1);
        orderMapper.insert(order);

        // 创建订单明细并扣减库存
        for (Cart cart : cartItems) {
            Product p = productMap.get(cart.getProductId());
            int cartQty = cart.getQuantity() != null ? cart.getQuantity() : 1;

            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(p.getId());
            item.setProductName(p.getTitle() != null ? p.getTitle() : "未知商品");
            item.setProductCover(p.getCover());
            item.setPrice(p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO);
            item.setQuantity(cartQty);
            orderItemMapper.insert(item);

            // 扣减库存（安全处理null）
            int stock = p.getStock() != null ? p.getStock() : 0;
            p.setStock(stock - cartQty);
            productMapper.updateById(p);

            // 清空购物车
            cartMapper.deleteById(cart.getId());
        }

        return Result.success("下单成功");
    }

    /**
     * 查询当前用户的所有订单
     */
    @Operation(summary = "我的订单")
    @GetMapping("/my")
    public Result<List<Order>> my(HttpServletRequest request) {

        // 从网关注入的请求头获取用户ID，无需重复解析JWT
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new BusinessException(401, "未登录");
        }
        Long userId = Long.valueOf(userIdStr);

        List<Order> list = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime)
        );
        return Result.success(list);
    }

    // ==================== Sentinel 降级/限流处理方法 ====================

    /**
     * 单商品下单被限流时的降级方法
     */
    public Result<String> orderCreateBlockHandler(CreateOrderDTO dto,
                                                   HttpServletRequest request,
                                                   BlockException e) {
        log.warn("[Sentinel降级] 单商品下单被拦截: type={}", e.getClass().getSimpleName());
        return Result.error(429, "下单请求过于频繁，请稍后再试");
    }

    /**
     * 单商品下单业务异常时的降级方法
     */
    public Result<String> orderCreateFallback(CreateOrderDTO dto,
                                              HttpServletRequest request,
                                              Throwable t) {
        log.error("[Sentinel降级] 下单服务异常: {}", t.getMessage());
        return Result.error(503, "下单服务暂不可用，请稍后重试");
    }

    /**
     * 购物车批量下单被限流时的降级方法
     */
    public Result<String> orderCreateFromCartBlockHandler(HttpServletRequest request,
                                                           BlockException e) {
        log.warn("[Sentinel降级] 批量下单被拦截: type={}", e.getClass().getSimpleName());
        return Result.error(429, "批量下单请求过于频繁，请稍后再试");
    }

    /**
     * 购物车批量下单业务异常时的降级方法
     */
    public Result<String> orderCreateFromCartFallback(HttpServletRequest request,
                                                       Throwable t) {
        log.error("[Sentinel降级] 批量下单服务异常: {}", t.getMessage());
        return Result.error(503, "批量下单服务暂不可用，请稍后重试");
    }
}