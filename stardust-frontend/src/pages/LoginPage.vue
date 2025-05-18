<template>
  <div id="login-page">
    <van-nav-bar
      title="登录"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <div class="login-container">
      <div class="logo">
        <div class="logo-text">星屑</div>
        <p>发现你的创作星空</p>
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
            v-model="password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
          />
        </van-cell-group>

        <div class="form-actions">
          <van-button round block type="primary" native-type="submit" size="large">
            登录
          </van-button>
        </div>
      </van-form>

      <div class="other-actions">
        <van-button type="text" @click="toRegister">没有账号？立即注册</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from 'vant';

const router = useRouter();
const username = ref('');
const password = ref('');

const onClickLeft = () => {
  router.back();
};

const onSubmit = (values) => {
  // 这里应该调用登录API
  console.log('登录表单提交:', values);
  
  // 模拟登录成功
  localStorage.setItem('userInfo', JSON.stringify({
    id: '001',
    username: values.username,
    avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    followers: 128,
    following: 56,
    posts: 32
  }));
  
  showToast({
    type: 'success',
    message: '登录成功',
    onClose: () => {
      router.push('/user');
    }
  });
};

const toRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
.login-container {
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 46px);
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
</style> 