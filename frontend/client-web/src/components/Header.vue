<!--
  全局顶部导航栏 — 新中式茶文化风格
  包含：Logo、导航菜单、用户状态区（未登录显示登录/注册按钮，已登录显示头像下拉菜单）
-->
<template>
  <header class="tea-header">
    <div class="header-inner container">
      <!-- Logo 区域 -->
      <router-link to="/" class="logo-area">
        <div class="logo-icon">
          <el-icon :size="28"><CoffeeCup /></el-icon>
        </div>
        <div class="logo-text">
          <span class="logo-title">茶文·茶文化品鉴购销服务平台</span>
          <span class="logo-subtitle">Tea Culture Platform</span>
        </div>
      </router-link>

      <!-- 主导航菜单 -->
      <nav class="nav-menu pc-nav">
        <router-link to="/" class="nav-item" active-class="nav-active">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </router-link>
        <router-link to="/articles" class="nav-item" active-class="nav-active">
          <el-icon><Document /></el-icon>
          <span>茶文化</span>
        </router-link>
        <router-link to="/exhibitions" class="nav-item" active-class="nav-active">
          <el-icon><Stamp /></el-icon>
          <span>展览</span>
        </router-link>
        <router-link to="/products" class="nav-item" active-class="nav-active">
          <el-icon><Goods /></el-icon>
          <span>商品</span>
        </router-link>
      </nav>

      <!-- 移动端汉堡按钮 -->
      <button class="mobile-menu-btn" @click="mobileMenuOpen = !mobileMenuOpen" aria-label="菜单">
        <span class="hamburger-line" :class="{ open: mobileMenuOpen }"></span>
      </button>

      <!-- 右侧操作区 -->
      <div class="header-actions">
        <!-- 已登录：显示购物车图标 + 用户下拉菜单 -->
        <template v-if="userStore.isLogin">
          <router-link to="/cart" class="cart-btn" title="购物车">
            <el-badge :value="cartStore.count" :hidden="cartStore.count === 0" :max="99">
              <el-icon :size="22"><ShoppingCart /></el-icon>
            </el-badge>
          </router-link>

          <el-dropdown trigger="hover" @command="handleCommand">
            <div class="user-area">
              <el-avatar :size="36" :icon="UserFilled" class="user-avatar" />
              <span class="user-name">{{ userStore.nickname }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="myExhibition">
                  <el-icon><Stamp /></el-icon>我的报名
                </el-dropdown-item>
                <el-dropdown-item command="myOrder">
                  <el-icon><Tickets /></el-icon>我的订单
                </el-dropdown-item>
                <el-dropdown-item command="address">
                  <el-icon><Location /></el-icon>收货地址
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <!-- 未登录：显示登录/注册按钮 -->
        <template v-else>
          <router-link to="/login" class="btn-text">登录</router-link>
          <router-link to="/register" class="btn-primary-sm">注册</router-link>
        </template>
      </div>
    </div>

    <!-- 移动端抽屉菜单 -->
    <Transition name="slide-down">
      <div v-if="mobileMenuOpen" class="mobile-drawer">
        <nav class="mobile-nav">
          <router-link to="/" class="mobile-nav-item" @click="mobileMenuOpen = false">
            <el-icon><HomeFilled /></el-icon>首页
          </router-link>
          <router-link to="/articles" class="mobile-nav-item" @click="mobileMenuOpen = false">
            <el-icon><Document /></el-icon>茶文化
          </router-link>
          <router-link to="/exhibitions" class="mobile-nav-item" @click="mobileMenuOpen = false">
            <el-icon><Stamp /></el-icon>展览
          </router-link>
          <router-link to="/products" class="mobile-nav-item" @click="mobileMenuOpen = false">
            <el-icon><Goods /></el-icon>商品
          </router-link>
        </nav>
      </div>
    </Transition>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const mobileMenuOpen = ref(false)

onMounted(() => {
  cartStore.refresh()
})

/** 下拉菜单命令处理 */
async function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'myExhibition':
      router.push('/my-exhibition')
      break
    case 'myOrder':
      router.push('/my-order')
      break
    case 'address':
      router.push('/address')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/')
      } catch {
        // 用户取消
      }
      break
  }
}
</script>

