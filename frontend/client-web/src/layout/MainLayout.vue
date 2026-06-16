<!--
  主布局组件
  顶部导航 + 内容区域 + 底部信息栏
  所有页面通过此布局获得统一的 Header 和 Footer
-->
<template>
  <div class="app-container">
    <Header />
    <main class="main-content">
      <slot />
    </main>
    <Footer />
    <BackToTop />
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import BackToTop from '@/components/BackToTop.vue'
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  width: 100%;
}
</style>