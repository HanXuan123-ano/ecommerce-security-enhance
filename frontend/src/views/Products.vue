<template>
  <div class="products-page">
    <!-- 固定头部导航栏（右贴边优化） -->
    <div class="fixed-header">
      <div class="header-content">
        <h1 class="system-title">电商安全增强系统</h1>

        <div class="search-box">
          <el-input v-model="searchKeyword" placeholder="搜索商品名称..." prefix-icon="Search" clearable />
        </div>

        <div class="nav-bar">
          <router-link to="/products" class="nav-item active">🏠 首页</router-link>
          <router-link to="/cart" class="nav-item">🛒 购物车</router-link>
          <router-link to="/my-orders" class="nav-item">📦 我的订单</router-link>
          <router-link to="/account" class="nav-item">👤 账号</router-link>
        </div>
      </div>
    </div>

    <div class="main-content">
      <el-loading v-if="loading" />

      <el-empty v-else-if="products.length === 0" description="暂无商品" />

      <div v-else class="products-grid">
        <el-card
            v-for="product in filteredProducts"
            :key="product.id"
            class="product-card"
            shadow="hover"
            @click="goToDetail(product.id)">
          <el-image :src="product.imageUrl" fit="cover" class="product-image" />
          <div class="product-info">
            <h3>{{ product.name }}</h3>
            <p class="description">{{ product.description }}</p>
            <div class="price-row">
              <span class="price">¥{{ product.price }}</span>
              <span class="stock">库存 {{ product.stock }} 件</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const products = ref([])
const searchKeyword = ref('')
const loading = ref(true)

const filteredProducts = computed(() => {
  if (!searchKeyword.value) return products.value
  return products.value.filter(p =>
      p.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const loadProducts = async () => {
  try {
    const res = await request.get('/products')
    products.value = res || []
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.products-page { min-height: 100vh; background: #f5f7fa; }
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
.main-content { padding-top: 90px; max-width: 1200px; margin: 0 auto; padding: 20px; }
.products-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 24px; }
.product-card { cursor: pointer; transition: transform 0.3s; }
.product-card:hover { transform: translateY(-8px); }
.product-image { width: 100%; height: 220px; border-radius: 8px; }
.product-info h3 { margin: 12px 0 8px; font-size: 18px; }
.description { color: #666; font-size: 14px; display: -webkit-box; -webkit-line-clamp: 2; overflow: hidden; }
.price-row { display: flex; justify-content: space-between; margin-top: 12px; }
.price { font-size: 20px; font-weight: bold; color: #e74c3c; }
.stock { color: #27ae60; font-size: 14px; }
</style>