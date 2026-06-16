<!--
  展览详情页 — 国博骨架 + 茶绿色系
  布局：Hero大图 / 侧边栏信息 / 正文 — 与首页配色完全统一
-->
<template>
  <MainLayout>
    <div class="container detail-page" v-loading="loading">
      <!-- ====== 加载中 ====== -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      </div>

      <!-- ====== 错误状态 ====== -->
      <ErrorState v-else-if="error" :message="error" @retry="loadExhibition" />

      <template v-else-if="exhibition.id">
        <!-- ====== 面包屑 ====== -->
        <nav class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span class="breadcrumb-sep">/</span>
          <router-link to="/exhibitions">展览</router-link>
          <span class="breadcrumb-sep">/</span>
          <span class="breadcrumb-current">{{ exhibition.title }}</span>
        </nav>

        <!-- ====== Hero 大图区 ====== -->
        <div class="hero-section">
          <div class="hero-image-wrap">
            <TeaImage :src="exhibition.cover" :alt="exhibition.title" img-class="hero-image" />
          </div>
          <!-- 浮动信息条 -->
          <div class="hero-overlay">
            <span class="overlay-status" :class="statusClass(exhibition.status)">
              {{ getStatusText(exhibition.status) }}
            </span>
            <h1 class="overlay-title">{{ exhibition.title }}</h1>
          </div>
        </div>

        <!-- ====== 主体内容区 ====== -->
        <div class="detail-body">
          <!-- 左侧：基本信息 -->
          <aside class="detail-sidebar">
            <div class="info-card">
              <h3 class="info-card-title">展览信息</h3>
              <ul class="info-list">
                <li class="info-row">
                  <span class="info-label">展览地点</span>
                  <span class="info-value">{{ exhibition.location || '待定' }}</span>
                </li>
                <li class="info-row">
                  <span class="info-label">开始时间</span>
                  <span class="info-value">{{ formatDate(exhibition.startTime) }}</span>
                </li>
                <li class="info-row">
                  <span class="info-label">结束时间</span>
                  <span class="info-value">{{ formatDate(exhibition.endTime) }}</span>
                </li>
                <li class="info-row">
                  <span class="info-label">展览状态</span>
                  <span class="info-value" :class="'status-text-' + statusClass(exhibition.status)">
                    {{ getStatusText(exhibition.status) }}
                  </span>
                </li>
              </ul>
            </div>

            <!-- 报名操作区 -->
            <div class="action-area">
              <button
                v-if="exhibition.status === 1"
                class="signup-btn"
                :disabled="signingUp"
                @click="handleSignup"
              >
                {{ signingUp ? '报名中...' : '预约参观' }}
              </button>
              <p v-else class="signup-disabled-text">
                {{ exhibition.status === 0 ? '展览尚未开始，敬请期待' : '展览已结束' }}
              </p>
            </div>
          </aside>

          <!-- 右侧：展览详情描述 -->
          <article class="detail-main" v-if="exhibition.description">
            <h2 class="section-title">展览详情</h2>
            <div class="desc-content">{{ exhibition.description }}</div>
          </article>

          <article class="detail-main" v-else>
            <h2 class="section-title">展览详情</h2>
            <p class="no-desc">暂无详细介绍</p>
          </article>
        </div>
      </template>

      <!-- ====== 展览不存在 ====== -->
      <div v-else class="empty-container">
        <el-icon :size="48"><Stamp /></el-icon>
        <p class="empty-text">展览不存在或已被删除</p>
        <router-link to="/exhibitions" class="empty-link">← 返回展览列表</router-link>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import TeaImage from '@/components/TeaImage.vue'
import { getExhibitionDetail, signupExhibition } from '@/api/exhibition'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const exhibition = ref({})
const loading = ref(true)
const error = ref('')
const signingUp = ref(false)

onMounted(() => {
  loadExhibition()
})

async function loadExhibition() {
  error.value = ''
  loading.value = true
  try {
    const res = await getExhibitionDetail(route.params.id)
    if (res && res.data) {
      exhibition.value = res.data
    } else {
      exhibition.value = {}
    }
  } catch {
    error.value = '展览详情加载失败，请检查后端服务是否启动'
    exhibition.value = {}
  } finally {
    loading.value = false
  }
}

function getStatusText(status) {
  const map = { 0: '即将展出', 1: '正在展出', 2: '已结束', 3: '已取消' }
  return map[status] || ''
}

function statusClass(status) {
  if (status === 1) return 'status-active'
  if (status === 0) return 'status-upcoming'
  return 'status-ended'
}

function formatDate(dateStr) {
  if (!dateStr) return '待定'
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

async function handleSignup() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录后再报名')
    router.push('/login')
    return
  }
  signingUp.value = true
  try {
    await signupExhibition({ exhibitionId: exhibition.value.id })
    ElMessage.success('报名成功！')
  } catch {
    // 错误已在拦截器处理
  } finally {
    signingUp.value = false
  }
}
</script>

<style scoped>
/* ============================================================
   国博骨架 + 茶绿色系 — 与首页完全统一
   ============================================================ */

.detail-page {
  padding-top: 32px;
  padding-bottom: 80px;
}

/* ----- 面包屑 ----- */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-bottom: 40px;
  letter-spacing: 0.5px;
}

