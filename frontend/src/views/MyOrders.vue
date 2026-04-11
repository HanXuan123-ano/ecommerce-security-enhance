<template>
  <div class="my-orders-container">
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

            <!-- 新增：未支付订单显示“去支付”按钮 -->
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

const filteredOrders = computed(() => {
  if (statusFilter.value === 'all') return orders.value
  return orders.value.filter(order => order.status === statusFilter.value)
})

const loadOrders = async () => {
  try {
    const res = await request.get(`/orders?username=${username.value}`)
    orders.value = res || []
  } catch (error) {
    console.error(error)
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
.my-orders-container { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: flex-start; justify-content: center; padding: 20px; }
.orders-card { width: 100%; max-width: 900px; }
.header-title { display: flex; justify-content: space-between; align-items: center; }
.order-item { margin-bottom: 16px; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.order-items { margin: 12px 0; }
.item-row { display: flex; justify-content: space-between; padding: 6px 0; border-bottom: 1px dashed #eee; }
.item-row:last-child { border-bottom: none; }
.order-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; font-size: 16px; }
.total { font-weight: bold; color: #e74c3c; }
</style>