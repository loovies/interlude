<template>
  <div class="test-container">
    <h2>抖音风格视频播放器测试</h2>
    <div class="player-wrapper">
      <DouyinVideoPlayer ref="playerRef" :video-data="testVideoData" :autoplay="true" />
    </div>
    
    <div class="test-controls">
      <h3>测试控制</h3>
      <div class="control-buttons">
        <button @click="togglePlay">播放/暂停</button>
        <button @click="toggleDanmu">切换弹幕</button>
        <button @click="toggleFullscreen">切换全屏</button>
        <button @click="changeQuality('1080p')">切换到1080P</button>
        <button @click="changeQuality('720p')">切换到720P</button>
      </div>
      
      <div class="video-info-display">
        <h4>当前视频信息：</h4>
        <p>标题：{{ testVideoData.title }}</p>
        <p>作者：{{ testVideoData.author }}</p>
        <p>发布时间：{{ testVideoData.createTime }}</p>
        <p>描述：{{ testVideoData.description }}</p>
        <p>时长：{{ testVideoData.duration }}秒</p>
        <p>清晰度：{{ testVideoData.qualities.map(q => q.quality).join(', ') }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import DouyinVideoPlayer from './DouyinVideoPlayer.vue'

const testVideoData = ref({
  videoId: 1,
  title: '测试视频 - 超美风景航拍',
  description: '这是一个测试视频，展示抖音风格视频播放器的所有功能。包含弹幕、清晰度切换、播放速率控制等。',
  duration: 186,
  author: '测试用户',
  createTime: '2024-01-15T10:30:00',
  likes: 12500,
  comments: 856,
  shares: 324,
  qualities: [
    {
      quality: '4k',
      url: 'https://media.w3.org/2010/05/sintel/trailer.mp4',
      resolution: '3840×2160',
      bitrate: 15000,
      recommended: true,
    },
    {
      quality: '1080p',
      url: 'https://media.w3.org/2010/05/sintel/trailer.mp4',
      resolution: '1920×1080',
      bitrate: 8000,
    },
    {
      quality: '720p',
      url: 'https://media.w3.org/2010/05/sintel/trailer.mp4',
      resolution: '1280×720',
      bitrate: 4000,
    },
  ],
})

const playerRef = ref<InstanceType<typeof DouyinVideoPlayer>>()

const togglePlay = () => {
  if (playerRef.value) {
    // 这里需要根据播放器状态决定调用play还是pause
    // 实际使用中可以通过ref调用暴露的方法
    console.log('切换播放/暂停')
  }
}

const toggleDanmu = () => {
  if (playerRef.value) {
    playerRef.value.toggleDanmu?.()
  }
}

const toggleFullscreen = () => {
  if (playerRef.value) {
    playerRef.value.toggleFullscreen?.()
  }
}

const changeQuality = (quality: string) => {
  testVideoData.value = {
    ...testVideoData.value,
    qualities: testVideoData.value.qualities.map(q => ({
      ...q,
      recommended: q.quality === quality
    }))
  }
}
</script>

<style scoped>
.test-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.player-wrapper {
  width: 100%;
  height: 600px;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.test-controls {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 20px;
}

h3, h4 {
  color: #333;
  margin-bottom: 15px;
}

.control-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.control-buttons button {
  padding: 10px 20px;
  background: #ff2d55;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}

.control-buttons button:hover {
  background: #ff1a45;
}

.video-info-display {
  background: white;
  border-radius: 6px;
  padding: 15px;
  border: 1px solid #e0e0e0;
}

.video-info-display p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}

.video-info-display p:first-of-type {
  margin-top: 0;
}

.video-info-display p:last-of-type {
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .player-wrapper {
    height: 400px;
  }
  
  .control-buttons {
    flex-direction: column;
  }
  
  .control-buttons button {
    width: 100%;
  }
}
</style>
