<template>
  <div class="account-page">
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
          <router-link to="/account" class="nav-item active">👤 账号</router-link>
        </div>
      </div>
    </div>

    <div class="main-content">
      <el-card class="account-card">
        <el-row>
          <el-col :span="6">
            <el-menu :default-active="activeMenu" @select="handleMenuSelect">
              <el-menu-item index="profile">👤 个人资料</el-menu-item>
              <el-menu-item index="address">📍 收货地址</el-menu-item>
              <el-menu-item index="payment">💳 支付方式</el-menu-item>
            </el-menu>
          </el-col>

          <el-col :span="18">
            <div v-if="activeMenu === 'profile'" class="content-panel">
              <h2>个人资料</h2>
              <p>用户名：testuser</p>
              <p>邮箱：test@example.com</p>
            </div>
            <div v-else-if="activeMenu === 'address'" class="content-panel">
              <h2>收货地址</h2>
              <p>上海市浦东新区张江高科技园区</p>
            </div>
            <div v-else-if="activeMenu === 'payment'" class="content-panel">
              <h2>支付方式</h2>
              <p>支付宝 / 微信支付（模拟）</p>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeMenu = ref('profile')
const searchKeyword = ref('')

const handleMenuSelect = (key) => {
  activeMenu.value = key
}
</script>

<style scoped>
.account-page { min-height: 100vh; background: #f5f7fa; }
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
.account-card { max-width: 1100px; margin: 90px auto 20px; }
.content-panel { padding: 20px; }
</style>