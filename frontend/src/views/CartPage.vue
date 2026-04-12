<template>
  <div class="cart-container">
    <!-- 固定头部导航栏（极致右贴边） -->
    <div class="fixed-header">
      <div class="header-content">
        <h1 class="system-title">电商安全增强系统</h1>

        <div class="search-box">
          <el-input v-model="searchKeyword" placeholder="搜索商品..." prefix-icon="Search" clearable />
        </div>

        <div class="nav-bar">
          <router-link to="/products" class="nav-item">🏠 首页</router-link>
          <router-link to="/cart" class="nav-item active">🛒 购物车</router-link>
          <router-link to="/my-orders" class="nav-item">📦 我的订单</router-link>
          <router-link to="/account" class="nav-item">👤 账号</router-link>
        </div>
      </div>
    </div>

    <div class="main-content">
      <el-card class="cart-card">
        <template #header>
          <h2 style="margin: 0;">我的购物车</h2>
        </template>

        <el-empty v-if="cartItems.length === 0" description="购物车空空如也～" />

        <div v-else class="cart-items">
          <el-card v-for="item in cartItems" :key="item.id" class="cart-item" shadow="hover">
            <div class="item-content">
              <el-image :src="item.product.imageUrl" fit="cover" class="product-img" />
              <div class="product-info">
                <h4>{{ item.product.name }}</h4>
                <p class="price">单价：¥{{ item.price }}</p>
              </div>
              <div class="quantity-control">
                <el-button circle size="small" @click="updateQuantity(item, item.quantity - 1)">-</el-button>
                <span class="quantity">{{ item.quantity }}</span>
                <el-button circle size="small" @click="updateQuantity(item, item.quantity + 1)">+</el-button>
              </div>
              <div class="subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
              <el-button type="danger" size="small" circle icon="Delete" @click="removeItem(item.id)" />
            </div>
          </el-card>
        </div>

        <div v-if="cartItems.length > 0" class="cart-footer">
          <div class="total-info">
            <span>共 {{ cartItems.length }} 件商品</span>
            <span class="total-amount">合计：<strong>¥{{ totalAmount.toFixed(2) }}</strong></span>
          </div>
          <el-button type="primary" size="large" style="width: 200px; height: 52px;" @click="goToOrderConfirm">
            去结算
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const username = ref('testuser')
const cartItems = ref([])
const searchKeyword = ref('')

const totalAmount = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
)

const loadCart = async () => {
  try {
    const res = await request.get(`/cart?username=${username.value}`)
    cartItems.value = res?.items || []
  } catch (error) {
    ElMessage.error('加载购物车失败')
  }
}

const updateQuantity = async (item, newQuantity) => {
  if (newQuantity < 1) return
  try {
    await request.post(`/cart/add?username=${username.value}&productId=${item.product.id}&quantity=${newQuantity - item.quantity}`)
    loadCart()
  } catch (error) {
    ElMessage.error('修改数量失败')
  }
}

const removeItem = async (cartItemId) => {
  try {
    const item = cartItems.value.find(i => i.id === cartItemId)
    await request.post(`/cart/add?username=${username.value}&productId=${item.product.id}&quantity=-999`)
    loadCart()
    ElMessage.success('商品已删除')
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const goToOrderConfirm = () => {
  if (cartItems.value.length === 0) {
    ElMessage.error('购物车为空')
    return
  }
  router.push('/order-confirm')
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-container { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.fixed-header { position: fixed; top: 0; left: 0; right: 0; height: 70px; background: linear-gradient(90deg, #667eea, #764ba2); color: white; z-index: 1000; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.header-content {
  width: 100%;
  max-width: none;                  /* 关键：去掉最大宽度限制 */
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px 0 20px;           /* 左边留20px，右边不留 */
  gap: 20px;
}
.system-title { font-size: 22px; font-weight: bold; margin: 0; }
.search-box { flex: 1; max-width: 420px; }
.nav-bar {
  display: flex;
  gap: 24px;
  align-items: center;
  margin-left: auto !important;
  margin-right: 25px !important;     /* 强力贴边 */
}
.nav-item {
  color: white;
  text-decoration: none;
  font-size: 16px;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}
.nav-item:hover, .nav-item.active { background: rgba(255,255,255,0.2); }
.main-content { padding-top: 90px; }
.cart-card { width: 100%; max-width: 900px; margin: 0 auto; }
.cart-item { margin-bottom: 12px; }
.item-content { display: flex; align-items: center; gap: 16px; }
.product-img { width: 80px; height: 80px; border-radius: 8px; }
.product-info { flex: 1; }
.quantity-control { display: flex; align-items: center; gap: 8px; margin: 0 20px; }
.quantity { min-width: 30px; text-align: center; font-size: 16px; }
.subtotal { font-size: 18px; font-weight: bold; color: #e74c3c; margin-right: 20px; }
.cart-footer { margin-top: 20px; display: flex; justify-content: space-between; align-items: center; padding-top: 16px; border-top: 2px solid #eee; }
.total-info { font-size: 18px; }
.total-amount { color: #e74c3c; }
</style>