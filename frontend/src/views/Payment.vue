<template>
  <div class="payment-container">
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

const loadOrder = async () => {
  try {
    const res = await request.get(`/orders?username=${username.value}`)
    // 关键修复：拦截器已经返回最内层数组，直接使用 res
    const order = res.find(o => o.id === Number(orderId.value))
    if (order) {
      orderNo.value = order.orderNo
      totalAmount.value = order.totalAmount
      items.value = order.items || []
    }
  } catch (error) {
    console.error(error)
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
    const res = await request.post(`/orders/pay?orderId=${orderId.value}`)
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
.payment-container { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; padding: 20px; }
.payment-card { width: 100%; max-width: 600px; }
.order-info { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
.total-price { font-size: 28px; font-weight: bold; color: #e74c3c; }
.preview-item { padding: 8px 0; border-bottom: 1px solid #eee; }
.preview-item:last-child { border-bottom: none; }
.pay-section { margin-top: 30px; }
.tips { margin-top: 20px; }
</style>