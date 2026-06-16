<!--
  全局底部栏 — 新中式茶文化风格
  包含平台信息、快速链接、联系方式
-->
<template>
  <footer class="tea-footer">
    <div class="footer-inner container">
      <!-- 左侧：平台介绍 -->
      <div class="footer-brand">
        <div class="footer-logo">
          <el-icon :size="24"><CoffeeCup /></el-icon>
          <span class="footer-logo-text">茶文·茶文化品鉴购销服务平台</span>
        </div>
        <p class="footer-desc">
          传承千年茶文化，汇聚茶文、展览、茶品于一体。<br />
          以茶会友，品味生活的诗意与雅致。
        </p>
      </div>

      <!-- 中间：快速链接 -->
      <div class="footer-links">
        <h4>快速链接</h4>
        <router-link to="/">首页</router-link>
        <router-link to="/articles">茶文化文章</router-link>
        <router-link to="/exhibitions">展览活动</router-link>
        <router-link to="/products">精选茶品</router-link>
      </div>

      <!-- 中间：用户服务 -->
      <div class="footer-links">
        <h4>用户服务</h4>
        <router-link to="/profile">个人中心</router-link>
        <router-link to="/cart">购物车</router-link>
        <router-link to="/my-order">我的订单</router-link>
        <router-link to="/address">收货地址</router-link>
      </div>

      <!-- 右侧：联系方式 -->
      <div class="footer-contact">
        <h4>联系我们</h4>
        <p><el-icon><Clock /></el-icon> 服务时间：9:00 - 21:00</p>
        <p><el-icon><Message /></el-icon> 邮箱：2084383287@qq.com</p>
        <p><el-icon><Phone /></el-icon> 电话：400-888-6666</p>
      </div>
    </div>

    <!-- 底部版权 -->
    <div class="footer-bottom">
      <div class="container">
        <p>© 2026 茶文·雅集 Tea Culture Platform — 课程设计项目 版权所有</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
// 纯展示组件，无需逻辑
</script>

<style scoped>
/* ---- 页脚整体 ---- */
.tea-footer {
  background: #2C3A30;
  color: rgba(255, 255, 255, 0.75);
  margin-top: var(--space-2xl);
}

.footer-inner {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1.5fr;
  gap: var(--space-xl);
  padding: var(--space-xl) var(--space-md);
}

/* ---- 品牌区 ---- */
.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: var(--space-sm);
  color: #FFF;
}

.footer-logo-text {
  font-family: var(--font-serif);
  font-size: 1.3rem;
  font-weight: 700;
  color: #FFF;
  letter-spacing: 2px;
}

.footer-desc {
  font-size: 0.85rem;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.55);
}

/* ---- 链接区 ---- */
.footer-links h4 {
  color: var(--tea-accent);
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: var(--space-sm);
  font-family: var(--font-sans);
}

.footer-links a {
  display: block;
  color: rgba(255, 255, 255, 0.65);
  font-size: 0.85rem;
  padding: 4px 0;
  transition: all var(--transition-fast);
}

.footer-links a:hover {
  color: var(--tea-accent);
  padding-left: 4px;
}

/* ---- 联系方式 ---- */
.footer-contact h4 {
  color: var(--tea-accent);
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: var(--space-sm);
  font-family: var(--font-sans);
}

.footer-contact p {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.55);
  margin-bottom: 6px;
}

.footer-contact .el-icon {
  color: var(--tea-accent);
}

/* ---- 底部版权条 ---- */
.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: var(--space-md) 0;
  text-align: center;
}

.footer-bottom p {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.35);
}

/* ---- 响应式 ---- */
@media (max-width: 768px) {
  .footer-inner {
    grid-template-columns: 1fr 1fr;
    gap: var(--space-lg);
  }
  .footer-brand {
    grid-column: 1 / -1;
    text-align: center;
  }
  .footer-logo {
    justify-content: center;
  }
}
</style>