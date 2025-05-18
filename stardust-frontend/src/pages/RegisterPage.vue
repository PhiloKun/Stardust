<template>
  <div id="register-page">
    <van-nav-bar
      title="注册"
      left-arrow
      @click-left="onClickLeft"
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

        <van-cell-group inset class="terms">
          <van-checkbox v-model="agreeTerms" shape="square" icon-size="16px">
            我已阅读并同意<a href="#" @click.prevent="showTerms">《用户协议》</a>
          </van-checkbox>
        </van-cell-group>

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
        <van-button type="text" @click="toLogin">已有账号？立即登录</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, showDialog } from 'vant';

const router = useRouter();
const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const agreeTerms = ref(false);

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
.register-container {
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 46px);
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
}

.form-actions {
  margin: 24px 16px;
}

.other-actions {
  text-align: center;
  margin-top: 20px;
}
</style> 