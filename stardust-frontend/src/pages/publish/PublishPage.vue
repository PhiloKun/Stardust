<!-- 发布页面组件 -->
<template>
  <!-- 页面根容器，支持深色模式 -->
  <div id="publish-page" :class="{ 'dark-mode': isDarkMode }">
    <!-- 顶部导航栏 -->
    <van-nav-bar title="发布" left-arrow @click-left="goBack" fixed />
    <div class="publish-content">
      <!-- 视频上传标签页 -->
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

      <!-- 视频描述输入框 -->
      <van-cell-group title="视频描述">
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
      </van-cell-group>

      <!-- 标签输入区域 -->
      <van-cell-group title="标签">
        <div class="tag-input-box">
          <!-- 已添加的标签列表 -->
          <div v-for="(tag, index) in tags" :key="index">
            <div v-if="editingTagIndex === index">
              <van-field
                v-model="editingTagValue"
                placeholder="编辑标签"
                class="edit-tag-input-field"
                clearable
                @keyup.enter="saveTagEdit(index)"
                @blur="saveTagEdit(index)"
              />
              <van-button
                type="primary"
                size="small"
                @click="saveTagEdit(index)"
                >保存</van-button
              >
              <van-button size="small" @click="cancelTagEdit">取消</van-button>
            </div>
            <div v-else>
              <van-tag
                :key="index"
                size="medium"
                type="primary"
                closeable
                @close="removeTag(index)"
                @click="startEditTag(index, tag)"
                >{{ tag }}</van-tag
              >
            </div>
          </div>
          <!-- 新标签输入框和操作按钮 -->
          <template v-if="showTagInput">
            <van-field
              v-model="newTag"
              placeholder="输入标签内容"
              class="new-tag-input-field"
              clearable
            />
            <van-button
              type="primary"
              size="small"
              @click="handleConfirmTag"
              :disabled="!newTag.trim() || newTag.trim().length > 20"
              >确定</van-button
            >
            <van-button size="small" @click="handleCancelTag">取消</van-button>
          </template>
          <!-- 添加新标签的入口 -->
          <van-button
            v-if="!showTagInput && editingTagIndex === -1 && tags.length < 6"
            type="primary"
            size="small"
            plain
            round
            @click="showTagInput = true"
          >
            + 添加标签
          </van-button>
        </div>
      </van-cell-group>

      <!-- 标签数量显示 -->
      <div class="tag-count">{{ tags.length }}/6</div>
    </div>
    <!-- 上传进度条 -->
    <div v-if="isUploading && uploadProgress > 0 && uploadProgress < 100" class="upload-progress-bar">
      <van-progress :percentage="uploadProgress" color="#1989fa" track-color="#e5e5e5" show-pivot pivot-text="{{uploadProgress}}%" />
    </div>
    <!-- 底部发布按钮 -->
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
// 导入必要的Vue组件和工具
import { ref, inject, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import VideoUploader from "@/components/VideoUploader.vue";
import { showToast } from "vant";
import { useVideoStore } from "@/stores/videoStore";
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/userStore";

// 路由和深色模式设置
const router = useRouter();
const isDarkMode = inject("darkMode", ref(false));

// 控制标签输入框的显示/隐藏 (用于添加新标签)
const showTagInput = ref(false);
// 控制和管理标签编辑状态
const editingTagIndex = ref(-1); // -1 表示没有标签正在编辑
const editingTagValue = ref(""); // 存储正在编辑的标签值

// 视频相关状态管理
const videoStore = useVideoStore();
const {
  activeTab,
  videoFile,
  videoUrl,
  description,
  tags,
  newTag,
  isUploading,
  uploadProgress,
} = storeToRefs(videoStore);
const { addTag, removeTag, uploadVideo } = videoStore;

// 用户相关状态管理
const userStore = useUserStore();
const { userInfo, isLoggedIn } = storeToRefs(userStore);
const userId = ref(null);

// 组件挂载时获取用户ID
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

// 监听 newTag 输入，实时校验长度
watch(newTag, (newValue) => {
  if (newValue.length > 20) {
    showToast("新标签最多20个字符");
  }
});

// 监听 editingTagValue 输入，实时校验长度
watch(editingTagValue, (newValue) => {
  if (newValue.length > 20) {
    showToast("编辑标签最多20个字符");
  }
});

// 返回上一页
function goBack() {
  router.back();
}

// 处理确定添加标签 (用于新标签)
function handleConfirmTag() {
  addTag(); // 调用 store 中的 addTag 方法
  // 检查是否添加成功 (addTag 成功时会清空 newTag)
  if (newTag.value === "") {
    showTagInput.value = false;
  }
}

// 处理取消添加标签 (用于新标签)
function handleCancelTag() {
  showTagInput.value = false;
  newTag.value = ""; // 清空输入框内容
}

// 进入编辑标签模式
function startEditTag(index, value) {
  // 如果当前正在编辑，则先退出编辑模式
  if (editingTagIndex.value !== -1) {
    cancelTagEdit();
  }
  editingTagIndex.value = index;
  editingTagValue.value = value;
  showTagInput.value = false; // 隐藏新标签输入
}

// 保存标签编辑
function saveTagEdit(index) {
  const trimmedValue = editingTagValue.value.trim();
  if (!trimmedValue) {
    showToast("标签内容不能为空");
    return;
  }
  if (trimmedValue.length > 20) {
    showToast("每个标签最多20个字符");
    return;
  }

  // 检查是否与其他现有标签重复 (排除当前正在编辑的标签)
  const isDuplicate = tags.value.some(
    (tag, i) => i !== index && tag === trimmedValue
  );
  if (isDuplicate) {
    showToast("标签已存在");
    return;
  }

  // 更新标签
  tags.value[index] = trimmedValue;

  // 退出编辑模式
  cancelTagEdit();
}

// 取消标签编辑
function cancelTagEdit() {
  editingTagIndex.value = -1;
  editingTagValue.value = ""; // 清空编辑框内容
}

// 处理发布操作
async function handlePublish() {
  // 检查是否有正在输入或编辑的标签
  if (showTagInput.value) {
    showToast('请先完成新标签的添加或取消');
    return;
  }
  if (editingTagIndex.value !== -1) {
    showToast('请先保存或取消当前标签的编辑');
    return;
  }

  if (!userId.value) {
    showToast("用户信息未加载，请稍后再试或重新登录");
    return;
  }
  await uploadVideo(userId.value);
}
</script>

<style scoped>
/* 页面基础样式 */
#publish-page {
  min-height: 100vh;
  background: #ffffff;
  /* 浅色模式下使用白色背景 */
  padding-bottom: 80px;
  /* 为固定底部的按钮留出空间 */
}

