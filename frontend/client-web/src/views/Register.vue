<!--
  注册页 — 新中式茶文化风格
  与登录页一致的布局，提供用户注册功能
-->
<template>
  <div class="auth-page">
    <!-- 背景装饰 -->
    <div class="auth-bg">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <!-- 注册卡片 -->
    <div class="auth-card">
      <!-- 左侧装饰面板 -->
      <div class="auth-panel">
        <div class="panel-content">
          <div class="panel-icon">
            <el-icon :size="48"><CoffeeCup /></el-icon>
          </div>
          <h2>茶文·雅集</h2>
          <p class="panel-desc">以茶会友，开启风雅之旅</p>
          <div class="panel-divider"></div>
          <p class="panel-sub">已有账号？</p>
          <router-link to="/login" class="panel-link">返回登录 →</router-link>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="auth-form">
        <h3 class="form-title">用户注册</h3>
        <p class="form-subtitle">加入我们，品味千年茶韵</p>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @submit.prevent="handleRegister"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="4-20位字母或数字"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item label="昵称" prop="nickname">
            <el-input
              v-model="form.nickname"
              placeholder="给自己取一个雅致的昵称"
              :prefix-icon="EditPen"
              clearable
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="6-20位密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleRegister"
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
              {{ loading ? '注册中...' : '注 册' }}
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, EditPen } from '@element-plus/icons-vue'
import { register } from '@/api/user'

const router = useRouter()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

/** 自定义确认密码校验 */
const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

/** 表单校验规则 */
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4到20位', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6到20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

/** 注册处理 */
async function handleRegister() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    await register({
      username: form.username,
      password: form.password,
      nickname: form.nickname
    })
    ElMessage.success('注册成功！请登录')
    router.push('/login')
  } catch (e) {
    console.error('[注册] 请求失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ---- 与登录页共享样式 ---- */
.auth-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--tea-bg);
  overflow: hidden;
}

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

.auth-card {
  position: relative;
  display: flex;
  width: 800px;
  max-width: 95vw;
  min-height: 540px;
  background: var(--tea-white);
  border-radius: var(--radius-xl);
  box-shadow: 0 20px 60px rgba(46, 91, 75, 0.12);
  overflow: hidden;
}

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

.auth-form {
  flex: 1;
  padding: var(--space-lg) var(--space-lg);
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
  margin-bottom: var(--space-md);
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 1rem;
  letter-spacing: 4px;
  margin-top: var(--space-sm);
}

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

@media (max-width: 768px) {
  .auth-panel { display: none; }
  .auth-card { flex-direction: column; min-height: auto; }
  .auth-form { padding: var(--space-lg); }
}
</style>