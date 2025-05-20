<template>
  <div id="user-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar
      title="我的"
      left-arrow={false}
      fixed
    >
      <template #left>
        <van-icon name="wap-nav" size="25" @click="showSidebar = true" />
      </template>
      <template #right>
        <van-icon v-if="isLoggedIn" name="setting-o" size="24" @click="showSettingSidebar = true" />
      </template>
    </van-nav-bar>

    <div v-if="isLoggedIn" class="user-content">
      <!-- 用户基本信息 -->
      <div class="user-header">
        <div class="user-avatar">
          <van-image
            round
            width="80"
            height="80"
            :src="userInfo.avatar"
            :error-content="userInfo.username.charAt(0).toUpperCase()"
          />
        </div>
        <div class="user-info">
          <h2>{{ userInfo.username }}</h2>
          <p class="user-id">星屑号: {{ userInfo.id }}</p>
          <div class="user-bio-row">
            <span class="user-bio">{{ userInfo.signature || '这个人很低调，什么都没写~' }}</span>
            <van-icon name="edit" class="edit-icon" @click="showEditSignature" />
          </div>
        </div>
      </div>

      <!-- 视频内容标签页 -->
      <van-tabs v-model="activeTab" sticky animated swipeable class="video-tabs">
        <van-tab name="works" title="作品">
          <div class="video-grid" v-if="userVideos.length > 0">
            <div v-for="(video, index) in userVideos" :key="index" class="video-item">
              <div class="video-cover">
                <img :src="video.cover" alt="视频封面">
                <span class="play-count">
                  <van-icon name="play-circle-o" />
                  {{ formatCount(video.playCount) }}
                </span>
              </div>
              <div class="video-title">{{ video.title }}</div>
            </div>
            <div class="no-more-tip">暂时没有更多了</div>
          </div>
          <div v-else class="empty-content">
            <van-empty image="search" description="还没有发布作品">
              <van-button round type="primary" size="small" icon="plus" @click="toPublish">
                发布视频
              </van-button>
            </van-empty>
          </div>
        </van-tab>
        
        <van-tab name="favorites" title="收藏">
          <div class="empty-content">
            <van-empty description="还没有收藏内容" />
          </div>
        </van-tab>
        
        <van-tab name="likes" title="喜欢">
          <div class="empty-content">
            <van-empty description="喜欢的视频列表为空" />
          </div>
        </van-tab>
      </van-tabs>
    </div>
    
    <!-- 未登录状态 -->
    <div v-else class="not-logged-in">
      <van-empty
        description="您尚未登录"
        image="https://fastly.jsdelivr.net/npm/@vant/assets/custom-empty-image.png"
      >
        <van-button round type="primary" size="small" @click="toLogin">
          立即登录/注册
        </van-button>
      </van-empty>
    </div>
    <SidebarMenu
      :model-value="showSidebar"
      @update:model-value="showSidebar = $event"
      :dark-mode="isDarkMode"
      @update:dark-mode="isDarkMode = $event"
    />
    <!-- 编辑简介弹窗 -->
    <van-dialog
      v-model="showSignatureDialog"
      title="编辑个人简介"
      show-cancel-button
      @confirm="updateSignature"
    >
      <van-field
        v-model="tempSignature"
        type="textarea"
        placeholder="请输入您的个人简介"
        rows="3"
        maxlength="50"
        show-word-limit
      />
    </van-dialog>
    <!-- 设置侧边栏 -->
    <UserSettingSidebar
      v-model="showSettingSidebar"
      :dark-mode="isDarkMode"
      @logout="confirmLogout"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import SidebarMenu from "@/components/SidebarMenu.vue";
import UserSettingSidebar from "@/components/UserSettingSidebar.vue";
import { showToast, showSuccessToast } from 'vant';

const router = useRouter();
const userInfo = ref({
  id: '',
  username: '',
  avatar: '',
  followers: 0,
  following: 0,
  posts: 0,
  likes: 0,
  signature: ''
});

// 注入主题状态
const globalDarkMode = inject('darkMode', ref(false));
const isDarkMode = ref(globalDarkMode.value);

// 登录状态
const isLoggedIn = ref(false);

// 当前活动的标签页
const activeTab = ref('works');

// 编辑简介弹窗
const showSignatureDialog = ref(false);
const tempSignature = ref('');

// 设置侧边栏
const showSettingSidebar = ref(false);

