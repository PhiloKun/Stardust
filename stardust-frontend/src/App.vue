<template>
  <van-config-provider :theme="theme">
    <div id="app" :class="{ 'dark-theme': isDarkMode }">
      <router-view />
      <van-tabbar route v-model="active" active-color="#1989fa" v-if="!showTabbar">
        <van-tabbar-item to="/home" name="home" icon="home-o">首页</van-tabbar-item>
        <van-tabbar-item to="/shop" name="shop" icon="cart-o">商城</van-tabbar-item>
        <van-tabbar-item to="/publish" name="publish" icon="plus">发布</van-tabbar-item>
        <van-tabbar-item to="/message" name="message" icon="chat-o">消息</van-tabbar-item>
        <van-tabbar-item to="/user" name="user" icon="user-circle-o">我</van-tabbar-item>
      </van-tabbar>
    </div>
  </van-config-provider>
</template>
<script setup>
import { ref, computed, provide, onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const active = ref("home");
const isDarkMode = ref(false);
const theme = computed(() => (isDarkMode.value ? "dark" : "light"));

// 初始化主题设置
onMounted(() => {
  const savedDarkMode = localStorage.getItem("darkMode");
  if (savedDarkMode !== null) {
    isDarkMode.value = savedDarkMode === "true";
  }
});

// 提供全局主题状态
provide("darkMode", isDarkMode);

// 切换主题的方法
const toggleDarkMode = (value) => {
  isDarkMode.value = value;
  localStorage.setItem("darkMode", value.toString());
  document.documentElement.classList.toggle("dark-theme", value);
};

provide("toggleDarkMode", toggleDarkMode);

const showTabbar = computed(() => {
  // 只要是需要隐藏底部栏的页面，返回 true
  if (/^\/video\//.test(route.path)) return true;
  return (
    route.path === "/publish" ||
    route.path === "/search" ||
    route.path === "/about"
  );
});
</script>
<style>
/* 全局主题样式变量 */
:root {
  /* 浅色主题变量 */
  --primary-color: #1989fa;
  --secondary-color: #fe2c55;
  --background-color: #f5f5f5;
  --card-background: #ffffff;
  --text-color: #333333;
  --text-color-secondary: #666666;
  --border-color: #ebedf0;
  --shadow-color: rgba(0, 0, 0, 0.1);
}

/* 深色主题变量 */
.dark-theme {
  --primary-color: #1989fa;
  --secondary-color: #fe2c55;
  --background-color: #121212;
  --card-background: #1c1c1e;
  --text-color: #f5f5f5;
  --text-color-secondary: #aaaaaa;
  --border-color: #272729;
  --shadow-color: rgba(0, 0, 0, 0.3);

  /* Vant UI 深色主题变量覆盖 */
  --van-background: #121212;
  --van-background-2: #1c1c1e;
  --van-background-3: #252527;
  --van-text-color: #f5f5f5;
  --van-text-color-2: #aaaaaa;
  --van-border-color: #272729;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--text-color);
  min-height: 100vh;
  background-color: var(--background-color);
  font-weight: 500;
}
</style>
