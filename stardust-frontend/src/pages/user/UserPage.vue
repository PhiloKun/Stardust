<template>
  <div id="user-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar title="我的" fixed>
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
        <div class="user-avatar" @click="onEditAvatar">
          <van-image round width="80" height="80" :src="userInfo.avatar"
            :error-content="userInfo.username.charAt(0).toUpperCase()" />
          <input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="onAvatarChange" />
          <van-icon name="edit" class="avatar-edit-icon" />
        </div>
        <div class="user-info">
          <h2>{{ userInfo.username }}</h2>
          <p class="user-id">星屑号: {{ userInfo.id }}</p>
        </div>
      </div>
      <!-- 新增：用户数据统计栏和欢迎语 -->
      <div class="user-stats">
        <div class="stat-item">
          <div class="stat-value">{{ userVideos.length }}</div>
          <div class="stat-label">作品</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">123</div>
          <div class="stat-label">获赞</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">88</div>
          <div class="stat-label">粉丝</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">12</div>
          <div class="stat-label">关注</div>
        </div>
      </div>

      <!-- 视频内容标签页 -->
      <van-tabs v-model="activeTab" sticky animated swipeable class="video-tabs">
        <van-tab name="works" title="作品">
          <div class="video-grid" v-if="userVideos.length > 0">
            <div class="video-item publish-item" @click="toPublish">
              <div class="publish-plus">+</div>
              <div class="video-title">发布</div>
            </div>
            <div v-for="(video, index) in userVideos" :key="index" class="video-item" @click="toPlay(video)">
              <div class="video-cover">
                <img :src="video.coverUrl" alt="视频封面" :style="{ objectFit: coverFitModes[index] || 'cover' }"
                  @load="e => onCoverLoad(e, index)" />
                <van-icon name="delete" class="delete-icon" @click.stop="confirmDelete(video, index)" />
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

        <van-tab name="likes" title="喜欢">
          <div class="empty-content">
            <van-empty description="喜欢的视频列表为空" />
          </div>
        </van-tab>

        <van-tab name="favorites" title="收藏">
          <div class="empty-content">
            <van-empty description="还没有收藏内容" />
          </div>
        </van-tab>
      </van-tabs>
    </div>

    <!-- 未登录状态 -->
    <div v-else class="not-logged-in">
      <van-empty description="您尚未登录" image="https://fastly.jsdelivr.net/npm/@vant/assets/custom-empty-image.png">
        <van-button round type="primary" size="small" @click="toLogin">
          立即登录/注册
        </van-button>
      </van-empty>
    </div>
    <SidebarMenu :model-value="showSidebar" @update:model-value="showSidebar = $event" :dark-mode="isDarkMode"
      @update:dark-mode="isDarkMode = $event" />
    <!-- 设置侧边栏 -->
    <UserSettingSidebar v-model="showSettingSidebar" :dark-mode="isDarkMode" @logout="confirmLogout" />
  </div>
</template>

