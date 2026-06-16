<!--
  首页 — 新中式茶文化风格
  包含：Hero横幅、茶文化文章区块、展览活动区块、精选茶品区块
  数据来自网关 /home/index 聚合接口
-->
<template>
  <MainLayout>
    <!-- ========== Hero 横幅 ========== -->
    <section class="hero-section">
      <div class="hero-inner container">
        <div class="hero-text">
          <p class="hero-tag">— 传承千年 —</p>
          <h1 class="hero-title">茶文化购销服务平台</h1>
          <p class="hero-subtitle">
            围绕"茶文化内容、茶展览活动、茶商品电商"三大核心业务。<br />
            是一个集茶文化内容传播、茶事展览活动、茶叶电商<br />
            交易于一体的垂直领域综合型 Web 平台<br />
          </p>
          <div class="hero-actions">
            <router-link to="/articles" class="hero-btn-primary">品读茶文</router-link>
            <router-link to="/products" class="hero-btn-outline">精选茶品</router-link>
          </div>
        </div>
        <div class="hero-visual">
          <div class="hero-image-wrap">
            <img
              src="http://127.0.0.1:9000/tea-platform/tea.png"
              alt="茶文化"
              class="hero-main-image"
            />
            <!-- 茶叶飘落装饰 -->
            <div class="leaf leaf-1"></div>
            <div class="leaf leaf-2"></div>
            <div class="leaf leaf-3"></div>
          </div>
        </div>
      </div>
      <!-- 底部波浪装饰 -->
      <div class="hero-wave">
        <svg viewBox="0 0 1440 120" preserveAspectRatio="none">
          <path fill="#F8F9F5" d="M0,60 C360,120 720,0 1440,60 L1440,120 L0,120 Z" />
        </svg>
      </div>
    </section>

    <!-- ========== 茶文化文章 ========== -->
    <section class="page-section container fade-up-section" v-if="articles.length">
      <div class="section-header">
        <h2 class="section-title">茶文化文章</h2>
        <router-link to="/articles" class="more-link">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      <div class="card-grid">
        <router-link
          v-for="item in articles.slice(0, 6)"
          :key="item.id"
          :to="`/article/${item.id}`"
          class="card"
        >
          <TeaImage :src="item.cover" :alt="item.title" img-class="card-cover" />
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-desc">{{ item.summary }}</p>
            <div class="card-meta">
              <span class="card-author">{{ item.author }}</span>
              <span class="card-views">{{ item.viewCount || 0 }} 阅读</span>
            </div>
          </div>
        </router-link>
      </div>
    </section>

    <!-- ========== 展览活动 ========== -->
    <section class="page-section container fade-up-section" v-if="exhibitions.length">
      <div class="section-header">
        <h2 class="section-title">展览活动</h2>
        <router-link to="/exhibitions" class="more-link">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      <div class="card-grid">
        <router-link
          v-for="item in exhibitions.slice(0, 6)"
          :key="item.id"
          :to="`/exhibition/${item.id}`"
          class="card"
        >
          <TeaImage :src="item.cover" :alt="item.title" img-class="card-cover" />
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-desc">{{ item.location }}</p>
            <div class="card-meta">
              <span class="tea-tag">{{ getStatusText(item.status) }}</span>
              <span class="card-date">{{ formatDate(item.startTime) }}</span>
            </div>
          </div>
        </router-link>
      </div>
    </section>

    <!-- ========== 精选茶叶商品 ========== -->
    <section class="page-section container fade-up-section" v-if="products.length">
      <div class="section-header">
        <h2 class="section-title">精选茶叶商品</h2>
        <router-link to="/products" class="more-link">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      <div class="card-grid">
        <div
          v-for="item in products.slice(0, 6)"
          :key="item.id"
          class="card"
        >
          <TeaImage :src="item.cover" :alt="item.title" img-class="card-cover" />
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-desc line-clamp-2">{{ item.description }}</p>
            <div class="card-footer">
              <span class="price">¥{{ formatPrice(item.price) }}</span>
              <router-link :to="`/product/${item.id}`" class="buy-btn">
                查看详情
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 统一空状态：数据加载成功但全部无内容 ========== -->
    <EmptyState
      v-if="!loading && !error && !articles.length && !exhibitions.length && !products.length"
      icon="Goods"
      title="暂无内容"
      description="茶文化内容正在筹备中，敬请期待~"
      action-text="浏览文章"
      action-link="/articles"
    />

    <!-- ========== 骨架屏加载态 ========== -->
    <div v-if="loading" class="page-section container">
      <div class="skeleton-section" v-for="n in 3" :key="n">
        <div class="skeleton-header">
          <div class="skeleton skeleton-title" style="width: 160px"></div>
          <div class="skeleton skeleton-text" style="width: 80px; height: 14px"></div>
        </div>
        <div class="card-grid">
          <div class="card skeleton-card" v-for="i in 3" :key="i">
            <div class="skeleton skeleton-img"></div>
            <div class="skeleton-card-body">
              <div class="skeleton skeleton-text" style="width: 70%"></div>
              <div class="skeleton skeleton-text" style="width: 100%"></div>
              <div class="skeleton skeleton-text" style="width: 50%"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 错误状态 ========== -->
    <ErrorState v-if="error" :message="error" @retry="loadHomeData" />
  </MainLayout>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import MainLayout from '@/layout/MainLayout.vue'
