<!-- 用户管理 — 列表/搜索/禁用/删除 -->
<template>
  <AdminLayout>
    <div class="page-toolbar">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称" clearable style="width:260px" @clear="load" @keyup.enter="load">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" @click="load">
        <el-icon><Search /></el-icon>搜索
      </el-button>
    </div>

    <el-card shadow="never">
      <div v-if="loading" class="loading-container"><el-icon class="is-loading" :size="32"><Loading /></el-icon></div>
      <template v-else>
        <el-table :data="list" border stripe v-loading="loading" empty-text="暂无用户">
          <el-table-column prop="id" label="ID" width="100" />
          <el-table-column prop="username" label="用户名" width="140" />
          <el-table-column prop="nickname" label="昵称" width="140" />
          <el-table-column label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="row.role === 1 ? 'danger' : 'info'" size="small">
                {{ row.role === 1 ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '正常' : '已禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="注册时间" min-width="170">
            <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button text size="small" type="primary" @click="toggleStatus(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-popconfirm title="确定删除该用户？此操作不可恢复" @confirm="del(row.id)" v-if="row.role !== 1">
                <template #reference><el-button text size="small" type="danger">删除</el-button></template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top:16px;display:flex;justify-content:flex-end">
          <el-pagination
            v-model:current-page="page" :page-size="size" :total="total"
            layout="total, prev, pager, next" @current-change="load" background small
          />
        </div>
      </template>
    </el-card>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AdminLayout from '@/layout/AdminLayout.vue'
import { getUserList, toggleUserStatus, deleteUser } from '@/api/admin-user'

const list = ref([])
const loading = ref(true)
const keyword = ref('')
const page = ref(1)
const size = 10
const total = ref(0)

onMounted(() => load())
async function load() {
  loading.value = true
  try {
    const res = await getUserList({ page: page.value, size, keyword: keyword.value || undefined })
    list.value = res?.data?.records || []
    total.value = res?.data?.total || 0
  } catch { list.value = [] } finally { loading.value = false }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await toggleUserStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
    load()
  } catch { /* 拦截器处理 */ }
}

async function del(id) {
  try {
    await deleteUser(id)
    ElMessage.success('已删除')
    load()
  } catch { /* 拦截器处理 */ }
}

function formatDate(d) { if (!d) return ''; const t = new Date(d); return `${t.getFullYear()}-${String(t.getMonth()+1).padStart(2,'0')}-${String(t.getDate()).padStart(2,'0')} ${String(t.getHours()).padStart(2,'0')}:${String(t.getMinutes()).padStart(2,'0')}` }
</script>