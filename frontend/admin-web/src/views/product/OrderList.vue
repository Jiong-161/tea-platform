<!-- 订单管理 — 列表/搜索/状态变更 -->
<template>
  <AdminLayout>
    <div class="page-toolbar">
      <div style="display:flex;gap:8px;flex-wrap:wrap">
        <el-input v-model="keyword" placeholder="搜索订单号" clearable style="width:240px" @keyup.enter="load">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:120px" @change="load">
          <el-option label="全部" :value="undefined" />
          <el-option label="已支付" :value="1" />
          <el-option label="待支付" :value="0" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border stripe v-loading="loading" empty-text="暂无订单">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180" show-overflow-tooltip />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">¥{{ formatPrice(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusMap[row.status] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" min-width="170">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button text size="small" type="success" v-if="row.status === 0" @click="updateStatus(row.id, 1)">标记已支付</el-button>
            <el-button text size="small" type="primary" v-if="row.status === 1" @click="updateStatus(row.id, 2)">标记完成</el-button>
            <el-button text size="small" type="danger" v-if="row.status !== 3" @click="updateStatus(row.id, 3)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;display:flex;justify-content:flex-end">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="total,prev,pager,next" @current-change="load" background small />
      </div>
    </el-card>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AdminLayout from '@/layout/AdminLayout.vue'
import { getOrderList, updateOrderStatus } from '@/api/admin-product'

const list = ref([]); const loading = ref(true)
const keyword = ref(''); const statusFilter = ref(undefined)
const page = ref(1); const size = 10; const total = ref(0)
const statusMap = { 0: '待支付', 1: '已支付', 2: '已完成', 3: '已取消' }

onMounted(() => load())
async function load() {
  loading.value = true
  try {
    const res = await getOrderList({ page: page.value, size, keyword: keyword.value || undefined, status: statusFilter.value })
    list.value = res?.data?.records || []
    total.value = res?.data?.total || 0
  } catch { list.value = [] } finally { loading.value = false }
}

async function updateStatus(id, status) {
  try {
    await updateOrderStatus(id, status)
    ElMessage.success('状态已更新')
    load()
  } catch {}
}

function statusType(s) {
  return s === 1 ? 'success' : s === 2 ? '' : s === 0 ? 'warning' : 'danger'
}

function formatDate(d) { if (!d) return ''; const t = new Date(d); return `${t.getFullYear()}-${String(t.getMonth()+1).padStart(2,'0')}-${String(t.getDate()).padStart(2,'0')} ${String(t.getHours()).padStart(2,'0')}:${String(t.getMinutes()).padStart(2,'0')}` }
function formatPrice(p) { return p != null ? Number(p).toFixed(2) : '0.00' }
</script>