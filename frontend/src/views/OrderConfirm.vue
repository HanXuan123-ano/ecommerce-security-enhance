<template>
  <div class="order-confirm-container">
    <!-- 固定头部导航栏（右贴边优化） -->
    <div class="fixed-header">
      <div class="header-content">
        <h1 class="system-title">电商安全增强系统</h1>

        <div class="search-box">
          <el-input v-model="searchKeyword" placeholder="搜索商品..." prefix-icon="Search" clearable />
        </div>

        <div class="nav-bar">
          <router-link to="/products" class="nav-item">🏠 首页</router-link>
          <router-link to="/cart" class="nav-item">🛒 购物车</router-link>
          <router-link to="/my-orders" class="nav-item">📦 我的订单</router-link>
          <router-link to="/account" class="nav-item">👤 账号</router-link>
        </div>
      </div>
    </div>

    <div class="main-content">
      <el-card class="order-card">
        <template #header>
          <h2 style="margin: 0;">订单确认</h2>
        </template>

        <div v-if="cartItems.length > 0" class="items-section">
          <h3>商品信息</h3>
          <el-card v-for="item in cartItems" :key="item.id" class="item-card" shadow="hover">
            <div class="item-info">
              <el-image :src="item.product.imageUrl" fit="cover" class="product-img" />
              <div class="product-detail">
                <h4>{{ item.product.name }}</h4>
                <p class="price">¥{{ item.price }} × {{ item.quantity }}</p>
              </div>
              <div class="subtotal">
                ¥{{ (item.price * item.quantity).toFixed(2) }}
              </div>
            </div>
          </el-card>
        </div>

        <div class="total-section">
          <div class="total-row">
            <span class="label">商品总金额</span>
            <span class="value">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>

        <div class="address-section">
          <h3>收货信息</h3>
          <el-card shadow="never">
            <p><strong>收货人：</strong>测试用户</p>
            <p><strong>联系电话：</strong>138xxxx8888</p>
            <p><strong>收货地址：</strong>上海市浦东新区张江高科技园区</p>
          </el-card>
        </div>

        <div class="action-section">
          <el-button
              type="primary"
              size="large"
              style="width: 100%; height: 56px; font-size: 18px;"
              :loading="loading"
              @click="handleCreateOrder">
            确认下单并支付
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
const loading = ref(false)
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

const handleCreateOrder = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.error('购物车为空，无法下单')
    return
  }

  loading.value = true
  try {
    const res = await request.post(`/orders/create?username=${username.value}`)
    ElMessage.success('订单创建成功！')
    router.push({
      path: '/payment',
      query: { orderId: res.id }
    })
  } catch (error) {
    ElMessage.error(error.message || '下单失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.order-confirm-container { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.fixed-header { position: fixed; top: 0; left: 0; right: 0; height: 70px; background: linear-gradient(90deg, #667eea, #764ba2); color: white; z-index: 1000; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.header-content {
  width: 100%;
  max-width: none;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 20px;
}
.system-title { font-size: 22px; font-weight: bold; margin: 0; }
.search-box { flex: 1; max-width: 420px; }
.nav-bar {
  display: flex;
  gap: 24px;
  align-items: center;
  margin-left: auto !important;
  margin-right: 25px !important;
}
.nav-item { color: white; text-decoration: none; font-size: 16px; padding: 6px 12px; border-radius: 6px; transition: all 0.3s; display: flex; align-items: center; gap: 6px; }
.nav-item:hover, .nav-item.active { background: rgba(255,255,255,0.2); }
.main-content { padding-top: 90px; }
.order-card { width: 100%; max-width: 800px; margin: 0 auto; }
.item-card { margin-bottom: 12px; }
.item-info { display: flex; align-items: center; gap: 16px; }
.product-img { width: 80px; height: 80px; border-radius: 8px; }
.product-detail { flex: 1; }
.subtotal { font-size: 18px; font-weight: bold; color: #e74c3c; }
.total-section { margin: 20px 0; padding: 16px; background: #f8f9fa; border-radius: 8px; }
.total-row { display: flex; justify-content: space-between; font-size: 18px; font-weight: bold; }
.action-section { margin-top: 24px; }
</style>