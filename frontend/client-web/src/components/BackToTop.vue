<!--
  回到顶部按钮组件
  滚动超过阈值后显示，点击平滑滚动到页面顶部
  Props:
    - threshold: 显示阈值（像素），默认 300
    - right: 距右距离，默认 32px
    - bottom: 距底距离，默认 40px
-->
<template>
  <Transition name="back-top-fade">
    <button
      v-if="visible"
      class="back-to-top"
      :style="{ right, bottom }"
      @click="scrollToTop"
      aria-label="回到顶部"
      title="回到顶部"
    >
      <el-icon :size="18"><ArrowUp /></el-icon>
    </button>
  </Transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ArrowUp } from '@element-plus/icons-vue'

const props = defineProps({
  threshold: { type: Number, default: 300 },
  right: { type: String, default: '32px' },
  bottom: { type: String, default: '40px' },
})

const visible = ref(false)

function onScroll() {
  visible.value = window.scrollY > props.threshold
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.back-to-top {
  position: fixed;
  z-index: var(--z-sticky);
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--tea-primary);
  color: #FFF;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-lg);
  transition: all var(--transition-normal);
}

.back-to-top:hover {
  background: var(--tea-primary-hover);
  transform: translateY(-3px);
  box-shadow: var(--shadow-xl);
}

.back-to-top:active {
  transform: scale(0.92) translateY(0);
}

/* 过渡动画 */
.back-top-fade-enter-active {
  transition: all 0.35s var(--ease-out-back);
}
.back-top-fade-leave-active {
  transition: all 0.25s ease-in;
}
.back-top-fade-enter-from {
  opacity: 0;
  transform: translateY(16px) scale(0.8);
}
.back-top-fade-leave-to {
  opacity: 0;
  transform: translateY(8px) scale(0.9);
}
</style>
