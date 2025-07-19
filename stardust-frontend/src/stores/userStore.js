import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

/**
 * 用户状态管理store
 * 使用Pinia进行状态管理，包含用户登录、注册、登出等功能
 */
export const useUserStore = defineStore('user', () => {
  // 用户状态
  // 尝试从 localStorage 读取用户信息
  const storedUserInfo = localStorage.getItem('userInfo');
  let initialUserInfo = null;
  if (storedUserInfo) {
    try {
      initialUserInfo = JSON.parse(storedUserInfo);
      // 确保读取到的 id 是字符串类型
      if (initialUserInfo && initialUserInfo.id !== undefined) {
        initialUserInfo.id = String(initialUserInfo.id);
      } else {
        // 如果 userInfo 或 id 不存在/格式错误，则视为无效
        console.error("Error parsing userInfo from localStorage or id is missing/invalid.", initialUserInfo);
        initialUserInfo = null;
        localStorage.removeItem('userInfo'); // 清除无效存储
        localStorage.removeItem('token');
      }
    } catch (e) {
      console.error("Error parsing userInfo from localStorage:", e);
      initialUserInfo = null;
      localStorage.removeItem('userInfo'); // 清除无效存储
      localStorage.removeItem('token');
    }
  }

  const userInfo = ref(initialUserInfo); // 用户信息
  const token = ref(localStorage.getItem('token') || ''); // 用户token
  const isLoggedIn = ref(!!token.value && !!userInfo.value); // 登录状态不仅取决于token，也取决于userInfo是否存在

  /**
   * 用户登录方法
   * @param {string} username - 用户名
   * @param {string} password - 密码
   * @returns {Promise<{success: boolean, data?: object, message: string}>} 登录结果
   */
  const login = async (username, password) => {
    try {
      // 调用登录接口
      const res = await request.post('/user/login', { username, password })
      const userData = res.data.data

      // 更新状态
      userData.id = String(userData.id)
      // 移除profile字段
      delete userData.profile
      userInfo.value = userData
      token.value = userData.token
      isLoggedIn.value = true

      // 持久化存储
      localStorage.setItem('userInfo', JSON.stringify(userData))
      localStorage.setItem('token', userData.token)

      return { success: true, data: userData, message: '登录成功' }
    } catch (e) {
      console.error('登录失败:', e)
      // 错误信息处理
      const errorMsg = e.response?.data?.message ||
        e.response?.data?.error ||
        e.message ||
        '登录失败，请检查用户名和密码'

      return { success: false, message: errorMsg }
    }
  }

  /**
   * 用户注册方法
   * @param {string} username - 用户名
   * @param {string} password - 密码
   * @param {string} confirmPassword - 确认密码
   * @returns {Promise<{success: boolean, data?: object, message: string}>} 注册结果
   */
  const register = async (username, password, confirmPassword) => {
    try {
      // 调用注册接口
      const res = await request.post('/user/register', {
        username,
        password,
        confirmPassword
      })
      const userData = res.data.data

      // 注册成功后自动登录
      userData.id = String(userData.id)
      userInfo.value = userData
      token.value = userData.token
      isLoggedIn.value = true

      // 持久化存储
      localStorage.setItem('userInfo', JSON.stringify(userData))
      localStorage.setItem('token', userData.token)

      return { success: true, data: userData, message: '注册成功' }
    } catch (e) {
      console.error('注册失败:', e)
      // 错误信息处理
      const errorMsg = e.response?.data?.message ||
        e.response?.data?.error ||
        e.message ||
        '注册失败，请稍后再试'

      return { success: false, message: errorMsg }
    }
  }

  /**
   * 用户登出方法
   * @returns {{success: boolean, message: string}} 登出结果
   */
  const logout = () => {
    // 清除状态
    userInfo.value = null
    token.value = ''
    isLoggedIn.value = false

    // 清除本地存储
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')

    return { success: true, message: '已退出登录' }
  }

  /**
   * 获取用户详细信息
   * @param {string} userId - 用户ID
   * @returns {Promise<{success: boolean, data?: object, message: string}>} 获取结果
   */
  const fetchUserInfo = async (userId) => {
    try {
      // 调用获取用户信息接口
      const res = await request.get(`/user/${userId}`)
      const userData = res.data.data

      // 更新用户信息
      userData.id = String(userData.id)
      delete userData.profile
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