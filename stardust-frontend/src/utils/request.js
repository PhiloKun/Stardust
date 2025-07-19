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

export function fetchVideoList() {
  return service.get('/video/list');
}

export function uploadAvatar(file, userId) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('userId', userId);
  return service.post('/user/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}

export function fetchUserVideos(userId) {
  return service.get(`/video/user/${userId}`);
}

export function fetchVideoDetail(id) {
  return service.get(`/video/${id}`);
}

export function deleteVideo(videoId, userId) {
  return service.delete(`/video/${videoId}`, { headers: { userId } });
}

export function favoriteVideo(videoId, userId) {
  return service.post(`/api/video/favorite/${videoId}`, null, { params: { userId } });
}
export function unfavoriteVideo(videoId, userId) {
  return service.delete(`/api/video/favorite/${videoId}`, { params: { userId } });
}
export function getVideoFavoriteCount(videoId) {
  return service.get(`/api/video/favorite/${videoId}/count`);
}
export function getVideoFavoriteStatus(videoId, userId) {
  return service.get(`/api/video/favorite/${videoId}/status`, { params: { userId } });
} 