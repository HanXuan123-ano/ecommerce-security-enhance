<template>
  <div class="verify-container">
    <el-card class="verify-card">
      <template #header>
        <h2 style="text-align: center; margin: 0;">二次验证</h2>
        <p style="text-align: center; color: #666; margin: 10px 0 0 0; font-size: 14px;">
          请输入验证码完成登录（5分钟有效，一次性使用）
        </p>
      </template>

      <el-form :model="verifyForm">
        <el-form-item>
          <el-input
              v-model="verifyForm.code"
              placeholder="请输入6位验证码"
              maxlength="6"
              style="text-align: center; font-size: 28px; letter-spacing: 12px;"
              autofocus
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              style="width: 100%; height: 50px; font-size: 18px;"
              :loading="loading"
              @click="handleVerify">
            验证并登录
          </el-button>
        </el-form-item>

        <div style="text-align: center; margin-top: 15px;">
          <el-button type="info" plain @click="sendCode" :loading="sending">
            发送验证码
          </el-button>
          <el-link style="margin-left: 20px;" @click="$router.push('/login')">返回登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const sending = ref(false)

const verifyForm = ref({ code: '' })
const username = ref(route.query.username || '')

// 修复后的发送验证码
const sendCode = async () => {
  if (!username.value) {
    ElMessage.error('用户名丢失，请重新登录')
    return
  }
  sending.value = true
  try {
    // 正确传递 query 参数的方式
    await request.post('/auth/verify-code', null, {
      params: { username: username.value }
    })
    ElMessage.success('验证码已发送（请查看后端控制台）')
  } catch (error) {
    ElMessage.error('发送失败，请重试')
    console.error('发送验证码失败详情:', error)
  } finally {
    sending.value = false
  }
}

// 修复后的验证
const handleVerify = async () => {
  if (!verifyForm.value.code || verifyForm.value.code.length !== 6) {
    ElMessage.error('请输入6位验证码')
    return
  }

  loading.value = true
  try {
    await request.post('/auth/verify', null, {
      params: {
        username: username.value,
        code: verifyForm.value.code
      }
    })
    ElMessage.success('2FA验证成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '验证码错误或已过期')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.verify-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.verify-card {
  width: 420px;
}
</style>