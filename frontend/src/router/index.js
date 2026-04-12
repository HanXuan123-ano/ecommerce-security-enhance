import { createRouter, createWebHistory } from 'vue-router'

// ==================== 基础页面 ====================
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import VerifyCode from '@/views/VerifyCode.vue'

// ==================== 业务页面 ====================
import CartPage from '@/views/CartPage.vue'
import OrderConfirm from '@/views/OrderConfirm.vue'
import Payment from '@/views/Payment.vue'
import MyOrders from '@/views/MyOrders.vue'
import Products from '@/views/Products.vue'

// ==================== 新增页面 ====================
import ProductDetail from '@/views/ProductDetail.vue'
import Account from '@/views/Account.vue'           // 新增：账号详情页

const routes = [
  {
    path: '/',
    redirect: '/products',
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
  },
  {
    path: '/verify-code',
    name: 'VerifyCode',
    component: VerifyCode,
  },
  // ==================== 商品展示相关 ====================
  {
    path: '/products',
    name: 'Products',
    component: Products,
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail,           // 商品详情页
  },
  // ==================== 业务页面 ====================
  {
    path: '/cart',
    name: 'CartPage',
    component: CartPage,
  },
  {
    path: '/order-confirm',
    name: 'OrderConfirm',
    component: OrderConfirm,
  },
  {
    path: '/payment',
    name: 'Payment',
    component: Payment,
  },
  {
    path: '/my-orders',
    name: 'MyOrders',
    component: MyOrders,
  },
  // ==================== 新增：账号详情页 ====================
  {
    path: '/account',
    name: 'Account',
    component: Account,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router