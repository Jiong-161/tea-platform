<!--
  后台管理布局：左侧边栏 + 顶部栏 + 内容区
-->
<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-brand">
        <span class="brand-icon">🍵</span>
        <div>
          <div class="brand-title">茶文化展览购销服务系统</div>
          <div class="brand-sub">管理后台</div>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1E3D32"
        text-color="rgba(255,255,255,0.7)"
        active-text-color="#D9B396"
        class="sidebar-menu"
      >
        <el-menu-item index="/">
          <el-icon><DataBoard /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/articles">
          <el-icon><Document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/exhibitions">
          <el-icon><Stamp /></el-icon>
          <span>展览管理</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><Tickets /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <a href="http://localhost:5173" target="_blank" class="goto-front">
          <el-icon><Back /></el-icon> 返回前台
        </a>
      </div>
    </aside>

    <!-- 右侧主体 -->
    <div class="main-area">
      <!-- 顶部栏 -->
      <header class="topbar">
        <h2 class="page-title">{{ pageTitle }}</h2>
        <div class="topbar-right">
          <el-dropdown trigger="hover" @command="handleCommand">
            <div class="user-area">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="user-name">{{ adminStore.nickname }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { useAdminStore } from '@/stores/admin'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/exhibition')) return '/exhibitions'
  return path
})

const pageTitle = computed(() => route.meta.title || '管理后台')

function handleCommand(cmd) {
  if (cmd === 'logout') {
    adminStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex; min-height: 100vh;
}

/* ---- 侧边栏 ---- */
.sidebar {
  width: var(--sidebar-width);
  background: var(--sidebar-bg);
  display: flex; flex-direction: column;
  flex-shrink: 0;
  position: fixed; top: 0; left: 0; bottom: 0;
  z-index: 100;
}

.sidebar-brand {
  padding: 20px 20px 16px;
  display: flex; align-items: center; gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}

.brand-icon { font-size: 28px; }
.brand-title { color: var(--tea-accent); font-family: var(--font-serif); font-size: 1.1rem; font-weight: 700; letter-spacing: 2px; }
.brand-sub { color: rgba(255,255,255,0.35); font-size: 0.7rem; letter-spacing: 1px; }

.sidebar-menu {
  flex: 1; border-right: none; padding-top: 8px;
}
.sidebar-menu .el-menu-item {
  height: 48px; line-height: 48px; margin: 2px 8px; border-radius: 8px;
}
.sidebar-menu .el-menu-item:hover {
  background: var(--sidebar-active) !important;
}
.sidebar-menu .el-menu-item.is-active {
  background: rgba(217,179,150,0.18) !important; border-right: 3px solid var(--tea-accent);
}

.sidebar-footer {
  padding: 16px 20px; border-top: 1px solid rgba(255,255,255,0.08);
}
.goto-front {
  color: rgba(255,255,255,0.45); font-size: 0.8rem; display: flex; align-items: center; gap: 6px;
  transition: color var(--transition-fast);
}
.goto-front:hover { color: var(--tea-accent); }

/* ---- 主区域 ---- */
.main-area {
  flex: 1; margin-left: var(--sidebar-width);
  display: flex; flex-direction: column; min-height: 100vh;
}

.topbar {
  height: 56px; background: var(--tea-white);
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 24px; border-bottom: 1px solid var(--tea-border);
  position: sticky; top: 0; z-index: 50;
}
.page-title { font-size: 1.1rem; font-family: var(--font-serif); color: var(--text-primary); }

.topbar-right { display: flex; align-items: center; gap: 16px; }
.user-area {
  display: flex; align-items: center; gap: 8px; cursor: pointer;
  padding: 4px 8px; border-radius: 20px; transition: background var(--transition-fast);
}
.user-area:hover { background: var(--tea-primary-light); }
.user-name { font-size: 0.85rem; color: var(--text-primary); }

.content { flex: 1; padding: 24px; }
</style>