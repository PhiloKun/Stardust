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
            :show-error-message="false"
          />
          <van-field
            v-model="password"
            name="password"
            :type="showPwd ? 'text' : 'password'"
            label="密码"
            placeholder="请输入密码"
            :rules="[
              { required: true, message: '请输入密码' },
              { pattern: /^.{6,20}$/, message: '密码需6-20位' }
            ]"
            :show-error-message="false"
          >
            <template #right-icon>
              <van-icon :name="showPwd ? 'eye-o' : 'closed-eye'" @click="showPwd = !showPwd" />
            </template>
          </van-field>
          <van-field
            v-model="confirmPassword"
            name="confirmPassword"
            :type="showConfirmPwd ? 'text' : 'password'"
            label="确认密码"
            placeholder="请再次输入密码"
            :rules="[
              { required: true, message: '请确认密码' },
              { pattern: /^.{6,20}$/, message: '密码需6-20位' },
              { validator: validateConfirmPassword, message: '两次输入的密码不一致' }
            ]"
            :show-error-message="false"
          >
            <template #right-icon>
              <van-icon :name="showConfirmPwd ? 'eye-o' : 'closed-eye'" @click="showConfirmPwd = !showConfirmPwd" />
            </template>
          </van-field>
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
const showPwd = ref(false);
const showConfirmPwd = ref(false);

const notAllNumber = (val) => {
  return !/^[0-9]+$/.test(val);
};

const validateConfirmPassword = (val) => {
  return val === password.value;
};

const onSubmit = async () => {
  // Manual validation
  if (!username.value) {
    showToast({ message: '请输入用户名', type: 'fail', className: 'custom-toast custom-toast-danger' });
    return;
  }
  if (!/^[a-zA-Z0-9\u4e00-\u9fa5]{2,20}$/.test(username.value)) {
     showToast({ message: '用户名需2-20位，仅限字母、数字、中文', type: 'fail', className: 'custom-toast custom-toast-danger' });
     return;
  }
   if (/^[0-9]+$/.test(username.value)) {
     showToast({ message: '用户名不能全为数字', type: 'fail', className: 'custom-toast custom-toast-danger' });
     return;
  }

  if (!password.value) {
    showToast({ message: '请输入密码', type: 'fail', className: 'custom-toast custom-toast-danger' });
    return;
  }
  if (!/^.{6,20}$/.test(password.value)) {
    showToast({ message: '密码需6-20位', type: 'fail', className: 'custom-toast custom-toast-danger' });
    return;
  }

  if (!confirmPassword.value) {
    showToast({ message: '请确认密码', type: 'fail', className: 'custom-toast custom-toast-danger' });
    return;
  }
  if (password.value !== confirmPassword.value) {
    showToast({
      message: '两次输入的密码不一致',
      type: 'fail',
      duration: 2000,
      className: 'custom-toast custom-toast-warning'
    });
    return;
  }

  // If all frontend validations pass, proceed with registration
  try {
    const result = await userStore.register(
      username.value,
      password.value,
      confirmPassword.value
    );

    if (result.success) {
      showToast({
        message: result.message || '注册成功，请登录',
        type: 'success',
        duration: 2000,
        className: 'custom-toast custom-toast-success',
        onClose: () => {
          router.push('/user');
        }
      });
    } else {
      // Handle backend validation errors, like username already exists
      showToast({
        message: result.message || '注册失败，请稍后再试',
        type: 'fail',
        duration: 2000,
        className: 'custom-toast custom-toast-danger'
      });
    }
  } catch (e) {
    console.error('注册错误:', e);
    showToast({
      message: e.message || '注册过程中发生错误',
      type: 'fail',
      duration: 2000,
      className: 'custom-toast custom-toast-danger'
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

/* 强制隐藏包含"用户名已存在"文本的元素 */
/* 这是一种强行隐藏不需要提示的方法，可能需要根据实际DOM结构微调 */
[data-v-f3f3eb70]:contains('用户名已存在') {
    display: none !important;
}

:deep(.van-toast) {
  font-weight: bold;
  font-size: 15px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 10px 18px;
  text-align: center;
  max-width: 80%;
  margin: 0 auto;
  color: var(--toast-text-color, #333);
}

:deep(.custom-toast-success) {
  background: linear-gradient(90deg, #1989fa 60%, #4fc3f7 100%) !important;
  color: #fff !important;
}

:deep(.custom-toast-danger) {
  background: linear-gradient(90deg, #ff4d4f 60%, #ffb6b9 100%) !important;
  color: #fff !important;
}

:deep(.custom-toast-warning) {
    background: linear-gradient(90deg, #ffb300 60%, #ffe066 100%) !important;
    color: #333 !important;
}
</style> 