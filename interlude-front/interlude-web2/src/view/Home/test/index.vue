<template>
  <div class="test-page">
    <div class="player-container">
      <DouyinVideoPlayer :video-data="testVideoData" />
    </div>
    
    <div class="test-info">
      <h3>测试说明：</h3>
      <ul>
        <li>✅ 开关弹幕已改为element-plus的switch组件</li>
        <li>✅ 发送弹幕输入框在弹幕设置右边，宽度已加宽</li>
        <li>✅ 清晰度弹出框方向改为向上</li>
        <li>✅ 声音弹窗框改为竖的</li>
        <li>✅ 视频描述位置向下调整，用户名字体增大</li>
        <li>✅ 视频加载问题已优化</li>
        <li>✅ 右下角被盖住的发弹幕按钮已删除</li>
      </ul>
      
      <div class="video-data">
        <h4>当前视频数据：</h4>
        <pre>{{ JSON.stringify(testVideoData, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import DouyinVideoPlayer from '@/components/video/DouyinVideoPlayer.vue'

const testVideoData = ref({
  videoId: 1,
  title: '测试视频 - 抖音风格播放器',
  description: '这是一个测试视频，展示优化后的抖音风格视频播放器。包含所有修改的功能：element-plus开关、弹幕设置、清晰度选择、音量控制等。',
  duration: 186,
  author: '测试用户张三',
  createTime: '2024-01-15T10:30:00',
  likes: 12500,
  comments: 856,
  shares: 324,
  qualities: [
    {
      quality: '1080p',
      url: 'https://vjs.zencdn.net/v/oceans.mp4',
      resolution: '1920×1080',
      bitrate: 8000,
      recommended: true,
    },
    {
      quality: '720p',
      url: 'https://vjs.zencdn.net/v/oceans.mp4',
      resolution: '1280×720',
      bitrate: 4000,
    },
    {
      quality: '480p',
      url: 'https://vjs.zencdn.net/v/oceans.mp4',
      resolution: '854×480',
      bitrate: 2000,
    },
  ],
})
</script>

<style scoped>
.test-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  background: #f5f5f5;
  min-height: 100vh;
}

.player-container {
  width: 100%;
  height: 600px;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.test-info {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h3 {
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #ff2d55;
}

ul {
  list-style: none;
  padding: 0;
  margin-bottom: 20px;
}

li {
  padding: 8px 0;
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
}

li:before {
  content: "✓";
  color: #4cd964;
  font-weight: bold;
  margin-right: 10px;
  font-size: 16px;
}

.video-data {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 15px;
  margin-top: 20px;
}

h4 {
  color: #333;
  margin-bottom: 10px;
}

pre {
  background: #2d2d2d;
  color: #f8f8f2;
  padding: 15px;
  border-radius: 4px;
  overflow: auto;
  font-size: 12px;
  line-height: 1.4;
  max-height: 300px;
}

@media (max-width: 768px) {
  .player-container {
    height: 400px;
  }
  
  .test-info {
    padding: 15px;
  }
  
  li {
    font-size: 13px;
  }
  
  pre {
    font-size: 11px;
    padding: 10px;
  }
}
</style>