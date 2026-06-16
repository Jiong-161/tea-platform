<!-- 管理员登录页 -->
<template>
  <div class="admin-login">
    <div class="login-card">
      <div class="login-header">
        <span class="login-icon">🍵</span>
        <h1>茶文·雅集</h1>
        <p>管理后台</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="管理员用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" round class="login-btn">
            {{ loading ? '登录中...' : '管理员登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <a href="http://localhost:5173" target="_blank">← 返回前台</a>
      </div>
    </div>
    <p class="login-tip">仅限管理员账号登录（role=1）</p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const adminStore = useAdminStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: 'admin', password: '123456' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  loading.value = true
  try {
    await adminStore.login(form.username, form.password)
    ElMessage.success('登录成功，欢迎回来！')
    router.push('/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login {
  min-height: 100vh; display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  background: linear-gradient(160deg, #1E3D32 0%, #2E5B4B 50%, #3D7A65 100%);
}
.login-card {
  width: 400px; background: #FFF; border-radius: 16px; padding: 40px 36px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}
.login-header { text-align: center; margin-bottom: 32px; }
.login-icon { font-size: 48px; }
.login-header h1 { font-family: var(--font-serif); color: var(--tea-primary); font-size: 1.5rem; letter-spacing: 4px; margin: 8px 0 4px; }
.login-header p { color: var(--text-secondary); font-size: 0.85rem; }
.login-btn { width: 100%; height: 44px; letter-spacing: 2px; }
.login-footer { text-align: center; margin-top: 16px; }
.login-footer a { color: var(--text-secondary); font-size: 0.85rem; }
.login-tip { color: rgba(255,255,255,0.4); margin-top: 20px; font-size: 0.75rem; }
</style>