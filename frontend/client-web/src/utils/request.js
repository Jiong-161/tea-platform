/**
 * Axios 请求封装
 * - 集成 json-bigint 解决 JS 大数精度丢失问题（storeAsString: true）
 * - 自动携带 JWT Token（请求头：Authorization: Bearer xxx）
 * - 统一处理后端 Result<T> 响应格式：{ code, message, data }
 * - 全局错误拦截与提示
 * - 登录失效自动跳转
 */
import axios from 'axios'
import JSONbig from 'json-bigint'
import { ElMessage } from 'element-plus'
import { deepCleanUrls } from '@/utils/urlCleaner'

/** 创建 Axios 实例 */
const request = axios.create({
  // 开发环境通过 Vite proxy 转发，生产环境通过 Nginx 或直接访问网关
  // .env.development 中配置 VITE_API_URL，生产环境在 .env.production 中配置
  baseURL: import.meta.env.VITE_API_URL || '',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
  // ====== 核心：响应转换管道 ======
  // 1. json-bigint 替代 JSON.parse，大数安全存储为字符串
  // 2. deepCleanUrls 清洗所有 cover/avatar 等URL字段中的空格，避免 %20 编码 404
  transformResponse: [function (data) {
    if (typeof data === 'string') {
      try {
        const parsed = JSONbig({ storeAsString: true }).parse(data)
        return deepCleanUrls(parsed)
      } catch (e) {
        // 解析失败返回原始数据（如非JSON格式的字符串）
        return data
      }
    }
    return data
  }]
})

/**
 * 请求拦截器 —— 自动携带 JWT Token
 */
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('TOKEN')
    if (token) {
      config.headers.Authorization = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('[请求发送失败]', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器 —— 统一解包后端 Result<T> 格式
 * 后端返回格式：{ code: 200, message: "success", data: 实际数据 }
 */
request.interceptors.response.use(
  res => {
    const body = res.data

    // 情况1：后端直接返回字符串（如 JWT Token）
    if (typeof body === 'string') {
      return body
    }

    // 情况2：标准 Result<T> 格式
    if (body && body.code !== undefined) {
      if (body.code === 200) {
        // 成功 → 返回整个 Result 对象，调用方可取 .data 获取实际数据
        return body
      } else if (body.code === 401) {
        // 登录失效 → 清除 Token 并跳转登录页
        localStorage.removeItem('TOKEN')
        ElMessage.error(body.message || '登录已失效，请重新登录')
        setTimeout(() => {
          window.location.href = '/login'
        }, 500)
        return Promise.reject(new Error(body.message || '未登录'))
      } else {
        // 其他业务错误（如密码错误、用户名已存在等）
        ElMessage.error(body.message || '操作失败')
        return Promise.reject(new Error(body.message || '操作失败'))
      }
    }

    // 情况3：非标准格式（兜底直接返回）
    return body
  },
  error => {
    // 网络错误 / HTTP 状态码异常（如 404、500）
    const msg = error.response?.data?.message || error.message || '网络异常，请稍后重试'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request