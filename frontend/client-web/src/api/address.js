/**
 * 收货地址相关 API 接口
 * 对应后端 user-service（网关路径 /user/address/**）
 */
import request from '@/utils/request'

/** 获取收货地址列表 */
export function getAddressList() {
  return request({ url: '/user/address/list', method: 'get' })
}

/** 新增收货地址 */
export function addAddress(data) {
  return request({ url: '/user/address/add', method: 'post', data })
}

/** 修改收货地址 */
export function updateAddress(id, data) {
  return request({ url: '/user/address/update', method: 'put', params: { id }, data })
}

/** 删除收货地址 */
export function deleteAddress(id) {
  return request({ url: `/user/address/${id}`, method: 'delete' })
}

/** 设置默认收货地址 */
export function setDefaultAddress(id) {
  return request({ url: `/user/address/default/${id}`, method: 'put' })
}