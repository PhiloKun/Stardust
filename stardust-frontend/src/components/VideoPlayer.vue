<template>
  <div class="video-player-component" ref="playerContainer" tabindex="0">
    <van-swipe class="video-swipe" :loop="false" vertical :show-indicators="false" @change="onVideoChange" ref="swiper">
      <van-swipe-item v-for="(video, index) in videoList" :key="index">
        <div class="video-container">
          <!-- 只渲染当前和下一个视频的video标签 -->
          <video v-if="shouldLoadVideo(index)" class="video-player" :src="video.videoUrl" :poster="video.coverUrl"
            x5-video-player-type="h5" x5-playsinline="true" webkit-playsinline="true" playsinline="true"
            :ref="(el) => { videoRefs[index] = el; }" loop @click="togglePlay(index)"
            @timeupdate="updateProgress(index)" @loadedmetadata="videoLoaded(index)" @waiting="onVideoWaiting(index)"
            @canplay="onVideoCanPlay(index)"></video>
          <!-- 其余只显示封面 -->
          <img v-else class="video-cover" :src="video.coverUrl" :alt="video.description"
            style="width:100%;height:100%;object-fit:cover;" />

          <!-- 视频加载中状态图标 -->
          <div class="loading-status" v-if="isVideoLoading[index]">
            <van-icon name="loading" size="48" color="#1989fa" />
          </div>

          <!-- 播放状态图标 -->
          <div class="play-status" v-if="isVideoPlaying[index] === false">
            <van-icon name="play-circle-o" size="50" color="#fff" />
          </div>

          <!-- 右侧操作栏（抖音风格，竖直居中，靠右） -->
          <div class="action-bar dy-action-bar">
            <!-- 新增：作者头像在最上方 -->
            <div class="author-avatar-wrapper">
              <div class="avatar-follow-container">
                <van-image round width="40" height="40" :src="video.avatar" class="author-avatar" />
                <button class="follow-btn" :class="{ followed: video.followed }" @click="toggleFollow(video)">
                  <span v-if="!video.followed">+</span>
                  <span v-else>✓</span>
                </button>
              </div>
            </div>
            <div class="action-item">
              <van-icon name="like-o" size="28" color="#fff" />
              <span>{{ video.likes }}</span>
            </div>
            <div class="action-item">
              <van-icon name="chat-o" size="28" color="#fff" />
              <span>{{ video.comments }}</span>
            </div>
            <div class="action-item">
              <van-icon name="star-o" size="28" color="#fff" />
              <span>收藏</span>
            </div>
            <div class="action-item">
              <van-icon name="share-o" size="28" color="#fff" />
              <span>分享</span>
            </div>
          </div>

          <!-- 左下角信息区（抖音风格） -->
          <div class="video-info dy-video-info">
            <div class="author">
              <!-- 头像已移至右侧，这里只保留昵称 -->
              <span class="username">@{{ video.username }}</span>
            </div>
            <div class="description"
              :class="{ 'desc-collapsed': !descExpandMap[index], 'desc-expanded': descExpandMap[index] }">
              {{ video.description }}
              <span v-if="isDescOverflow(video.description)" class="desc-toggle"
                @click="descExpandMap[index] = !descExpandMap[index]">
                {{ descExpandMap[index] ? '收起' : '展开' }}
              </span>
            </div>
            <div class="tags" v-if="video.tags && video.tags.length > 0">
              <span class="tag" v-for="(tag, tagIndex) in video.tags" :key="tagIndex">
                #{{ tag }}
              </span>
            </div>
          </div>
        </div>
      </van-swipe-item>
    </van-swipe>

    <!-- 全局进度条，固定在底部 -->
    <div class="video-progress-container" v-show="showControls" @click.stop="seekVideo($event, currentVideoIndex)"
      @touchstart.stop="startDrag($event)" @touchmove.stop="onDrag($event)" @touchend.stop="endDrag()"
      @mousedown.stop="startDragMouse($event)">
      <div class="video-progress">
        <div class="progress-background"></div>
        <div class="progress-current" :class="{ dragging: isDragging }"
          :style="{ width: getProgressWidth(currentVideoIndex) }">
          <div class="progress-dot"></div>
        </div>
      </div>
      <!-- 视频时间显示 -->
      <div class="video-time" v-if="isPcDevice">
        <span>{{ formatTime(currentTimes[currentVideoIndex] || 0) }}</span>
        <span> / </span>
        <span>{{ formatTime(videoDurations[currentVideoIndex] || 0) }}</span>
      </div>
    </div>

    <!-- 电脑端操作指引 -->
    <div class="pc-controls-guide" v-if="isPcDevice && showGuide">
      <div class="guide-item">
        <van-space>
          <van-icon name="arrow-up" />
          <van-icon name="arrow-down" />
          <span>键盘⬆️⬇️/鼠标滚轮</span>
        </van-space>
      </div>
      <div class="guide-item">
        <van-space>
          <span class="key-box">空格</span>
          <span>播放/暂停</span>
        </van-space>
      </div>
      <div class="guide-item close-guide">
        <van-button size="mini" type="primary" @click="showGuide = false">关闭提示</van-button>
      </div>
    </div>

    <!-- 开启提示按钮，仅在未显示指引时展示 -->
    <van-button v-if="isPcDevice && !showGuide" class="open-guide-btn" size="mini" type="primary"
      @click="showGuide = true" icon="question-o">
      操作指引
    </van-button>
  </div>
