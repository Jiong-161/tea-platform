<!--
  个人中心页 — 用户信息展示与修改密码
-->
<template>
  <MainLayout>
    <div class="page-banner">
      <div class="container">
        <h1 class="banner-title">个人中心</h1>
        <p class="banner-sub">管理您的账户信息</p>
      </div>
    </div>

    <div class="container page-section">
      <div class="profile-grid">
        <!-- 左侧：用户信息卡片 -->
        <div class="profile-card">
          <div class="profile-avatar">
            <el-avatar :size="80" :icon="UserFilled" />
          </div>
          <h2 class="profile-nickname">{{ userStore.nickname || '用户' }}</h2>
          <p class="profile-username">@{{ userStore.username }}</p>
          <el-divider />
          <div class="profile-details">
            <div class="detail-item">
              <span class="detail-label">用户角色</span>
              <span class="detail-value">{{ roleText }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">注册时间</span>
              <span class="detail-value">{{ formatDate(userInfo.createTime) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">手机号</span>
              <span class="detail-value">{{ userInfo.phone || '未绑定' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">邮箱</span>
              <span class="detail-value">{{ userInfo.email || '未绑定' }}</span>
            </div>
          </div>
        </div>

        <!-- 右侧：快捷入口 + 修改密码 -->
        <div class="profile-right">
          <!-- 快捷入口 -->
          <div class="quick-links">
            <h3 class="section-label">快捷入口</h3>
            <div class="links-grid">
              <router-link to="/cart" class="link-card">
                <el-icon :size="28"><ShoppingCart /></el-icon>
                <span>我的购物车</span>
              </router-link>
              <router-link to="/my-order" class="link-card">
                <el-icon :size="28"><Tickets /></el-icon>
                <span>我的订单</span>
              </router-link>
              <router-link to="/my-exhibition" class="link-card">
                <el-icon :size="28"><Stamp /></el-icon>
                <span>我的报名</span>
              </router-link>
              <router-link to="/address" class="link-card">
                <el-icon :size="28"><Location /></el-icon>
                <span>收货地址</span>
              </router-link>
            </div>
          </div>

          <!-- 修改密码 -->
          <div class="password-card">
            <h3 class="section-label">修改密码</h3>
            <el-form
              ref="formRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-position="top"
            >
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入旧密码"
                  show-password
                />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码（6-20位）"
                  show-password
                />
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  @click="handleChangePassword"
                  :loading="changingPwd"
                >
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import { useUserStore } from '@/stores/user'
import { changePassword } from '@/api/user'

const userStore = useUserStore()
const changingPwd = ref(false)
const formRef = ref(null)

// 确保用户信息已加载
onMounted(async () => {
  await userStore.ensureUserInfo()
})

const userInfo = computed(() => userStore.userInfo || {})

const roleText = computed(() => {
  return userInfo.value.role === 1 ? '管理员' : '普通用户'
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (_rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6到20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

function formatDate(dateStr) {
  if (!dateStr) return '未知'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

async function handleChangePassword() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  changingPwd.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    // 清除登录态，跳转到登录页
    await userStore.logout()
    window.location.href = '/login'
  } catch (e) {
    console.error('[修改密码] 操作失败:', e)
  } finally {
    changingPwd.value = false
  }
}
</script>

<style scoped>
/* 使用全局 .page-banner 样式 */

.profile-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: var(--space-lg);
}

/* 用户信息卡片 */
.profile-card {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  padding: var(--space-xl);
  text-align: center;
}

.profile-avatar {
  margin-bottom: var(--space-md);
}

.profile-nickname {
  font-family: var(--font-serif);
  font-size: 1.3rem;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.profile-username {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 0.9rem;
}

.detail-label {
  color: var(--text-secondary);
}

.detail-value {
  color: var(--text-primary);
  font-weight: 500;
}

/* 右侧 */
.profile-right {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.section-label {
  font-family: var(--font-serif);
  font-size: 1.15rem;
  color: var(--text-primary);
  margin-bottom: var(--space-md);
}

/* 快捷入口 */
.quick-links {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  padding: var(--space-lg);
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

.link-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: var(--space-md);
  border-radius: var(--radius-md);
  background: var(--tea-bg);
  color: var(--text-primary);
  transition: all var(--transition-fast);
  text-decoration: none;
  text-align: center;
}

.link-card:hover {
  background: var(--tea-primary-light);
  color: var(--tea-primary);
  transform: translateY(-2px);
}

.link-card span {
  font-size: 0.85rem;
}

/* 修改密码 */
.password-card {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  padding: var(--space-lg);
}

@media (max-width: 768px) {
  .profile-grid { grid-template-columns: 1fr; }
  .links-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>