import axios from 'axios'

/**
 * 创建 axios 实例
 * @description 配置基础请求设置
 */
const service = axios.create({
  baseURL: '/api', // 统一代理前缀，vite.config.js 需配置代理
  timeout: 5000
})

/**
 * 请求拦截器
 * @description 在请求发送前统一处理请求配置
 */
service.interceptors.request.use(
  config => {
    // 记录请求信息
    console.log('发送请求:', config.method.toUpperCase(), config.url, config.data || config.params);
    // 添加认证 token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * @description 在收到响应后统一处理响应数据
 */
service.interceptors.response.use(
  response => {
    console.log('收到响应:', response.config.url, response.status, response.data);
    return response
  },
  error => {
    console.error('响应错误:', 
      error.config?.url, 
      error.response?.status, 
      error.response?.data || error.message
    );
    return Promise.reject(error)
  }
)

export default service 