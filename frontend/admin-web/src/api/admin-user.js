/** 后台 — 用户管理 API */
import request from './request'

/** 用户分页列表 */
export function getUserList(params) { return request({ url: '/admin/user/list', method: 'get', params }) }
/** 用户详情 */
export function getUserDetail(id) { return request({ url: `/admin/user/${id}`, method: 'get' }) }
/** 启用/禁用用户 */
export function toggleUserStatus(id, status) { return request({ url: `/admin/user/status/${id}`, method: 'put', params: { status } }) }
/** 删除用户 */
export function deleteUser(id) { return request({ url: `/admin/user/${id}`, method: 'delete' }) }

/* 复用用户端登录接口 */
export function login(data) { return request({ url: '/user/login', method: 'post', data }) }
export function getCurrentUser() { return request({ url: '/user/current', method: 'get' }) }