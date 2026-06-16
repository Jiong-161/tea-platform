<!--
  收货地址管理页 — 新增、编辑、删除、设置默认地址
-->
<template>
  <MainLayout>
    <div class="page-banner">
      <div class="container">
        <h1 class="banner-title">收货地址管理</h1>
        <p class="banner-sub">管理您的收货地址信息</p>
      </div>
    </div>

    <div class="container page-section">
      <!-- 操作栏 -->
      <div class="toolbar">
        <el-button type="primary" @click="openDialog(null)">
          <el-icon><Plus /></el-icon>新增地址
        </el-button>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>

      <!-- 错误状态 -->
      <ErrorState v-else-if="error" :message="error" @retry="loadAddresses" />

      <!-- 地址列表 -->
      <template v-else-if="!error && addresses.length">
        <div class="address-grid">
          <div
            v-for="item in addresses"
            :key="item.id"
            class="address-card"
            :class="{ 'is-default': item.isDefault === 1 }"
          >
            <div class="address-header">
              <div class="address-contact">
                <span class="receiver-name">{{ item.receiverName }}</span>
                <span class="receiver-phone">{{ item.phone }}</span>
              </div>
              <el-tag v-if="item.isDefault === 1" type="success" size="small">
                默认
              </el-tag>
            </div>
            <p class="address-detail">
              {{ item.province }}{{ item.city }}{{ item.district }} {{ item.detail }}
            </p>
            <div class="address-actions">
              <el-button text size="small" @click="openDialog(item)">编辑</el-button>
              <el-button
                v-if="item.isDefault !== 1"
                text
                size="small"
                type="primary"
                @click="handleSetDefault(item)"
              >
                设为默认
              </el-button>
              <el-button text size="small" type="danger" @click="handleDelete(item)">
                删除
              </el-button>
            </div>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <div v-else class="empty-container">
        <el-icon :size="64"><Location /></el-icon>
        <p>暂无收货地址</p>
        <el-button type="primary" round @click="openDialog(null)">
          添加收货地址
        </el-button>
      </div>
    </div>

    <!-- 地址表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑收货地址' : '新增收货地址'"
      width="520px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="收货人" prop="receiverName">
              <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="form.province" placeholder="省份" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="form.city" placeholder="城市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区/县" prop="district">
              <el-input v-model="form.district" placeholder="区/县" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="form.detail"
            type="textarea"
            :rows="2"
            placeholder="街道、门牌号等详细信息"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          保存
        </el-button>
      </template>
    </el-dialog>
  </MainLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

const addresses = ref([])
const loading = ref(true)
const error = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

onMounted(() => {
  loadAddresses()
})

async function loadAddresses() {
  error.value = ''
  loading.value = true
  try {
    const res = await getAddressList()
    if (res && res.data) {
      addresses.value = res.data || []
    }
  } catch {
    error.value = '地址数据加载失败，请检查后端服务是否启动'
    addresses.value = []
  } finally {
    loading.value = false
  }
}

/** 打开新增/编辑对话框 */
function openDialog(item) {
  if (item) {
    isEdit.value = true
    editingId.value = item.id
    Object.assign(form, {
      receiverName: item.receiverName || '',
      phone: item.phone || '',
      province: item.province || '',
      city: item.city || '',
      district: item.district || '',
      detail: item.detail || '',
      isDefault: item.isDefault === 1
    })
  } else {
    isEdit.value = false
    editingId.value = null
    Object.assign(form, {
      receiverName: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detail: '',
      isDefault: false
    })
  }
  dialogVisible.value = true
}

/** 提交表单 */
async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const data = {
      receiverName: form.receiverName,
      phone: form.phone,
      province: form.province,
      city: form.city,
      district: form.district,
      detail: form.detail,
      isDefault: form.isDefault ? 1 : 0
    }

    if (isEdit.value) {
      await updateAddress(editingId.value, data)
      ElMessage.success('地址修改成功')
    } else {
      await addAddress(data)
      ElMessage.success('地址添加成功')
    }

    dialogVisible.value = false
    loadAddresses()
  } catch {
    // 错误已在拦截器处理
  } finally {
    submitting.value = false
  }
}

/** 删除地址 */
async function handleDelete(item) {
  try {
    await ElMessageBox.confirm(`确定删除收货地址"${item.receiverName}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAddress(item.id)
    ElMessage.success('地址已删除')
    loadAddresses()
  } catch {
    // 用户取消
  }
}

/** 设置默认地址 */
async function handleSetDefault(item) {
  try {
    await setDefaultAddress(item.id)
    ElMessage.success('已设为默认地址')
    loadAddresses()
  } catch {
    // 错误已在拦截器处理
  }
}
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #3D5A4B, var(--tea-primary));
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

.toolbar {
  margin-bottom: var(--space-lg);
}

/* 地址卡片网格 */
.address-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-md);
}

.address-card {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  padding: var(--space-lg);
  border: 2px solid transparent;
  transition: all var(--transition-fast);
}

.address-card.is-default {
  border-color: var(--tea-primary);
  background: var(--tea-primary-light);
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-sm);
}

.receiver-name {
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-right: var(--space-md);
}

.receiver-phone {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.address-detail {
  font-size: 0.9rem;
  color: var(--text-regular);
  margin-bottom: var(--space-sm);
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 4px;
}

@media (max-width: 768px) {
  .address-grid { grid-template-columns: 1fr; }
}
</style>