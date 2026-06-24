import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref(null)

  // 设置 Token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (user) => {
    userInfo.value = user
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  // 获取 Token
  const getToken = () => {
    return token.value || localStorage.getItem('token')
  }

  // 获取用户信息
  const getUserInfo = () => {
    return userInfo.value || JSON.parse(localStorage.getItem('userInfo'))
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 检查是否已登录
  const isLoggedIn = () => {
    return !!getToken()
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    getToken,
    getUserInfo,
    logout,
    isLoggedIn
  }
})
