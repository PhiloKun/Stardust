<template>
  <div class="about-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar
      title="关于星屑"
      left-arrow
      fixed
      @click-left="onClickLeft"
    />
    
    <div class="content">
      <div class="app-info">
        <div class="logo">
          <div class="logo-text">星屑</div>
          <p class="slogan">发现你的创作星空</p>
        </div>
        
        <p class="app-desc">星屑(Stardust)是一个短视频分享平台，将用户的每个短视频比作宇宙中的星屑，渺小却共同构成璀璨星河，传递"个体价值汇聚成光"的哲学感。</p>
        
        <div class="feature-list">
          <div class="feature-item">
            <van-icon name="star" color="var(--primary-color)" size="24" />
            <span>收集生活中的微光时刻</span>
          </div>
          <div class="feature-item">
            <van-icon name="cluster" color="var(--primary-color)" size="24" />
            <span>创建专属于你的星系</span>
          </div>
          <div class="feature-item">
            <van-icon name="friends" color="var(--primary-color)" size="24" />
            <span>与其他创作者共建星河</span>
          </div>
        </div>
      </div>
      
      <van-cell-group inset title="关于作者">
        <van-cell title="创始人 & 开发者" value="PhiloKun" />
        <van-cell title="个人网站" value="www.zhangkunzhe.cn" is-link url="http://www.zhangkunzhe.cn" />
        <van-cell title="作者简介" label="热爱创新与探索的全栈开发者，致力于将艺术与科技完美结合，创造出有温度的数字产品。" />
      </van-cell-group>
      
      <van-cell-group inset title="项目介绍">
        <van-cell title="开发初衷" label="打造一个让每个普通人都能发光的短视频平台，传递每个个体都有价值的理念。" />
        <van-cell title="技术栈" label="前端：Vue.js + Vant UI，后端：Spring Boot" />
        <van-cell title="开源协议" value="MIT" />
      </van-cell-group>
      
      <div class="version-info">
        <p>当前版本: v1.0.0</p>
        <p>©2025 PhiloKun</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ref, inject } from 'vue';

const router = useRouter();

// 注入主题状态
const isDarkMode = inject('darkMode', ref(false));

// 返回上一页
const onClickLeft = () => {
  router.back();
};
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  background-color: var(--background-color, #f7f8fa);
  color: var(--text-color, #333);
}

.content {
  padding: 70px 16px 16px 16px; /* 顶部padding适配导航栏高度 */
}

.dark-mode {
  background-color: var(--background-color, #121212);
  color: var(--text-color, #fff);
}

.app-info {
  background-color: var(--card-background, #fff);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px var(--shadow-color, rgba(100, 101, 102, 0.08));
}

.logo {
  text-align: center;
  margin-bottom: 20px;
}

.logo-text {
  font-size: 32px;
  margin: 10px 0;
  color: var(--primary-color, #1989fa);
  font-weight: bold;
}

.slogan {
  font-size: 16px;
  color: var(--text-color-secondary, #999);
  margin: 0;
}

.app-desc {
  margin-bottom: 20px;
  line-height: 1.6;
  color: var(--text-color-secondary, #666);
  text-align: justify;
}

.feature-list {
  margin-top: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.feature-item .van-icon {
  margin-right: 10px;
}

.version-info {
  margin-top: 30px;
  text-align: center;
  color: var(--text-color-secondary, #999);
  font-size: 14px;
}

.version-info p {
  margin: 5px 0;
}
</style> 