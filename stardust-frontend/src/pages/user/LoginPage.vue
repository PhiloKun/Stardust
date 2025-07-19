<template>
  <div id="login-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar title="登录">
      <template #left>
        <van-icon name="wap-nav" size="25" @click="showSidebar = true" />
      </template>
    </van-nav-bar>
    <SidebarMenu
      v-model="showSidebar"
      :dark-mode="isDarkMode"
      @update:dark-mode="val => isDarkMode = val"
    />
    <div class="login-container">
      <div class="logo">
        <div class="logo-text">星屑</div>
        <p>请输入用户名和密码登录</p>
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
            :type="showPwd ? 'text' : 'password'"
            label="密码"
            placeholder="请输入密码"
            :rules="[
              { required: true, message: '请输入密码' },
              { pattern: /^.{6,20}$/, message: '密码需6-20位' }
            ]"
          >
            <template #right-icon>
              <van-icon :name="showPwd ? 'eye-o' : 'closed-eye'" @click="showPwd = !showPwd" />
            </template>
          </van-field>
        </van-cell-group>
        <div class="form-actions">
          <van-button round block type="primary" native-type="submit" size="large">
            登录
          </van-button>
        </div>
      </van-form>
      <div class="other-actions">
        <span class="link-text" @click="toRegister">
          没有账号？<span class="highlight-link">立即注册</span>
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
const isDarkMode = inject('darkMode', ref(false));
const showSidebar = ref(false);
const showPwd = ref(false);

const notAllNumber = (val) => {
  return !/^[0-9]+$/.test(val);
};

const onSubmit = async (values) => {
  try {
    const result = await userStore.login(values.username, values.password);
    
    if (result.success) {
      showToast({
        message: '登录成功,欢迎回来！',
        type: 'success',
        duration: 2000,
        className: 'custom-toast custom-toast-success',
        onClose: () => {
          router.push('/user');
        }
      });
    } else {
      showToast({
        message: '登录失败,请检查用户名和密码',
        type: 'fail',
        duration: 2000,
        className: 'custom-toast custom-toast-danger'
      });
    }
  } catch (e) {
    console.error('登录异常:', e);
    showToast({
      message: e.message || '登录过程中发生错误',
      type: 'fail',
      duration: 2000,
      className: 'custom-toast custom-toast-danger'
    });
  }
};

const toRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
#login-page {
  min-height: 100vh;
  background: var(--background-color, #f7f8fa);
}
.dark-mode {
  background: #18181c !important;
  color: #fff;
}
.login-container {
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

.link-text.tip-text {
  color: #888;
  font-size: 14px;
  margin-top: 10px;
  display: block;
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
.custom-success-toast {
  background: linear-gradient(90deg, #1989fa 60%, #4fc3f7 100%) !important;
  color: #fff !important;
  border-radius: 12px !important;
  font-size: 17px !important;
  font-weight: bold;
  box-shadow: 0 4px 24px rgba(25,137,250,0.18);
  padding: 18px 28px !important;
}

:deep(.van-notify) {
  font-weight: bold;
  font-size: 16px;
  letter-spacing: 1px;
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
