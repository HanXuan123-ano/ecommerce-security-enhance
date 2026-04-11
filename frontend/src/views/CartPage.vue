<template>
  <div class="cart-container">
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
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const username = ref('testuser')
const cartItems = ref([])

const totalAmount = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
)

const loadCart = async () => {
  try {
    console.log('🚀 开始请求购物车接口...')
    const res = await request.get(`/cart?username=${username.value}`)

    console.log('📥 后端返回的完整 res:', res)

    // 关键修复：拦截器已经返回最内层数据，直接取 res.items
    cartItems.value = res?.items || []

    console.log('✅ 最终 cartItems 赋值结果:', cartItems.value)
  } catch (error) {
    console.error('❌ loadCart 发生错误:', error)
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
.cart-container { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: flex-start; justify-content: center; padding: 20px; }
.cart-card { width: 100%; max-width: 900px; }
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