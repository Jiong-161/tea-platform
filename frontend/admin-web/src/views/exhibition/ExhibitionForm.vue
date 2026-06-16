<!-- 展览新增/编辑对话框 -->
<template>
  <el-dialog v-model="visible" :title="isEdit ? '编辑展览' : '新增展览'" width="600px" destroy-on-close @closed="reset">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="展览标题" prop="title">
        <el-input v-model="form.title" placeholder="展览标题" />
      </el-form-item>
      <el-form-item label="地点" prop="location">
        <el-input v-model="form.location" placeholder="展览地点" />
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" />
      </el-form-item>
      <el-form-item label="封面URL" prop="cover">
        <el-input v-model="form.cover" placeholder="封面图片URL" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.status" style="width:100%">
          <el-option label="未开始" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已结束" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="展览介绍" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="展览详细介绍" />
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
import { saveExhibition } from '@/api/admin-exhibition'

const emit = defineEmits(['saved'])
const visible = ref(false); const isEdit = ref(false); const editId = ref(null)
const saving = ref(false); const formRef = ref(null)

const form = reactive({
  title: '', location: '', startTime: '', endTime: '', cover: '', description: '', status: 0
})
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  description: [{ required: true, message: '请输入展览介绍', trigger: 'blur' }]
}

function open(row) {
  if (row) {
    isEdit.value = true; editId.value = row.id
    Object.assign(form, {
      title: row.title || '', location: row.location || '',
      startTime: row.startTime || '', endTime: row.endTime || '',
      cover: row.cover || '', description: row.description || '', status: row.status ?? 0
    })
  } else {
    isEdit.value = false; editId.value = null; reset()
  }
  visible.value = true
}

function reset() {
  Object.assign(form, { title: '', location: '', startTime: '', endTime: '', cover: '', description: '', status: 0 })
}

async function submit() {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  saving.value = true
  try {
    await saveExhibition({ ...form }, editId.value)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    visible.value = false; emit('saved')
  } catch {} finally { saving.value = false }
}

defineExpose({ open })
</script>