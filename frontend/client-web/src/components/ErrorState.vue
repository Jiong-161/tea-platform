<!--
  通用错误状态组件
  当数据加载失败时显示错误信息和重试按钮
-->
<template>
  <div class="error-state">
    <el-icon :size="48"><WarningFilled /></el-icon>
    <p class="error-msg">{{ message }}</p>
    <el-button type="primary" round @click="$emit('retry')">重新加载</el-button>
  </div>
</template>

<script setup>
import { WarningFilled } from '@element-plus/icons-vue'

defineProps({
  message: {
    type: String,
    default: '数据加载失败，请检查后端服务是否启动'
  }
})

defineEmits(['retry'])
</script>

<style scoped>
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 260px;
  padding: var(--space-xl);
  color: var(--text-secondary);
  gap: var(--space-md);
}

.error-msg {
  font-size: 0.95rem;
  color: var(--text-secondary);
  text-align: center;
}
</style>