</template>

<script setup>
import {
  ref,
  onMounted,
  reactive,
  onBeforeUnmount,
  computed,
  inject,
} from "vue";
import { fetchVideoList } from '@/utils/request';

// 视频引用集合
const videoRefs = reactive({});
const currentVideoIndex = ref(0);
const playerContainer = ref(null);
const swiper = ref(null);

// 视频控制状态
const showControls = ref(false);
const controlsTimer = ref(null);
const isVideoPlaying = reactive({});
const videoDurations = reactive({});
const currentTimes = reactive({});

// 进度条拖动状态
const isDragging = ref(false);
const dragStartX = ref(0);
const dragStartPosition = ref(0);

// 检测是否为PC设备
const isPcDevice = ref(false);

// 注入主题状态
const isDarkMode = inject("darkMode", ref(false));

// 模拟视频数据
const videoList = ref([]);

// 展开/收起简介的状态，key为视频index
const descExpandMap = reactive({});

// 判断简介是否需要展开按钮
function isDescOverflow(text) {
  // 简单判断：字数超过40就显示展开按钮（可根据实际UI调整）
  return text && text.length > 40;
}

// 检测设备类型
const checkDeviceType = () => {
  // 更完善的设备检测
  const userAgent = navigator.userAgent.toLowerCase();
  const isMobile = /iphone|ipod|android|ios|mobile|phone|pad/.test(userAgent);
  isPcDevice.value = !isMobile;
};

// 在组件挂载后播放第一个视频
onMounted(async () => {
  checkDeviceType();
  try {
    const res = await fetchVideoList();
    if (res && res.data && res.data.data) {
      videoList.value = res.data.data;
    }
  } catch (e) {
    console.error('获取视频列表失败', e);
  }
  setTimeout(() => {
    playVideo(0);
  }, 100);

  // 给容器添加焦点，以便捕获键盘事件
  if (playerContainer.value) {
    playerContainer.value.focus();
  }

  // 添加键盘事件监听
  document.addEventListener("keydown", handleKeydown);

  // 添加鼠标滚轮事件监听
  if (playerContainer.value) {
    playerContainer.value.addEventListener("wheel", handleWheel);
  }

  // 显示初始控制栏
  showControlsTemporarily();

  // 监听鼠标移动显示控制栏
  playerContainer.value.addEventListener("mousemove", showControlsTemporarily);

  // 触摸开始时显示控制栏
  playerContainer.value.addEventListener("touchstart", showControlsTemporarily);

  // PC端默认显示操作指引
  if (isPcDevice.value) {
    showGuide.value = true;
  }
});

