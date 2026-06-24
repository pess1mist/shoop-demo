import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000,
  // 禁用默认缓存，确保每次请求都获取最新数据
  headers: {
    'Cache-Control': 'no-cache',
    'Pragma': 'no-cache'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从 Pinia store 获取 token
    const userStore = useUserStore()
    const token = userStore.getToken()
    
    // 如果存在 token，添加到请求头
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 对 GET 请求添加时间戳，防止浏览器缓存
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }
    
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data;
    
    // 检查返回码
    if (res.code !== 200) {
      // 401: 未授权，需要重新登录
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        window.location.href = '/login'
      }
      
      ElMessage.error(res.message || '请求失败');
      return Promise.reject(new Error(res.message || 'Error'));
    }
    
    // 直接返回 data 部分
    return res.data;
  },
  error => {
    console.error('Response error:', error)
    
    // 处理 HTTP 错误状态码
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.message || '请求失败')
      }
    } else {
      ElMessage.error(error.message || '请求失败')
    }
    
    return Promise.reject(error)
  }
)

export default service
