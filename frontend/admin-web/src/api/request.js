/**
 * Axios 请求封装 — 后台管理端
 * - 集成 json-bigint 解决 JS 大数精度丢失问题（storeAsString: true）
 * - Token 存 localStorage key: ADMIN_TOKEN
 */
import axios from 'axios'
import JSONbig from 'json-bigint'
import { ElMessage } from 'element-plus'
import { deepCleanUrls } from '@/utils/urlCleaner'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
  // ====== 核心：响应转换管道 ======
  // 1. json-bigint 解析大数安全存储
  // 2. deepCleanUrls 清洗URL中的空格 → 避免 %20 编码 404
  transformResponse: [function (data) {
    if (typeof data === 'string') {
      try {
        const parsed = JSONbig({ storeAsString: true }).parse(data)
        return deepCleanUrls(parsed)
      } catch (e) {
        return data
      }
    }
    return data
  }]
})

// 请求拦截：自动携带管理员 Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('ADMIN_TOKEN')
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
}, error => Promise.reject(error))

// 响应拦截：解包 Result<T>
request.interceptors.response.use(res => {
  const body = res.data
  if (typeof body === 'string') return body
  if (body?.code === 200) return body
  if (body?.code === 401) {
    localStorage.removeItem('ADMIN_TOKEN')
    ElMessage.error(body.message || '登录已失效')
    setTimeout(() => { window.location.href = '/login' }, 500)
    return Promise.reject(new Error('未授权'))
  }
  ElMessage.error(body?.message || '操作失败')
  return Promise.reject(new Error(body?.message))
}, error => {
  const msg = error.response?.data?.message || error.message || '网络异常'
  ElMessage.error(msg)
  return Promise.reject(error)
})

export default request