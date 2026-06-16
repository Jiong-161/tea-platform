<!-- 文章管理 — 列表/搜索/审核/删除/编辑 -->
<template>
  <AdminLayout>
    <div class="page-toolbar">
      <div style="display:flex;gap:8px;flex-wrap:wrap">
        <el-input v-model="keyword" placeholder="搜索标题" clearable style="width:220px" @keyup.enter="load">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:120px" @change="load">
          <el-option label="全部" :value="undefined" />
          <el-option label="已发布" :value="1" />
          <el-option label="待审核" :value="0" />
        </el-select>
        <el-button type="primary" @click="openForm()">
          <el-icon><Plus /></el-icon>发布文章
        </el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border stripe v-loading="loading" empty-text="暂无文章">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="viewCount" label="阅读" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">{{ row.status === 1 ? '已发布' : '待审' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button text size="small" type="primary" @click="openForm(row)">编辑</el-button>
            <el-button text size="small" type="success" @click="audit(row, 1)" v-if="row.status !== 1">通过</el-button>
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

    <!-- 编辑对话框 -->
    <ArticleForm ref="formRef" @saved="load" />
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AdminLayout from '@/layout/AdminLayout.vue'
import ArticleForm from './ArticleForm.vue'
import { getArticleList, deleteArticle, auditArticle } from '@/api/admin-content'

const list = ref([])
const loading = ref(true)
const keyword = ref('')
const statusFilter = ref(undefined)
const page = ref(1)
const size = 10
const total = ref(0)
const formRef = ref(null)

onMounted(() => load())
async function load() {
  loading.value = true
  try {
    const res = await getArticleList({ page: page.value, size, keyword: keyword.value || undefined, status: statusFilter.value })
    list.value = res?.data?.records || []
    total.value = res?.data?.total || 0
  } catch { list.value = [] } finally { loading.value = false }
}

function openForm(row) { formRef.value?.open(row) }
async function audit(row, status) {
  try { await auditArticle(row.id, status); ElMessage.success(status === 1 ? '审核通过' : '已驳回'); load() } catch {}
}
async function del(id) {
  try { await deleteArticle(id); ElMessage.success('已删除'); load() } catch {}
}
function formatDate(d) { if (!d) return ''; const t = new Date(d); return `${t.getFullYear()}-${String(t.getMonth()+1).padStart(2,'0')}-${String(t.getDate()).padStart(2,'0')}` }
</script>