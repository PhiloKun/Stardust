<template>
  <van-icon
    :name="isFavorited ? 'star' : 'star-o'"
    @click="toggleFavorite"
    color="#ffd700"
    size="28"
  />
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  videoId: String,
  userId: String
})
const isFavorited = ref(false)

const fetchFavoriteStatus = async () => {
  if (!props.userId) return
  const res = await axios.get(`/api/video/favorite/${props.videoId}/status`, { params: { userId: props.userId } })
  isFavorited.value = res.data.data
}
const toggleFavorite = async () => {
  if (!props.userId) {
    // 可弹窗提示登录
    return
  }
  if (isFavorited.value) {
    await axios.delete(`/api/video/favorite/${props.videoId}`, { params: { userId: props.userId } })
  } else {
    await axios.post(`/api/video/favorite/${props.videoId}`, null, { params: { userId: props.userId } })
  }
  fetchFavoriteStatus()
}
onMounted(fetchFavoriteStatus)
watch(() => props.videoId, fetchFavoriteStatus)
</script> 