/* 深色模式样式 */
.dark-mode#publish-page {
  background: #18181c !important;
  /* 强制深色背景 */
  color: #fff;
}

/* 内容区域样式 */
.publish-content {
  margin: 46px auto 0 auto;
  /* NavBar高度一般为46px，保留顶部外边距 */
  padding: 16px 12px 0 12px;
  /* 调整顶部内边距 */
  width: 100%;
  /* 确保宽度填满父容器 */
  box-sizing: border-box;
  /* 确保padding不增加宽度 */
}

/* 标签页样式 */
.publish-content :deep(.van-tabs) {
  margin-bottom: 24px;
  /* 为标签页添加底部外边距 */
}

/* 描述输入框样式 */
.desc-field {
  margin-bottom: 20px;
  /* 增加底部外边距 */
  border: 1px solid #dcdfe6;
  /* 添加浅色模式下的边框和背景 */
  border-radius: 4px;
  background-color: #fff;
}

/* 深色模式下的描述输入框样式 */
.dark-mode .desc-field {
  border-color: #4a4a52;
  background-color: #232326;
}

.dark-mode .desc-field :deep(.van-field__control) {
  color: #fff !important;
}

/* 标签输入区域样式 */
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

/* 深色模式下的标签输入区域样式 */
.dark-mode .tag-input-box {
  border-color: #4a4a52;
  /* 深色模式边框颜色 */
  background-color: #232326;
  /* 深色模式背景颜色 */
}

/* 标签样式 */
.tag-input-box .van-tag {
  flex-shrink: 0;
  /* 标签不缩小 */
  cursor: pointer;
  /* 添加手型光标提示可点击 */
}

/* 新标签输入框样式 */
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

.new-tag-input-field {
  flex-grow: 1;
  padding: 0;
  margin-right: 8px;
  /* 确定按钮的间距 */
}

.dark-mode .new-tag-input-field :deep(.van-field__control) {
  color: #fff !important;
}

/* 深色模式下的新标签输入框样式 */
.dark-mode .new-tag-input {
  color: #fff;
  /* 深色模式字体颜色 */
}

/* 标签编辑输入框样式 */
.edit-tag-input-field {
  flex-grow: 1;
  padding: 0;
  margin-right: 8px;
  /* 按钮的间距 */
  /* 确保编辑框在flex布局中正确显示 */
  min-width: 0;
  /* 允许在空间不足时缩小 */
}

.dark-mode .edit-tag-input-field :deep(.van-field__control) {
  color: #fff !important;
}

/* 标签计数器样式 */
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

/* 深色模式下的标签计数器样式 */
.dark-mode .tag-count {
  color: #adadad;
  /* 深色模式下的计数器颜色 */
}

/* 底部发布按钮区域样式 */
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
  left: 50%;
  /* 水平居中 */
  transform: translateX(-50%);
  /* 水平居中 */
  width: 100%;
  /* 确保宽度填满 */
}

/* 深色模式下的底部发布按钮区域样式 */
.dark-mode .publish-footer {
  background: #18181c;
  /* 深色模式背景 */
}

/* 发布按钮样式 */
.publish-footer .publish-btn {
  font-size: 17px;
  font-weight: 600;
  border-radius: 8px;
}

/* 去除van-nav-bar底部分割线和阴影 */
:deep(.van-nav-bar) {
  border-bottom: none !important;
  box-shadow: none !important;
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

.upload-progress-bar {
  width: 90%;
  margin: 0 auto 16px auto;
  z-index: 100;
}
</style>
