<template>
  <div id="register-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar title="注册">
      <template #left>
        <van-icon name="wap-nav" size="25" @click="showSidebar = true" />
      </template>
    </van-nav-bar>
    <SidebarMenu
      v-model="showSidebar"
      :dark-mode="isDarkMode"
      @update:dark-mode="val => isDarkMode = val"
    />
    <div class="register-container">
      <div class="logo">
        <div class="logo-text">星屑</div>
        <p>注册新账号，开启创作之旅</p>
      </div>
      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <van-field
            v-model="username"
            name="username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[
              { required: true, message: '请输入用户名' },
              { pattern: /^[a-zA-Z0-9\u4e00-\u9fa5]{2,20}$/, message: '2-20位，仅限字母、数字、中文' },
              { validator: notAllNumber, message: '用户名不能全为数字' }
            ]"
          />
          <van-field
            v-model="password"
            name="password"
            type="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[
              { required: true, message: '请输入密码' },
              { pattern: /^.{6,20}$/, message: '密码需6-20位' }
            ]"
            
          />
          <van-field
            v-model="confirmPassword"
            name="confirmPassword"
            type="password"
            label="确认密码"
            placeholder="请再次输入密码"
            :rules="[
              { required: true, message: '请确认密码' },
              { pattern: /^.{6,20}$/, message: '密码需6-20位' },
              { validator: validateConfirmPassword, message: '两次输入的密码不一致' }
            ]"
          />
        </van-cell-group>
        <div class="form-actions">
          <van-button round block type="primary" native-type="submit" size="large">
            注册
          </van-button>
        </div>
      </van-form>
      <div class="other-actions">
        <span class="link-text" @click="toLogin">
          已有账号？<span class="highlight-link">立即登录</span>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from 'vant';
import SidebarMenu from '@/components/SidebarMenu.vue';
import { useUserStore } from '@/stores/userStore';

const router = useRouter();
const userStore = useUserStore();
const username = ref('');
const password = ref('');
const confirmPassword = ref('');
const isDarkMode = inject('darkMode', ref(false));
const showSidebar = ref(false);

const notAllNumber = (val) => {
  return !/^[0-9]+$/.test(val);
};

const validateConfirmPassword = (val) => {
  return val === password.value;
};

const onSubmit = async (values) => {
  if (password.value !== confirmPassword.value) {
    showToast('两次输入的密码不一致');
    return;
  }
  try {
    const result = await userStore.register(
      username.value,
      password.value,
      confirmPassword.value
    );
    
    if (result.success) {
      console.log('注册成功');
      
      showToast({
        type: 'success',
        message: '注册成功，请登录',
        onClose: () => {
          router.push('/login');
        }
      });
    } else {
      showToast({
        type: 'fail',
        message: result.message
      });
    }
  } catch (e) {
    console.error('注册错误:', e);
    
    showToast({
      type: 'fail',
      message: '注册过程中发生错误'
    });
  }
};

const toLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
#register-page {
  min-height: 100vh;
  background: var(--background-color, #f7f8fa);
}
.dark-mode {
  background: #18181c !important;
  color: #fff;
}
.register-container {
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 46px);
  background: transparent;
}

.logo {
  text-align: center;
  margin-bottom: 40px;
}

.logo-text {
  font-size: 32px;
  margin: 10px 0;
  color: var(--primary-color, #1989fa);
  font-weight: bold;
}

.logo p {
  font-size: 16px;
  color: #999;
  margin: 0;
}

.form-actions {
  margin: 24px 16px;
}

.other-actions {
  text-align: center;
  margin-top: 20px;
}

.link-text {
  color: var(--primary-color, #1989fa);
  font-size: 16px;
  cursor: pointer;
  user-select: none;
  font-weight: 500;
}

.link-text .highlight-link {
  text-decoration: underline;
  font-weight: bold;
  margin-left: 2px;
}

.link-text:hover,
.link-text:active {
  color: #1765c1;
}

/* 现代风格输入框，仅底部细线 */
.van-cell,
.van-cell-group {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}
.van-field__control {
  background: transparent !important;
  border: none !important;
  border-bottom: 1.5px solid #e5e6eb;
  border-radius: 0;
  padding: 10px 0 8px 0;
  margin-bottom: 0;
  transition: border-color 0.2s;
  font-size: 16px;
}
.van-field__control:focus {
  border-bottom: 1.5px solid var(--primary-color, #1989fa);
  outline: none;
}
.van-field__label {
  color: #333 !important;
  font-weight: 500;
  font-size: 15px;
}
.van-field__control::placeholder {
  color: #bbb !important;
  font-size: 15px;
}
.dark-mode .van-field__control {
  border-bottom: 1.5px solid #333;
  color: #fff;
}
.dark-mode .van-field__control:focus {
  border-bottom: 1.5px solid #1989fa;
}
.dark-mode .van-field__label {
  color: #bbb !important;
}
</style> 