// 组件卸载前移除事件监听
onBeforeUnmount(() => {
  document.removeEventListener("keydown", handleKeydown);

  if (playerContainer.value) {
    playerContainer.value.removeEventListener("wheel", handleWheel);
    playerContainer.value.removeEventListener(
      "mousemove",
      showControlsTemporarily
    );
    playerContainer.value.removeEventListener(
      "touchstart",
      showControlsTemporarily
    );
  }

  // 清除控制栏定时器
  if (controlsTimer.value) {
    clearTimeout(controlsTimer.value);
  }
});

// 视频加载完成
const videoLoaded = (index) => {
  const video = videoRefs[index];
  if (video) {
    videoDurations[index] = video.duration;
  }
};

// 更新进度
const updateProgress = (index) => {
  const video = videoRefs[index];
  if (video) {
    currentTimes[index] = video.currentTime;
    isVideoPlaying[index] = !video.paused;
  }
};

// 获取进度条宽度
const getProgressWidth = (index) => {
  if (!videoDurations[index] || videoDurations[index] === 0) return "0%";
  const percentage = ((currentTimes[index] || 0) / videoDurations[index]) * 100;
  return `${percentage}%`;
};

// 开始拖动进度条
const startDrag = (event) => {
  isDragging.value = true;
  console.log('touch start, isDragging:', isDragging.value);
  dragStartX.value = event.touches[0].clientX;
  const container = event.currentTarget;
  const rect = container.getBoundingClientRect();
  dragStartPosition.value = (dragStartX.value - rect.left) / rect.width;
  const video = videoRefs[currentVideoIndex.value];
  if (video && !video.paused) {
    video.pause();
  }
};

// 拖动进度条中
const onDrag = (event) => {
  if (!isDragging.value) return;
  console.log('touch move, isDragging:', isDragging.value);
  const container = event.currentTarget;
  const rect = container.getBoundingClientRect();
  const x = event.touches[0].clientX;
  const position = Math.max(0, Math.min(1, (x - rect.left) / rect.width));
  const video = videoRefs[currentVideoIndex.value];
  if (video && videoDurations[currentVideoIndex.value]) {
    video.currentTime = position * videoDurations[currentVideoIndex.value];
    currentTimes[currentVideoIndex.value] = video.currentTime;
  }
};

// 结束拖动进度条
const endDrag = () => {
  isDragging.value = false;
  console.log('touch end, isDragging:', isDragging.value);
  const video = videoRefs[currentVideoIndex.value];
  if (video && isVideoPlaying[currentVideoIndex.value]) {
    video.play();
  }
};

// 点击进度条跳转
const seekVideo = (event, index) => {
  const video = videoRefs[index];
  if (!video || !videoDurations[index]) return;

  const container = event.currentTarget;
  const rect = container.getBoundingClientRect();
  const position = (event.clientX - rect.left) / rect.width;

  video.currentTime = position * videoDurations[index];
  currentTimes[index] = video.currentTime;

  // 如果视频是暂停的，点击进度条后应该继续保持暂停状态
  if (video.paused) {
    isVideoPlaying[index] = false;
  } else {
    isVideoPlaying[index] = true;
  }
};

// 格式化时间
const formatTime = (seconds) => {
  if (isNaN(seconds)) return "00:00";

  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = Math.floor(seconds % 60);

  return `${minutes.toString().padStart(2, "0")}:${remainingSeconds
    .toString()
    .padStart(2, "0")}`;
};

// 暂时显示控制栏
const showControlsTemporarily = () => {
  showControls.value = true;

  if (controlsTimer.value) {
    clearTimeout(controlsTimer.value);
  }

  controlsTimer.value = setTimeout(() => {
    showControls.value = false;
  }, 3000);
};

