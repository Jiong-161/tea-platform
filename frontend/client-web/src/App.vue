<!--
  根组件
  使用 <router-view> 渲染当前路由匹配的页面组件
  配合 <transition> 实现页面切换淡入淡出效果
-->
<template>
  <router-view v-slot="{ Component }">
    <transition name="page-fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup>
// 根组件无需额外逻辑
</script>