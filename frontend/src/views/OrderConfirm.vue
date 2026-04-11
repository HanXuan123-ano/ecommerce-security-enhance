<template>
  <div class="order-confirm-container">
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

const totalAmount = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
)

const loadCart = async () => {
  try {
    const res = await request.get(`/cart?username=${username.value}`)
    // 关键修复：拦截器已经返回最内层数据
    cartItems.value = res?.items || []
  } catch (error) {
    console.error(error)
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
      query: { orderId: res.id }   // 直接取 res.id
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
.order-confirm-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
.order-card { width: 100%; max-width: 800px; }
.item-card { margin-bottom: 12px; }
.item-info { display: flex; align-items: center; gap: 16px; }
.product-img { width: 80px; height: 80px; border-radius: 8px; }
.product-detail { flex: 1; }
.subtotal { font-size: 18px; font-weight: bold; color: #e74c3c; }
.total-section { margin: 20px 0; padding: 16px; background: #f8f9fa; border-radius: 8px; }
.total-row { display: flex; justify-content: space-between; font-size: 18px; font-weight: bold; }
.action-section { margin-top: 24px; }
</style>