import EmptyState from '@/components/EmptyState.vue'
import ErrorState from '@/components/ErrorState.vue'
import TeaImage from '@/components/TeaImage.vue'
import { getHomeData } from '@/api/home'

const articles = ref([])
const exhibitions = ref([])
const products = ref([])
const loading = ref(true)
const error = ref('')

onMounted(() => {
  loadHomeData()
})

/** 入场动画：IntersectionObserver 交错淡入上升 */
function initFadeUpObserver() {
  const sections = document.querySelectorAll('.fade-up-section')
  if (!sections.length) return

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-visible')
          observer.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.1 }
  )

  sections.forEach((el) => observer.observe(el))

  // 兜底：1秒后强制显示所有区块（防止 Observer 未触发）
  setTimeout(() => {
    sections.forEach((el) => el.classList.add('is-visible'))
  }, 1000)
}

async function loadHomeData() {
  error.value = ''
  loading.value = true
  try {
    const res = await getHomeData()
    console.log('[Home] API 响应:', JSON.stringify(res))

    // 兼容多种响应格式
    const data = res?.data || res || {}
    console.log('[Home] 解析后的 data:', JSON.stringify(data))

    articles.value = data.articles || []
    exhibitions.value = data.exhibitions || []
    products.value = data.products || []

    console.log(`[Home] articles=${articles.value.length}, exhibitions=${exhibitions.value.length}, products=${products.value.length}`)
  } catch (e) {
    console.error('[Home] 加载失败:', e)
    error.value = '首页数据加载失败，请检查后端服务是否启动'
  } finally {
    loading.value = false
    // 数据加载完成后，等 Vue 完成 DOM 更新再初始化入场动画
    // 修复：此前 initFadeUpObserver 在 onMounted 中调用时 DOM 尚未渲染，
    // .fade-up-section 元素因 v-if 条件不满足而不存在，导致 observer 和兜底 timeout 均未注册
    await nextTick()
    initFadeUpObserver()
  }
}

/** 展览状态文本 */
function getStatusText(status) {
  const map = { 0: '未开始', 1: '进行中', 2: '已结束', 3: '已取消' }
  return map[status] || '未知'
}

/** 格式化日期 */
function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

/** 格式化价格 */
function formatPrice(price) {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}
</script>

