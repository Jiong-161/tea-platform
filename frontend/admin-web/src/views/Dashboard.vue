<!-- 仪表盘 — 数据概览 -->
<template>
  <AdminLayout>
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" :style="{ background: item.bg }">
            <el-icon :size="28"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-num">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-row :gutter="20" style="margin-top:24px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><h3>📝 最近文章</h3></template>
          <div v-if="recentArticles.length" class="quick-list">
            <div v-for="a in recentArticles" :key="a.id" class="quick-item">
              <span class="quick-title">{{ a.title }}</span>
              <span class="quick-meta">{{ a.author }} · {{ formatDate(a.createTime) }}</span>
            </div>
          </div>
          <div v-else class="empty-container" style="min-height:120px"><p>暂无数据</p></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><h3>📦 最近订单</h3></template>
          <div v-if="recentOrders.length" class="quick-list">
            <div v-for="o in recentOrders" :key="o.id" class="quick-item">
              <span class="quick-title">#{{ o.orderNo }}</span>
              <span class="quick-meta">{{ o.username }} · ¥{{ formatPrice(o.totalAmount) }}</span>
            </div>
          </div>
          <div v-else class="empty-container" style="min-height:120px"><p>暂无数据</p></div>
        </el-card>
      </el-col>
    </el-row>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Document, Stamp, Goods } from '@element-plus/icons-vue'
import AdminLayout from '@/layout/AdminLayout.vue'
import { getUserList } from '@/api/admin-user'
import { getArticleList } from '@/api/admin-content'
import { getExhibitionList } from '@/api/admin-exhibition'
import { getProductList, getOrderList } from '@/api/admin-product'

const stats = ref([
  { label: '用户总数', value: 0, icon: User, bg: '#E8F0ED' },
  { label: '文章总数', value: 0, icon: Document, bg: '#F5EDE4' },
  { label: '展览数量', value: 0, icon: Stamp, bg: '#E8F0ED' },
  { label: '商品数量', value: 0, icon: Goods, bg: '#F5EDE4' }
])
const recentArticles = ref([])
const recentOrders = ref([])

onMounted(async () => {
  try {
    const [users, articles, exhibitions, products, orders] = await Promise.all([
      getUserList({ page: 1, size: 1 }),
      getArticleList({ page: 1, size: 1 }),
      getExhibitionList({ page: 1, size: 1 }),
      getProductList({ page: 1, size: 1 }),
      getOrderList({ page: 1, size: 1 })
    ])
    stats.value[0].value = users?.data?.total || 0
    stats.value[1].value = articles?.data?.total || 0
    stats.value[2].value = exhibitions?.data?.total || 0
    stats.value[3].value = products?.data?.total || 0

    // 最近文章和订单
    const recentArts = await getArticleList({ page: 1, size: 5 })
    recentArticles.value = recentArts?.data?.records || []
    const recentOrds = await getOrderList({ page: 1, size: 5 })
    recentOrders.value = recentOrds?.data?.records || []
  } catch { /* 错误已由拦截器处理 */ }
})

function formatDate(d) { if (!d) return ''; const t = new Date(d); return `${t.getFullYear()}-${String(t.getMonth()+1).padStart(2,'0')}-${String(t.getDate()).padStart(2,'0')}` }
function formatPrice(p) { return p != null ? Number(p).toFixed(2) : '0.00' }
</script>

<style scoped>
.stat-card {
  display: flex; align-items: center; gap: 16px; padding: 4px 0;
}
.stat-icon {
  width: 56px; height: 56px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: var(--tea-primary);
}
.stat-num { font-size: 1.8rem; font-weight: 700; color: var(--text-primary); font-family: var(--font-serif); }
.stat-label { font-size: 0.85rem; color: var(--text-secondary); margin-top: 2px; }

.quick-list { display: flex; flex-direction: column; gap: 10px; }
.quick-item { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid var(--tea-border); }
.quick-item:last-child { border-bottom: none; }
.quick-title { color: var(--text-primary); font-size: 0.9rem; }
.quick-meta { color: var(--text-secondary); font-size: 0.8rem; white-space: nowrap; }
</style>