// 处理键盘事件
const handleKeydown = (event) => {
  // 阻止默认行为，避免页面滚动
  if (
    ["ArrowUp", "ArrowDown", " ", "ArrowLeft", "ArrowRight"].includes(event.key)
  ) {
    event.preventDefault();
  }

  switch (event.key) {
    case "ArrowUp":
      navigateToPrevVideo();
      break;
    case "ArrowDown":
      navigateToNextVideo();
      break;
    case " ": // 空格键
      togglePlay(currentVideoIndex.value);
      break;
    case "ArrowLeft": // 快退5秒
      seekRelative(-5);
      break;
    case "ArrowRight": // 快进5秒
      seekRelative(5);
      break;
  }
};

// 相对当前位置跳转视频
const seekRelative = (seconds) => {
  const video = videoRefs[currentVideoIndex.value];
  if (!video) return;

  // 计算新的时间位置
  let newTime = video.currentTime + seconds;
  newTime = Math.max(0, Math.min(video.duration, newTime));

  // 设置新时间
  video.currentTime = newTime;
  currentTimes[currentVideoIndex.value] = newTime;

  // 显示控制栏
  showControlsTemporarily();
};

// 处理鼠标滚轮事件
const handleWheel = (event) => {
  event.preventDefault();

  // 防抖，避免滚动太快
  if (wheelTimer) clearTimeout(wheelTimer);

  wheelTimer = setTimeout(() => {
    if (event.deltaY > 0) {
      // 向下滚动
      navigateToNextVideo();
    } else {
      // 向上滚动
      navigateToPrevVideo();
    }
  }, 200);
};

let wheelTimer = null;

// 切换到上一个视频
const navigateToPrevVideo = () => {
  if (currentVideoIndex.value > 0 && swiper.value) {
    swiper.value.prev();
  }
};

// 切换到下一个视频
const navigateToNextVideo = () => {
  if (currentVideoIndex.value < videoList.value.length - 1 && swiper.value) {
    swiper.value.next();
  }
};

// 切换视频时触发
const onVideoChange = (index) => {
  currentVideoIndex.value = index;
  pauseAllVideos();

  // 短暂延迟后播放视频，确保DOM更新完成
  setTimeout(() => {
    playVideo(index);
  }, 50);
};

// 暂停所有视频
const pauseAllVideos = () => {
  Object.values(videoRefs).forEach((video) => {
    if (video) {
      if (!video.paused) {
        video.pause();
      }
      // 重置视频进度到开始位置
      video.currentTime = 0;
    }
  });
};

// 播放指定索引的视频
const playVideo = (index) => {
  const video = videoRefs[index];
  if (video) {
    // 确保从头开始播放
    video.currentTime = 0;
    video.play().catch((err) => {
      console.log("自动播放被阻止", err);
    });
    isVideoPlaying[index] = true;
    showControlsTemporarily();
  }
};

// 点击视频切换播放/暂停
const togglePlay = (index) => {
  const video = videoRefs[index];
  if (video) {
    if (video.paused) {
      video.play();
      isVideoPlaying[index] = true;
    } else {
      video.pause();
      isVideoPlaying[index] = false;
    }
    showControlsTemporarily();
  }
};

const showGuide = ref(true);

// 在<script setup>中添加：
const isVideoLoading = reactive({});

const onVideoWaiting = (index) => {
  isVideoLoading[index] = true;
};
const onVideoCanPlay = (index) => {
  isVideoLoading[index] = false;
};
// 在playVideo、pauseAllVideos等切换视频时，建议也初始化isVideoLoading[index] = true;

