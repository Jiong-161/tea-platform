<!-- 商品管理 — 上下架/删除 -->
<template>
  <AdminLayout>
    <div class="page-toolbar">
      <div style="display:flex;gap:8px;flex-wrap:wrap;align-items:center">
        <el-button type="primary" @click="$router.push('/product-form')">
          <el-icon><Plus /></el-icon>新增商品
        </el-button>
        <el-input v-model="keyword" placeholder="搜索商品" clearable style="width:220px" @keyup.enter="load">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:120px" @change="load">
          <el-option label="全部" :value="undefined" />
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border stripe v-loading="loading" empty-text="暂无商品">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button text size="small" type="primary" @click="$router.push(`/product-form?edit=${row.id}`)">
              编辑
            </el-button>
            <el-button text size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggle(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-popconfirm title="确定删除？" @confirm="del(row.id)">
              <template #reference><el-button text size="small" type="danger">删除</el-button></template>
            </el-popconfirm>
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
import { getProductList, toggleProductStatus, deleteProduct } from '@/api/admin-product'

const list = ref([]); const loading = ref(true)
const keyword = ref(''); const statusFilter = ref(undefined)
const page = ref(1); const size = 10; const total = ref(0)

onMounted(() => load())
async function load() {
  loading.value = true
  try {
    const res = await getProductList({ page: page.value, size, keyword: keyword.value || undefined, status: statusFilter.value })
    list.value = res?.data?.records || []
    total.value = res?.data?.total || 0
  } catch { list.value = [] } finally { loading.value = false }
}

async function toggle(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await toggleProductStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
    load()
  } catch {}
}

async function del(id) {
  try { await deleteProduct(id); ElMessage.success('已删除'); load() } catch {}
}

function formatDate(d) { if (!d) return ''; const t = new Date(d); return `${t.getFullYear()}-${String(t.getMonth()+1).padStart(2,'0')}-${String(t.getDate()).padStart(2,'0')}` }
function formatPrice(p) { return p != null ? Number(p).toFixed(2) : '0.00' }
</script>