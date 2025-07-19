<template>
  <van-popup
    :show="modelValue"
    position="right"
    :style="{ width: '300px', height: '100%', background: darkMode ? 'rgba(24,24,28,0.98)' : '#fff', boxShadow: darkMode ? '0 0 24px #0008' : '0 0 24px #ccc8' }"
    @update:show="onUpdateShow"
    round
    closeable
    close-icon="close"
  >
    <div class="setting-sidebar" :class="{ 'dark-mode': darkMode }">
      <!-- 顶部用户信息 -->
      <div v-if="userStore.userInfo" class="sidebar-user">
        <van-image
          round
          width="64"
          height="64"
          :src="userStore.userInfo.avatar"
          :error-content="userStore.userInfo.username ? userStore.userInfo.username.charAt(0).toUpperCase() : '?'"
        />
        <div class="user-meta">
          <div class="user-name">{{ userStore.userInfo.username }}</div>
          <div class="user-id">星屑号: {{ userStore.userInfo.id }}</div>
        </div>
      </div>
      <div class="sidebar-header">
        <van-icon name="setting-o" size="24" class="sidebar-icon" />
        <span class="sidebar-title">设置</span>
      </div>
      <van-divider class="sidebar-divider" />
      <div class="sidebar-content">
        <!-- 预留更多设置项 -->
        <!-- <van-cell title="深色模式" :value="darkMode ? '已开启' : '已关闭'" is-link /> -->
        <van-button round block type="danger" class="logout-btn" @click="onLogout">退出账号</van-button>
      </div>
      <div class="sidebar-footer">
        <span class="footer-text">星屑 Stardust © 2025</span>
      </div>
    </div>
  </van-popup>
</template>

<script setup>
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();

const props = defineProps({
  modelValue: Boolean,
  darkMode: Boolean
});
const emit = defineEmits(['update:modelValue', 'logout']);

const onUpdateShow = (val) => {
  emit('update:modelValue', val);
};
const onLogout = () => {
  emit('logout');
};
</script>

<style scoped>
.setting-sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 0 0 0 0;
  background: #fff;
  color: #222;
  border-radius: 16px 0 0 16px;
  box-shadow: 0 0 24px #ccc8;
  position: relative;
}
.dark-mode.setting-sidebar {
  background: rgba(24,24,28,0.98);
  color: #fff;
  box-shadow: 0 0 24px #0008;
}
.sidebar-user {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 36px 0 18px 0;
  background: transparent;
}
.sidebar-user .van-image {
  box-shadow: 0 2px 12px rgba(0,0,0,0.10);
  margin-bottom: 8px;
}
.user-meta {
  text-align: center;
}
.user-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 2px;
}
.user-id {
  font-size: 13px;
  color: #888;
}
.dark-mode .user-id {
  color: #aaa;
}
.sidebar-header {
  display: flex;
  align-items: center;
  padding: 0 24px;
  margin-bottom: 0;
  margin-top: 8px;
}
.sidebar-icon {
  color: #1989fa;
  margin-right: 8px;
}
.sidebar-title {
  font-size: 18px;
  font-weight: 600;
}
.sidebar-divider {
  margin: 12px 0 0 0;
}
.sidebar-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 24px 24px 0 24px;
}
.logout-btn {
  margin-top: 24px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  box-shadow: 0 2px 8px rgba(255,0,0,0.08);
  transition: background 0.2s, color 0.2s;
}
.logout-btn:active {
  background: #ff4d4f;
  color: #fff;
}
.sidebar-footer {
  text-align: center;
  padding: 18px 0 12px 0;
  font-size: 13px;
  color: #bbb;
}
.dark-mode .sidebar-footer {
  color: #666;
}
</style> 