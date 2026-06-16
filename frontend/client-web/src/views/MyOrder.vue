<!--
  我的订单页 — 查看历史订单记录
-->
<template>
  <MainLayout>
    <div class="page-banner">
      <div class="container">
        <h1 class="banner-title">我的订单</h1>
        <p class="banner-sub">查看您的订单记录</p>
      </div>
    </div>

    <div class="container page-section">
      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>

      <!-- 错误状态 -->
      <ErrorState v-else-if="error" :message="error" @retry="loadData()" />

      <!-- 订单列表 -->
      <template v-else-if="!error && orders.length">
        <div class="order-list">
          <div v-for="item in orders" :key="item.id" class="order-card">
            <div class="order-header">
              <div class="order-info">
                <span class="order-no">订单号：{{ item.orderNo }}</span>
                <span class="order-date">{{ formatDate(item.createTime) }}</span>
              </div>
              <el-tag :type="item.status === 1 ? 'success' : 'warning'" size="small">
                {{ item.status === 1 ? '已完成' : '待支付' }}
              </el-tag>
            </div>
            <div class="order-body">
              <div class="order-amount">
                <span class="amount-label">订单金额</span>
                <span class="price">¥{{ formatPrice(item.totalAmount) }}</span>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <div v-else class="empty-container">
        <el-icon :size="64"><Tickets /></el-icon>
        <p>还没有订单记录</p>
        <el-button type="primary" round @click="$router.push('/products')">
          去选购茶品
        </el-button>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import { getMyOrder } from '@/api/product'

const orders = ref([])
const loading = ref(true)
const error = ref('')

onMounted(() => {
  loadData()
})

async function loadData() {
  error.value = ''
  loading.value = true
  try {
    const res = await getMyOrder()
    if (res && res.data) {
      orders.value = res.data || []
    }
  } catch {
    error.value = '订单数据加载失败，请检查后端服务是否启动'
    orders.value = []
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

function formatPrice(price) {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #4A6741, var(--tea-primary));
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

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.order-card {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.order-card:hover {
  box-shadow: var(--tea-shadow-hover);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-md) var(--space-lg);
  background: var(--tea-bg);
}

.order-no {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 0.9rem;
  margin-right: var(--space-md);
}

.order-date {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.order-body {
  padding: var(--space-lg);
}

.order-amount {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: var(--space-md);
}

.amount-label {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.price {
  color: #C0392B;
  font-weight: 700;
  font-size: 1.3rem;
}
</style>