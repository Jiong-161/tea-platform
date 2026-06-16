<!--
  登录页 — 新中式茶文化风格
  居中卡片式布局，左侧装饰茶文化元素，右侧登录表单
-->
<template>
  <div class="auth-page">
    <!-- 背景装饰 -->
    <div class="auth-bg">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="auth-card">
      <!-- 左侧装饰面板 -->
      <div class="auth-panel">
        <div class="panel-content">
          <div class="panel-icon">
            <el-icon :size="48"><CoffeeCup /></el-icon>
          </div>
          <h2>茶文·展览·商品</h2>
          <p class="panel-desc">一盏清茶，品味千年风雅</p>
          <div class="panel-divider"></div>
          <p class="panel-sub">还没有账号？</p>
          <router-link to="/register" class="panel-link">立即注册 →</router-link>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="auth-form">
        <h3 class="form-title">用户登录</h3>
        <p class="form-subtitle">欢迎回来，续写您的茶缘</p>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @submit.prevent="handleLogin"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              native-type="submit"
              :loading="loading"
              class="submit-btn"
              round
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 返回首页 -->
    <div class="back-home">
      <router-link to="/">
        <el-icon><ArrowLeft /></el-icon> 返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

/** 表单校验规则 */
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4到20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6到20位', trigger: 'blur' }
  ]
}

/** 登录处理 */
async function handleLogin() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    const res = await login({ username: form.username, password: form.password })
    // 后端登录接口返回 data 为 JWT Token 字符串
    const token = res.data || res
    userStore.login(token)
    ElMessage.success('登录成功，欢迎回来！')
    // 如果有重定向参数则跳转，否则到首页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    console.error('[登录] 请求失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ---- 整体布局 ---- */
.auth-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--tea-bg);
  overflow: hidden;
}

/* ---- 背景装饰圈 ---- */
.auth-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
}

.bg-circle-1 {
  width: 600px;
  height: 600px;
  background: var(--tea-primary);
  top: -200px;
  right: -100px;
}

.bg-circle-2 {
  width: 400px;
  height: 400px;
  background: var(--tea-accent);
  bottom: -150px;
  left: -80px;
}

.bg-circle-3 {
  width: 200px;
  height: 200px;
  background: var(--tea-secondary);
  top: 40%;
  left: 10%;
}

/* ---- 登录卡片 ---- */
.auth-card {
  position: relative;
  display: flex;
  width: 800px;
  max-width: 95vw;
  min-height: 500px;
  background: var(--tea-white);
  border-radius: var(--radius-xl);
  box-shadow: 0 20px 60px rgba(46, 91, 75, 0.12);
  overflow: hidden;
}

/* ---- 左侧装饰面板 ---- */
.auth-panel {
  width: 45%;
  background: linear-gradient(160deg, var(--tea-primary), #1E3D32);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: var(--space-xl);
}

.panel-content {
  color: #FFF;
}

.panel-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto var(--space-lg);
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.panel-content h2 {
  color: #FFF;
  font-family: var(--font-serif);
  font-size: 1.6rem;
  letter-spacing: 4px;
  margin-bottom: var(--space-sm);
}

.panel-desc {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
}

.panel-divider {
  width: 40px;
  height: 2px;
  background: var(--tea-accent);
  margin: var(--space-lg) auto;
}

.panel-sub {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.55);
  margin-bottom: var(--space-sm);
}

.panel-link {
  color: var(--tea-accent);
  font-weight: 500;
  font-size: 0.95rem;
  border-bottom: 1px solid transparent;
  transition: all var(--transition-fast);
}

.panel-link:hover {
  border-bottom-color: var(--tea-accent);
  color: var(--tea-accent);
}

/* ---- 右侧表单 ---- */
.auth-form {
  flex: 1;
  padding: var(--space-xl) var(--space-lg);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-title {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.form-subtitle {
  font-size: 0.85rem;
  color: var(--text-secondary);
  margin-bottom: var(--space-lg);
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 1rem;
  letter-spacing: 4px;
  margin-top: var(--space-sm);
}

/* ---- 返回首页链接 ---- */
.back-home {
  position: absolute;
  bottom: var(--space-lg);
  text-align: center;
}

.back-home a {
  color: var(--text-secondary);
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.back-home a:hover {
  color: var(--tea-primary);
}

/* ---- 响应式 ---- */
@media (max-width: 768px) {
  .auth-panel {
    display: none;
  }
  .auth-card {
    flex-direction: column;
  }
  .auth-form {
    padding: var(--space-lg);
  }
}
</style>