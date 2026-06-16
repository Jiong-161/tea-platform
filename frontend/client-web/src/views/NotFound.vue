<!--
  404 页面未找到
-->
<template>
  <MainLayout>
    <div class="not-found">
      <div class="not-found-inner">
        <div class="error-code">404</div>
        <h2>页面未找到</h2>
        <p>您访问的页面不存在或已被移除</p>
        <div class="divider"></div>
        <el-button type="primary" round size="large" @click="$router.push('/')">
          <el-icon><HomeFilled /></el-icon>返回首页
        </el-button>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '@/layout/MainLayout.vue'
</script>

<style scoped>
.not-found {
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.error-code {
  font-family: var(--font-serif);
  font-size: 8rem;
  font-weight: 700;
  color: var(--tea-primary);
  line-height: 1;
  opacity: 0.3;
  letter-spacing: 8px;
}

.not-found h2 {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  color: var(--text-primary);
  margin: var(--space-md) 0 var(--space-sm);
}

.not-found p {
  color: var(--text-secondary);
  margin-bottom: var(--space-lg);
}

.divider {
  width: 60px;
  height: 2px;
  background: var(--tea-accent);
  margin: 0 auto var(--space-lg);
}
</style>