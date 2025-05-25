<template>
  <div class="uploader-content">
    <div v-if="!mediaUrl" class="upload-actions">
      <van-uploader
        accept="video/*"
        :max-count="1"
        :after-read="onFileRead"
        capture="environment"
      >
        <van-button icon="plus" type="primary">上传视频</van-button>
      </van-uploader>
      <van-button icon="photograph" type="default" @click="openCamera">拍摄视频</van-button>
    </div>
    <div v-else class="media-preview">
      <video :src="mediaUrl" controls class="preview-video" />
      <van-button icon="delete" type="danger" size="small" @click="removeMedia">移除</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { showToast } from 'vant';

const emit = defineEmits(['success', 'update:mediaFile', 'update:mediaUrl']);
const props = defineProps({
    mediaFile: Object,
    mediaUrl: String
});

// const emit = defineEmits(['update:mediaFile', 'update:mediaUrl']); // 移除emit更新父组件状态

// const mediaUrl = ref(''); // 恢复内部状态管理
// const mediaFile = ref(null); // 恢复内部状态管理

function onFileRead(file) {
  if (file && file.file) {
    // mediaFile.value = file.file; // 直接修改内部状态
    // mediaUrl.value = URL.createObjectURL(file.file);
    emit('update:mediaFile', file.file);
    emit('update:mediaUrl', URL.createObjectURL(file.file));
  }
}
function removeMedia() {
  // mediaUrl.value = ''; // 直接修改内部状态
  // mediaFile.value = null;
   emit('update:mediaFile', null);
    emit('update:mediaUrl', '');
}
function openCamera() {
  const uploader = document.querySelector('.video-uploader-input');
  if (uploader) uploader.click();
}
// function handlePublish() {
//   if (!mediaFile.value) {
//     showToast('请先上传或拍摄视频');
//     return;
//   }
//   // 这里可上传到后端
//   showToast('视频发布成功！');
//   emit('success');
//   removeMedia();
// }

defineExpose({
    // mediaFile,
    // mediaUrl,
    removeMedia
});
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
  /* padding: 20px; */ /* 移除合并上传区域的样式 */
  /* border: 2px dashed #ebedf0; */ /* 移除合并上传区域的样式 */
  /* border-radius: 8px; */ /* 移除合并上传区域的样式 */
  /* text-align: center; */ /* 移除合并上传区域的样式 */
  /* cursor: pointer; */ /* 移除合并上传区域的样式 */
  /* color: #888; */ /* 移除合并上传区域的样式 */
  /* transition: border-color 0.3s ease; */ /* 移除合并上传区域的样式 */
}

.dark-mode .uploader-content .upload-actions {
    border-color: #4a4a52; /* 深色模式边框颜色 */
    color: #adadad; /* 深色模式字体颜色 */
}

/*
.upload-actions:hover {
    border-color: #1989fa;
}
*/

.media-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.preview-video {
  max-width: 100%;
  max-height: 260px;
  border-radius: 10px;
  background: #000;
}

@media (min-width: 768px) {
    .media-preview {
        max-width: 1200px;
        margin: 0 auto;
    }
    .preview-video {
        max-height: 700px;
    }
}
</style> 