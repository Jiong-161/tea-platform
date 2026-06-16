<!--
  文章详情页 — 展示茶文化文章完整内容
  包含：封面图、标题、作者、发布时间、正文内容、评论区域
-->
<template>
  <MainLayout>
    <div class="container article-page" v-loading="loading">
      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>
      <!-- 错误状态 -->
      <ErrorState v-else-if="error" :message="error" @retry="loadArticle" />
      <!-- 文章内容 -->
      <template v-else-if="article.id">
        <!-- 面包屑 -->
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span> / </span>
          <router-link to="/articles">茶文化文章</router-link>
          <span> / </span>
          <span class="current">{{ article.title }}</span>
        </div>

        <!-- 文章内容区 -->
        <article class="article-main">
          <!-- 封面图 -->
          <div class="cover-wrapper">
            <TeaImage :src="article.cover" :alt="article.title" img-class="article-cover" />
          </div>

          <!-- 标题与元信息 -->
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span><el-icon><User /></el-icon>{{ article.author || '匿名' }}</span>
            <span><el-icon><Clock /></el-icon>{{ formatDate(article.createTime) }}</span>
            <span><el-icon><View /></el-icon>{{ article.viewCount || 0 }} 阅读</span>
          </div>

          <!-- 正文 -->
          <div class="article-content" v-html="renderedContent"></div>
        </article>

        <!-- 评论区 -->
        <section class="comment-section">
          <h3 class="comment-title">
            <el-icon><ChatDotRound /></el-icon> 茶友评论 ({{ comments.length }})
          </h3>

          <!-- 发表评论 -->
          <div class="comment-form" v-if="userStore.isLogin">
            <el-input
              v-model="commentText"
              type="textarea"
              :rows="3"
              placeholder="写下您对这篇文章的感悟..."
              maxlength="500"
              show-word-limit
            />
            <el-button
              type="primary"
              size="small"
              class="comment-submit"
              @click="submitComment"
              :loading="submitting"
            >
              发表评论
            </el-button>
          </div>
          <div class="comment-login-tip" v-else>
            <router-link to="/login">登录</router-link> 后即可评论
          </div>

          <!-- 评论列表 -->
          <div class="comment-list" v-if="comments.length">
            <div v-for="item in comments" :key="item.id" class="comment-item">
              <div class="comment-avatar">
                <el-avatar :size="40" :icon="UserFilled" />
              </div>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-user">{{ item.nickname || item.username }}</span>
                  <span class="comment-time">{{ formatDate(item.createTime) }}</span>
                </div>
                <p class="comment-text">{{ item.content }}</p>
              </div>
            </div>
          </div>

          <!-- 暂无评论 -->
          <div v-else class="empty-container" style="min-height: 120px;">
            <p>暂无评论，来抢沙发吧~</p>
          </div>
        </section>
      </template>

      <!-- 文章不存在 -->
      <div v-else class="empty-container">
        <el-icon :size="64"><Document /></el-icon>
        <p>文章不存在或已被删除</p>
        <el-button @click="$router.push('/articles')">返回文章列表</el-button>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import TeaImage from '@/components/TeaImage.vue'
import { getArticleDetail } from '@/api/content'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const article = ref({})
const loading = ref(true)
const error = ref('')
const commentText = ref('')
const submitting = ref(false)
const comments = ref([])

onMounted(() => {
  loadArticle()
})

async function loadArticle() {
  error.value = ''
  loading.value = true
  try {
    const res = await getArticleDetail(route.params.id)
    if (res && res.data) {
      article.value = res.data
    }
  } catch {
    error.value = '文章详情加载失败，请检查后端服务是否启动'
    article.value = {}
  } finally {
    loading.value = false
  }
}

/** 将文章内容中的换行转为 HTML */
const renderedContent = computed(() => {
  const content = article.value.content || ''
  return content.replace(/\n/g, '<br/>')
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

/** 提交评论（前端模拟展示，后端如需可对接评论接口） */
function submitComment() {
  const text = commentText.value.trim()
  if (!text) {
    ElMessage.warning('请输入评论内容')
    return
  }
  submitting.value = true
  // 模拟评论 — 实际项目对接后端评论接口
  setTimeout(() => {
    comments.value.unshift({
      id: Date.now(),
      nickname: userStore.nickname,
      username: userStore.username,
      content: text,
      createTime: new Date().toISOString()
    })
    commentText.value = ''
    submitting.value = false
    ElMessage.success('评论发表成功')
  }, 300)
}
</script>

<style scoped>
.article-page {
  padding-top: var(--space-lg);
  padding-bottom: var(--space-2xl);
}

/* 面包屑 */
.breadcrumb {
  font-size: 0.85rem;
  color: var(--text-secondary);
  margin-bottom: var(--space-lg);
}

.breadcrumb a {
  color: var(--text-secondary);
}

.breadcrumb a:hover {
  color: var(--tea-primary);
}

.breadcrumb .current {
  color: var(--tea-primary);
}

/* 封面 */
.cover-wrapper {
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: var(--space-lg);
}

.article-cover {
  width: 100%;
  max-height: 480px;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 280px;
  background: linear-gradient(135deg, var(--tea-primary-light), var(--tea-accent-light));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--tea-primary);
}

/* 标题与元信息 */
.article-title {
  font-family: var(--font-serif);
  font-size: 2rem;
  color: var(--text-primary);
  margin-bottom: var(--space-md);
  line-height: 1.4;
}

.article-meta {
  display: flex;
  gap: var(--space-lg);
  font-size: 0.85rem;
  color: var(--text-secondary);
  padding-bottom: var(--space-lg);
  border-bottom: 1px solid var(--tea-border);
  margin-bottom: var(--space-lg);
}

.article-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 正文 */
.article-content {
  font-size: 1.05rem;
  line-height: 2;
  color: var(--text-regular);
  margin-bottom: var(--space-2xl);
}

/* 评论区 */
.comment-section {
  border-top: 1px solid var(--tea-border);
  padding-top: var(--space-lg);
}

.comment-title {
  font-family: var(--font-serif);
  font-size: 1.2rem;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: var(--space-lg);
}

.comment-form {
  margin-bottom: var(--space-lg);
}

.comment-submit {
  margin-top: var(--space-sm);
}

.comment-login-tip {
  text-align: center;
  padding: var(--space-lg);
  background: var(--tea-bg);
  border-radius: var(--radius-md);
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: var(--space-lg);
}

.comment-login-tip a {
  color: var(--tea-primary);
  font-weight: 500;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.comment-item {
  display: flex;
  gap: var(--space-md);
  padding: var(--space-md);
  background: var(--tea-bg);
  border-radius: var(--radius-md);
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.comment-user {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 0.9rem;
}

.comment-time {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.comment-text {
  font-size: 0.9rem;
  color: var(--text-regular);
  line-height: 1.6;
}

@media (max-width: 768px) {
  .article-title { font-size: 1.5rem; }
  .article-meta { flex-wrap: wrap; gap: 12px; }
}
</style>