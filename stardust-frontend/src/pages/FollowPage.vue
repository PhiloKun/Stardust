<template>
    <div id="follow-page" :class="{ 'dark-mode': isDarkMode }">
        <template v-if="isLoggedIn">
            <h1>关注</h1>
            <!-- 这里可以放关注内容列表 -->
        </template>
        <template v-else>
            <van-empty
                description="您尚未登录"
                image="https://fastly.jsdelivr.net/npm/@vant/assets/custom-empty-image.png"
                class="empty-block"
            >
                <van-button round type="primary" size="small" @click="toLogin">
                    立即登录/注册
                </van-button>
            </van-empty>
        </template>
    </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const isLoggedIn = ref(false);
// 注入全局深色模式
const globalDarkMode = inject('darkMode', ref(false));
const isDarkMode = ref(globalDarkMode.value);

const checkLoginStatus = () => {
    try {
        const savedUserInfo = localStorage.getItem('userInfo');
        isLoggedIn.value = !!savedUserInfo;
    } catch (error) {
        isLoggedIn.value = false;
    }
};

const toLogin = () => {
    router.push('/login');
};

onMounted(() => {
    checkLoginStatus();
});
</script>

<style scoped>
#follow-page {
    min-height: 100vh;
    width: 100vw;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: var(--background-color, #f7f8fa);
    color: var(--text-color, #333);
    transition: background-color 0.3s, color 0.3s;
    box-sizing: border-box;
}

.dark-mode {
    background-color: var(--background-color, #121212) !important;
    color: var(--text-color, #f5f5f5) !important;
}

.empty-block {
    /* 让van-empty内容在深色下更清晰 */
    --van-empty-description-color: var(--text-color, #f5f5f5);
    --van-empty-image-size: 120px;
    --van-empty-padding: 32px 0;
}

.dark-mode .van-button {
    /* 深色下按钮更突出 */
    background: var(--primary-color, #1989fa) !important;
    color: #fff !important;
    border: none;
}
</style>
