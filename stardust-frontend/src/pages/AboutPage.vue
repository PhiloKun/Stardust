<template>
  <div class="about-page">
    <h1>关于我们</h1>
    <div class="content">
      <p>这是Stardust项目的关于页面</p>
      <p>Stardust是一个社交分享平台，旨在让用户分享生活中的精彩瞬间。</p>
      <van-cell-group inset title="团队成员">
        <van-cell title="产品经理" value="张三" />
        <van-cell title="前端开发" value="李四" />
        <van-cell title="后端开发" value="王五" />
        <van-cell title="UI设计" value="赵六" />
      </van-cell-group>
      <div class="version-info">
        <p>当前版本: v1.0.0</p>
        <p>©2023 Stardust Team</p>
      </div>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.about-page {
  padding: 16px;
}

h1 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.content {
  margin-top: 10px;
}

.content p {
  margin-bottom: 16px;
  line-height: 1.6;
  color: #666;
}

.version-info {
  margin-top: 30px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.version-info p {
  margin: 5px 0;
}
</style> 