// PC端鼠标拖动进度条
const startDragMouse = (event) => {
  isDragging.value = true;
  console.log('mouse down, isDragging:', isDragging.value);
  document.addEventListener('mousemove', onDragMouse);
  document.addEventListener('mouseup', endDragMouse);
};
const onDragMouse = (event) => {
  if (!isDragging.value) return;
  console.log('mouse move, isDragging:', isDragging.value);
  const container = document.querySelector('.video-progress-container');
  const rect = container.getBoundingClientRect();
  const x = event.clientX;
  const position = Math.max(0, Math.min(1, (x - rect.left) / rect.width));
  const video = videoRefs[currentVideoIndex.value];
  if (video && videoDurations[currentVideoIndex.value]) {
    video.currentTime = position * videoDurations[currentVideoIndex.value];
    currentTimes[currentVideoIndex.value] = video.currentTime;
  }
};
const endDragMouse = () => {
  isDragging.value = false;
  console.log('mouse up, isDragging:', isDragging.value);
  document.removeEventListener('mousemove', onDragMouse);
  document.removeEventListener('mouseup', endDragMouse);
  const video = videoRefs[currentVideoIndex.value];
  if (video && isVideoPlaying[currentVideoIndex.value]) {
    video.play();
  }
};

function shouldLoadVideo(index) {
  return (
    index === currentVideoIndex.value ||
    index === currentVideoIndex.value + 1
  );
}

// 关注按钮切换逻辑（仅前端效果）
function toggleFollow(video) {
  video.followed = !video.followed;
}
</script>

<style scoped>
.video-player-component {
  position: absolute;
  top: 46px;
  /* 导航栏高度 */
  left: 0;
  right: 0;
  bottom: 50px;
  /* 留出底部导航栏的空间 */
  width: 100%;
  height: calc(100% - 46px - 50px);
  /* 减去顶部导航栏和底部导航栏的高度 */
  margin: 0;
  border-radius: 0;
  overflow: hidden;
  background-color: #000;
  z-index: 1;
  /* 降低z-index，确保不会覆盖导航栏 */
  outline: none;
  /* 去除focus时的边框 */
}

.video-swipe {
  height: 100%;
  width: 100%;
}

.video-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #000;
  aspect-ratio: 9/16;
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #000;
  display: block;
}

@media (max-width: 600px) {
  .video-player {
    object-fit: contain;
    background: #000;
  }
}

/* 视频进度条样式 */
.video-progress-container {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40px;
  /* 增加触摸区域 */
  display: flex;
  align-items: center;
  z-index: 15;
  padding: 0 12px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.3), transparent);
  transition: opacity 0.3s ease;
}

.video-progress {
  width: 100%;
  height: 6px;
  /* 加粗更明显 */
  position: relative;
  cursor: pointer;
}

.progress-background {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.progress-current {
  position: absolute;
  height: 100%;
  background-color: #fe2c55;
  /* 红色 */
  border-radius: 3px;
  transition: width 0.1s, background-color 0.2s;
}

.progress-current.dragging {
  background-color: #1989fa !important;
}

.progress-dot {
  position: absolute;
  right: -5px;
  top: 50%;
  transform: translateY(-50%);
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #1989fa;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.2s ease;
}

.video-progress-container:hover .progress-dot,
.video-progress-container:active .progress-dot {
  opacity: 1;
}

/* 播放状态图标 */
.play-status {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 20;
  opacity: 0.9;
}

/* 视频信息 */
.video-info {
  position: absolute;
  left: 12px;
  bottom: 60px;
  z-index: 10;
  max-width: 70%;
  color: white;
  text-shadow: 0 0 2px rgba(0, 0, 0, 0.5);
}

.author {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.username {
  margin-left: 8px;
  font-weight: bold;
}

.description {
  margin-bottom: 2px;
  font-size: 13px;
  line-height: 1.4;
  word-break: break-all;
  position: relative;
  color: #fff;
}

/* 标签样式 */
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-top: 5px;
}

.tag {
  font-size: 12px;
  color: #1989fa;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
}

/* 右侧操作区 */
.action-bar {
  position: absolute;
  right: 12px;
  bottom: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 16px;
}

.action-item span {
  margin-top: 4px;
  font-size: 12px;
  color: white;
}

/* 电脑端控制指引 */
.pc-controls-guide {
  position: absolute;
  top: 20px;
  right: 20px;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 10px;
  border-radius: 8px;
  color: white;
  z-index: 15;
  font-size: 12px;
  transition: opacity 0.3s ease;
  min-width: 160px;
}

.guide-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.guide-item:last-child {
  margin-bottom: 0;
}

.key-box {
  display: inline-block;
  padding: 2px 6px;
  border: 1px solid white;
  border-radius: 4px;
  margin-right: 8px;
}

/* 视频时间显示 */
.video-time {
  position: absolute;
  right: 12px;
  font-size: 12px;
  color: white;
  margin-left: 10px;
}

.close-guide {
  margin-top: 8px;
  text-align: right;
}

.open-guide-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 16;
  opacity: 0.3;
  transition: opacity 0.2s;
  pointer-events: auto;
}

