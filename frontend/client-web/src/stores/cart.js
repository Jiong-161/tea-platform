/**
 * 购物车状态管理（Pinia Store）
 * 维护购物车商品数量，支持跨组件响应式更新
 * 当用户在商品列表/详情页添加商品后，Header 角标自动更新
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCartList } from '@/api/product'

export const useCartStore = defineStore('cart', () => {
  /** 购物车商品总数 */
  const count = ref(0)

  /**
   * 刷新购物车数量 —— 从后端获取最新数据
   * 在登录后、添加商品后、删除商品后调用
   */
  async function refresh() {
    try {
      const res = await getCartList()
      count.value = (res && res.data) ? res.data.length : 0
    } catch {
      // 未登录或请求失败时归零
      count.value = 0
    }
  }

  /** 清空购物车计数（退出登录时调用） */
  function clear() {
    count.value = 0
  }

  /** 手动增加计数（用于本地乐观更新） */
  function increment(n = 1) {
    count.value += n
  }

  /** 手动减少计数 */
  function decrement(n = 1) {
    count.value = Math.max(0, count.value - n)
  }

  return { count, refresh, clear, increment, decrement }
})