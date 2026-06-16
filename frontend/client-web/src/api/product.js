/**
 * 商品 / 购物车 / 订单相关 API 接口
 * 对应后端 product-service（网关路径 /product/**）
 */
import request from '@/utils/request'

/* ---- 商品 ---- */

/** 获取商品列表 */
export function getProductList() {
  return request({ url: '/product/list', method: 'get' })
}

/** 获取商品详情 */
export function getProductDetail(id) {
  return request({ url: `/product/${id}`, method: 'get' })
}

/* ---- 购物车 ---- */

/** 加入购物车 */
export function addToCart(data) {
  return request({ url: '/product/cart/add', method: 'post', data })
}

/** 获取购物车列表 */
export function getCartList() {
  return request({ url: '/product/cart/list', method: 'get' })
}

/** 修改购物车商品数量 */
export function updateCart(data) {
  return request({ url: '/product/cart/update', method: 'put', data })
}

/** 删除购物车商品 */
export function deleteCartItem(id) {
  return request({ url: `/product/cart/${id}`, method: 'delete' })
}

/* ---- 订单 ---- */

/** 创建订单（单商品直接下单） */
export function createOrder(data) {
  return request({ url: '/product/order/create', method: 'post', data })
}

/** 从购物车批量下单 */
export function createOrderFromCart() {
  return request({ url: '/product/order/create-from-cart', method: 'post' })
}

/** 获取我的订单列表 */
export function getMyOrder() {
  return request({ url: '/product/order/my', method: 'get' })
}