<!--
  商品详情页 — 展示茶叶商品详细信息
  支持加购和立即购买功能（需登录）
-->
<template>
  <MainLayout>
    <div class="container detail-page" v-loading="loading">
      <div v-if="loading" class="loading-container"><el-icon class="is-loading" :size="40"><Loading /></el-icon></div>
      <ErrorState v-else-if="error" :message="error" @retry="loadProduct" />
      <template v-else-if="product.id">
        <!-- 面包屑 -->
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span> / </span>
          <router-link to="/products">精选茶品</router-link>
          <span> / </span>
          <span class="current">{{ product.title }}</span>
        </div>

        <div class="detail-grid">
          <!-- 左侧：商品封面图 -->
          <div class="detail-left">
            <div class="cover-wrapper">
              <TeaImage :src="product.cover" :alt="product.title" img-class="detail-cover" />
            </div>
          </div>

          <!-- 右侧：商品信息 -->
          <div class="detail-right">
            <h1 class="detail-title">{{ product.title }}</h1>

            <div class="price-area">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ formatPrice(product.price) }}</span>
            </div>

            <div class="stock-info">
              <span v-if="product.stock > 0" class="in-stock">
                <el-icon><CircleCheck /></el-icon> 有货（库存 {{ product.stock }}）
              </span>
              <span v-else class="out-stock">
                <el-icon><CircleClose /></el-icon> 已售罄
              </span>
            </div>

            <!-- 数量选择 -->
            <div class="quantity-selector" v-if="product.stock > 0">
              <span class="qty-label">数量</span>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product.stock"
                size="large"
              />
            </div>

            <!-- 操作按钮 -->
            <div class="action-buttons" v-if="product.stock > 0">
              <el-button
                type="primary"
                size="large"
                round
                @click="handleAddToCart"
                :loading="addingToCart"
              >
                <el-icon><ShoppingCart /></el-icon>加入购物车
              </el-button>
              <el-button
                size="large"
                round
                class="buy-now-btn"
                @click="handleBuyNow"
                :loading="buying"
              >
                立即购买
              </el-button>
            </div>

            <div v-else class="sold-out-block">
              <el-button size="large" round disabled>已售罄</el-button>
            </div>
          </div>
        </div>

        <!-- 商品描述 -->
        <section class="detail-desc" v-if="product.description">
          <h2 class="section-title">商品详情</h2>
          <div class="desc-content">{{ product.description }}</div>
        </section>
      </template>

      <!-- 商品不存在 -->
      <div v-else class="empty-container">
        <el-icon :size="64"><Goods /></el-icon>
        <p>商品不存在或已下架</p>
        <el-button @click="$router.push('/products')">返回商品列表</el-button>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import TeaImage from '@/components/TeaImage.vue'
import { getProductDetail, addToCart, createOrder } from '@/api/product'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const product = ref({})
const loading = ref(true)
const error = ref('')
const quantity = ref(1)
const addingToCart = ref(false)
const buying = ref(false)

onMounted(() => {
  loadProduct()
})

async function loadProduct() {
  error.value = ''
  loading.value = true
  try {
    const id = route.params.id
    if (!id) {
      error.value = '商品ID无效'
      product.value = {}
      return
    }
    const res = await getProductDetail(id)
    // 兼容多种响应格式：{code,message,data} 或直接返回data
    const data = res?.data || res
    if (data && (data.id || data.id === 0)) {
      // 确保关键字段有默认值，避免模板渲染空白
      product.value = {
        ...data,
        title: data.title || '未知商品',
        price: data.price != null ? data.price : 0,
        stock: data.stock != null ? data.stock : 0,
        description: data.description || '',
        cover: data.cover || ''
      }
    } else {
      // 返回数据为空→商品不存在或已下架
      product.value = {}
    }
  } catch (e) {
    // catch到的异常说明请求失败（拦截器已弹错误）
    error.value = e?.message || '商品详情加载失败，请检查后端服务是否启动'
    product.value = {}
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
async function handleAddToCart() {
  if (!checkLogin()) return
  addingToCart.value = true
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已加入购物车')
    // 刷新 Header 购物车角标
    const { useCartStore } = await import('@/stores/cart')
    useCartStore().refresh()
  } catch {
    // 错误已在拦截器处理
  } finally {
    addingToCart.value = false
  }
}

/** 立即购买 */
async function handleBuyNow() {
  if (!checkLogin()) return
  buying.value = true
  try {
    await createOrder({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('下单成功！')
    router.push('/my-order')
  } catch {
    // 错误已在拦截器处理
  } finally {
    buying.value = false
  }
}

function checkLogin() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return false
  }
  return true
}
</script>

<style scoped>
.detail-page {
  padding-top: var(--space-lg);
  padding-bottom: var(--space-2xl);
}

.breadcrumb {
  font-size: 0.85rem;
  color: var(--text-secondary);
  margin-bottom: var(--space-lg);
}

.breadcrumb a { color: var(--text-secondary); }
.breadcrumb a:hover { color: var(--tea-primary); }
.breadcrumb .current { color: var(--tea-primary); }

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-xl);
  margin-bottom: var(--space-xl);
}

/* 封面 */
.cover-wrapper {
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--tea-bg);
}

.detail-cover {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  aspect-ratio: 1;
  background: linear-gradient(135deg, var(--tea-primary-light), var(--tea-accent-light));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--tea-primary);
}

/* 商品信息 */
.detail-right {
  display: flex;
  flex-direction: column;
}

.detail-title {
  font-family: var(--font-serif);
  font-size: 1.8rem;
  color: var(--text-primary);
  margin-bottom: var(--space-lg);
}

.price-area {
  margin-bottom: var(--space-md);
  padding: var(--space-md);
  background: #FFF5F5;
  border-radius: var(--radius-md);
}

.price-symbol {
  font-size: 1.2rem;
  color: var(--tea-price);
  font-weight: 600;
}

.price-value {
  font-size: 2.5rem;
  color: var(--tea-price);
  font-weight: 700;
  font-family: var(--font-serif);
}

.stock-info {
  margin-bottom: var(--space-lg);
  font-size: 0.9rem;
}

.in-stock {
  color: #27AE60;
  display: flex;
  align-items: center;
  gap: 4px;
}

.out-stock {
  color: #E74C3C;
  display: flex;
  align-items: center;
  gap: 4px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
}

.qty-label {
  font-size: 0.95rem;
  color: var(--text-primary);
}

.action-buttons {
  display: flex;
  gap: var(--space-md);
}

.action-buttons .el-button {
  height: 48px;
  padding: 12px 32px;
  font-size: 1rem;
}

.buy-now-btn {
  background: var(--tea-accent);
  border-color: var(--tea-accent);
  color: #FFF;
}

.buy-now-btn:hover {
  background: #C8A080;
  border-color: #C8A080;
  color: #FFF;
}

.sold-out-block .el-button {
  width: 200px;
  height: 48px;
}

/* 商品描述 */
.detail-desc {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  padding: var(--space-lg);
}

.detail-desc .section-title {
  margin-bottom: var(--space-md);
}

.desc-content {
  font-size: 1rem;
  line-height: 2;
  color: var(--text-regular);
  white-space: pre-wrap;
}

@media (max-width: 768px) {
  .detail-grid { grid-template-columns: 1fr; }
  .detail-title { font-size: 1.4rem; }
  .price-value { font-size: 2rem; }
}
</style>