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
    <SidebarMenu :model-value="showSidebar" @update:model-value="showSidebar = $event" :dark-mode="isDarkMode"
      @update:dark-mode="isDarkMode = $event" />
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
    image: 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=400&q=80',
  },
  {
    id: 2,
    title: '星空夜灯',
    desc: '点亮房间的浪漫星空氛围',
    price: 89.0,
    image: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80',
  },
  {
    id: 3,
    title: '创意手账本',
    desc: '记录灵感与生活的美好瞬间',
    price: 25.5,
    image: 'https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=crop&w=400&q=80',
  },
  {
    id: 4,
    title: '星屑帆布包',
    desc: '简约百搭，出行好伴侣',
    price: 49.0,
    image: 'https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=400&q=80',
  },
  {
    id: 5,
    title: '星空抱枕',
    desc: '柔软舒适，点缀你的卧室',
    price: 59.0,
    image: 'https://images.unsplash.com/photo-1519710164239-da123dc03ef4?auto=format&fit=crop&w=400&q=80',
  },
  {
    id: 6,
    title: '星屑钥匙扣',
    desc: '小巧精致，随身携带星光',
    price: 15.0,
    image: 'https://images.unsplash.com/photo-1503602642458-232111445657?auto=format&fit=crop&w=400&q=80',
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
  box-shadow: 0 2px 16px 0 rgba(0, 0, 0, 0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s, transform 0.2s;
  border: 1px solid var(--border-color, #eee);
}

.product-card:hover {
  box-shadow: 0 4px 24px 0 rgba(25, 137, 250, 0.18);
  transform: translateY(-4px) scale(1.02);
}

.product-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
  background: #f2f2f2;
}

.dark-mode .product-image {
  background: #232323;
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
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 1px;
}
</style>
