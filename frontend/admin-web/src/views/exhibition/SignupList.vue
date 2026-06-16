<!-- 展览报名记录查看 -->
<template>
  <AdminLayout>
    <div class="page-toolbar">
      <h3 style="font-family:var(--font-serif)">展览报名记录</h3>
      <el-select v-model="exhibitionId" placeholder="按展览筛选" clearable style="width:240px" @change="load">
        <el-option v-for="e in exhibitions" :key="e.id" :label="e.title" :value="e.id" />
      </el-select>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border stripe v-loading="loading" empty-text="暂无报名记录">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="exhibitionId" label="展览ID" width="80" />
        <el-table-column prop="username" label="报名用户" width="140" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="createTime" label="报名时间" min-width="170">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AdminLayout from '@/layout/AdminLayout.vue'
import { getSignupList, getExhibitionList } from '@/api/admin-exhibition'

const list = ref([]); const exhibitions = ref([])
const loading = ref(true); const exhibitionId = ref(undefined)

onMounted(async () => {
  try {
    const [signups, exhs] = await Promise.all([
      getSignupList({}), getExhibitionList({ page: 1, size: 100 })
    ])
    list.value = signups?.data || []
    exhibitions.value = exhs?.data?.records || []
  } catch { list.value = [] } finally { loading.value = false }
})

async function load() {
  loading.value = true
  try {
    const res = await getSignupList({ exhibitionId: exhibitionId.value || undefined })
    list.value = res?.data || []
  } catch { list.value = [] } finally { loading.value = false }
}

function formatDate(d) { if (!d) return ''; const t = new Date(d); return `${t.getFullYear()}-${String(t.getMonth()+1).padStart(2,'0')}-${String(t.getDate()).padStart(2,'0')} ${String(t.getHours()).padStart(2,'0')}:${String(t.getMinutes()).padStart(2,'0')}` }
</script>