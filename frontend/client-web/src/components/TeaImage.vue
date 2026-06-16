<!--
  茶图组件 — 图片加载失败时显示茶文化风格占位符
  用法: <TeaImage :src="url" :alt="title" class="card-cover" />
-->
<template>
  <div class="tea-image" :class="wrapperClass">
    <img
      v-if="!failed"
      :src="safeSrc"
      :alt="alt"
      :class="imgClass"
      @error="onError"
      @load="onLoad"
    />
    <div v-else class="tea-placeholder" :class="placeholderClass">
      <el-icon :size="iconSize"><CoffeeCup /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { cleanUrl } from '@/utils/urlCleaner'

const props = defineProps({
  src: { type: String, default: '' },
  alt: { type: String, default: '' },
  iconSize: { type: [String, Number], default: 48 },
  imgClass: { type: String, default: '' },
  placeholderClass: { type: String, default: '' },
  wrapperClass: { type: String, default: '' }
})

const failed = ref(false)

/** 自动清洗URL：去除空格，避免 %20 编码导致 404 */
const safeSrc = computed(() => cleanUrl(props.src))

function onError() {
  failed.value = true
}

function onLoad() {
  failed.value = false
}
</script>

<style scoped>
.tea-image {
  position: relative;
  overflow: hidden;
}

.tea-image img {
  display: block;
  width: 100%;
  height: 100%;
}

.tea-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #E8F0ED, #F5EDE4);
  color: var(--tea-primary);
  opacity: 0.6;
}
</style>