/**
 * 用户状态管理（Pinia Store）
 * 管理登录态、Token、用户基本信息
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCurrentUser, logout as logoutApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  /** JWT Token */
  const token = ref(localStorage.getItem('TOKEN') || '')
  /** 用户信息对象 */
  const userInfo = ref(null)
  /** 是否已加载用户信息 */
  const loaded = ref(false)

  /** 是否已登录 */
  const isLogin = computed(() => !!token.value)

  /** 用户名 */
  const username = computed(() => userInfo.value?.username || '')
  /** 昵称 */
  const nickname = computed(() => userInfo.value?.nickname || username.value || '用户')

  /**
   * 登录：保存 Token 并拉取用户信息
   * @param {string} t - JWT Token 字符串
   */
  async function login(t) {
    token.value = t
    localStorage.setItem('TOKEN', t)
    // 登录后立即获取用户信息并刷新购物车
    try {
      await Promise.all([fetchUserInfo(), refreshCartAsync()])
    } catch (e) {
      console.warn('获取用户信息失败，将在后续操作中重试', e)
    }
  }

  /**
   * 退出登录：调用后端接口清除 Redis 中的 Token，然后清除本地状态
   */
  async function logout() {
    try {
      if (token.value) {
        await logoutApi()
      }
    } catch (e) {
      console.warn('退出登录接口调用失败', e)
    } finally {
      token.value = ''
      userInfo.value = null
      loaded.value = false
      localStorage.removeItem('TOKEN')
      // 清空购物车计数
      clearCartCount()
    }
  }

  /** 刷新购物车计数（延迟加载 cartStore 避免循环依赖） */
  async function refreshCartAsync() {
    try {
      const { useCartStore } = await import('@/stores/cart')
      useCartStore().refresh()
    } catch { /* 忽略 */ }
  }

  /** 清空购物车计数 */
  function clearCartCount() {
    try {
      // 动态导入避免循环依赖
      import('@/stores/cart').then(m => m.useCartStore().clear())
    } catch { /* 忽略 */ }
  }

  /**
   * 拉取当前登录用户信息
   */
  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getCurrentUser()
      if (res && res.data) {
        userInfo.value = res.data
        loaded.value = true
      }
    } catch (e) {
      // Token 无效时清除
      if (e.message && e.message.includes('401')) {
        token.value = ''
        userInfo.value = null
        localStorage.removeItem('TOKEN')
      }
      throw e
    }
  }

  /**
   * 确保用户信息已加载（懒加载，供页面组件调用）
   */
  async function ensureUserInfo() {
    if (!loaded.value && token.value) {
      await fetchUserInfo()
    }
  }

  return {
    token,
    userInfo,
    loaded,
    isLogin,
    username,
    nickname,
    login,
    logout,
    fetchUserInfo,
    ensureUserInfo
  }
})