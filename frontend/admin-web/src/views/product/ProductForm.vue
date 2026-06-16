<!-- 商品新增/编辑表单页 -->
<template>
  <AdminLayout>
    <div class="page-toolbar">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/products' }">商品管理</el-breadcrumb-item>
        <el-breadcrumb-item>{{ isEdit ? '编辑商品' : '新增商品' }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-card shadow="never" style="max-width:720px">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        v-loading="submitting"
      >
        <el-form-item label="商品名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入商品名称" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="封面图URL" prop="cover">
          <el-input v-model="form.cover" placeholder="请输入商品封面图URL地址" />
          <div v-if="form.cover" style="margin-top:8px">
            <el-image :src="form.cover" style="width:120px;height:120px;border-radius:4px" fit="cover">
              <template #error><span style="color:#999;font-size:12px">图片加载失败</span></template>
            </el-image>
          </div>
        </el-form-item>

        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" style="width:200px" />
          <span style="margin-left:8px;color:#999">元</span>
        </el-form-item>

        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="1" style="width:200px" />
        </el-form-item>

        <el-form-item label="商品状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="6"
            placeholder="请输入商品描述详情"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '立即创建' }}
          </el-button>
          <el-button @click="$router.push('/products')">返回列表</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </AdminLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AdminLayout from '@/layout/AdminLayout.vue'
import { createProduct, updateProduct, getProductDetail } from '@/api/admin-product'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const isEdit = ref(false)
const editId = ref(null)

const form = reactive({
  title: '',
  cover: '',
  description: '',
  price: undefined,
  stock: undefined,
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能为负数', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'integer', min: 0, message: '库存不能为负数', trigger: 'blur' }
  ]
}

onMounted(() => {
  const id = route.query.edit
  if (id) {
    isEdit.value = true
    // 保持ID为字符串，避免 JS 大数精度丢失（Snowflake ID场景）
    editId.value = id
    loadProduct(id)
  }
})

/** 编辑时加载商品数据（使用专用API，不受分页限制） */
async function loadProduct(id) {
  try {
    const res = await getProductDetail(id)
    const product = res?.data || res
    if (product && product.id) {
      form.title = product.title || ''
      form.cover = product.cover || ''
      form.description = product.description || ''
      form.price = product.price != null ? product.price : 0
      form.stock = product.stock != null ? product.stock : 0
      form.status = product.status != null ? product.status : 1
    } else {
      ElMessage.error('商品不存在')
      router.push('/products')
    }
  } catch {
    ElMessage.error('加载商品信息失败')
    router.push('/products')
  }
}

/** 提交新增或编辑 */
async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const data = {
      title: form.title.trim(),
      cover: form.cover || '',
      description: form.description.trim(),
      price: form.price != null ? Number(form.price) : 0,
      stock: form.stock != null ? Number(form.stock) : 0,
      status: form.status != null ? form.status : 1
    }

    let res
    if (isEdit.value) {
      res = await updateProduct(editId.value, data)
    } else {
      res = await createProduct(data)
    }

    if (res && res.code === 200) {
      ElMessage.success(isEdit.value ? '商品修改成功' : '商品新增成功')
      router.push('/products')
    } else {
      ElMessage.error(res?.message || '操作失败，请重试')
    }
  } catch {
    // 错误已在拦截器提示
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-toolbar {
  margin-bottom: 16px;
}
</style>