<!-- 文章新增/编辑对话框 -->
<template>
  <el-dialog v-model="visible" :title="isEdit ? '编辑文章' : '发布文章'" width="640px" destroy-on-close @closed="reset">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="文章标题" />
      </el-form-item>
      <el-form-item label="作者" prop="author">
        <el-input v-model="form.author" placeholder="作者名" />
      </el-form-item>
      <el-form-item label="摘要" prop="summary">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要" />
      </el-form-item>
      <el-form-item label="封面URL" prop="cover">
        <el-input v-model="form.cover" placeholder="图片URL（可不填）" />
      </el-form-item>
      <el-form-item label="正文" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="8" placeholder="文章正文内容（支持HTML）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { saveArticle, getCategoryList } from '@/api/admin-content'

const emit = defineEmits(['saved'])
const visible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const saving = ref(false)
const formRef = ref(null)
const categories = ref([])

const form = reactive({
  categoryId: null, title: '', summary: '', cover: '', content: '', author: ''
})

const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  summary: [{ required: true, message: '请输入摘要', trigger: 'blur' }],
  content: [{ required: true, message: '请输入正文', trigger: 'blur' }]
}

async function open(row) {
  // 加载分类
  try {
    const res = await getCategoryList()
    categories.value = res?.data || []
  } catch { categories.value = [] }

  if (row) {
    isEdit.value = true
    editId.value = row.id
    Object.assign(form, {
      categoryId: row.categoryId, title: row.title || '', summary: row.summary || '',
      cover: row.cover || '', content: row.content || '', author: row.author || ''
    })
  } else {
    isEdit.value = false
    editId.value = null
    reset()
  }
  visible.value = true
}

function reset() {
  Object.assign(form, { categoryId: null, title: '', summary: '', cover: '', content: '', author: '' })
}

async function submit() {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  saving.value = true
  try {
    await saveArticle({ ...form }, editId.value)
    ElMessage.success(isEdit.value ? '编辑成功' : '发布成功')
    visible.value = false
    emit('saved')
  } catch {} finally { saving.value = false }
}

defineExpose({ open })
</script>