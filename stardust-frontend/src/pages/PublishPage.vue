<template>
  <div id="publish-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar title="发布" left-arrow @click-left="goBack" fixed />
    <div class="publish-content">
      <van-tabs v-model="activeTab" shrink>
        <van-tab title="视频" name="video">
          <VideoUploader
            :mediaFile="videoFile"
            :mediaUrl="videoUrl"
            @update:mediaFile="val => videoFile = val"
            @update:mediaUrl="val => videoUrl = val"
          />
        </van-tab>
        <van-tab title="图片" name="image">
          <ImageUploader
            :mediaFile="imageFile"
            :mediaUrl="imageUrl"
            @update:mediaFile="val => imageFile = val"
            @update:mediaUrl="val => imageUrl = val"
          />
        </van-tab>
      </van-tabs>
      <van-field
        v-model="description"
        rows="3"
        autosize
        type="textarea"
        maxlength="100"
        show-word-limit
        placeholder="添加描述（可选）"
        class="desc-field"
      />
      <van-button
        type="primary"
        block
        :disabled="activeTab === 'video' ? !videoUrl : !imageUrl"
        class="publish-btn"
        @click="handlePublish"
      >
        发布
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import VideoUploader from '@/components/VideoUploader.vue';
import ImageUploader from '@/components/ImageUploader.vue';
import { showToast } from 'vant';

const router = useRouter();
const isDarkMode = inject('darkMode', ref(false));
const activeTab = ref('video');
// 独立管理视频和图片的文件及url
const videoFile = ref(null);
const videoUrl = ref('');
const imageFile = ref(null);
const imageUrl = ref('');
const description = ref('');
const isLoggedIn = ref(false);

onMounted(() => {
  const savedUserInfo = localStorage.getItem('userInfo');
  isLoggedIn.value = !!savedUserInfo;
});

function goBack() {
  router.back();
}

function handlePublish() {
  // 根据当前tab判断发布内容
  if (activeTab.value === 'video') {
    if (!videoFile.value) {
      showToast('请先上传或拍摄视频');
      return;
    }
    // 上传视频逻辑
    showToast('视频发布成功！');
    videoUrl.value = '';
    videoFile.value = null;
  } else if (activeTab.value === 'image') {
    if (!imageFile.value) {
      showToast('请先上传或拍摄图片');
      return;
    }
    // 上传图片逻辑
    showToast('图片发布成功！');
    imageUrl.value = '';
    imageFile.value = null;
  }
  description.value = '';
}
</script>

<style scoped>
#publish-page {
  min-height: 100vh;
  background: #18181c; /* 深色模式下强制深色 */
  padding-bottom: 60px;
}
.dark-mode {
  background: #18181c;
  color: #fff;
}
.publish-content {
  max-width: 500px;
  margin: 46px auto 0 auto; /* NavBar高度一般为46px */
  padding: 24px 12px 0 12px;
}
.desc-field {
  margin: 24px 0 12px 0;
}
.publish-btn {
  margin-top: 18px;
  font-size: 17px;
  font-weight: 600;
  border-radius: 8px;
}
.dark-mode .desc-field .van-field__control {
  background: #232326 !important;
  color: #fff !important;
}
/* 去除van-nav-bar底部分割线和阴影 */
:deep(.van-nav-bar) {
  border-bottom: none !important;
  box-shadow: none !important;
  background: transparent !important;
}
.dark-mode :deep(.van-nav-bar) {
  background: #18181c !important;
}
</style>