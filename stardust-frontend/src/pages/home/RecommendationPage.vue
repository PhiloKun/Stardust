<template>
    <div class="recommendation-page">
        <VideoPlayer />
    </div>
</template>

<script setup>
import VideoPlayer from '@/components/VideoPlayer.vue';
</script>

<style scoped>
.recommendation-page {
    position: relative;
    width: 100%;
    height: 100vh;
    overflow: hidden;
    padding: 0;
    margin: 0;
}
</style>