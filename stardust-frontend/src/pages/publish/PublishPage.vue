<template>
  <div id="publish-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar title="发布" left-arrow @click-left="goBack" fixed />
    <div class="publish-content">
      <van-tabs v-model="activeTab" shrink>
        <van-tab title="视频" name="video">
          <VideoUploader
            :mediaFile="videoFile"
            :mediaUrl="videoUrl"
            @update:mediaFile="(val) => (videoFile = val)"
            @update:mediaUrl="(val) => (videoUrl = val)"
          />
        </van-tab>
      </van-tabs>

      <van-field
        v-model="description"
        rows="6"
        autosize
        type="textarea"
        maxlength="100"
        show-word-limit
        placeholder="添加描述"
        class="desc-field"
        clearable="true"
      />

      <div class="tag-input-box">
        <van-tag
          v-for="(tag, index) in tags"
          :key="index"
          size="medium"
          type="primary"
          @close="removeTag(index)"
          >{{ tag }}</van-tag
        >
        <van-field
          v-model="newTag"
          placeholder="输入标签(可选) (回车添加)"
          :disabled="tags.length >= 6"
          @keyup.enter="addTag"
          class="new-tag-input"
        >
          <template #button>
            <van-icon 
              name="delete-o" 
              class="clear-tags-icon"
              @click="tags = []"
              v-if="tags.length > 0"
            />
          </template>
        </van-field>
      </div>

      <div class="tag-count">{{ tags.length }}/6</div>
    </div>
    <div class="publish-footer">
      <van-button
        type="primary"
        block
        :disabled="!description.trim() || !videoUrl"
        class="publish-btn"
        @click="handlePublish"
      >
        发布
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from "vue";
import { useRouter } from "vue-router";
import VideoUploader from "@/components/VideoUploader.vue";
import { showToast } from "vant";
import { useVideoStore } from "@/stores/videoStore";
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/userStore";

const router = useRouter();
const isDarkMode = inject("darkMode", ref(false));

const videoStore = useVideoStore();
const {
  activeTab,
  videoFile,
  videoUrl,
  description,
  tags,
  newTag,
  isUploading,
} = storeToRefs(videoStore);
const { addTag, removeTag, uploadVideo } = videoStore;

const userStore = useUserStore();
const { userInfo, isLoggedIn } = storeToRefs(userStore);
const userId = ref(null);

onMounted(() => {
  if (userInfo.value && userInfo.value.id) {
    userId.value = userInfo.value.id;
  } else {
    const savedUserInfo = localStorage.getItem("userInfo");
    if (savedUserInfo) {
      try {
        const userInfoFromLocal = JSON.parse(savedUserInfo);
        if (userInfoFromLocal && userInfoFromLocal.id !== undefined) {
          userId.value = String(userInfoFromLocal.id);
        } else {
          console.error(
            "Error parsing userId from localStorage or id is missing/invalid.",
            userInfoFromLocal
          );
        }
      } catch (e) {
        console.error("Error parsing userInfo from localStorage:", e);
      }
    }
  }
});

function goBack() {
  router.back();
}

async function handlePublish() {
  if (!userId.value) {
    showToast("用户信息未加载，请稍后再试或重新登录");
    return;
  }
  await uploadVideo(userId.value);
}
</script>

<style scoped>
#publish-page {
  min-height: 100vh;
  background: #ffffff; /* 浅色模式下使用白色背景 */
  padding-bottom: 80px;
  /* 为固定底部的按钮留出空间 */
}

.dark-mode#publish-page {
  background: #18181c !important; /* 强制深色背景 */
  color: #fff;
}

.publish-content {
  /* max-width: 500px; */
  /* 移除最大宽度限制 */
  margin: 46px auto 0 auto;
  /* NavBar高度一般为46px，保留顶部外边距 */
  padding: 16px 12px 0 12px;
  /* 调整顶部内边距 */
  width: 100%;
  /* 确保宽度填满父容器 */
  box-sizing: border-box;
  /* 确保padding不增加宽度 */
}

.publish-content :deep(.van-tabs) {
  margin-bottom: 24px;
  /* 为标签页添加底部外边距 */
}

.desc-field {
  margin-bottom: 20px;
  /* 增加底部外边距 */
  /* 添加浅色模式下的边框和背景 */
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
}

.dark-mode .desc-field {
    border-color: #4a4a52;
    background-color: #232326;
}

.dark-mode .desc-field :deep(.van-field__control) {
  color: #fff !important;
}

.tag-input-box {
  border: 1px solid #dcdfe6;
  /* 模拟 van-field 边框 */
  border-radius: 4px;
  /* 模拟 van-field 圆角 */
  padding: 16px 12px;
  /* 增加垂直内边距 */
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  /* 标签和输入框之间的间距 */
  margin-bottom: 16px;
  /* 增加底部外边距 */
  background-color: #fff;
  /* 浅色模式下使用白色背景 */
}

.dark-mode .tag-input-box {
  border-color: #4a4a52;
  /* 深色模式边框颜色 */
  background-color: #232326;
  /* 深色模式背景颜色 */
}

.tag-input-box .van-tag {
  flex-shrink: 0;
  /* 标签不缩小 */
}

.new-tag-input {
  flex-grow: 1;
  /* 输入框占据剩余空间 */
  border: none;
  outline: none;
  background: transparent;
  /* 输入框背景透明 */
  color: #323233;
  /* 默认字体颜色 */
  padding: 0;
  /* 移除默认内边距 */
}

.dark-mode .new-tag-input {
  color: #fff;
  /* 深色模式字体颜色 */
}

.tag-count {
  text-align: right;
  font-size: 12px;
  color: #969799;
  /* 类似 van-field 提示文字的颜色 */
  margin-top: -8px;
  /* 向上微调位置 */
  margin-bottom: 20px;
  /* 增加底部外边距 */
}

.dark-mode .tag-count {
  color: #adadad;
  /* 深色模式下的计数器颜色 */
}

.publish-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 12px;
  background: #ffffff;
  /* 浅色模式下使用白色背景 */
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  /* 添加阴影 */
  box-sizing: border-box;
  /* 确保padding不增加宽度 */
  /* max-width: 500px; */
  /* 移除最大宽度限制 */
  left: 50%;
  /* 水平居中 */
  transform: translateX(-50%);
  /* 水平居中 */
  width: 100%;
  /* 确保宽度填满 */
}

.dark-mode .publish-footer {
  background: #18181c;
  /* 深色模式背景 */
}

.publish-footer .publish-btn {
  font-size: 17px;
  font-weight: 600;
  border-radius: 8px;
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

/* 恢复van-tabs 相关的深色模式样式 */

.dark-mode :deep(.van-tabs__content) {
  background-color: #18181c !important;
}

.dark-mode :deep(.van-tab__pane) {
  background-color: #18181c !important;
}

.dark-mode :deep(.van-tabs__wrap) {
  background-color: #18181c !important;
}

.dark-mode :deep(.van-tabs__nav) {
  background-color: #18181c !important;
}

.dark-mode :deep(.van-tag--primary) {
  background-color: #0c346c !important;
  /* 深色模式下调整 van-tag 的背景色 */
  color: #fff !important;
}
</style>
