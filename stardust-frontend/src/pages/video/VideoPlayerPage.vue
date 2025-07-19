<template>
  <div class="video-player-page">
    <video ref="videoRef" v-if="video && video.videoUrl && !error" class="main-video" :src="video.videoUrl"
      :poster="video.coverUrl" autoplay loop @click="togglePlay" @timeupdate="onTimeUpdate"
      @loadedmetadata="onLoadedMetadata" :muted="false" :style="{ objectFit: objectFitMode }"></video>
    <div class="custom-controls" v-if="video && !error">
      <div class="progress-bar" ref="progressBarRef" @mousedown="onProgressMouseDown"
        @touchstart="onProgressTouchStart">
        <div class="progress" :style="{ width: progressPercent + '%' }"></div>
        <div class="progress-thumb" :style="{ left: progressPercent + '%' }"></div>
      </div>
      <div class="progress-time-row">
        <span class="current-time">{{ formatTimeDisplay(currentTime) }}</span>
        <span class="total-time">{{ formatTimeDisplay(duration) }}</span>
      </div>
      <div class="play-btn" @click.stop="togglePlay">
        <van-icon :name="isPlaying ? 'pause' : 'play'" size="32" color="#fff" />
      </div>
    </div>
    <div class="video-title" v-if="video">{{ video.title }}</div>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-if="error" class="error-tip">视频加载失败</div>
    <van-icon name="arrow-left" class="back-icon" @click="goBack" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchVideoDetail } from '@/utils/request';

const route = useRoute();
const router = useRouter();
const video = ref(null);
const loading = ref(true);
const error = ref(false);

const videoRef = ref(null);
const isPlaying = ref(true);
const duration = ref(0);
const currentTime = ref(0);

const objectFitMode = ref('cover');

const progressBarRef = ref(null);
const isDragging = ref(false);

const progressPercent = computed(() =>
  duration.value ? (currentTime.value / duration.value) * 100 : 0
);

const goBack = () => router.back();

const onVideoError = () => {
  error.value = true;
  loading.value = false;
};

const togglePlay = () => {
  if (!videoRef.value) return;
  if (videoRef.value.paused) {
    videoRef.value.play();
    isPlaying.value = true;
  } else {
    videoRef.value.pause();
    isPlaying.value = false;
  }
};

const onTimeUpdate = () => {
  if (videoRef.value && !isDragging.value) {
    currentTime.value = videoRef.value.currentTime;
  }
};

const onLoadedMetadata = () => {
  if (videoRef.value) {
    duration.value = videoRef.value.duration;
    // 智能切换object-fit
    const ratio = videoRef.value.videoWidth / videoRef.value.videoHeight;
    objectFitMode.value = ratio < 0.8 ? 'cover' : 'contain';
  }
};

// 拖动进度条（PC端）
const onProgressMouseDown = (e) => {
  isDragging.value = true;
  updateProgress(e.clientX);
  window.addEventListener('mousemove', onMouseMove);
  window.addEventListener('mouseup', onMouseUp);
};
const onMouseMove = (e) => {
  updateProgress(e.clientX);
};
const onMouseUp = (e) => {
  updateProgress(e.clientX, true);
  isDragging.value = false;
  window.removeEventListener('mousemove', onMouseMove);
  window.removeEventListener('mouseup', onMouseUp);
};

// 拖动进度条（移动端）
const onProgressTouchStart = (e) => {
  isDragging.value = true;
  updateProgress(e.touches[0].clientX);
  window.addEventListener('touchmove', onTouchMove);
  window.addEventListener('touchend', onTouchEnd);
};
const onTouchMove = (e) => {
  updateProgress(e.touches[0].clientX);
};
const onTouchEnd = (e) => {
  updateProgress(e.changedTouches[0].clientX, true);
  isDragging.value = false;
  window.removeEventListener('touchmove', onTouchMove);
  window.removeEventListener('touchend', onTouchEnd);
};

// 计算拖动位置对应的时间
function updateProgress(clientX, setVideo = false) {
  const bar = progressBarRef.value;
  if (!bar || !duration.value) return;
  const rect = bar.getBoundingClientRect();
  let percent = (clientX - rect.left) / rect.width;
  percent = Math.max(0, Math.min(1, percent));
  const newTime = percent * duration.value;
  currentTime.value = newTime;
  if (setVideo && videoRef.value) {
    videoRef.value.currentTime = newTime;
  }
}

function formatTimeDisplay(sec) {
  sec = Math.floor(sec || 0);
  const m = Math.floor(sec / 60).toString().padStart(2, '0');
  const s = (sec % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
}

onMounted(async () => {
  const id = route.params.id;
  try {
    const res = await fetchVideoDetail(id);
    if (res && res.data && res.data.data) {
      video.value = res.data.data;
      loading.value = false;
      error.value = !video.value.videoUrl;
    } else {
      error.value = true;
      loading.value = false;
    }
  } catch (e) {
    error.value = true;
    loading.value = false;
  }
});
</script>

<style scoped>
html,
body,
#app {
  width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 0;
  background: #000;
  overflow: hidden;
}

.video-player-page {
  position: fixed;
  left: 0;
  top: 0;
  width: 100vw;
  height: 100vh;
  background: #000;
  z-index: 9999;
}

.main-video {
  width: 100vw;
  height: 100vh;
  object-fit: cover;
  background: #000;
  display: block;
}

@media (max-width: 600px) {
  .main-video {
    object-fit: contain;
    background: #000;
  }
}

.custom-controls {
  position: absolute;
  left: 0;
  bottom: 80px;
  /* 距离底部导航栏有间距 */
  width: 100vw;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
}

.progress-bar {
  width: 80vw;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  margin-bottom: 8px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.progress {
  height: 100%;
  background: #fff;
  width: 0;
  transition: width 0.2s;
}

.progress-thumb {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  background: #fff;
  border-radius: 50%;
  left: 0;
  margin-left: -7px;
  pointer-events: none;
}

.play-btn {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  padding: 8px;
  margin-bottom: 4px;
}

.video-title {
  position: absolute;
  bottom: 60px;
  left: 0;
  width: 100vw;
  color: #fff;
  text-align: center;
  font-size: 20px;
  text-shadow: 0 2px 8px #000;
}

.back-icon {
  position: absolute;
  top: 20px;
  left: 20px;
  color: #fff;
  font-size: 28px;
  z-index: 10;
  opacity: 0.6;
  transition: opacity 0.2s;
  cursor: pointer;
}

.back-icon:hover,
.back-icon:active {
  opacity: 1;
}

.loading {
  color: #fff;
  text-align: center;
  margin-top: 40vh;
}

.error-tip {
  color: #fff;
  text-align: center;
  margin-top: 40vh;
  font-size: 18px;
}

.progress-time-row {
  width: 80vw;
  display: flex;
  justify-content: space-between;
  color: #fff;
  font-size: 13px;
  margin-bottom: 4px;
  user-select: none;
  text-shadow: 0 1px 4px #000;
}
</style>