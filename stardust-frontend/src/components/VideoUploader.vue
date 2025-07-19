<template>
  <div class="uploader-content">
    <div v-if="!mediaUrl && !isRecording && !cameraStream" class="upload-actions">
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

    <!-- 摄像头预览区域 -->
    <div v-if="!mediaUrl && cameraStream" class="camera-preview">
      <video ref="cameraVideo" autoplay muted playsinline class="preview-video"></video>
      <!-- 根据录制状态切换按钮 -->
      <template v-if="!isRecording">
        <van-button type="success" size="small" @click="startRecording">开始录制</van-button>
        <van-button type="default" size="small" @click="stopCamera">取消</van-button>
      </template>
      <template v-else>
        <van-button type="danger" size="small" @click="stopRecording">停止录制</van-button>
      </template>
    </div>

    <div v-if="mediaUrl" class="media-preview">
      <video :src="mediaUrl" controls class="preview-video" />
      <van-button icon="delete" type="danger" size="small" @click="removeMedia">移除</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, shallowRef, watch } from 'vue';
import { showToast } from 'vant';

const emit = defineEmits(['success', 'update:mediaFile', 'update:mediaUrl']);
const props = defineProps({
    mediaFile: Object,
    mediaUrl: String
});

// 内部状态
const cameraVideo = ref(null); // 摄像头预览video元素引用
const cameraStream = shallowRef(null); // 摄像头媒体流
const mediaRecorder = shallowRef(null); // MediaRecorder 实例
const recordedChunks = ref([]); // 存储录制的数据块
const isRecording = ref(false); // 是否正在录制

function onFileRead(file) {
  if (file && file.file) {
    emit('update:mediaFile', file.file);
    emit('update:mediaUrl', URL.createObjectURL(file.file));
  }
}

function removeMedia() {
   emit('update:mediaFile', null);
    emit('update:mediaUrl', '');
}

async function openCamera() {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
    cameraStream.value = stream;
  } catch (err) {
    console.error('无法获取摄像头权限: ', err);
    showToast('无法获取摄像头权限，请检查设置。');
  }
}

// 添加 watch 监听 cameraStream 和 cameraVideo
watch([cameraStream, cameraVideo], ([newStream, newVideoElement]) => {
  if (newStream && newVideoElement) {
    newVideoElement.srcObject = newStream;
  }
});

function stopCamera() {
    if (cameraStream.value) {
        cameraStream.value.getTracks().forEach(track => track.stop());
        cameraStream.value = null;
        if (cameraVideo.value) {
            cameraVideo.value.srcObject = null;
        }
    }
    isRecording.value = false;
    recordedChunks.value = [];
}

function startRecording() {
    if (!cameraStream.value) {
        showToast('摄像头流未准备好');
        return;
    }

    try {
        recordedChunks.value = [];
        mediaRecorder.value = new MediaRecorder(cameraStream.value);

        mediaRecorder.value.ondataavailable = (event) => {
            if (event.data.size > 0) {
                recordedChunks.value.push(event.data);
            }
        };

        mediaRecorder.value.onstop = () => {
            const blob = new Blob(recordedChunks.value, { type: 'video/webm' });
            const file = new File([blob], `video-${Date.now()}.webm`, { type: 'video/webm' });
            emit('update:mediaFile', file);
            emit('update:mediaUrl', URL.createObjectURL(blob));
            stopCamera(); // 停止摄像头并清理
        };

        mediaRecorder.value.start();
        isRecording.value = true;
    } catch (error) {
        console.error('录制启动失败:', error);
        showToast('录制启动失败，请重试。');
        stopCamera(); // 出错时也停止摄像头
    }
}

function stopRecording() {
    if (mediaRecorder.value && mediaRecorder.value.state !== 'inactive') {
        mediaRecorder.value.stop();
        isRecording.value = false;
    }
}

defineExpose({
    removeMedia,
    stopCamera // 暴露 stopCamera 方法以便外部需要时调用
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
}

.dark-mode .uploader-content .upload-actions {
    border-color: #4a4a52; /* 深色模式边框颜色 */
    color: #adadad; /* 深色模式字体颜色 */
}

.camera-preview {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
}

.recording-indicator {
    text-align: center;
    margin-top: 20px;
    color: red;
}

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
    .media-preview,
    .camera-preview {
        max-width: 1200px;
        margin: 0 auto;
    }
    .preview-video {
        max-height: 700px;
    }
}
</style> 