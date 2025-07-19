import axios from 'axios'

const service = axios.create({
  baseURL: '/api', // 统一代理前缀，vite.config.js 需配置代理
  timeout: 300000
})

// 请求拦截器（可选，根据需要扩展）
service.interceptors.request.use(
  config => {
    // 可在此处添加 token 等
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => response,
  error => Promise.reject(error)
)

export default service