<style scoped>
/* ---- 顶栏整体 ---- */
.tea-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--tea-border);
  box-shadow: 0 2px 16px rgba(46, 91, 75, 0.06);
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

/* ---- Logo ---- */
.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--tea-primary), var(--tea-secondary));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFF;
}

.logo-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.logo-title {
  font-family: var(--font-serif);
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--tea-primary);
  letter-spacing: 2px;
}

.logo-subtitle {
  font-size: 0.65rem;
  color: var(--tea-secondary);
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* ---- 导航菜单 ---- */
.nav-menu {
  display: flex;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border-radius: 8px;
  font-size: 0.95rem;
  color: var(--text-regular);
  text-decoration: none;
  transition: all var(--transition-fast);
  position: relative;
}

.nav-item:hover {
  color: var(--tea-primary);
  background: var(--tea-primary-light);
}

.nav-active {
  color: var(--tea-primary);
  background: var(--tea-primary-light);
  font-weight: 600;
}

/* ---- 右侧操作区 ---- */
.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.cart-btn {
  color: var(--text-regular);
  padding: 6px;
  border-radius: 8px;
  transition: all var(--transition-fast);
  display: flex;
}

.cart-btn:hover {
  color: var(--tea-primary);
  background: var(--tea-primary-light);
}

.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: 24px;
  transition: all var(--transition-fast);
}

.user-area:hover {
  background: var(--tea-primary-light);
}

.user-avatar {
  background: linear-gradient(135deg, var(--tea-accent), var(--tea-primary));
}

.user-name {
  font-size: 0.9rem;
  color: var(--text-primary);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow-icon {
  font-size: 0.75rem;
  color: var(--text-secondary);
  transition: transform var(--transition-fast);
}

/* 未登录按钮 */
.btn-text {
  color: var(--text-primary);
  font-size: 0.9rem;
  padding: 6px 16px;
  border-radius: 6px;
  transition: all var(--transition-fast);
}

.btn-text:hover {
  color: var(--tea-primary);
  background: var(--tea-primary-light);
}

.btn-primary-sm {
  background: var(--tea-primary);
  color: #FFF;
  font-size: 0.9rem;
  font-weight: 500;
  padding: 8px 20px;
  border-radius: 6px;
  transition: all var(--transition-normal);
}

.btn-primary-sm:hover {
  background: var(--tea-primary-hover);
  box-shadow: var(--tea-shadow);
  color: #FFF;
}

/* ---- 响应式 ---- */
@media (max-width: 768px) {
  .pc-nav { display: none !important; }
  .logo-subtitle { display: none; }
  .mobile-menu-btn { display: flex; }
}

/* ---- 移动端汉堡按钮 ---- */
.mobile-menu-btn {
  display: none;
  flex-direction: column;
  justify-content: center;
  gap: 5px;
  width: 32px;
  height: 32px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
}

.hamburger-line {
  display: block;
  width: 20px;
  height: 2px;
  background: var(--text-primary);
  border-radius: 2px;
  transition: all 0.3s ease;
}

.hamburger-line.open:nth-child(1) {
  transform: rotate(45deg) translate(5px, 5px);
}
.hamburger-line.open:nth-child(2) {
  opacity: 0;
}
.hamburger-line.open:nth-child(3) {
  transform: rotate(-45deg) translate(5px, -5px);
}

/* ---- 移动端抽屉 ---- */
.mobile-drawer {
  position: absolute;
  top: 64px;
  left: 0;
  right: 0;
  background: var(--tea-white);
  box-shadow: var(--shadow-lg);
  border-top: 1px solid var(--tea-border);
  z-index: var(--z-dropdown);
}

.mobile-nav {
  display: flex;
  flex-direction: column;
  padding: var(--space-md);
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  font-size: 1rem;
  color: var(--text-primary);
  border-radius: var(--radius-sm);
  text-decoration: none;
  transition: all var(--transition-fast);
}

.mobile-nav-item:hover,
.mobile-nav-item.router-link-active {
  background: var(--tea-primary-light);
  color: var(--tea-primary);
  font-weight: 600;
}

/* 抽屉动画 */
.slide-down-enter-active {
  transition: all 0.3s ease;
}
.slide-down-leave-active {
  transition: all 0.2s ease;
}
.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>