.breadcrumb a {
  color: var(--text-secondary);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.breadcrumb a:hover {
  color: var(--tea-primary);
}

.breadcrumb-sep {
  color: var(--tea-border);
}

.breadcrumb-current {
  color: var(--text-primary);
}

/* ----- Hero 大图区 ----- */
.hero-section {
  position: relative;
  margin-bottom: 48px;
  border-radius: var(--radius-md);
  overflow: hidden;
}

.hero-image-wrap {
  width: 100%;
  aspect-ratio: 21 / 9;
  background: var(--tea-primary-light);
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.hero-placeholder {
  width: 100%;
  height: 100%;
  display: none;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--tea-primary-light), var(--tea-accent-light));
  color: var(--tea-primary);
  opacity: 0.5;
}

/* 浮动信息条 */
.hero-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px 48px 32px;
  background: linear-gradient(to top, rgba(46, 59, 43, 0.50) 0%, transparent 100%);
}

.overlay-status {
  display: inline-block;
  font-size: 0.75rem;
  color: #FFF;
  padding: 3px 12px;
  letter-spacing: 1px;
  margin-bottom: 10px;
}

.overlay-status.status-active {
  background: rgba(46, 91, 75, 0.85);
}

.overlay-status.status-upcoming {
  background: rgba(143, 151, 121, 0.75);
}

.overlay-status.status-ended {
  background: rgba(181, 191, 175, 0.65);
}

.overlay-title {
  font-family: var(--font-serif);
  font-size: 2rem;
  font-weight: 700;
  color: #FFF;
  letter-spacing: 4px;
  margin: 0;
  line-height: 1.4;
}

/* ----- 主体内容区（侧边栏 + 正文） ----- */
.detail-body {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 60px;
}

/* 左侧信息栏 */
.detail-sidebar {
  flex-shrink: 0;
}

.info-card {
  background: var(--tea-white);
  border: 1px solid var(--tea-border);
  border-radius: var(--radius-md);
  padding: 28px 24px;
  margin-bottom: 24px;
}

.info-card-title {
  font-family: var(--font-serif);
  font-size: 1rem;
  font-weight: 600;
  color: var(--tea-primary);
  letter-spacing: 2px;
  margin: 0 0 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--tea-border);
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 10px 0;
  border-bottom: 1px dashed var(--tea-border);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 0.82rem;
  color: var(--text-secondary);
  flex-shrink: 0;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 0.85rem;
  color: var(--text-primary);
  text-align: right;
  line-height: 1.5;
}

/* 状态文字着色 */
.status-text-status-active {
  color: var(--tea-primary);
  font-weight: 600;
}

.status-text-status-upcoming {
  color: var(--tea-secondary);
}

.status-text-status-ended {
  color: var(--text-placeholder);
}

/* 报名按钮 — 与首页按钮风格一致 */
.action-area {
  padding: 0;
}

.signup-btn {
  display: block;
  width: 100%;
  padding: 14px 0;
  background: var(--tea-primary);
  color: #FFF;
  border: none;
  border-radius: 28px;
  font-size: 0.95rem;
  letter-spacing: 2px;
  cursor: pointer;
  font-family: inherit;
  transition: all var(--transition-normal);
}

.signup-btn:hover:not(:disabled) {
  background: var(--tea-primary-hover);
  box-shadow: var(--tea-shadow-hover);
}

.signup-btn:disabled {
  background: var(--text-placeholder);
  cursor: not-allowed;
  color: #FFF;
}

.signup-disabled-text {
  font-size: 0.85rem;
  color: var(--text-secondary);
  text-align: center;
  letter-spacing: 1px;
  padding: 14px 0;
  border: 1px solid var(--tea-border);
  border-radius: 28px;
}

/* 右侧正文 */
.detail-main {
  min-width: 0;
}

.section-title {
  font-family: var(--font-serif);
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--tea-primary);
  letter-spacing: 2px;
  margin: 0 0 24px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--tea-border);
}

.desc-content {
  font-size: 0.95rem;
  line-height: 2.2;
  color: var(--text-regular);
  white-space: pre-wrap;
  letter-spacing: 0.3px;
}

.no-desc {
  font-size: 0.9rem;
  color: var(--text-placeholder);
  letter-spacing: 1px;
  padding: 40px 0;
}

/* ----- 加载 / 空 / 错误状态 ----- */
.loading-container {
  display: flex;
  justify-content: center;
  padding: 120px 0;
}

.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: var(--text-secondary);
  gap: 12px;
}

.empty-text {
  font-size: 0.9rem;
}

.empty-link {
  color: var(--tea-primary);
  font-size: 0.85rem;
  text-decoration: none;
  letter-spacing: 1px;
  transition: color var(--transition-fast);
}

.empty-link:hover {
  color: var(--tea-primary-hover);
}

/* ----- 响应式 ----- */
@media (max-width: 768px) {
  .detail-body {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .hero-overlay {
    padding: 24px 20px 20px;
  }

  .overlay-title {
    font-size: 1.3rem;
    letter-spacing: 2px;
  }

  .hero-image-wrap {
    aspect-ratio: 4 / 3;
  }

  .section-title {
    font-size: 1.05rem;
  }

  .desc-content {
    font-size: 0.9rem;
    line-height: 1.9;
  }
}
</style>