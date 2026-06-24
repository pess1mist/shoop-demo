<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="logo-wrapper">
          <el-icon class="logo-icon">
            <DataLine />
          </el-icon>
        </div>
        <h1 class="title">智能制造内控管理系统</h1>
        <p class="subtitle">智能 · 高效 · 安全</p>
      </div>

      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" prefix-icon="User" clearable />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" prefix-icon="Lock"
            show-password />
        </el-form-item>

        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>© 2025 智能制造内控管理系统。All rights reserved.</p>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataLine } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 表单验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
})

// 处理登录
const handleLogin = async () => {
  // ElMessage.success('登录成功')
  // console.log(route,router)
  // const redirect = route.query.redirect || '/dashboard'
  // router.push(redirect)
  // return false;
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await login({
          username: loginForm.username,
          password: loginForm.password
        })

        // {
        //   "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwidXNlcklkIjoxLCJpYXQiOjE3NzQ5MjIzNTksImV4cCI6MTc3NTAwODc1OX0.KUsqVT_T0DVU4-HUyfPeokZQHvYy-Eq8IPkDnphZgmE",
        //     "user": {
        //     "id": 1,
        //       "username": "test",
        //         "realName": "测试用户",
        //           "email": "test@example.com",
        //             "phone": "12345678901"
        //   }
        // }
        // 保存 token 和用户信息
        userStore.setToken(response.token)
        userStore.setUserInfo(response.user)

        ElMessage.success('登录成功')

        // 跳转到之前访问的页面或首页
        const redirect = route.query.redirect || '/dashboard'
        router.push(redirect)
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.login-box {
  position: relative;
  z-index: 10;
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-wrapper {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.logo-icon {
  font-size: 40px;
  color: white;
}

.title {
  font-size: 26px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px 0;
}

.subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-form {
  margin-top: 30px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 2px;
}

.login-footer {
  margin-top: 30px;
  text-align: center;
}

.login-footer p {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -150px;
  right: -150px;
  animation-delay: 5s;
}

.circle-3 {
  width: 250px;
  height: 250px;
  top: 50%;
  right: 10%;
  animation-delay: 10s;
}

@keyframes float {

  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }

  33% {
    transform: translate(50px, -50px) scale(1.1);
  }

  66% {
    transform: translate(-50px, 50px) scale(0.9);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
  }

  .title {
    font-size: 22px;
  }

  .logo-wrapper {
    width: 60px;
    height: 60px;
  }

  .logo-icon {
    font-size: 30px;
  }
}
</style>
