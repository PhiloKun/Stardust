<template>
  <div class="uploader-content">
    <div v-if="!mediaUrl" class="upload-actions">
      <van-uploader
        accept="image/*"
        :max-count="1"
        :after-read="onFileRead"
        capture="environment"
      >
        <van-button icon="plus" type="primary">上传图片</van-button>
      </van-uploader>
      <van-button icon="photograph" type="default" @click="openCamera">拍摄照片</van-button>
    </div>
    <div v-else class="media-preview">
      <img :src="mediaUrl" class="preview-image" />
      <van-button icon="delete" type="danger" size="small" @click="removeMedia">移除</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { showToast } from 'vant';

const emit = defineEmits(['success']);
const mediaUrl = ref('');
const mediaFile = ref(null);

function onFileRead(file) {
  if (file && file.file) {
    mediaFile.value = file.file;
    mediaUrl.value = URL.createObjectURL(file.file);
  }
}
function removeMedia() {
  mediaUrl.value = '';
  mediaFile.value = null;
}
function openCamera() {
  const uploader = document.querySelector('.image-uploader-input');
  if (uploader) uploader.click();
}
function handlePublish() {
  if (!mediaFile.value) {
    showToast('请先上传或拍摄图片');
    return;
  }
  // 这里可上传到后端
  showToast('图片发布成功！');
  emit('success');
  removeMedia();
}
</script>

<style scoped>
.uploader-content {
  margin: 0 auto;
  padding: 18px 0 0 0;
}
.upload-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}
.media-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.preview-image {
  max-width: 100%;
  max-height: 260px;
  border-radius: 10px;
  background: #000;
}
</style> 