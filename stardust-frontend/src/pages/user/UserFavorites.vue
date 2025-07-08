<template>
  <div class="favorites-page">
    <van-nav-bar title="我的收藏" />
    <div v-if="loading" class="loading">
      <van-loading size="24px">加载中...</van-loading>
    </div>
    <div v-else>
      <div v-if="videos.length > 0" class="video-list">
        <div
          v-for="video in videos"
          :key="video.id"
          class="video-card"
          @click="toPlay(video)"
        >
          <img :src="video.coverUrl" class="video-cover" />
          <div class="video-info">
            <div class="video-title">{{ video.description }}</div>
            <div class="video-author">@{{ video.username }}</div>
          </div>
        </div>
      </div>
      <div v-else class="empty-content">
        <van-empty description="还没有收藏内容" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const userId = localStorage.getItem('userId') // 或从全局store获取
const videos = ref([])
const loading = ref(true)
const router = useRouter()

const fetchFavorites = async () => {
  loading.value = true
  try {
    // 1. 获取收藏视频ID列表
    const res = await axios.get('/api/video/favorite/list', { params: { userId } })
    const videoIds = res.data.data || []
    // 2. 批量获取视频详情
    if (videoIds.length > 0) {
      const detailRes = await axios.post('/video/batch', videoIds)
      videos.value = detailRes.data.data || []
    } else {
      videos.value = []
    }
  } catch (e) {
    videos.value = []
  }
  loading.value = false
}

const toPlay = (video) => {
  router.push({ name: 'VideoPlayer', params: { id: video.id } })
}

onMounted(fetchFavorites)
</script>

<style scoped>
.favorites-page {
  background: #f8f8f8;
  min-height: 100vh;
}
.video-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 12px;
}
.video-card {
  width: 46vw;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px #eee;
  cursor: pointer;
}
.video-cover {
  width: 100%;
  height: 120px;
  object-fit: cover;
}
.video-info {
  padding: 8px;
}
.video-title {
  font-weight: bold;
  font-size: 15px;
  margin-bottom: 4px;
}
.video-author {
  color: #888;
  font-size: 13px;
}
.empty-content {
  margin-top: 60px;
}
.loading {
  margin-top: 60px;
  text-align: center;
}
</style> 