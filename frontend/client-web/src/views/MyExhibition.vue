<!--
  我的展览报名页 — 查看已报名的展览活动记录
-->
<template>
  <MainLayout>
    <div class="page-banner">
      <div class="container">
        <h1 class="banner-title">我的展览报名</h1>
        <p class="banner-sub">查看您已报名的展览活动</p>
      </div>
    </div>

    <div class="container page-section">
      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>

      <!-- 错误状态 -->
      <ErrorState v-else-if="error" :message="error" @retry="loadData()" />

      <!-- 报名列表 -->
      <template v-else-if="!error && list.length">
        <div class="card-grid">
          <div v-for="item in list" :key="item.id" class="card signup-card">
            <router-link :to="`/exhibition/${item.exhibitionId}`" class="card-link">
              <div class="card-body">
                <div class="card-icon">
                  <el-icon :size="32"><Stamp /></el-icon>
                </div>
                <div class="card-info">
                  <h3 class="card-title">展览 #{{ item.exhibitionId }}</h3>
                  <p class="card-meta">
                    <span><el-icon><User /></el-icon>{{ item.username }}</span>
                    <span><el-icon><Clock /></el-icon>{{ formatDate(item.createTime) }}</span>
                  </p>
                </div>
              </div>
              <div class="card-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </router-link>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <div v-else class="empty-container">
        <el-icon :size="64"><Stamp /></el-icon>
        <p>还没有报名任何展览</p>
        <el-button type="primary" round @click="$router.push('/exhibitions')">
          去看看展览
        </el-button>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import { getMyExhibition } from '@/api/exhibition'

const list = ref([])
const loading = ref(true)
const error = ref('')

onMounted(() => {
  loadData()
})

async function loadData() {
  error.value = ''
  loading.value = true
  try {
    const res = await getMyExhibition()
    if (res && res.data) {
      list.value = res.data || []
    }
  } catch {
    error.value = '报名记录加载失败，请检查后端服务是否启动'
    list.value = []
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #3D5A4B, var(--tea-primary));
  padding: var(--space-xl) 0;
  text-align: center;
  color: #FFF;
}

.banner-title {
  font-family: var(--font-serif);
  font-size: 2rem;
  color: #FFF;
  letter-spacing: 4px;
  margin-bottom: var(--space-xs);
}

.banner-sub {
  font-size: 0.95rem;
  opacity: 0.7;
}

.card-grid {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.card {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  transition: all var(--transition-normal);
}

.card:hover {
  box-shadow: var(--tea-shadow-hover);
  transform: translateY(-2px);
}

.card-link {
  display: flex;
  align-items: center;
  padding: var(--space-md) var(--space-lg);
  text-decoration: none;
  color: inherit;
}

.card-body {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  flex: 1;
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-md);
  background: var(--tea-primary-light);
  color: var(--tea-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-title {
  font-family: var(--font-serif);
  font-size: 1rem;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.card-meta {
  display: flex;
  gap: var(--space-md);
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.card-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-arrow {
  color: var(--tea-secondary);
}
</style>