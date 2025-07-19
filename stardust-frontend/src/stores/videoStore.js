import { defineStore } from 'pinia';
import { ref } from 'vue';
// import axios from 'axios'; // 移除全局 axios 导入
import request from '@/utils/request'; // 导入配置好的 service 实例
import { showToast } from 'vant';

export const useVideoStore = defineStore('video', () => {
    // 状态
    const activeTab = ref('video');
    const videoFile = ref(null);
    const videoUrl = ref(''); // 用于显示视频预览或上传后的 URL
    const description = ref('');
    const tags = ref([]);
    const newTag = ref('');
    const isUploading = ref(false); // 上传状态
    const uploadProgress = ref(0); // 上传进度百分比

    // Actions
    const addTag = () => {
        const tag = newTag.value.trim();
        if (tag) {
            if (tags.value.length >= 6) {
                showToast('最多只能添加6个标签');
                newTag.value = '';
                return;
            }
            if (tag.length > 20) {
                showToast('每个标签最多20个字符');
                newTag.value = '';
                return;
            }
            if (!tags.value.includes(tag)) {
                tags.value.push(tag);
                newTag.value = '';
            } else {
                showToast('标签已存在');
                newTag.value = '';
            }
        }
    };

    const removeTag = (index) => {
        tags.value.splice(index, 1);
    };

    // 视频上传Action
    const uploadVideo = async (userId) => {
        if (!description.value.trim()) {
            showToast('描述不能为空');
            return { success: false, message: '描述不能为空' };
        }

        if (!videoFile.value) {
            showToast('请先上传视频');
            return { success: false, message: '请先上传视频' };
        }

        if (!userId) {
            showToast('用户信息未加载，请稍后再试或重新登录');
            return { success: false, message: '用户信息未加载' };
        }

        isUploading.value = true; // 设置上传状态为 true
        uploadProgress.value = 0;

        const formData = new FormData();
        formData.append('videoFile', videoFile.value);
        formData.append('description', description.value);
        tags.value.forEach(tag => formData.append('tags', tag));
        formData.append('userId', userId); // Append userId

        try {
            const response = await request.post('/video/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
                onUploadProgress: (progressEvent) => {
                    if (progressEvent.total) {
                        uploadProgress.value = Math.round((progressEvent.loaded / progressEvent.total) * 100);
                    }
                }
            });

            if (response.status === 200) {
                showToast('视频发布成功！');
                resetForm();
                uploadProgress.value = 0;
                return { success: true, data: response.data };
            }

            // 统一错误处理
            console.error('视频上传失败:', error.response?.data?.message || error.message);
            showToast('视频发布失败，请稍后重试');
            return { success: false };
        } finally {
            isUploading.value = false;
            uploadProgress.value = 0;
        }
    };

    // 重置表单状态 Action
    const resetForm = () => {
        videoFile.value = null;
        videoUrl.value = '';
        description.value = '';
        tags.value = [];
        newTag.value = '';
    };


    return {
        activeTab,
        videoFile,
        videoUrl,
        description,
        tags,
        newTag,
        isUploading,
        uploadProgress,
        addTag,
        removeTag,
        uploadVideo,
        resetForm
    };
}); 