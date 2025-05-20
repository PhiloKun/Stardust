<template>
  <div id="shop-page" :class="{ 'dark-mode': isDarkMode }">
    <van-nav-bar title="商城" fixed>
      <template #left>
        <van-icon name="wap-nav" size="25" @click="showSidebar = true" />
      </template>
    </van-nav-bar>
    <div class="shop-content">
      <div class="shop-header">
        <h2>发现好物</h2>
        <p class="shop-desc">精选优质商品，点亮你的生活</p>
      </div>
      <div class="product-list">
        <div v-for="item in products" :key="item.id" class="product-card">
          <img :src="item.image" :alt="item.title" class="product-image" />
          <div class="product-info">
            <div class="product-title">{{ item.title }}</div>
            <div class="product-desc">{{ item.desc }}</div>
            <div class="product-bottom">
              <span class="product-price">￥{{ item.price }}</span>
              <van-button size="small" type="primary" round>购买</van-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <SidebarMenu
      :model-value="showSidebar"
      @update:model-value="showSidebar = $event"
      :dark-mode="isDarkMode"
      @update:dark-mode="isDarkMode = $event"
    />
  </div>
</template>

<script setup>
import { ref, inject } from 'vue';
import SidebarMenu from "@/components/SidebarMenu.vue";
const showSidebar = ref(false);
const globalDarkMode = inject('darkMode', ref(false));
const isDarkMode = ref(globalDarkMode.value);

const products = ref([
  {
    id: 1,
    title: '星屑定制马克杯',
    desc: '高颜值陶瓷杯，温暖你的每一天',
    price: 39.9,
    image: 'https://img.yzcdn.cn/vant/cat.jpeg',
  },
  {
    id: 2,
    title: '星空夜灯',
    desc: '点亮房间的浪漫星空氛围',
    price: 89.0,
    image: 'https://img.yzcdn.cn/vant/apple-1.jpeg',
  },
  {
    id: 3,
    title: '创意手账本',
    desc: '记录灵感与生活的美好瞬间',
    price: 25.5,
    image: 'https://img.yzcdn.cn/vant/apple-2.jpeg',
  },
  {
    id: 4,
    title: '星屑帆布包',
    desc: '简约百搭，出行好伴侣',
    price: 49.0,
    image: 'https://img.yzcdn.cn/vant/apple-3.jpeg',
  },
]);
</script>

<style scoped>
#shop-page {
  min-height: 100vh;
  background-color: var(--background-color, #f7f8fa);
  color: var(--text-color, #333);
  transition: background-color 0.3s, color 0.3s;
}
.dark-mode {
  background-color: var(--background-color, #121212);
  color: var(--text-color, #fff);
}
.shop-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 70px 12px 90px 12px;
}
@media (min-width: 1200px) {
  .shop-content {
    max-width: 1200px;
    padding-left: 32px;
    padding-right: 32px;
  }
  .shop-header h2 {
    font-size: 32px;
  }
  .product-card {
    margin-bottom: 24px;
  }
}
@media (max-width: 600px) {
  .shop-content {
    padding-left: 6px;
    padding-right: 6px;
  }
}
.shop-header {
  text-align: center;
  margin-bottom: 24px;
}
.shop-header h2 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  letter-spacing: 2px;
}
.shop-desc {
  color: var(--text-color-secondary, #888);
  font-size: 15px;
}
.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}
.product-card {
  background: var(--card-background, #fff);
  border-radius: 16px;
  box-shadow: 0 2px 16px 0 rgba(0,0,0,0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s;
  border: 1px solid var(--border-color, #eee);
}
.dark-mode .product-card {
  background: var(--card-background, #1c1c1e);
  border: 1px solid var(--border-color, #272729);
  box-shadow: 0 2px 16px 0 rgba(0,0,0,0.18);
}
.product-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
  background: #f2f2f2;
}
.product-info {
  padding: 16px 14px 12px 14px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.product-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 6px;
}
.product-desc {
  font-size: 14px;
  color: var(--text-color-secondary, #888);
  margin-bottom: 14px;
}
.product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.product-price {
  color: var(--primary-color, #1989fa);
  font-size: 18px;
  font-weight: bold;
}
</style>
