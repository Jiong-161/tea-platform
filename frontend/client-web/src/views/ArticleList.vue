<!--
  文章列表页 — 茶文化文章浏览
  支持分页浏览，显示文章封面、标题、摘要、作者、阅读量
-->
<template>
  <MainLayout>
    <!-- 页面标题横幅 -->
    <div class="page-banner">
      <div class="container">
        <h1 class="banner-title">茶文化文章</h1>
        <p class="banner-sub">品读茶文化，感悟东方智慧</p>
      </div>
    </div>

    <div class="container page-section">
      <!-- 骨架屏加载态 -->
      <div v-if="loading" class="skeleton-list">
        <div class="article-card skeleton-article-card" v-for="i in 4" :key="i">
          <div class="skeleton skeleton-img" style="aspect-ratio: 280/180; flex-shrink: 0; width: 280px"></div>
          <div class="skeleton-article-body">
            <div class="skeleton skeleton-title" style="width: 60%"></div>
            <div class="skeleton skeleton-text" style="width: 100%"></div>
            <div class="skeleton skeleton-text" style="width: 85%"></div>
            <div class="skeleton skeleton-text" style="width: 50%; height: 14px"></div>
          </div>
        </div>
      </div>

      <!-- 错误状态 -->
      <ErrorState v-else-if="error" :message="error" @retry="loadArticles()" />

      <!-- 文章列表 -->
      <template v-else-if="!error">
        <div class="article-list" v-if="articles.length">
          <div v-for="item in articles" :key="item.id" class="article-card">
            <router-link :to="`/article/${item.id}`" class="article-link">
              <div class="article-cover-wrap">
                <TeaImage :src="item.cover" :alt="item.title" img-class="article-cover" />
              </div>
              <div class="article-info">
                <h3 class="article-title">{{ item.title }}</h3>
                <p class="article-summary">{{ item.summary }}</p>
                <div class="article-meta">
                  <span class="meta-item">
                    <el-icon><User /></el-icon>{{ item.author || '匿名' }}
                  </span>
                  <span class="meta-item">
                    <el-icon><View /></el-icon>{{ item.viewCount || 0 }} 阅读
                  </span>
                  <span class="meta-item">
                    <el-icon><Clock /></el-icon>{{ formatDate(item.createTime) }}
                  </span>
                </div>
              </div>
            </router-link>
          </div>
        </div>

        <!-- 空状态 -->
        <EmptyState
          v-else
          icon="Document"
          title="暂无文章"
          description="茶文化文章正在筹备中，敬请期待~"
          action-text="返回首页"
          action-link="/"
        />

        <!-- 分页 -->
        <div class="pagination-wrap" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            background
            layout="prev, pager, next"
            @current-change="loadArticles"
          />
        </div>
      </template>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import EmptyState from '@/components/EmptyState.vue'
import TeaImage from '@/components/TeaImage.vue'
import { getArticleList } from '@/api/content'

const articles = ref([])
const loading = ref(true)
const error = ref('')
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)

onMounted(() => {
  loadArticles()
})

async function loadArticles(page = 1) {
  error.value = ''
  loading.value = true
  try {
    const res = await getArticleList({ current: page, size: pageSize })
    if (res && res.data) {
      articles.value = res.data.records || []
      total.value = Number(res.data.total) || 0
    }
  } catch {
    error.value = '文章列表加载失败，请检查后端服务是否启动'
    articles.value = []
  } finally {
    loading.value = false
  }
}

/** 格式化日期 */
function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
/* 使用全局 .page-banner 样式 */

/* 文章列表 */
.article-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.article-card {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.article-card:hover {
  box-shadow: var(--tea-shadow-hover);
  transform: translateY(-2px);
}

.article-link {
  display: flex;
  text-decoration: none;
  color: inherit;
}

.article-cover-wrap {
  width: 280px;
  min-height: 180px;
  flex-shrink: 0;
  overflow: hidden;
}

.article-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.article-card:hover .article-cover {
  transform: scale(1.05);
}

.article-cover-placeholder {
  width: 100%;
  height: 100%;
  min-height: 180px;
  background: linear-gradient(135deg, var(--tea-primary-light), var(--tea-accent-light));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--tea-primary);
}

.article-info {
  flex: 1;
  padding: var(--space-lg);
  display: flex;
  flex-direction: column;
}

.article-title {
  font-family: var(--font-serif);
  font-size: 1.25rem;
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
}

.article-summary {
  font-size: 0.9rem;
  color: var(--text-secondary);
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: var(--space-md);
  margin-top: var(--space-sm);
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 分页 */
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: var(--space-xl);
}

@media (max-width: 768px) {
  .article-link { flex-direction: column; }
  .article-cover-wrap { width: 100%; height: 200px; }
}

/* ========== 骨架屏 ========== */
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.skeleton-article-card {
  pointer-events: none;
  display: flex;
}

.skeleton-article-body {
  flex: 1;
  padding: var(--space-lg);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

@media (max-width: 768px) {
  .skeleton-article-card { flex-direction: column; }
}
</style>