<template>
  <div id="user-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar
      title="我的"
      :right-text="isLoggedIn ? '设置' : ''"
      @click-right="onClickRight"
    />

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
          <p class="user-id">抖音号: {{ userInfo.id }}</p>
          <van-tag round type="primary" style="margin-top: 8px;">普通用户</van-tag>
        </div>
      </div>

      <!-- 用户签名 -->
      <div class="user-signature">
        <div class="signature-content">
          <span class="signature-text">{{ userInfo.signature || '这个人很懒，什么都没写~' }}</span>
          <van-icon name="edit" class="edit-icon" @click="showEditSignature" />
        </div>
      </div>

      <!-- 用户数据统计 -->
      <div class="user-data-stats">
        <div class="stat-item">
          <span class="stat-num">{{ userInfo.posts }}</span>
          <span class="stat-label">作品</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ userInfo.following }}</span>
          <span class="stat-label">关注</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ userInfo.followers }}</span>
          <span class="stat-label">粉丝</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ userInfo.likes || 0 }}</span>
          <span class="stat-label">获赞</span>
        </div>
      </div>

      <!-- 编辑资料按钮 -->
      <div class="profile-actions">
        <van-button icon="edit" type="primary" size="small" round plain>编辑资料</van-button>
        <van-button icon="share-o" type="default" size="small" round plain>分享主页</van-button>
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
    
    <!-- 设置菜单 -->
    <div v-if="isLoggedIn" class="settings-section">
      <van-cell-group inset>
        <van-cell title="主题设置" @click="toggleTheme" is-link center>
          <template #right-icon>
            <van-switch v-model="isDarkMode" size="24" />
          </template>
        </van-cell>
        <van-cell title="清除缓存" is-link center @click="showToast('已清除缓存')" />
        <van-cell title="帮助与反馈" is-link center @click="showToast('功能开发中')" />
        <van-cell title="关于我们" is-link center @click="showToast('星屑 v1.0.0')" />
      </van-cell-group>
      
      <!-- 退出登录按钮 -->
      <div class="logout-button-container">
        <van-button 
          round 
          block 
          plain
          type="danger" 
          class="logout-button" 
          @click="showLogoutPopup = true"
          size="large"
        >
          退出登录
        </van-button>
      </div>
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
    
    <!-- 退出登录弹窗 -->
    <van-popup
      v-model="showLogoutPopup"
      round
      position="bottom"
      :style="{ height: '30%' }"
    >
      <div class="logout-popup">
        <div class="logout-popup-title">
          <van-icon name="warning-o" size="24" class="warning-icon" />
          <h3>确认退出登录？</h3>
        </div>
        <p class="logout-popup-desc">退出后将无法接收消息提醒，是否确认退出？</p>
        <div class="logout-popup-buttons">
          <van-button round block @click="showLogoutPopup = false">取消</van-button>
          <van-button round block type="danger" @click="confirmLogout">确认退出</van-button>
        </div>
      </div>
    </van-popup>

    <!-- 修改签名弹窗 -->
    <van-dialog
      v-model="showSignatureDialog"
      title="修改个性签名"
      show-cancel-button
      @confirm="updateSignature"
    >
      <van-field
        v-model="tempSignature"
        type="textarea"
        placeholder="请输入您的个性签名"
        rows="3"
        maxlength="50"
        show-word-limit
      />
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { showDialog, showToast } from 'vant';

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

// 退出登录弹窗状态
const showLogoutPopup = ref(false);

// 签名编辑相关
const showSignatureDialog = ref(false);
const tempSignature = ref('');

// 当前活动的标签页
const activeTab = ref('works');

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

// 监听主题变化
watch(isDarkMode, (newVal) => {
  // 更新全局主题
  if (typeof toggleDarkMode === 'function') {
    toggleDarkMode(newVal);
  }
});

