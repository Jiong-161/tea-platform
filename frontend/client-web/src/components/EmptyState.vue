<!--
  空状态公共组件
  用于列表无数据、搜索无结果等场景
  Props:
    - icon: 图标名称（Element Plus icon name），默认 'Goods'
    - title: 标题文字，默认 '暂无数据'
    - description: 描述文字
    - actionText: 操作按钮文字
    - actionLink: 操作按钮跳转链接
-->
<template>
  <div class="empty-state">
    <div class="empty-visual">
      <el-icon :size="iconSize" class="empty-icon">
        <component :is="iconComponent" />
      </el-icon>
    </div>
    <h3 v-if="title" class="empty-title">{{ title }}</h3>
    <p v-if="description" class="empty-desc">{{ description }}</p>
    <router-link
      v-if="actionText && actionLink"
      :to="actionLink"
      class="empty-action"
    >
      {{ actionText }}
    </router-link>
    <slot />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  Goods,
  Document,
  ShoppingCart,
  FolderOpened,
  Search,
  Picture,
} from '@element-plus/icons-vue'

const props = defineProps({
  /** Element Plus 图标组件名或字符串 */
  icon: {
    type: [String, Object],
    default: 'Goods',
  },
  title: {
    type: String,
    default: '暂无数据',
  },
  description: {
    type: String,
    default: '',
  },
  actionText: {
    type: String,
    default: '',
  },
  actionLink: {
    type: String,
    default: '',
  },
})

/** 图标尺寸 */
const iconSize = computed(() => 64)

/** 图标映射：支持字符串名称传入 */
const iconMap = {
  Goods, Document, ShoppingCart, FolderOpened, Search, Picture,
}

const iconComponent = computed(() => {
  if (typeof props.icon === 'string') {
    return iconMap[props.icon] || Goods
  }
  return props.icon
})
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  padding: var(--space-xl) var(--space-md);
  text-align: center;
}

.empty-visual {
  margin-bottom: var(--space-md);
}

.empty-icon {
  color: var(--tea-border);
}

.empty-title {
  font-family: var(--font-serif);
  font-size: 1.15rem;
  color: var(--text-secondary);
  margin-bottom: var(--space-xs);
}

.empty-desc {
  font-size: 0.9rem;
  color: var(--text-placeholder);
  margin-bottom: var(--space-md);
  max-width: 320px;
}

.empty-action {
  display: inline-block;
  background: var(--tea-primary);
  color: #FFF;
  padding: 10px 28px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all var(--transition-normal);
}

.empty-action:hover {
  background: var(--tea-primary-hover);
  box-shadow: var(--shadow-md);
  color: #FFF;
  transform: translateY(-2px);
}
</style>
