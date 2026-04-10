import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

// Element Plus（按需导入，已在 vite.config.js 配置）
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// Pinia
import { createPinia } from 'pinia'

// Router
import router from './router'

const app = createApp(App)

app.use(ElementPlus, { locale: zhCn })

const pinia = createPinia()
app.use(pinia)
app.use(router)

app.mount('#app')