// 检查登录状态
const checkLoginStatus = () => {
  try {
    const savedUserInfo = localStorage.getItem('userInfo');
    if (savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo);
      // 补充可能缺少的字段
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

// 设置按钮点击事件
const onClickRight = () => {
  if (isLoggedIn.value) {
    // 导航到设置页面
    showToast('设置功能开发中');
  }
};

// 切换主题
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value;
};

// 确认登出
const confirmLogout = () => {
  try {
    // 清除本地存储
    localStorage.removeItem('userInfo');
    
    // 重置状态
    isLoggedIn.value = false;
    userInfo.value = {
      id: '',
      username: '',
      avatar: '',
      followers: 0,
      following: 0,
      posts: 0,
      likes: 0,
      signature: ''
    };
    
    // 关闭弹窗
    showLogoutPopup.value = false;
    
    // 显示成功提示
    showToast({
      type: 'success',
      message: '已退出登录'
    });
    
    // 退出后重定向到首页
    setTimeout(() => {
      router.push('/home');
    }, 500);
  } catch (error) {
    console.error('退出登录失败:', error);
    showToast('退出失败，请重试');
  }
};

// 显示签名编辑对话框
const showEditSignature = () => {
  tempSignature.value = userInfo.value.signature || '';
  showSignatureDialog.value = true;
};

// 更新签名
const updateSignature = () => {
  userInfo.value.signature = tempSignature.value;
  
  // 更新本地存储
  localStorage.setItem('userInfo', JSON.stringify(userInfo.value));
  
  showToast({
    type: 'success',
    message: '签名更新成功'
  });
};

// 前往登录页
const toLogin = () => {
  router.push('/login');
};

// 前往发布页
const toPublish = () => {
  router.push('/publish');
};
</script>

<style scoped>
#user-page {
  min-height: 100vh;
  padding-bottom: 100px;
  background-color: #f7f8fa;
}

.dark-mode {
  background-color: var(--background-color, #121212);
  color: var(--text-color, #fff);
}

.user-content {
  padding: 16px 16px 0;
}

.user-header {
  display: flex;
  padding: 20px;
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(100, 101, 102, 0.08);
}

.dark-mode .user-header {
  background-color: #1c1c1e;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

.user-info {
  margin-left: 16px;
  flex: 1;
}

.user-info h2 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 600;
}

.user-id {
  color: #969799;
  margin: 4px 0;
  font-size: 14px;
}

.dark-mode .user-id {
  color: #7b7b7d;
}

/* 用户签名样式 */
.user-signature {
  margin: 10px 0 16px;
}

.signature-content {
  display: flex;
  align-items: center;
  padding: 0 5px;
}

.signature-text {
  flex: 1;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.dark-mode .signature-text {
  color: #bbb;
}

.edit-icon {
  color: #999;
  font-size: 16px;
  margin-left: 8px;
}

/* 数据统计 - 抖音风格 */
.user-data-stats {
  display: flex;
  margin-bottom: 16px;
}

.user-data-stats .stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.stat-num {
  font-size: 18px;
  font-weight: bold;
  color: #323233;
}

.dark-mode .stat-num {
  color: #f5f5f7;
}

.stat-label {
  font-size: 13px;
  color: #969799;
  margin-top: 4px;
}

.dark-mode .stat-label {
  color: #7b7b7d;
}

/* 资料编辑按钮 */
.profile-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.profile-actions .van-button {
  flex: 1;
}

/* 视频标签页 */
.video-tabs {
  margin-bottom: 20px;
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
}

.play-count {
  position: absolute;
  bottom: 6px;
  left: 6px;
  color: white;
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
  color: #666;
  padding: 4px 0;
}

/* 空内容显示 */
.empty-content {
  padding: 60px 0;
}

/* 设置区域 */
.settings-section {
  margin-top: 40px;
  padding: 0 16px 30px;
}

/* 退出登录按钮容器 */
.logout-button-container {
  margin: 30px 0 20px;
}

.logout-button {
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-color: #ee0a24;
  color: #ee0a24;
}

/* 退出弹窗样式 */
.logout-popup {
  padding: 24px 16px;
  text-align: center;
}

.logout-popup-title {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 16px;
}

.warning-icon {
  margin-right: 8px;
  color: #ff976a;
}

.logout-popup-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.logout-popup-desc {
  margin-bottom: 24px;
  color: #969799;
  font-size: 14px;
}

.logout-popup-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.not-logged-in {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
}
</style>