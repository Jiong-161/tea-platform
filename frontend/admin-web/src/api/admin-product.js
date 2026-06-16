/** 后台 — 商品与订单管理 API */
import request from './request'

/* ---- 商品 ---- */
/** 商品分页列表 */
export function getProductList(params) { return request({ url: '/admin/product/list', method: 'get', params }) }
/** 获取单个商品详情（管理端，不受status过滤） */
export function getProductDetail(id) { return request({ url: `/admin/product/${id}`, method: 'get' }) }
/** 新增商品 */
export function createProduct(data) { return request({ url: '/admin/product', method: 'post', data }) }
/** 编辑商品 */
export function updateProduct(id, data) { return request({ url: `/admin/product/${id}`, method: 'put', data }) }
/** 上架/下架 */
export function toggleProductStatus(id, status) { return request({ url: `/admin/product/status/${id}`, method: 'put', params: { status } }) }
/** 删除商品 */
export function deleteProduct(id) { return request({ url: `/admin/product/${id}`, method: 'delete' }) }

/* ---- 订单 ---- */
/** 订单分页列表 */
export function getOrderList(params) { return request({ url: '/admin/order/list', method: 'get', params }) }
/** 修改订单状态 */
export function updateOrderStatus(id, status) { return request({ url: `/admin/order/status/${id}`, method: 'put', params: { status } }) }