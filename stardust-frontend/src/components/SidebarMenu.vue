<template>
  <div>
    <!-- 侧边栏 -->
    <van-popup
      v-model:show="show"
      position="left"
      :style="{ width: '80%', height: '100%' }"
      :overlay-class="isDarkMode ? 'dark-theme' : ''"
    >
      <div class="sidebar-content" :class="{ 'dark-theme': isDarkMode }">
        <div class="sidebar-header">
          <h3>设置</h3>
          <van-icon name="cross" @click="closeSidebar" />
        </div>
        
        <van-cell-group inset title="主题设置">
          <van-cell center title="深色模式">
            <template #right-icon>
              <van-switch v-model="isDarkMode" size="24" />
            </template>
          </van-cell>
        </van-cell-group>
        
        <van-cell-group inset title="关于">
          <van-cell title="版本" value="1.0.0" />
          <van-cell title="关于星屑" is-link @click="navigateToAbout" />
        </van-cell-group>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, inject, watch } from 'vue';
import { useRouter } from 'vue-router';

// 组件属性
const props = defineProps({
  modelValue: Boolean,
  darkMode: Boolean
});

// 组件事件
const emit = defineEmits([
  'update:modelValue',
  'update:darkMode'
]);

// 获取路由器实例
const router = useRouter();

// 注入主题状态
const isDarkMode = computed({
  get: () => props.darkMode,
  set: (value) => emit('update:darkMode', value)
});

// 注入全局主题切换方法
const toggleDarkMode = inject('toggleDarkMode', null);

// 监听 isDarkMode 变化，调用全局切换
watch(isDarkMode, (val) => {
  if (typeof toggleDarkMode === 'function') {
    toggleDarkMode(val);
  }
});

// 计算属性：控制侧边栏显示
const show = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
});

// 关闭侧边栏
const closeSidebar = () => {
  emit('update:modelValue', false);
};

// 导航到关于页面（现在是独立路由）
const navigateToAbout = () => {
  router.push('/about');
  closeSidebar();
};
</script>

<style scoped>
.sidebar-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 0;
  background-color: var(--card-background);
  color: var(--text-color);
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
}

.sidebar-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
}
</style> 