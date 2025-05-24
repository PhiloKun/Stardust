import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const token = ref(localStorage.getItem('token') || '')
  const isLoggedIn = ref(!!token.value)

  // 登录方法
  const login = async (username, password) => {
    try {
      const res = await request.post('/user/login', { username, password })
      const userData = res.data.data
      
      // 保存用户信息
      userInfo.value = userData
      token.value = userData.token
      isLoggedIn.value = true
      
      // 存储到本地
      localStorage.setItem('userInfo', JSON.stringify(userData))
      localStorage.setItem('token', userData.token)

      return { success: true, data: userData, message: '登录成功' }
    } catch (e) {
      console.error('登录失败:', e)
      const errorMsg = e.response?.data?.message || 
                      e.response?.data?.error || 
                      e.message || 
                      '登录失败，请检查用户名和密码'
      
      return { success: false, message: errorMsg }
    }
  }
  
  // 注册方法
  const register = async (username, password, confirmPassword) => {
    try {
      const res = await request.post('/user/register', {
        username,
        password,
        confirmPassword
      })
      const userData = res.data.data; 
      
      // 注册成功后，自动登录：保存用户信息和token
      userInfo.value = userData
      token.value = userData.token
      isLoggedIn.value = true

      // 存储到本地
      localStorage.setItem('userInfo', JSON.stringify(userData))
      localStorage.setItem('token', userData.token)

      // 调用 fetchUserInfo 获取完整用户详情
      await fetchUserInfo(userData.id);

      return { success: true, data: userData, message: '注册成功' };
    } catch (e) {
      console.error('注册失败:', e)
      const errorMsg = e.response?.data?.message || 
                      e.response?.data?.error || 
                      e.message || 
                      '注册失败，请稍后再试'

      return { success: false, message: errorMsg }
    }
  }
  
  // 登出方法
  const logout = () => {
    // 清除状态
    userInfo.value = null
    token.value = ''
    isLoggedIn.value = false
    
    // 清除本地存储
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
    
    return { success: true, message: '已退出登录' };
  }
  
  // 获取用户信息
  const fetchUserInfo = async (userId) => {
    try {
      const res = await request.get(`/user/${userId}`)
      const userData = res.data.data
      
      // 更新用户信息
      userInfo.value = userData
      localStorage.setItem('userInfo', JSON.stringify(userData))
      
      return { success: true, data: userData, message: '获取用户信息成功' }
    } catch (e) {
      console.error('获取用户信息失败:', e)
      const errorMsg = e.response?.data?.message || '获取用户信息失败'
      
      return { success: false, message: errorMsg }
    }
  }
  
  return {
    userInfo,
    token,
    isLoggedIn,
    login,
    register,
    logout,
    fetchUserInfo
  }
}) 