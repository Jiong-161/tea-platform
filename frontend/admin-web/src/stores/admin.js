/**
 * 管理员状态管理
 * 管理 Token + 用户信息 + role 校验
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getCurrentUser } from '@/api/admin-user'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('ADMIN_TOKEN') || '')
  const userInfo = ref(null)

  const isLogin = computed(() => !!token.value)
  const nickname = computed(() => userInfo.value?.nickname || '管理员')

  /**
   * 管理员登录 —— 登录后校验 role == 1
   */
  async function login(username, password) {
    const res = await loginApi({ username, password })
    const jwt = res.data || res

    // 解码 JWT 校验管理员角色
    try {
      const payload = JSON.parse(atob(jwt.split('.')[1]))
      if (payload.role !== 1) {
        throw new Error('当前账号无管理员权限，请联系系统管理员')
      }
    } catch (e) {
      if (e.message.includes('管理员')) throw e
      // JWT 解析失败也尝试登录
    }

    token.value = jwt
    localStorage.setItem('ADMIN_TOKEN', jwt)
    const userRes = await getCurrentUser()
    userInfo.value = userRes.data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('ADMIN_TOKEN')
  }

  /** 从 JWT 快速获取 role（无需网络请求） */
  function getRole() {
    if (!token.value) return 0
    try {
      const payload = JSON.parse(atob(token.value.split('.')[1]))
      return payload.role || 0
    } catch { return 0 }
  }

  return { token, userInfo, isLogin, nickname, login, logout, getRole }
})