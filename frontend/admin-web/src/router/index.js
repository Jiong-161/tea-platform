/**
 * 后台管理端路由
 * - /login 公开，其余路由需管理员登录
 * - 路由守卫校验 ADMIN_TOKEN
 */
import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '管理员登录 — 茶文·雅集' }
  },
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '仪表盘 — 管理后台', requiresAdmin: true }
  },
  {
    path: '/users',
    name: 'UserList',
    component: () => import('@/views/user/UserList.vue'),
    meta: { title: '用户管理 — 管理后台', requiresAdmin: true }
  },
  {
    path: '/articles',
    name: 'ArticleList',
    component: () => import('@/views/content/ArticleList.vue'),
    meta: { title: '文章管理 — 管理后台', requiresAdmin: true }
  },
  {
    path: '/exhibitions',
    name: 'ExhibitionList',
    component: () => import('@/views/exhibition/ExhibitionList.vue'),
    meta: { title: '展览管理 — 管理后台', requiresAdmin: true }
  },
  {
    path: '/exhibition/signups',
    name: 'SignupList',
    component: () => import('@/views/exhibition/SignupList.vue'),
    meta: { title: '报名记录 — 管理后台', requiresAdmin: true }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/product/ProductList.vue'),
    meta: { title: '商品管理 — 管理后台', requiresAdmin: true }
  },
  {
    path: '/product-form',
    name: 'ProductForm',
    component: () => import('@/views/product/ProductForm.vue'),
    meta: { title: '商品编辑 — 管理后台', requiresAdmin: true }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('@/views/product/OrderList.vue'),
    meta: { title: '订单管理 — 管理后台', requiresAdmin: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 全局守卫：管理页面需登录（Vue Router 4.x 使用 return）
router.beforeEach((to, from) => {
  document.title = to.meta.title || '管理后台'
  if (to.meta.requiresAdmin) {
    const token = localStorage.getItem('ADMIN_TOKEN')
    if (!token) {
      ElMessage.warning('请先登录管理员账号')
      return { path: '/login' }
    }
  }
  return true
})

/** 全局导航错误处理 */
router.onError((error) => {
  console.error('[路由错误]', error)
  if (/Loading chunk .* failed/i.test(error.message)) {
    window.location.reload()
  }
})

export default router