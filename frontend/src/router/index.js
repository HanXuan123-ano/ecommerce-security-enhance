import { createRouter, createWebHistory } from 'vue-router'

// 直接导入（不使用懒加载，避免 Vite 解析问题）
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import VerifyCode from '@/views/VerifyCode.vue'

// ==================== 新增的订单相关页面 ====================
import OrderConfirm from '@/views/OrderConfirm.vue'
import Payment from '@/views/Payment.vue'
import MyOrders from '@/views/MyOrders.vue'
import CartPage from '@/views/CartPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/login',
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
  // ==================== 新增的业务页面路由 ====================
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
  {
    path: '/cart',
    name: 'CartPage',
    component: CartPage,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router