import axios from 'axios'
import { useUserStore } from '../store/user'

// 创建用于刷新 token 的 axios 实例（不使用拦截器，避免循环调用）
const refreshService = axios.create({
  baseURL: '/api',
  timeout: 5000
})

/**
 * 自动刷新 Token 的中间件
 * 在请求拦截器中检查 token 是否即将过期，如果即将过期则自动刷新
 */
export const setupTokenRefresh = (service) => {
  // 请求拦截器 - 检查并刷新 token
  service.interceptors.request.use(
    async config => {
      // 从 Pinia store 获取 token
      const userStore = useUserStore()
      const token = userStore.getToken()
      
      // 如果存在 token，检查是否即将过期
      if (token) {
        try {
          // 解析 token 中的过期时间
          const payload = JSON.parse(atob(token.split('.')[1]))
          const expirationTime = payload.exp * 1000 // 转换为毫秒
          const now = Date.now()
          
          // 如果剩余时间少于 5 分钟，自动刷新 token
          if (expirationTime - now < 5 * 60 * 1000) {
            console.log('Token 即将过期，自动刷新...')
            
            try {
              // 调用刷新接口
              const response = await refreshService.post('/auth/refresh', null, {
                headers: {
                  'Authorization': `Bearer ${token}`
                }
              })
              
              if (response.data && response.data.code === 200) {
                const newToken = response.data.data
                
                // 保存新 token 到 store
                userStore.setToken(newToken)
                
                // 使用新 token
                config.headers['Authorization'] = `Bearer ${newToken}`
                console.log('Token 刷新成功')
              }
            } catch (error) {
              console.error('Token 刷新失败，可能需要重新登录', error)
              // 刷新失败，不阻断当前请求，继续使用旧 token
            }
          }
        } catch (error) {
          // Token 解析失败，忽略错误
          console.warn('Token 解析失败:', error.message)
        }
      }
      
      return config
    },
    error => {
      console.error('Request interceptor error:', error)
      return Promise.reject(error)
    }
  )
}
