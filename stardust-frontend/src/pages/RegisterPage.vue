<template>
  <div id="register-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar
      title="注册"
    >
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
        <p>加入创作者社区</p>
      </div>

      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <van-field
            v-model="username"
            name="username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请填写用户名' }]"
          />
          <van-field
            v-model="email"
            name="email"
            label="邮箱"
            placeholder="请输入邮箱"
            :rules="[
              { required: true, message: '请填写邮箱' },
              { pattern: /.+@.+\..+/, message: '请输入正确的邮箱格式' }
            ]"
          />
          <van-field
            v-model="password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
          />
          <van-field
            v-model="confirmPassword"
            type="password"
            name="confirmPassword"
            label="确认密码"
            placeholder="请再次输入密码"
            :rules="[
              { required: true, message: '请确认密码' },
              { validator: validateConfirmPassword, message: '两次输入的密码不一致' }
            ]"
          />
        </van-cell-group>

        <div class="terms">
          <van-checkbox v-model="agreeTerms" shape="square" icon-size="16px">
            我已阅读并同意
            <a href="#" class="user-protocol-link" @click.prevent="showTerms">《用户协议》</a>
          </van-checkbox>
        </div>

        <div class="form-actions">
          <van-button 
            round 
            block 
            type="primary" 
            native-type="submit" 
            size="large"
            :disabled="!agreeTerms"
          >
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
import { showToast, showDialog } from 'vant';
import SidebarMenu from '@/components/SidebarMenu.vue';

const router = useRouter();
const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const agreeTerms = ref(false);
const isDarkMode = inject('darkMode', ref(false));
const showSidebar = ref(false);

const onClickLeft = () => {
  router.back();
};

const validateConfirmPassword = () => {
  return password.value === confirmPassword.value;
};

const showTerms = () => {
  showDialog({
    title: '用户协议',
    message: '欢迎使用星屑平台。注册即表示您同意遵守我们的用户协议，包括内容发布规范、隐私保护条款以及相关法律法规。我们致力于为创作者提供良好的社区环境。',
    confirmButtonText: '我已了解'
  });
};

const onSubmit = (values) => {
  // 这里应该调用注册API
  console.log('注册表单提交:', values);
  
  // 模拟注册成功
  showToast({
    type: 'success',
    message: '注册成功',
    onClose: () => {
      router.push('/login');
    }
  });
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
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 46px);
  background: transparent;
}

.logo {
  text-align: center;
  margin-bottom: 30px;
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

.terms {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.user-protocol-link {
  color: var(--primary-color, #1989fa);
  text-decoration: underline;
  margin-left: 2px;
  cursor: pointer;
  font-weight: 500;
}

.user-protocol-link:hover {
  color: #1765c1;
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

/* 深色模式下输入框、表单等适配 */
.dark-mode .van-cell,
.dark-mode .van-cell-group,
.dark-mode .van-field__control {
  background: #232326 !important;
  color: #fff !important;
}

.dark-mode .van-button--primary {
  background: #1989fa;
  border: none;
}

/* 浅色模式下输入框美化 */
.van-cell,
.van-cell-group,
.van-field__control {
  background: #fff !important;
  border-radius: 8px;
  border: 1px solid #e5e6eb;
  margin-bottom: 12px;
  transition: border-color 0.2s;
}
.van-field__control:focus {
  border-color: var(--primary-color, #1989fa);
  outline: none;
}
.van-field__label {
  color: #333 !important;
}
.van-field__control::placeholder {
  color: #bbb !important;
}
</style> 