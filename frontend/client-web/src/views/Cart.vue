<!--
  购物车页 — 管理购物车中的商品
  功能：查看、修改数量、删除商品、下单
-->
<template>
  <MainLayout>
    <div class="page-banner">
      <div class="container">
        <h1 class="banner-title">我的购物车</h1>
        <p class="banner-sub">确认心仪茶品，一并收入囊中</p>
      </div>
    </div>

    <div class="container page-section">
      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>

      <!-- 错误状态 -->
      <ErrorState v-else-if="error" :message="error" @retry="loadCart" />

      <!-- 购物车有内容时 -->
      <template v-else-if="!error && cartItems.length">
        <div class="cart-table-wrap">
          <!-- PC 端表格布局 -->
          <table class="cart-table">
            <thead>
              <tr>
                <th class="col-image">商品</th>
                <th class="col-info"></th>
                <th class="col-price">单价</th>
                <th class="col-qty">数量</th>
                <th class="col-subtotal">小计</th>
                <th class="col-action">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in cartItems" :key="item.id">
                <td class="col-image">
                  <TeaImage :src="item.productCover" :alt="item.productName" img-class="cart-image" />
                </td>
                <td class="col-info">
                  <router-link :to="`/product/${item.productId}`" class="cart-name">
                    {{ item.productName }}
                  </router-link>
                </td>
                <td class="col-price">
                  <span class="price">¥{{ formatPrice(item.price) }}</span>
                </td>
                <td class="col-qty">
                  <el-input-number
                    v-model="item.quantity"
                    :min="1"
                    :max="99"
                    size="small"
                    @change="handleUpdateQty(item)"
                  />
                </td>
                <td class="col-subtotal">
                  <span class="price">¥{{ formatPrice(item.subtotal) }}</span>
                </td>
                <td class="col-action">
                  <el-button
                    type="danger"
                    size="small"
                    text
                    @click="handleDelete(item)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- 移动端卡片布局 -->
          <div class="cart-mobile-cards">
            <div class="cart-item-card" v-for="item in cartItems" :key="'m-' + item.id">
              <div class="mobile-item-top">
                <TeaImage :src="item.productCover" :alt="item.productName" img-class="mobile-cart-img" />
                <div class="mobile-item-info">
                  <router-link :to="`/product/${item.productId}`" class="cart-name">
                    {{ item.productName }}
                  </router-link>
                  <span class="price mobile-price">¥{{ formatPrice(item.price) }}</span>
                </div>
                <el-button
                  type="danger"
                  size="small"
                  text
                  class="mobile-delete-btn"
                  @click="handleDelete(item)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <div class="mobile-item-bottom">
                <div class="mobile-qty-wrap">
                  <el-input-number
                    v-model="item.quantity"
                    :min="1"
                    :max="99"
                    size="small"
                    @change="handleUpdateQty(item)"
                  />
                </div>
                <div class="mobile-subtotal">
                  <span class="subtotal-label">小计：</span>
                  <span class="price">¥{{ formatPrice(item.subtotal) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部操作栏 -->
        <div class="cart-footer">
          <div class="cart-total">
            <span class="total-label">合计：</span>
            <span class="price">¥{{ formatPrice(totalAmount) }}</span>
          </div>
          <div class="cart-actions">
            <el-button @click="handleClearAll">清空购物车</el-button>
            <el-button type="primary" size="large" round @click="handleCheckout">
              立即结算
            </el-button>
          </div>
        </div>
      </template>

      <!-- 购物车为空 -->
      <EmptyState
        v-else-if="!error"
        icon="ShoppingCart"
        title="购物车空空如也"
        description="快去挑选心仪的茶品吧~"
        action-text="去逛逛"
        action-link="/products"
      />
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/layout/MainLayout.vue'
import ErrorState from '@/components/ErrorState.vue'
import EmptyState from '@/components/EmptyState.vue'
import TeaImage from '@/components/TeaImage.vue'
import { getCartList, updateCart, deleteCartItem, createOrderFromCart } from '@/api/product'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const cartItems = ref([])
const loading = ref(true)

onMounted(() => {
  loadCart()
})

const error = ref('')

async function loadCart() {
  error.value = ''
  loading.value = true
  try {
    const res = await getCartList()
    if (res && res.data) {
      cartItems.value = res.data || []
    }
    cartStore.refresh()
  } catch {
    error.value = '购物车数据加载失败，请检查后端服务是否启动'
    cartItems.value = []
  } finally {
    loading.value = false
  }
}

function formatPrice(price) {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

/** 合计金额 */
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.price || 0) * (item.quantity || 0)
  }, 0)
})

/** 修改数量 */
async function handleUpdateQty(item) {
  try {
    await updateCart({ productId: item.productId, quantity: item.quantity })
    // 更新本地小计
    item.subtotal = (item.price || 0) * item.quantity
  } catch {
    // 错误已在拦截器处理，重新加载保证数据一致
    loadCart()
  }
}

