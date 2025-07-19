const routes = [
    { path: '/', redirect: '/home' },
    {
        path: '/home',
        component: () => import('@/pages/home/HomePage.vue'),
        children: [
            { path: '', redirect: '/home/recommendation' },
            { path: 'recommendation', component: () => import('@/pages/home/RecommendationPage.vue') },
            { path: 'follow', component: () => import('@/pages/home/FollowPage.vue') }
        ]
    },
    { path: '/about', component: () => import('@/pages/about/AboutPage.vue') },
    { path: '/publish', component: () => import('@/pages/publish/PublishPage.vue'), meta: { requiresAuth: true } },
    { path: '/user', component: () => import('@/pages/user/UserPage.vue') },
    { path: '/login', component: () => import('@/pages/user/LoginPage.vue') },
    { path: '/register', component: () => import('@/pages/user/RegisterPage.vue') },
    { path: '/search', component: () => import('@/pages/search/SearchPage.vue') },
    { path: '/shop', component: () => import('@/pages/shop/ShopPage.vue') },
    { path: '/message', component: () => import('@/pages/message/MessagePage.vue') },
    {
        path: '/video/:id',
        name: 'VideoPlayerPage',
        component: () => import('@/pages/video/VideoPlayerPage.vue')
    },
]

export default routes