<!--
  商品列表页 — 精选茶叶商品浏览
  展示所有在售商品的卡片网格，含价格、库存信息
-->
<template>
  <MainLayout>
    <!-- 页面标题横幅 -->
    <div class="page-banner">
      <div class="container">
        <h1 class="banner-title">精选茶品</h1>
        <p class="banner-sub">好茶知时节，甄选上品好茶</p>
      </div>
    </div>

    <div class="container page-section">
      <!-- 骨架屏加载态 -->
      <div v-if="loading" class="skeleton-grid">
        <div class="card skeleton-card" v-for="i in 8" :key="i">
          <div class="skeleton skeleton-img"></div>
          <div class="skeleton-card-body">
            <div class="skeleton skeleton-text" style="width: 70%"></div>
            <div class="skeleton skeleton-text" style="width: 100%"></div>
            <div class="skeleton skeleton-text" style="width: 40%"></div>
          </div>
        </div>
      </div>

      <!-- 错误状态 -->
      <ErrorState v-else-if="error" :message="error" @retry="loadProducts()" />

      <!-- 商品网格 -->
      <template v-else-if="!error">
        <div class="card-grid" v-if="products.length">
          <div
            v-for="item in products"
            :key="item.id"
            class="card product-card"
          >
            <router-link :to="`/product/${item.id}`" class="card-link">
              <div class="cover-wrap">
                <TeaImage :src="item.cover" :alt="item.title" img-class="card-cover" />
                <div v-if="item.stock <= 0" class="sold-out-mask">
                  <span>已售罄</span>
                </div>
              </div>
              <div class="card-body">
                <h3 class="card-title">{{ item.title }}</h3>
                <p class="card-desc">{{ item.description }}</p>
                <div class="card-footer">
                  <span class="price">¥{{ formatPrice(item.price) }}</span>
                  <span class="stock-info" v-if="item.stock > 0">
                    库存 {{ item.stock }}
                  </span>
                </div>
              </div>
            </router-link>
            <div class="card-actions">
              <el-button
                type="primary"
                size="small"
                round
                :disabled="item.stock <= 0"
                @click="addToCart(item)"
              >
                <el-icon><ShoppingCart /></el-icon>加入购物车
              </el-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <EmptyState
          v-else
          icon="Goods"
          title="暂无商品"
          description="还没有上架的茶品，敬请期待~"
          action-text="返回首页"
          action-link="/"
        />
      </template>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import EmptyState from '@/components/EmptyState.vue'
import TeaImage from '@/components/TeaImage.vue'
import { getProductList } from '@/api/product'
import { addToCart as addToCartApi } from '@/api/product'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const products = ref([])
const loading = ref(true)
const error = ref('')

onMounted(() => {
  loadProducts()
})

async function loadProducts() {
  error.value = ''
  loading.value = true
  try {
    const res = await getProductList()
    if (res && res.data) {
      products.value = res.data || []
    }
  } catch {
    error.value = '商品数据加载失败，请检查后端服务是否启动'
    products.value = []
  } finally {
    loading.value = false
  }
}

/** 格式化价格 */
function formatPrice(price) {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

/** 加入购物车 */
async function addToCart(item) {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addToCartApi({ productId: item.id, quantity: 1 })
    ElMessage.success('已加入购物车')
    // 刷新 Header 购物车角标
    const { useCartStore } = await import('@/stores/cart')
    useCartStore().refresh()
  } catch {
    // 错误已在拦截器处理
  }
}
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #5E7A5C, var(--tea-primary));
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

.card-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

.card {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  overflow: hidden;
  transition: all var(--transition-normal);
  display: flex;
  flex-direction: column;
}

.card:hover {
  transform: translateY(-6px);
  box-shadow: var(--tea-shadow-hover);
}

.card-link {
  color: inherit;
  text-decoration: none;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.cover-wrap {
  position: relative;
  overflow: hidden;
}

.card-cover {
  width: 100%;
  height: 220px;
  object-fit: cover;
  background: var(--tea-primary-light);
  transition: transform var(--transition-slow);
}

.card:hover .card-cover {
  transform: scale(1.05);
}

.sold-out-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFF;
  font-size: 1.1rem;
  font-weight: 600;
  letter-spacing: 2px;
}

.card-body {
  padding: var(--space-md);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-family: var(--font-serif);
  font-size: 1rem;
  color: var(--text-primary);
  margin-bottom: var(--space-xs);
}

.card-desc {
  font-size: 0.8rem;
  color: var(--text-secondary);
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--space-sm);
}

.price {
  color: var(--tea-price);
  font-weight: 700;
  font-size: 1.15rem;
}

.stock-info {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.card-actions {
  padding: 0 var(--space-md) var(--space-md);
}

.card-actions .el-button {
  width: 100%;
}

@media (max-width: 1200px) {
  .card-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .card-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 480px) {
  .card-grid { grid-template-columns: 1fr; }
}

/* ========== 骨架屏 ========== */
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

.skeleton-card {
  pointer-events: none;
}

.skeleton-card-body {
  padding: var(--space-md);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

@media (max-width: 1200px) {
  .skeleton-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .skeleton-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 480px) {
  .skeleton-grid { grid-template-columns: 1fr; }
}
</style>