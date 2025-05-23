import axios from 'axios'

const service = axios.create({
  baseURL: '/api', // 统一代理前缀，vite.config.js 需配置代理
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 记录请求信息
    console.log('发送请求:', config.method.toUpperCase(), config.url, config.data || config.params);
    // 可在此处添加 token 等
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

// 响应拦截器
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