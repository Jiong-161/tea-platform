/**
 * Vue Router 路由配置
 * - 基于 createWebHistory 的 SPA 路由
 * - 全局前置守卫：需要登录的页面未登录时自动跳转登录页
 */
import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

/**
 * 路由表
 * meta.requiresAuth: true 表示该页面需要登录才能访问
 */
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页 — 茶文化交流平台' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录 — 茶文化交流平台', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册 — 茶文化交流平台', guest: true }
  },
  {
    path: '/articles',
    name: 'ArticleList',
    component: () => import('@/views/ArticleList.vue'),
    meta: { title: '茶文化文章 — 茶文化交流平台' }
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('@/views/ArticleDetail.vue'),
    meta: { title: '文章详情 — 茶文化交流平台' }
  },
  {
    path: '/exhibitions',
    name: 'ExhibitionList',
    component: () => import('@/views/ExhibitionList.vue'),
    meta: { title: '茶文化展览 — 茶文化交流平台' }
  },
  {
    path: '/exhibition/:id',
    name: 'ExhibitionDetail',
    component: () => import('@/views/ExhibitionDetail.vue'),
    meta: { title: '展览详情 — 茶文化交流平台' }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/ProductList.vue'),
    meta: { title: '精选茶叶商品 — 茶文化交流平台' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetail.vue'),
    meta: { title: '商品详情 — 茶文化交流平台' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
    meta: { title: '购物车 — 茶文化交流平台', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心 — 茶文化交流平台', requiresAuth: true }
  },
  {
    path: '/address',
    name: 'Address',
    component: () => import('@/views/Address.vue'),
    meta: { title: '收货地址管理 — 茶文化交流平台', requiresAuth: true }
  },
  {
    path: '/my-exhibition',
    name: 'MyExhibition',
    component: () => import('@/views/MyExhibition.vue'),
    meta: { title: '我的展览报名 — 茶文化交流平台', requiresAuth: true }
  },
  {
    path: '/my-order',
    name: 'MyOrder',
    component: () => import('@/views/MyOrder.vue'),
    meta: { title: '我的订单 — 茶文化交流平台', requiresAuth: true }
  },
  {
    // 404 兜底
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // 路由切换时滚动到顶部
  scrollBehavior() {
    return { top: 0 }
  }
})

/**
 * 全局前置守卫
 * - 设置页面标题
 * - 检查需要登录的路由，未登录则跳转到登录页
 * - Vue Router 4.x 使用 return 而非 next()（避免弃用警告）
 */
router.beforeEach((to, from) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  // 需要登录的路由 — 检查 localStorage 中的 Token
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('TOKEN')
    if (!token) {
      ElMessage.warning('请先登录')
      return { path: '/login', query: { redirect: to.fullPath } }
    }
  }

  // 已登录用户访问登录/注册页 — 重定向到首页
  if (to.meta.guest) {
    const token = localStorage.getItem('TOKEN')
    if (token) {
      return { path: '/' }
    }
  }

  return true
})

/**
 * 全局导航错误处理：捕获 chunk 加载失败等异常，避免页面卡死
 */
router.onError((error) => {
  console.error('[路由错误]', error)
  const chunkFailedPattern = /Loading chunk .* failed/i
  if (chunkFailedPattern.test(error.message)) {
    // chunk 加载失败通常是因为版本更新，强制刷新页面
    window.location.reload()
  }
})

export default router