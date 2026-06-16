<!-- 展览管理 — 列表/搜索/新增/编辑/删除 -->
<template>
  <AdminLayout>
    <div class="page-toolbar">
      <div style="display:flex;gap:8px;flex-wrap:wrap">
        <el-input v-model="keyword" placeholder="搜索展览" clearable style="width:220px" @keyup.enter="load">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="openForm()"><el-icon><Plus /></el-icon>新增展览</el-button>
        <el-button @click="$router.push('/exhibition/signups')">查看报名记录</el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border stripe v-loading="loading" empty-text="暂无展览">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="展览标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="location" label="地点" width="140" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'info' : 'warning'" size="small">
              {{ statusMap[row.status] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text size="small" type="primary" @click="openForm(row)">编辑</el-button>
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

    <ExhibitionForm ref="formRef" @saved="load" />
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AdminLayout from '@/layout/AdminLayout.vue'
import ExhibitionForm from './ExhibitionForm.vue'
import { getExhibitionList, deleteExhibition } from '@/api/admin-exhibition'

const list = ref([]); const loading = ref(true)
const keyword = ref(''); const page = ref(1); const size = 10; const total = ref(0)
const formRef = ref(null)
const statusMap = { 0: '未开始', 1: '进行中', 2: '已结束', 3: '已取消' }

onMounted(() => load())
async function load() {
  loading.value = true
  try {
    const res = await getExhibitionList({ page: page.value, size, keyword: keyword.value || undefined })
    list.value = res?.data?.records || []
    total.value = res?.data?.total || 0
  } catch { list.value = [] } finally { loading.value = false }
}
function openForm(row) { formRef.value?.open(row) }
async function del(id) { try { await deleteExhibition(id); ElMessage.success('已删除'); load() } catch {} }
function formatDate(d) { if (!d) return ''; const t = new Date(d); return `${t.getFullYear()}-${String(t.getMonth()+1).padStart(2,'0')}-${String(t.getDate()).padStart(2,'0')}` }
</script>