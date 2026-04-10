<template>
  <div class="simple-test">
    <h2>简单视频播放测试</h2>
    
    <div class="test-controls">
      <button @click="testVideo1">测试视频1 (oceans.mp4)</button>
      <button @click="testVideo2">测试视频2 (bunny.mp4)</button>
      <button @click="testVideo3">测试视频3 (本地测试)</button>
    </div>
    
    <div class="player-container">
      <DouyinVideoPlayer :video-data="currentVideoData" :autoplay="true" />
    </div>
    
    <div class="debug-info">
      <h3>调试信息：</h3>
      <p>当前视频URL: {{ getCurrentVideoUrl() }}</p>
      <p>视频数据: {{ JSON.stringify(currentVideoData, null, 2) }}</p>
      <p>控制台查看详细日志</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import DouyinVideoPlayer from '@/components/video/DouyinVideoPlayer.vue'

const currentVideoData = ref({
  videoId: 1,
  title: '测试视频',
  description: '这是一个测试视频',
  duration: 60,
  author: '测试用户',
  createTime: '2024-01-01',
  qualities: [
    {
      quality: '720p',
      url: 'https://vjs.zencdn.net/v/oceans.mp4',
      resolution: '1280×720',
    }
  ]
})

const testVideo1 = () => {
  currentVideoData.value = {
    videoId: 1,
    title: '海洋视频测试',
    description: '这是一个可靠的测试视频，应该可以正常播放',
    duration: 45,
    author: 'VideoJS',
    createTime: '2024-01-01',
    qualities: [
      {
        quality: '720p',
        url: 'https://vjs.zencdn.net/v/oceans.mp4',
        resolution: '1280×720',
      }
    ]
  }
}

const testVideo2 = () => {
  currentVideoData.value = {
    videoId: 2,
    title: '兔子视频测试',
    description: '另一个测试视频',
    duration: 60,
    author: 'Blender Foundation',
    createTime: '2024-01-02',
    qualities: [
      {
        quality: '720p',
        url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
        resolution: '1280×720',
      }
    ]
  }
}

const testVideo3 = () => {
  // 使用一个更简单的URL
  currentVideoData.value = {
    videoId: 3,
    title: '简单测试视频',
    description: '使用最简单的视频URL进行测试',
    duration: 30,
    author: '测试',
    createTime: '2024-01-03',
    qualities: [
      {
        quality: '480p',
        url: 'https://www.w3schools.com/html/mov_bbb.mp4',
        resolution: '854×480',
      }
    ]
  }
}

const getCurrentVideoUrl = () => {
  if (currentVideoData.value.qualities.length > 0) {
    const quality = currentVideoData.value.qualities[0]
    return quality?.url || '无视频URL'
  }
  return '无视频URL'
}
</script>

<style scoped>
.simple-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.test-controls {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-bottom: 20px;
}

.test-controls button {
  padding: 10px 20px;
  background: #ff2d55;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.test-controls button:hover {
  background: #ff1a45;
}

.player-container {
  width: 100%;
  height: 400px;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.debug-info {
  background: #f5f5f5;
  border-radius: 6px;
  padding: 15px;
  font-family: monospace;
  font-size: 12px;
}

.debug-info h3 {
  margin-top: 0;
  color: #333;
}

.debug-info p {
  margin: 5px 0;
  color: #666;
}
</style>