.open-guide-btn:hover {
  opacity: 0.85;
}

.video-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #000;
  display: block;
}

/* 抖音风格右侧操作栏 */
.dy-action-bar {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
}

.dy-action-bar .action-item {
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
  font-size: 13px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.4);
}

.dy-action-bar .action-item:last-child {
  margin-bottom: 0;
}

.dy-action-bar .van-icon {
  margin-bottom: 2px;
}

/* 抖音风格左下角信息区 */
.dy-video-info {
  position: absolute;
  left: 12px;
  bottom: 18px;
  z-index: 10;
  border-radius: 16px;
  padding: 10px 16px 10px 10px;
  max-width: 70vw;
  min-width: 180px;
  color: #fff;
  font-size: 14px;
}

.dy-video-info .author {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  margin-left: -18px;
  /* 向左移动 */
}

.dy-video-info .author .van-image {
  margin-right: 8px;
}

.dy-video-info .username {
  font-weight: 700;
  font-size: 17px;
  margin-right: 8px;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.28), 0 1px 2px rgba(0, 0, 0, 0.18);
  letter-spacing: 0.5px;
  padding-left: 4px;
  border-radius: 6px;
  background: rgba(0, 0, 0, 0.10);
  line-height: 1.2;
}

.dy-video-info .description {
  margin-bottom: 2px;
  font-size: 13px;
  line-height: 1.4;
  word-break: break-all;
  position: relative;
  color: #fff;
}

.dy-video-info .tags {
  margin-top: 2px;
}

.dy-video-info .tag {
  display: inline-block;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 8px;
  padding: 1px 7px;
  font-size: 12px;
  margin-right: 4px;
  color: #fff;
}

.desc-collapsed {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
}

.desc-expanded {
  display: block;
  overflow: visible;
  white-space: normal;
}

.desc-toggle {
  color: #4fc3f7;
  margin-left: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.18);
}

@media (max-width: 600px) {
  .dy-video-info {
    max-width: 90vw;
    font-size: 13px;
    padding: 8px 10px 8px 8px;
  }

  .dy-action-bar {
    right: 8px;
  }
}

.author-avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 22px;
  position: relative;
}

.avatar-follow-container {
  position: relative;
  width: 40px;
  height: 40px;
}

.author-avatar {
  width: 40px !important;
  height: 40px !important;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.18);
  border: 2px solid #fff;
}

.follow-btn {
  position: absolute;
  right: 8px;
  top: 35px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #fe2c55;
  color: #fff;
  border: none;
  outline: none;
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.18);
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s;
  z-index: 2;
}

.follow-btn:hover {
  background: #ff4b6e;
  box-shadow: 0 4px 12px rgba(254, 44, 85, 0.18);
}

.follow-btn.followed {
  background: #aaa;
  color: #fff;
}
</style>