<script setup>
import { ref, onMounted, inject, computed, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import SidebarMenu from "@/components/SidebarMenu.vue";
import UserSettingSidebar from "@/components/UserSettingSidebar.vue";
import { showToast, showConfirmDialog } from "vant";
import { useUserStore } from "@/stores/userStore";
import { uploadAvatar, fetchUserVideos, deleteVideo } from '@/utils/request';
import { reactive } from 'vue'

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const userInfo = ref({
  id: "",
  username: "",
  avatar: ""
});

// 注入主题状态
const globalDarkMode = inject("darkMode", ref(false));
const isDarkMode = ref(globalDarkMode.value);

// 登录状态
const isLoggedIn = computed(() => userStore.isLoggedIn);

// 当前活动的标签页
const activeTab = ref("works");

// 设置侧边栏
const showSettingSidebar = ref(false);

// 用户视频数据
const userVideos = ref([]);

const avatarInput = ref(null);

// 新增：动态object-fit模式
const coverFitModes = reactive({});

function onCoverLoad(e, index) {
  const img = e.target;
  const ratio = img.naturalWidth / img.naturalHeight;
  coverFitModes[index] = ratio < 0.8 ? 'cover' : 'contain';
}

const fetchAndUpdateUserVideos = async () => {
  if (userStore.isLoggedIn && userStore.userInfo && userStore.userInfo.id) {
    const res = await fetchUserVideos(userStore.userInfo.id);
    if (res && res.data && res.data.data) {
      userVideos.value = res.data.data;
    }
  }
};

// 在组件挂载时检查登录状态
onMounted(async () => {
  if (userStore.isLoggedIn) {
    // 如果已登录，先更新本地 userInfo，然后从后端获取最新详情
    updateUserInfo();
    if (userStore.userInfo && userStore.userInfo.id) {
      await userStore.fetchUserInfo(userStore.userInfo.id);
      // 获取最新详情后再次更新本地 userInfo
      updateUserInfo();
      // 获取用户视频
      await fetchAndUpdateUserVideos();
    }
  } else {
    router.replace("/login");
  }
});

// 监听路由变化，强制刷新数据
watch(
  () => route.fullPath,
  async () => {
    await fetchAndUpdateUserVideos();
  }
);

// 更新用户信息
const updateUserInfo = () => {
  if (userStore.userInfo) {
    userInfo.value = { ...userStore.userInfo };
  }
};

// 前往登录页
const toLogin = () => {
  router.push("/login");
};

// 前往发布页
const toPublish = () => {
  router.push("/publish");
};

const showSidebar = ref(false);

// 登出方法
const confirmLogout = () => {
  const result = userStore.logout();
  showSettingSidebar.value = false;

  if (result.success) {
    showToast({
      message: result.message || "已安全退出账号",
      type: "success",
      duration: 1500,
      className: "custom-toast custom-toast-success",
      onClose: () => {
        router.push("/home");
      },
    });
  } else {
    showToast({
      message: result.message || "退出账号失败",
      type: "fail",
      duration: 2000,
      className: "custom-toast custom-toast-danger",
    });
  }
};

function onEditAvatar() {
  avatarInput.value && avatarInput.value.click();
}

async function onAvatarChange(e) {
  const file = e.target.files[0];
  if (!file) return;
  const res = await uploadAvatar(file, userInfo.value.id);
  if (res && res.data && res.data.data) {
    userInfo.value.avatar = res.data.data;
    userStore.userInfo.avatar = res.data.data;
    showToast('头像更新成功');
  }
}

const toPlay = (video) => {
  router.push(`/video/${video.id}`);
};

const confirmDelete = (video, idx) => {
  showConfirmDialog({
    title: '',
    message: `
      <div class="danger-title">⚠️ 删除作品</div>
      <div style="color:#FE2C55;font-weight:bold;margin-top:8px;">删除后不可恢复，确定要删除该作品吗？</div>
    `,
    confirmButtonColor: '#FE2C55',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    className: 'danger-dialog',
    allowHtml: true
  }).then(async () => {
    const res = await deleteVideo(video.id, userInfo.value.id);
    if (res && res.data && res.data.code === 0) {
      showToast('删除成功');
      setTimeout(() => {
        window.location.reload();
      }, 500);
    } else {
      showToast(res?.data?.message || '删除失败');
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
  box-shadow: 0 2px 12px var(--shadow-color, rgba(0, 0, 0, 0.18));
}

.user-info {
  margin-left: 16px;
  flex: 1;
}

.user-info h2 {
  margin: 0 0 8px 0;
  /* 原4px改为8px，下方间距更大 */
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color, #222);
}

.dark-mode .user-info h2 {
  color: var(--text-color, #fff);
}

.user-id {
  color: var(--text-color-secondary, #969799);
  font-size: 15px;
}

.dark-mode .user-id {
  color: var(--text-color-secondary, #aaa);
}

.user-bio-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  cursor: pointer;
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
  gap: 12px;
  margin-top: 10px;
  padding: 0 4px;
}

.video-item {
  position: relative;
  aspect-ratio: 9/16;
  overflow: hidden;
  background: #181818;
  /* 统一深色背景，可根据主题调整 */
  border-radius: 10px;
  /* 保留圆角 */
  box-shadow: none;
  /* 去除阴影 */
  transition: border-color 0.2s;
  cursor: pointer;
  border: 1px solid #444;
  /* 细灰色边框 */
}

.video-item:hover {
  border-color: #1e90ff;
  /* 悬浮时高亮描边，可选 */
}

.video-cover {
  width: 100%;
  height: 100%;
  background: #000;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #000;
  border-radius: 6px;
  display: block;
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
  box-shadow: 0 4px 24px rgba(25, 137, 250, 0.18);
  padding: 18px 28px !important;
}

.avatar-edit-icon {
  position: absolute;
  right: 0;
  bottom: 0;
  background: #fff;
  border-radius: 50%;
  padding: 2px;
  font-size: 18px;
  color: #1989fa;
  cursor: pointer;
}

.user-avatar {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.publish-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #222;
  /* 纯色背景 */
  color: #1e90ff;
  cursor: pointer;
  border: 1.5px dashed #1e90ff;
  /* 虚线描边区分“发布” */
  box-shadow: none;
  border-radius: 10px;
}

.publish-item:hover {
  border-color: #005fa3;
  color: #005fa3;
}

.publish-plus {
  font-size: 38px;
  font-weight: bold;
  line-height: 1;
  margin-bottom: 6px;
  font-family: 'Arial Black', 'Arial', sans-serif;
}

.publish-item .video-title {
  color: #1e90ff;
  font-size: 13px;
  margin: 0;
}

.edit-signature-popup {
  padding: 20px 16px 12px 16px;
}

.popup-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
  text-align: center;
}

.popup-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.popup-actions .van-button {
  flex: 1;
}

.delete-icon {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 2;
  background: rgba(30, 30, 30, 0.7);
  border-radius: 50%;
  padding: 2px;
  font-size: 18px;
  color: #fff;
  border: none;
  box-shadow: none;
  transition: background 0.2s;
}

.delete-icon:hover {
  background: #ff4d4f;
  color: #fff;
}

.danger-title {
  color: #FE2C55;
  font-weight: bold;
  font-size: 20px;
  text-align: center;
  margin-bottom: 4px;
}

.danger-dialog .van-dialog__header {
  color: #FE2C55;
  font-weight: bold;
  font-size: 18px;
}

.danger-dialog .van-dialog__message {
  color: #FE2C55;
  font-size: 15px;
}

.danger-dialog .van-dialog__footer {
  border-top: none;
}

.danger-dialog .van-button--danger {
  background: #FE2C55;
  border: none;
}

.canvas-signature-popup {
  background: #fffbe6;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(100, 100, 0, 0.12);
  padding: 24px 18px 16px 18px;
  min-width: 280px;
  max-width: 90vw;
  margin: 0 auto;
  border: 2px dashed #ffe58f;
  position: relative;
}

.canvas-title {
  font-family: 'Comic Sans MS', 'Chalkboard SE', 'Arial', cursive;
  font-size: 20px;
  color: #d48806;
  text-align: center;
  margin-bottom: 12px;
  letter-spacing: 1px;
}

.canvas-textarea {
  width: 100%;
  min-height: 80px;
  background: transparent;
  border: none;
  border-bottom: 2px dotted #ffd666;
  font-size: 16px;
  font-family: 'Comic Sans MS', 'Chalkboard SE', 'Arial', cursive;
  color: #614700;
  outline: none;
  resize: none;
  margin-bottom: 18px;
  padding: 8px 0;
}

.canvas-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  margin: 12px 0 0 0;
  padding: 8px 0 0 0;
  border-top: 1px solid #222;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #1e90ff;
}

.stat-label {
  font-size: 12px;
  color: #888;
}

.user-divider {
  height: 1px;
  background: linear-gradient(90deg, #222 0%, #444 50%, #222 100%);
  margin: 16px 0 0 0;
  border: none;
}
</style>
