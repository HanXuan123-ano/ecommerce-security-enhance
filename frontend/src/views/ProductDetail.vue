<template>
  <div class="product-detail-page">
    <!-- 固定头部导航栏（与首页一致） -->
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
      <el-card class="detail-card" v-if="product">
        <div class="detail-container">
          <!-- 左侧：大图 -->
          <div class="image-section">
            <el-image :src="product.imageUrl" fit="cover" class="main-image" />
          </div>

          <!-- 右侧：商品信息 -->
          <div class="info-section">
            <h1>{{ product.name }}</h1>
            <p class="description">{{ product.description }}</p>

            <div class="price-info">
              <span class="price">¥{{ product.price }}</span>
              <span class="stock">库存 {{ product.stock }} 件</span>
            </div>

            <div class="quantity-row">
              <span>购买数量</span>
              <el-input-number v-model="quantity" :min="1" :max="product.stock" />
            </div>

            <div class="action-buttons">
              <el-button type="primary" size="large" :loading="addLoading" @click="addToCart">
                加入购物车
              </el-button>
              <el-button type="success" size="large" @click="buyNow">
                立即购买
              </el-button>
            </div>
          </div>
        </div>
      </el-card>

      <el-empty v-else description="商品加载失败" />
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

const product = ref(null)
const quantity = ref(1)
const addLoading = ref(false)
const searchKeyword = ref('')

const loadProduct = async () => {
  try {
    const res = await request.get(`/products/${route.params.id}`)
    product.value = res
  } catch (error) {
    ElMessage.error('加载商品详情失败')
  }
}

const addToCart = async () => {
  if (!product.value) return
  addLoading.value = true
  try {
    await request.post(`/cart/add?username=testuser&productId=${product.value.id}&quantity=${quantity.value}`)
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error('加入失败')
  } finally {
    addLoading.value = false
  }
}

const buyNow = () => {
  if (!product.value) return
  router.push({
    path: '/order-confirm',
    query: { productId: product.value.id, quantity: quantity.value }
  })
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail-page { min-height: 100vh; background: #f5f7fa; }
.fixed-header { position: fixed; top: 0; left: 0; right: 0; height: 70px; background: linear-gradient(90deg, #667eea, #764ba2); color: white; z-index: 1000; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.header-content { max-width: 1200px; margin: 0 auto; height: 100%; display: flex; align-items: center; padding: 0 20px; gap: 20px; }
.system-title { font-size: 22px; font-weight: bold; margin: 0; }
.search-box { flex: 1; max-width: 420px; }
.nav-bar { display: flex; gap: 24px; align-items: center; }
.nav-item { color: white; text-decoration: none; font-size: 16px; padding: 6px 12px; border-radius: 6px; transition: all 0.3s; }
.main-content { padding-top: 90px; max-width: 1200px; margin: 0 auto; padding: 20px; }
.detail-card { max-width: 1100px; margin: 0 auto; }
.detail-container { display: flex; gap: 60px; padding: 40px; }
.image-section { flex: 1; }
.main-image { width: 100%; height: 480px; border-radius: 12px; }
.info-section { flex: 1; }
.price-info { margin: 20px 0; }
.price { font-size: 32px; font-weight: bold; color: #e74c3c; }
.stock { margin-left: 20px; color: #27ae60; }
.quantity-row { margin: 30px 0; display: flex; align-items: center; gap: 15px; }
.action-buttons { display: flex; gap: 16px; }
</style>