// 模拟用户视频数据
const userVideos = ref([
  {
    id: '1',
    title: '如何开始创作视频',
    cover: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    playCount: 1342
  },
  {
    id: '2',
    title: '星屑平台使用教程',
    cover: 'https://fastly.jsdelivr.net/npm/@vant/assets/apple-1.jpeg',
    playCount: 2198
  },
  {
    id: '3',
    title: '我的第一个作品',
    cover: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    playCount: 987
  }
]);

// 格式化数字
const formatCount = (count) => {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + 'w';
  } else if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'k';
  }
  return count;
};

// 在组件挂载时检查登录状态
onMounted(() => {
  checkLoginStatus();
});

// 检查登录状态
const checkLoginStatus = () => {
  try {
    const savedUserInfo = localStorage.getItem('userInfo');
    if (savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo);
      userInfo.value.likes = userInfo.value.likes || 233;
      isLoggedIn.value = true;
    } else {
      isLoggedIn.value = false;
    }
  } catch (error) {
    console.error('读取用户信息失败', error);
    localStorage.removeItem('userInfo');
    isLoggedIn.value = false;
  }
};

// 编辑个人简介
const showEditSignature = () => {
  tempSignature.value = userInfo.value.signature || '';
  showSignatureDialog.value = true;
};
const updateSignature = () => {
  userInfo.value.signature = tempSignature.value;
  localStorage.setItem('userInfo', JSON.stringify(userInfo.value));
  showToast({ type: 'success', message: '个人简介已更新' });
};

// 前往登录页
const toLogin = () => {
  router.push('/login');
};

// 前往发布页
const toPublish = () => {
  router.push('/publish');
};

const showSidebar = ref(false);

// 设置侧边栏
const confirmLogout = () => {
  localStorage.removeItem('userInfo');
  isLoggedIn.value = false;
  showSettingSidebar.value = false;
  showSuccessToast({
    message: '已安全退出账号',
    duration: 1500,
    className: 'custom-success-toast',
    onClose: () => {
      router.push('/home');
    }
  });
};
</script>

<style scoped>
#user-page {
  min-height: 100vh;
  padding-bottom: 100px;
  background-color: #f7f8fa;
}

.user-content {
  padding: 70px 16px 0 16px;
}

.dark-mode {
  background-color: #000 !important;
  color: var(--text-color, #fff);
}

.user-header {
  display: flex;
  padding: 20px;
  background-color: var(--card-background, #fff);
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px var(--shadow-color, rgba(100, 101, 102, 0.08));
}

.dark-mode .user-header {
  background-color: var(--card-background, #1c1c1e);
  box-shadow: 0 2px 12px var(--shadow-color, rgba(0,0,0,0.18));
}

.user-info {
  margin-left: 16px;
  flex: 1;
}

.user-info h2 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color, #222);
}

.dark-mode .user-info h2 {
  color: var(--text-color, #fff);
}

.user-id {
  color: var(--text-color-secondary, #969799);
  margin: 4px 0;
  font-size: 14px;
}

.dark-mode .user-id {
  color: var(--text-color-secondary, #aaa);
}

.user-bio-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}
.user-bio {
  font-size: 14px;
  color: var(--text-color-secondary, #888);
  flex: 1;
  word-break: break-all;
}
.edit-icon {
  color: #999;
  font-size: 16px;
  cursor: pointer;
}

/* 视频标签页 */
.video-tabs {
  margin-bottom: 20px;
  background: transparent;
}

/* 视频网格 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2px;
  margin-top: 10px;
}

.video-item {
  position: relative;
  aspect-ratio: 9/16;
  overflow: hidden;
  background: var(--card-background, #fff);
  border-radius: 8px;
}

.dark-mode .video-item {
  background: var(--card-background, #1c1c1e);
}

.video-cover {
  position: relative;
  width: 100%;
  height: 100%;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.play-count {
  position: absolute;
  bottom: 6px;
  left: 6px;
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.play-count .van-icon {
  margin-right: 2px;
}

.video-title {
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-color-secondary, #666);
  padding: 4px 0;
}

.dark-mode .video-title {
  color: var(--text-color-secondary, #bbb);
}

/* 空内容显示 */
.empty-content {
  padding: 60px 0;
}

.not-logged-in {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
}

.no-more-tip {
  grid-column: 1 / -1;
  text-align: center;
  color: var(--text-color-secondary, #aaa);
  font-size: 14px;
  margin: 24px 0 8px 0;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.custom-success-toast {
  background: linear-gradient(90deg, #1989fa 60%, #4fc3f7 100%) !important;
  color: #fff !important;
  border-radius: 12px !important;
  font-size: 17px !important;
  font-weight: bold;
  box-shadow: 0 4px 24px rgba(25,137,250,0.18);
  padding: 18px 28px !important;
}
</style>