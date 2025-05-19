<template>
  <div id="message-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar title="消息" fixed>
      <template #left>
        <van-icon name="wap-nav" size="25" @click="showSidebar = true" />
      </template>
    </van-nav-bar>
    <div class="message-content">
      <div class="message-header">
        <h2>消息中心</h2>
        <p class="message-desc">与好友互动，获取最新动态</p>
      </div>
      <div class="message-list">
        <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ unread: !msg.read }">
          <img :src="msg.avatar" class="avatar" />
          <div class="msg-main">
            <div class="msg-title-row">
              <span class="msg-title">{{ msg.title }}</span>
              <span class="msg-time">{{ msg.time }}</span>
            </div>
            <div class="msg-content">{{ msg.content }}</div>
          </div>
          <span v-if="!msg.read" class="unread-dot"></span>
        </div>
        <div v-if="messages.length === 0" class="empty-message">
          <van-empty description="暂无消息" />
        </div>
      </div>
    </div>
    <SidebarMenu
      :model-value="showSidebar"
      @update:model-value="showSidebar = $event"
      :dark-mode="isDarkMode"
      @update:dark-mode="isDarkMode = $event"
    />
  </div>
</template>

<script setup>
import { ref, inject } from 'vue';
import SidebarMenu from "@/components/SidebarMenu.vue";
const showSidebar = ref(false);
const globalDarkMode = inject('darkMode', ref(false));
const isDarkMode = ref(globalDarkMode.value);

const messages = ref([
  {
    id: 1,
    title: '系统通知',
    content: '欢迎加入星屑社区，开启你的创作之旅！',
    time: '09:30',
    avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    read: false
  },
  {
    id: 2,
    title: '小明',
    content: '你的作品很棒，期待更多分享！',
    time: '昨天',
    avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/apple-1.jpeg',
    read: true
  },
  {
    id: 3,
    title: '官方活动',
    content: '参与#星屑挑战赛#赢取精美周边，快来参加吧！',
    time: '前天',
    avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/apple-2.jpeg',
    read: false
  }
]);
</script>

<style scoped>
#message-page {
  min-height: 100vh;
  background-color: var(--background-color, #f7f8fa);
  color: var(--text-color, #333);
  transition: background-color 0.3s, color 0.3s;
}
.dark-mode {
  background-color: var(--background-color, #121212);
  color: var(--text-color, #fff);
}
.message-content {
  max-width: 700px;
  margin: 0 auto;
  padding: 70px 12px 32px 12px;
}
@media (min-width: 1200px) {
  .message-content {
    max-width: 1200px;
    padding-left: 32px;
    padding-right: 32px;
  }
  .message-header h2 {
    font-size: 32px;
  }
  .message-item {
    margin-bottom: 24px;
  }
}
@media (max-width: 600px) {
  .message-content {
    padding-left: 6px;
    padding-right: 6px;
  }
}
.message-header {
  text-align: center;
  margin-bottom: 18px;
}
.message-header h2 {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 6px;
}
.message-desc {
  color: var(--text-color-secondary, #888);
  font-size: 15px;
}
.message-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.message-item {
  display: flex;
  align-items: flex-start;
  background: var(--card-background, #fff);
  border-radius: 12px;
  box-shadow: 0 2px 8px 0 rgba(0,0,0,0.06);
  padding: 14px 16px;
  position: relative;
  border: 1px solid var(--border-color, #eee);
  transition: box-shadow 0.2s;
}
.dark-mode .message-item {
  background: var(--card-background, #1c1c1e);
  border: 1px solid var(--border-color, #272729);
  box-shadow: 0 2px 8px 0 rgba(0,0,0,0.18);
}
.message-item.unread {
  border-color: var(--primary-color, #1989fa);
}
.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 14px;
  background: #f2f2f2;
}
.msg-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.msg-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.msg-title {
  font-size: 16px;
  font-weight: 600;
}
.msg-time {
  font-size: 13px;
  color: var(--text-color-secondary, #aaa);
}
.msg-content {
  font-size: 15px;
  color: var(--text-color-secondary, #666);
}
.unread-dot {
  width: 10px;
  height: 10px;
  background: var(--primary-color, #1989fa);
  border-radius: 50%;
  position: absolute;
  right: 16px;
  top: 18px;
  box-shadow: 0 0 4px var(--primary-color, #1989fa);
}
.empty-message {
  margin-top: 60px;
}
</style>


