const routes = [
    { path: '/', redirect: '/home' },
    { 
        path: '/home', 
        component: () => import('@/pages/HomePage.vue'),
        children: [
            { path: '', redirect: '/home/recommendation' },
            { path: 'recommendation', component: () => import('@/pages/RecommendationPage.vue') },
            { path: 'follow', component: () => import('@/pages/FollowPage.vue') }
        ]
    },
    { path: '/about', component: () => import('@/pages/AboutPage.vue') },
    { path: '/publish', component: () => import('@/pages/PublishPage.vue') },
    { path: '/user', component: () => import('@/pages/UserPage.vue') },
    { path: '/login', component: () => import('@/pages/LoginPage.vue') },
    { path: '/register', component: () => import('@/pages/RegisterPage.vue') },
    { path: '/search', component: () => import('@/pages/SearchPage.vue') },
    { path: '/shop', component: () => import('@/pages/ShopPage.vue') },
    { path: '/message', component: () => import('@/pages/MessagePage.vue') },
]

export default routes