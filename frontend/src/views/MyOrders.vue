<template>
  <div class="my-orders-container">
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
          <router-link to="/my-orders" class="nav-item active">📦 我的订单</router-link>
          <router-link to="/account" class="nav-item">👤 账号</router-link>
        </div>
      </div>
    </div>

    <div class="main-content">
      <el-card class="orders-card">
        <template #header>
          <div class="header-title">
            <h2 style="margin: 0;">我的订单</h2>
            <el-radio-group v-model="statusFilter" size="small">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="PENDING">待支付</el-radio-button>
              <el-radio-button label="PAID">已支付</el-radio-button>
            </el-radio-group>
          </div>
        </template>

        <el-empty v-if="filteredOrders.length === 0" description="暂无订单" />

        <div v-else class="orders-list">
          <el-card
              v-for="order in filteredOrders"
              :key="order.id"
              class="order-item"
              shadow="hover">
            <div class="order-header">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <el-tag :type="order.status === 'PAID' ? 'success' : 'warning'">
                {{ order.status === 'PAID' ? '已支付' : '待支付' }}
              </el-tag>
            </div>

            <div class="order-items">
              <div v-for="item in order.items" :key="item.id" class="item-row">
                <span>{{ item.product.name }}</span>
                <span class="quantity">× {{ item.quantity }}</span>
                <span class="price">¥{{ item.price }}</span>
              </div>
            </div>

            <div class="order-footer">
              <span>共 {{ order.items.length }} 件商品</span>
              <span class="total">实付 ¥{{ order.totalAmount.toFixed(2) }}</span>

              <el-button
                  v-if="order.status === 'PENDING'"
                  type="primary"
                  size="small"
                  style="margin-left: 16px;"
                  @click="goToPay(order.id)">
                去支付
              </el-button>
            </div>
          </el-card>
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
const orders = ref([])
const statusFilter = ref('all')
const searchKeyword = ref('')

const filteredOrders = computed(() => {
  if (statusFilter.value === 'all') return orders.value
  return orders.value.filter(order => order.status === statusFilter.value)
})

const loadOrders = async () => {
  try {
    const res = await request.get(`/orders?username=${username.value}`)
    orders.value = res || []
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  }
}

const goToPay = (orderId) => {
  router.push({
    path: '/payment',
    query: { orderId: orderId }
  })
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.my-orders-container { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
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
.orders-card { width: 100%; max-width: 900px; margin: 0 auto; }
.header-title { display: flex; justify-content: space-between; align-items: center; }
.order-item { margin-bottom: 16px; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.order-items { margin: 12px 0; }
.item-row { display: flex; justify-content: space-between; padding: 6px 0; border-bottom: 1px dashed #eee; }
.item-row:last-child { border-bottom: none; }
.order-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; font-size: 16px; }
.total { font-weight: bold; color: #e74c3c; }
</style>