<template>
  <div class="payment-container">
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
      <el-card class="payment-card">
        <template #header>
          <h2 style="margin: 0; text-align: center;">订单支付</h2>
        </template>

        <div class="order-info">
          <p><strong>订单编号：</strong> {{ orderNo }}</p>
          <p><strong>支付金额：</strong> <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span></p>
        </div>

        <div v-if="items.length > 0" class="items-preview">
          <h4>支付商品</h4>
          <div v-for="item in items" :key="item.id" class="preview-item">
            {{ item.product.name }} × {{ item.quantity }}
          </div>
        </div>

        <div class="pay-section">
          <el-button
              type="primary"
              size="large"
              style="width: 100%; height: 60px; font-size: 20px;"
              :loading="loading"
              @click="handlePay">
            立即支付 ¥{{ totalAmount.toFixed(2) }}
          </el-button>
        </div>

        <div class="tips">
          <p style="text-align: center; color: #666; font-size: 14px;">
            本系统为模拟支付，仅用于毕业设计演示
          </p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const orderId = ref(route.query.orderId)
const username = ref('testuser')
const orderNo = ref('')
const totalAmount = ref(0)
const items = ref([])
const searchKeyword = ref('')

const loadOrder = async () => {
  try {
    const res = await request.get(`/orders?username=${username.value}`)
    const order = res.find(o => o.id === Number(orderId.value))
    if (order) {
      orderNo.value = order.orderNo
      totalAmount.value = order.totalAmount
      items.value = order.items || []
    }
  } catch (error) {
    ElMessage.error('加载订单信息失败')
  }
}

const handlePay = async () => {
  if (!orderId.value) {
    ElMessage.error('订单ID丢失')
    return
  }

  loading.value = true
  try {
    await request.post(`/orders/pay?orderId=${orderId.value}`)
    ElMessage.success('支付成功！')
    router.push('/my-orders')
  } catch (error) {
    ElMessage.error(error.message || '支付失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (orderId.value) {
    loadOrder()
  } else {
    ElMessage.error('订单ID丢失，请重新下单')
    router.push('/cart')
  }
})
</script>

<style scoped>
.payment-container { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
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
.payment-card { width: 100%; max-width: 600px; margin: 0 auto; }
.order-info { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
.total-price { font-size: 28px; font-weight: bold; color: #e74c3c; }
.preview-item { padding: 8px 0; border-bottom: 1px solid #eee; }
.preview-item:last-child { border-bottom: none; }
.pay-section { margin-top: 30px; }
.tips { margin-top: 20px; }
</style>