/** 删除单个商品 */
async function handleDelete(item) {
  try {
    await ElMessageBox.confirm(`确定删除"${item.productName}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    // 用户取消
    return
  }
  try {
    await deleteCartItem(item.id)
    cartItems.value = cartItems.value.filter(i => i.id !== item.id)
    cartStore.refresh()
    ElMessage.success('已删除')
  } catch {
    // API错误已在拦截器提示，重新加载保证数据一致
    loadCart()
  }
}

/** 清空购物车 */
async function handleClearAll() {
  try {
    await ElMessageBox.confirm('确定清空购物车吗？', '确认清空', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    // 用户取消
    return
  }
  try {
    // 逐个删除
    for (const item of cartItems.value) {
      await deleteCartItem(item.id)
    }
    cartItems.value = []
    cartStore.clear()
    ElMessage.success('购物车已清空')
  } catch {
    // API错误时重新加载
    loadCart()
  }
}

/** 结算下单（批量下单，合并为一个订单） */
async function handleCheckout() {
  // 先确认
  try {
    await ElMessageBox.confirm('确认下单购买购物车中的所有商品吗？', '确认下单', {
      confirmButtonText: '确认下单',
      cancelButtonText: '再看看',
      type: 'info'
    })
  } catch {
    // 用户取消确认
    return
  }

  // 确认后调用下单接口
  try {
    const res = await createOrderFromCart()
    // 检查响应状态
    if (res && res.code === 200) {
      cartItems.value = []
      cartStore.clear()
      ElMessage.success(res.message || '下单成功！')
      router.push('/my-order')
    } else {
      ElMessage.error(res?.message || '下单失败')
    }
  } catch (e) {
    // API异常时重新加载购物车（拦截器已弹错误提示）
    console.error('下单失败:', e)
    loadCart()
  }
}
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #4A6741, var(--tea-primary));
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

/* 购物车表格 */
.cart-table-wrap {
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
  overflow: hidden;
}

.cart-table {
  width: 100%;
  border-collapse: collapse;
}

.cart-table th {
  background: var(--tea-primary-light);
  color: var(--tea-primary);
  font-weight: 600;
  padding: 14px 16px;
  text-align: left;
  font-size: 0.9rem;
}

.cart-table td {
  padding: 16px;
  border-bottom: 1px solid var(--tea-border);
  vertical-align: middle;
}

.cart-table tbody tr:last-child td {
  border-bottom: none;
}

.col-image { width: 120px; }
.col-price, .col-qty, .col-subtotal, .col-action { width: 120px; text-align: center; }
.col-info { min-width: 200px; }

.cart-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  background: var(--tea-bg);
}

.cart-name {
  color: var(--text-primary);
  font-weight: 500;
  font-size: 0.95rem;
}

.cart-name:hover {
  color: var(--tea-primary);
}

.col-price, .col-subtotal {
  text-align: center;
}

.price {
  color: var(--tea-price);
  font-weight: 700;
  font-size: 1.05rem;
}

/* 底部操作栏 */
.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: var(--space-lg);
  margin-top: var(--space-lg);
  padding: var(--space-md) var(--space-lg);
  background: var(--tea-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--tea-shadow);
}

.total-label {
  font-size: 1rem;
  color: var(--text-primary);
}

.cart-total .price {
  font-size: 1.5rem;
}

.cart-actions {
  display: flex;
  gap: var(--space-sm);
}

.empty-sub {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: var(--space-md);
}

/* 移动端卡片布局（PC端隐藏） */
.cart-mobile-cards { display: none; }

@media (max-width: 768px) {
  /* 隐藏 PC 表格，显示移动端卡片 */
  .cart-table { display: none; }
  .cart-mobile-cards { display: flex; flex-direction: column; gap: var(--space-md); }

  .cart-item-card {
    background: var(--tea-white);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
    padding: var(--space-md);
    display: flex;
    flex-direction: column;
    gap: var(--space-sm);
  }

  .mobile-item-top {
    display: flex;
    align-items: center;
    gap: var(--space-sm);
  }

  .mobile-cart-img {
    width: 72px;
    height: 72px;
    object-fit: cover;
    border-radius: var(--radius-md);
    flex-shrink: 0;
    background: var(--tea-bg);
  }

  .mobile-item-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .mobile-price {
    font-size: 1rem;
  }

  .mobile-delete-btn {
    flex-shrink: 0;
    padding: 4px;
  }

  .mobile-item-bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: var(--space-sm);
    border-top: 1px solid var(--tea-border);
    margin-left: 88px;
  }

  .mobile-subtotal {
    font-size: 0.95rem;
  }

  .subtotal-label {
    color: var(--text-secondary);
    font-size: 0.85rem;
  }

  .cart-footer { flex-direction: column; align-items: stretch; }
  .cart-total { text-align: left; }
  .cart-actions { justify-content: flex-end; }
}
</style>