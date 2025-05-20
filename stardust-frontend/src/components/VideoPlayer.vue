<template>
  <div class="video-player-component" ref="playerContainer" tabindex="0">
    <van-swipe class="video-swipe" :loop="false" vertical :show-indicators="false" @change="onVideoChange" ref="swiper">
      <van-swipe-item v-for="(video, index) in videoList" :key="index">
        <div class="video-container">
          <!-- 视频播放器 -->
          <video class="video-player" :src="video.videoUrl" :poster="video.coverUrl" x5-video-player-type="h5"
            x5-playsinline="true" webkit-playsinline="true" playsinline="true" muted :ref="(el) => {
                videoRefs[index] = el;
              }" loop @click="togglePlay(index)" @timeupdate="updateProgress(index)"
            @loadedmetadata="videoLoaded(index)"></video>

          <!-- 播放状态图标 -->
          <div class="play-status" v-if="isVideoPlaying[index] === false">
            <van-icon name="play-circle-o" size="50" color="#fff" />
          </div>

          <!-- 右侧操作栏 -->
          <div class="action-bar">
            <div class="action-item">
              <van-icon name="like-o" size="30" color="#fff" />
              <span>{{ video.likes }}</span>
            </div>
            <div class="action-item">
              <van-icon name="chat-o" size="30" color="#fff" />
              <span>{{ video.comments }}</span>
            </div>
            <div class="action-item">
              <van-icon name="star-o" size="30" color="#fff" />
              <span>收藏</span>
            </div>
            <div class="action-item">
              <van-icon name="share-o" size="30" color="#fff" />
              <span>分享</span>
            </div>
          </div>

          <!-- 底部信息区 -->
          <div class="video-info">
            <div class="author">
              <van-image round width="40" height="40" :src="video.avatar" />
              <span class="username">@{{ video.username }}</span>
            </div>
            <div class="description">
              {{ video.description }}
            </div>
            <!-- 添加标签展示 -->
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
      @touchstart.stop="startDrag($event)" @touchmove.stop="onDrag($event)" @touchend.stop="endDrag()">
      <div class="video-progress">
        <div class="progress-background"></div>
        <div class="progress-current" :style="{ width: getProgressWidth(currentVideoIndex) }">
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
    <van-button
      v-if="isPcDevice && !showGuide"
      class="open-guide-btn"
      size="mini"
      type="primary"
      @click="showGuide = true"
      icon="question-o"
    >
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
const videoList = ref([
  {
    videoUrl:
      "https://lf9-cdn-tos.bytecdntp.com/cdn/expire-1-M/byted-player-videos/1.0.0/xgplayer-demo-360p.mp4",
    coverUrl: "https://pic.616pic.com/bg_w1180/00/11/35/G7cf0897US.jpg",
    username: "舞动生活",
    description: "跟随音乐的节奏一起舞动起来",
    tags: ["舞蹈", "音乐"],
    likes: "8.7w",
    comments: "1.8w",
    avatar: "https://fastly.jsdelivr.net/npm/@vant/assets/tiger.jpeg",
  },
  {
    videoUrl: "https://media.w3.org/2010/05/sintel/trailer.mp4",
    coverUrl: "https://pic.616pic.com/bg_w1180/00/04/18/j5i38lDy48.jpg",
    username: "电影爱好者",
    description: "精彩电影片段分享，带你领略电影的魅力",
    tags: ["电影", "预告片"],
    likes: "9.6w",
    comments: "2.4w",
    avatar: "https://fastly.jsdelivr.net/npm/@vant/assets/apple-3.jpeg",
  },
  {
    videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4",
    coverUrl: "https://pic.616pic.com/bg_w1180/00/15/72/N8cf0897US.jpg",
    username: "美食达人",
    description: "今天教大家做一道美味的家常菜",
    tags: ["美食", "烹饪", "家常菜"],
    likes: "12.3w",
    comments: "3.5w",
    avatar: "https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg",
  },
  {
    videoUrl:
      "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
    coverUrl: "https://pic.616pic.com/bg_w1180/00/19/83/K7cf0897US.jpg",
    username: "旅行者小张",
    description: "带你探索世界各地的美景",
    tags: ["旅行", "风景", "vlog"],
    likes: "15.6w",
    comments: "4.2w",
    avatar: "https://fastly.jsdelivr.net/npm/@vant/assets/ipad.jpeg",
  },
  {
    videoUrl: "https://download.samplelib.com/mp4/sample-5s.mp4",
    coverUrl: "https://pic.616pic.com/bg_w1180/00/21/94/P8cf0897US.jpg",
    username: "萌宠日记",
    description: "记录我家可爱宠物的日常",
    tags: ["萌宠", "猫咪", "宠物"],
    likes: "20.1w",
    comments: "5.8w",
    avatar: "https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg",
  },
  {
    videoUrl:
      "https://assets.mixkit.co/videos/preview/mixkit-countryside-meadow-4075-large.mp4",
    coverUrl: "https://pic.616pic.com/bg_w1180/00/13/42/Z7cf0897US.jpg",
    username: "乡村时光",
    description: "远离城市喧嚣，感受乡村宁静时光",
    tags: ["乡村", "自然", "慢生活"],
    likes: "11.7w",
    comments: "2.9w",
    avatar: "https://fastly.jsdelivr.net/npm/@vant/assets/desert.jpeg",
  },
]);

// 检测设备类型
const checkDeviceType = () => {
  // 更完善的设备检测
  const userAgent = navigator.userAgent.toLowerCase();
  const isMobile = /iphone|ipod|android|ios|mobile|phone|pad/.test(userAgent);
  isPcDevice.value = !isMobile;
};

// 在组件挂载后播放第一个视频
onMounted(() => {
  checkDeviceType();

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
  dragStartX.value = event.touches[0].clientX;

  const container = event.currentTarget;
  const rect = container.getBoundingClientRect();
  dragStartPosition.value = (dragStartX.value - rect.left) / rect.width;

  // 暂停视频
  const video = videoRefs[currentVideoIndex.value];
  if (video && !video.paused) {
    video.pause();
  }
};

// 拖动进度条中
const onDrag = (event) => {
  if (!isDragging.value) return;

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

  // 恢复视频播放状态
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
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #000;
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
  height: 2px;
  /* 更细的进度条 */
  position: relative;
  cursor: pointer;
}

.progress-background {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 1px;
}

.progress-current {
  position: absolute;
  height: 100%;
  background-color: #fe2c55;
  /* 抖音红色 */
  border-radius: 1px;
  transition: width 0.1s;
}

.progress-dot {
  position: absolute;
  right: -5px;
  top: 50%;
  transform: translateY(-50%);
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #fe2c55;
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
  font-size: 14px;
  margin-bottom: 8px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
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
</style>
