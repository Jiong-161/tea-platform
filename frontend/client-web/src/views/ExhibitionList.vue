<!--
  展览列表页 — 仿国博布局 + 项目茶绿色系
  骨架：国博的 tabs / 极简卡片 / 充足留白
  配色：深茶绿主色 / 暖棕辅助 / 茶白背景 — 与首页完全统一
-->
<template>
  <MainLayout>
    <!-- ====== 页面标题区（统一 page-banner 风格） ====== -->
    <div class="page-banner exhibition-page-header">
      <div class="container">
        <p class="banner-en">EXHIBITIONS & EVENTS</p>
        <h1 class="banner-title">展览活动</h1>
        <p class="banner-sub">探索茶文化的多元表达形式</p>
      </div>
    </div>

    <div class="container page-content">
      <!-- ====== 分类筛选标签栏 ====== -->
      <div class="filter-bar">
        <el-tabs v-model="activeTab" class="museum-tabs" @tab-change="handleTabChange">
          <el-tab-pane label="全部展览" name="all" />
          <el-tab-pane label="正在展出" name="1" />
          <el-tab-pane label="即将展出" name="0" />
          <el-tab-pane label="已结束" name="2" />
        </el-tabs>
        <span class="filter-count">共 {{ filteredList.length }} 个展览</span>
      </div>

      <!-- ====== 加载中 ====== -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      </div>

      <!-- ====== 错误状态 ====== -->
      <ErrorState v-else-if="error" :message="error" @retry="loadExhibitions()" />

      <!-- ====== 展览卡片网格 ====== -->
      <template v-else-if="!error">
        <div class="exhibition-grid" v-if="filteredList.length">
          <router-link
            v-for="item in filteredList"
            :key="item.id"
            :to="`/exhibition/${item.id}`"
            class="exhibition-card"
          >
            <!-- 封面图 -->
            <div class="card-image">
              <TeaImage :src="item.cover" :alt="item.title" img-class="card-cover" />
              <!-- 状态标签 -->
              <span class="card-status" :class="statusClass(item.status)">
                {{ getStatusText(item.status) }}
              </span>
            </div>

            <!-- 信息区 -->
            <div class="card-info">
              <h3 class="card-title">{{ item.title }}</h3>
              <div class="card-meta">
                <el-icon class="meta-icon"><Location /></el-icon>
                <span class="meta-text">{{ item.location || '地点待定' }}</span>
                <span class="meta-divider">|</span>
                <el-icon class="meta-icon"><Clock /></el-icon>
                <span class="meta-text">{{ formatDate(item.startTime) }}</span>
              </div>
            </div>
          </router-link>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-container">
          <el-icon :size="48"><Stamp /></el-icon>
          <p class="empty-text">暂无展览</p>
        </div>
      </template>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import TeaImage from '@/components/TeaImage.vue'
import { getExhibitionList } from '@/api/exhibition'

const exhibitions = ref([])
const loading = ref(true)
const error = ref('')
const activeTab = ref('all')

onMounted(() => {
  loadExhibitions()
})

async function loadExhibitions() {
  error.value = ''
  loading.value = true
  try {
    const res = await getExhibitionList()
    if (res && res.data) {
      exhibitions.value = res.data || []
    }
  } catch {
    error.value = '展览数据加载失败，请检查后端服务是否启动'
    exhibitions.value = []
  } finally {
    loading.value = false
  }
}

const filteredList = computed(() => {
  if (activeTab.value === 'all') return exhibitions.value
  return exhibitions.value.filter(item => String(item.status) === activeTab.value)
})

function handleTabChange() {}

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
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
/* ============================================================
   国博骨架 + 茶绿色系 — 与首页完全统一
   ============================================================ */

/* ----- 页面标题区（统一 page-banner 风格） ----- */
.exhibition-page-header .banner-en {
  font-size: 0.75rem;
  letter-spacing: 4px;
  opacity: 0.6;
  margin-bottom: 8px;
}

/* ----- 筛选标签栏 ----- */
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 48px 0 32px;
  border-bottom: 1px solid var(--tea-border);
}

.museum-tabs {
  flex: 1;
}

.museum-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.museum-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: var(--tea-border);
}

.museum-tabs :deep(.el-tabs__item) {
  font-size: 0.95rem;
  color: var(--text-secondary);
  height: 48px;
  line-height: 48px;
  padding: 0 24px;
  letter-spacing: 1px;
  font-weight: 400;
  transition: color var(--transition-fast);
}

.museum-tabs :deep(.el-tabs__item:hover) {
  color: var(--tea-primary);
}

.museum-tabs :deep(.el-tabs__item.is-active) {
  color: var(--tea-primary);
  font-weight: 600;
}

/* 激活下划线：茶绿色 */
.museum-tabs :deep(.el-tabs__active-bar) {
  background-color: var(--tea-primary);
  height: 2px;
}

.filter-count {
  font-size: 0.8rem;
  color: var(--text-secondary);
  white-space: nowrap;
  flex-shrink: 0;
}

/* ----- 卡片网格 ----- */
.exhibition-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px 28px;
}

.exhibition-card {
  display: block;
  text-decoration: none;
  color: inherit;
  background: var(--tea-white);
  border-radius: var(--radius-md);
  box-shadow: var(--tea-shadow);
  transition: box-shadow var(--transition-normal);
  overflow: hidden;
}

.exhibition-card:hover {
  box-shadow: var(--tea-shadow-hover);
}

/* 封面图 — 统一 3:2 比例 */
.card-image {
  position: relative;
  width: 100%;
  aspect-ratio: 3 / 2;
  overflow: hidden;
  background: var(--tea-primary-light);
}

.card-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.card-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--tea-primary-light), var(--tea-accent-light));
  color: var(--tea-primary);
  opacity: 0.5;
}

/* 状态标签（覆盖在图片左上角） */
.card-status {
  position: absolute;
  top: 0;
  left: 0;
  font-size: 0.75rem;
  padding: 4px 14px;
  letter-spacing: 1px;
  color: #FFF;
}

.status-active {
  background: rgba(46, 91, 75, 0.85);    /* 茶绿 — 正在展出 */
}

.status-upcoming {
  background: rgba(143, 151, 121, 0.80);  /* 辅助绿灰 — 即将展出 */
}

.status-ended {
  background: rgba(181, 191, 175, 0.70);  /* 浅灰绿 — 已结束 */
}

/* 信息区 */
.card-info {
  padding: 18px 16px 20px;
}

.card-title {
  font-family: var(--font-serif);
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.6;
  margin: 0 0 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  letter-spacing: 0.5px;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.meta-icon {
  font-size: 0.85rem;
  flex-shrink: 0;
}

.meta-text {
  color: var(--text-secondary);
}

.meta-divider {
  color: var(--tea-border);
  margin: 0 2px;
}

/* ----- 加载 / 空 / 错误状态 ----- */
.loading-container {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100px 0;
  color: var(--text-secondary);
  gap: 12px;
}

.empty-text {
  font-size: 0.9rem;
}

/* ----- 响应式 ----- */
@media (max-width: 1024px) {
  .exhibition-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px 20px;
  }
}

@media (max-width: 640px) {
  .exhibition-page-header {
    padding: 48px 0 28px;
  }
  .exhibition-page-header .banner-title {
    font-size: 1.5rem;
    letter-spacing: 4px;
  }
  .exhibition-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
    margin-top: 32px;
  }
  .filter-count {
    padding-bottom: 14px;
  }
  .museum-tabs :deep(.el-tabs__item) {
    padding: 0 14px;
    font-size: 0.85rem;
  }
}
</style>