import { createWebHistory, createRouter } from 'vue-router'
import routes from './routes'
const router = createRouter({
    history: createWebHistory(),
    routes,
  })

// 路由守卫：未登录用户访问需要登录的页面时跳转到登录页
router.beforeEach((to, from, next) => {
  if (to.meta && to.meta.requiresAuth) {
    const userInfo = localStorage.getItem('userInfo');
    if (!userInfo) {
      next('/login');
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router