<style scoped>
/* ========== Hero 横幅 ========== */
.hero-section {
  position: relative;
  background: linear-gradient(170deg, #E8F0ED 0%, #F8F9F5 40%, #F5EDE4 100%);
  padding: var(--space-2xl) 0 80px;
  overflow: hidden;
}

.hero-inner {
  display: flex;
  align-items: center;
  gap: var(--space-xl);
}

.hero-text {
  flex: 1;
}

.hero-tag {
  display: inline-block;
  font-size: 0.85rem;
  color: var(--tea-secondary);
  letter-spacing: 4px;
  margin-bottom: var(--space-sm);
}

.hero-title {
  font-family: var(--font-serif);
  font-size: 3.5rem;
  font-weight: 700;
  color: var(--tea-primary);
  letter-spacing: 8px;
  margin-bottom: var(--space-md);
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.1rem;
  color: var(--text-secondary);
  line-height: 1.8;
  margin-bottom: var(--space-lg);
}

.hero-actions {
  display: flex;
  gap: var(--space-md);
}

.hero-btn-primary {
  background: var(--tea-primary);
  color: #FFF;
  padding: 12px 32px;
  border-radius: 28px;
  font-size: 1rem;
  font-weight: 500;
  letter-spacing: 1px;
  transition: all var(--transition-normal);
}

.hero-btn-primary:hover {
  background: var(--tea-primary-hover);
  box-shadow: var(--tea-shadow-hover);
  color: #FFF;
  transform: translateY(-2px);
}

.hero-btn-outline {
  border: 2px solid var(--tea-primary);
  color: var(--tea-primary);
  padding: 12px 32px;
  border-radius: 28px;
  font-size: 1rem;
  font-weight: 500;
  letter-spacing: 1px;
  transition: all var(--transition-normal);
}

.hero-btn-outline:hover {
  background: var(--tea-primary);
  color: #FFF;
}

.hero-visual {
  flex-shrink: 0;
  width: 420px;
}

.hero-image-wrap {
  width: 100%;
  aspect-ratio: 4 / 3;
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl);
  position: relative;
}

.hero-main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 6s ease-out;
}

.hero-section:hover .hero-main-image {
  transform: scale(1.05);
}

/* 茶叶飘落装饰 */
.leaf {
  position: absolute;
  width: 12px;
  height: 12px;
  background: var(--tea-accent);
  border-radius: 50% 0 50% 0;
  opacity: 0.4;
  animation: leafFloat 8s ease-in-out infinite;
}

.leaf-1 { top: 15%; left: 10%; animation-delay: 0s; }
.leaf-2 { top: 60%; right: 5%; animation-delay: 2.5s; }
.leaf-3 { bottom: 20%; left: 20%; animation-delay: 5s; }

@keyframes leafFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.4; }
  50% { transform: translateY(-16px) rotate(180deg); opacity: 0.7; }
}

.hero-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
}

.hero-wave svg {
  display: block;
  width: 100%;
  height: 80px;
}

/* ========== 内容区块 ========== */
.page-section {
  margin-top: var(--space-xl);
  margin-bottom: var(--space-xl);
}

.more-link {
  color: var(--tea-secondary);
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color var(--transition-fast);
}

.more-link:hover {
  color: var(--tea-primary);
}

/* ========== 卡片 ========== */
.card {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  overflow: hidden;
  transition: all var(--transition-normal);
  display: flex;
  flex-direction: column;
}

.card:hover {
  transform: translateY(-6px);
  box-shadow: var(--tea-shadow-hover);
}

.card-cover {
  width: 100%;
  height: 220px;
  object-fit: cover;
  background: var(--tea-primary-light);
  transition: transform var(--transition-slow);
  flex-shrink: 0;
}

.card:hover .card-cover {
  transform: scale(1.05);
}

.card-body {
  padding: var(--space-md);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-family: var(--font-serif);
  font-size: 1.1rem;
  color: var(--text-primary);
  margin-bottom: var(--space-xs);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: 0.85rem;
  color: var(--text-secondary);
  margin-bottom: var(--space-sm);
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.buy-btn {
  background: var(--tea-primary);
  color: #FFF;
  font-size: 0.8rem;
  padding: 6px 16px;
  border-radius: 16px;
  transition: all var(--transition-fast);
}

.buy-btn:hover {
  background: var(--tea-primary-hover);
  color: #FFF;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .hero-inner { flex-direction: column; text-align: center; }
  .hero-visual { display: none; }
  .hero-title { font-size: 2.5rem; }
  .hero-actions { justify-content: center; }
}

/* ========== 骨架屏 ========== */
.skeleton-section {
  margin-bottom: var(--space-xl);
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-md);
}

.skeleton-card {
  pointer-events: none;
}

.skeleton-card-body {
  padding: var(--space-md);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ========== 入场动画 ========== */
/* 默认不可见，由 JS 控制显示 */
.fade-up-section {
  opacity: 0;
  transform: translateY(24px);
  transition: opacity 0.6s var(--ease-out-expo), transform 0.6s var(--ease-out-expo);
}

.fade-up-section.is-visible {
  opacity: 1;
  transform: translateY(0);
}
</style>