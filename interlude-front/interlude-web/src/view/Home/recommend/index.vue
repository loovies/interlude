<template>
  <div class="recommend-page">
    <VideoFeed @active-video-change="handleActiveVideoChange" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import VideoFeed from '../../../components/video/VideoFeed.vue'

// 当前视频信息
const currentVideo = ref<any>(null)

// 处理激活视频变化
const handleActiveVideoChange = (index: number, video: any) => {
  currentVideo.value = video
  console.log('当前激活视频:', index, video)
}

// 处理点赞
const handleLike = () => {
  console.log('点赞视频:', currentVideo.value?.videoId)
  // 这里可以调用API进行点赞
}

// 处理评论
const handleComment = () => {
  console.log('评论视频:', currentVideo.value?.videoId)
  // 这里可以打开评论面板
}

// 处理分享
const handleShare = () => {
  console.log('分享视频:', currentVideo.value?.videoId)
  // 这里可以打开分享面板
}

// 处理收藏
const handleCollect = () => {
  console.log('收藏视频:', currentVideo.value?.videoId)
  // 这里可以调用API进行收藏
}
</script>

<style lang="scss" scoped>
.recommend-page {
  position: relative;
  height: calc(100vh - 80px);
  overflow: hidden;
}
</style>