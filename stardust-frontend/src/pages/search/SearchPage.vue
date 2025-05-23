<template>
  <div id="search-page">
    <van-nav-bar 
      title="搜索" 
      left-arrow
      @click-left="onClickLeft"
    />
    
    <div class="search-container">
      <div class="search-input-group">
        <van-search
          v-model="searchValue"
          placeholder="请输入搜索关键词"
          shape="round"
          @search="onSearch"
          class="search-input"
        />
        <van-button 
          type="primary" 
          size="small" 
          class="search-button"
          @click="onSearch"
        >
          搜索
        </van-button>
      </div>
    </div>
    
    <div class="search-results" v-if="searchResults.length">
      <van-cell v-for="(item, index) in searchResults" :key="index" :title="item.title" />
    </div>
    
    <div class="empty-result" v-else-if="hasSearched">
      <van-empty description="没有找到相关内容" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const searchValue = ref('');
const searchResults = ref([]);
const hasSearched = ref(false);
// 返回上一页
const onClickLeft = () => {
  router.back();
};

const onSearch = () => {
  hasSearched.value = true;
  // 模拟搜索结果，实际项目中应该调用API
  if (searchValue.value) {
    searchResults.value = [
      { title: `搜索结果1: ${searchValue.value}` },
      { title: `搜索结果2: ${searchValue.value}` },
      { title: `搜索结果3: ${searchValue.value}` }
    ];
  } else {
    searchResults.value = [];
  }
};
</script>

<style scoped>
.search-container {
  padding: 10px;
}

.search-input-group {
  display: flex;
  align-items: center;
}

.search-input {
  flex: 1;
}

.search-button {
  margin-left: 10px;
  border-radius: 4px;
}

.search-results {
  margin-top: 10px;
}

.empty-result {
  margin-top: 80px;
  display: flex;
  justify-content: center;
}
</style>