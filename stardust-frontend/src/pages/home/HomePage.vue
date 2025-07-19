<template>
  <div id="home-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar fixed>
      <template #left>
        <van-icon name="wap-nav" size="25" @click="showSidebar = true" />
      </template>
      <template #title>
        <van-tabs v-model:active="active" animated @change="onTabChange">
          <van-tab v-for="item in tabItems" :key="item.name" :title="item.title">
          </van-tab>
        </van-tabs>
      </template>
      <template #right>
        <van-icon name="search" size="25" @click="toSearch" />
      </template>
    </van-nav-bar>
    <router-view />

    <!-- 使用侧边栏组件 -->
    <SidebarMenu v-model="showSidebar" :darkMode="isDarkMode" @update:darkMode="isDarkMode = $event"
      @navigate-to-about="goToAbout" />
  </div>
</template>

<script setup>
import { useRouter, useRoute } from "vue-router";
import { ref, onMounted, watch, inject } from "vue";
import SidebarMenu from "@/components/SidebarMenu.vue";

const router = useRouter();
const route = useRoute();

// 侧边栏状态
const showSidebar = ref(false);

// 从全局获取主题状态
const globalDarkMode = inject('darkMode');
const toggleDarkMode = inject('toggleDarkMode');

// 主题设置
const isDarkMode = ref(globalDarkMode.value);

// 定义标签及对应的路由
const tabItems = ref([
  { title: "推荐", name: "recommendation" },
  { title: "关注", name: "follow" },

]);

// 默认激活第一个标签
const active = ref(0);

// 根据当前路由初始化激活的标签
onMounted(() => {
  const currentPath = route.path;
  const index = tabItems.value.findIndex(item => currentPath.includes(item.name));
  if (index !== -1) {
    active.value = index;
  } else {
    // 默认导航到推荐页
    router.push("/home/recommendation");
  }
});

// 监听主题变化
watch(isDarkMode, (newValue) => {
  // 更新全局主题状态
  toggleDarkMode(newValue);
});

// 监听全局主题变化，同步到组件
watch(globalDarkMode, (newValue) => {
  isDarkMode.value = newValue;
});

// 标签切换处理
const onTabChange = (index) => {
  router.push(`/home/${tabItems.value[index].name}`);
};

// 前往关于页面
const goToAbout = () => {
  router.push('/home/about');
  active.value = tabItems.value.findIndex(item => item.name === 'about');
};

// 跳转搜索页面
const toSearch = () => {
  router.push("/search");
};
</script>

<style scoped>
/* 深色模式样式 */
.dark-mode {
  background-color: var(--background-color);
  color: var(--text-color);
}

.van-nav-bar {
  z-index: 1002 !important;
  position: fixed !important;
  left: 0;
  top: 0;
  width: 100%;
}

.van-tabs {
  z-index: 1003 !important;
  position: relative;
}
</style>