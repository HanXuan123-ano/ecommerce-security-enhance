import { createRouter, createWebHistory } from 'vue-router'

// 直接导入（不使用懒加载，避免 Vite 解析问题）
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import VerifyCode from '@/views/VerifyCode.vue'   // ← 新增这一行

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
    path: '/verify-code',           // ← 新增验证码路由
    name: 'VerifyCode',
    component: VerifyCode,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router