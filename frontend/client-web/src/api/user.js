/**
 * 用户相关 API 接口
 * 对应后端 user-service（网关路径 /user/**）
 */
import request from '@/utils/request'

/** 用户登录 */
export function login(data) {
  return request({ url: '/user/login', method: 'post', data })
}

/** 用户注册 */
export function register(data) {
  return request({ url: '/user/register', method: 'post', data })
}

/** 退出登录 */
export function logout() {
  return request({ url: '/user/logout', method: 'post' })
}

/** 获取当前登录用户信息 */
export function getCurrentUser() {
  return request({ url: '/user/current', method: 'get' })
}

/** 修改密码 */
export function changePassword(data) {
  return request({ url: '/user/password', method: 'post', data })
}