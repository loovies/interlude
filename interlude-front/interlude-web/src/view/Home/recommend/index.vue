<template>
  <div class="recommend-page">
    <VideoFeed />
    
    <!-- 侧边栏操作区域 -->
    <div class="sidebar-actions">
      <!-- 用户头像 -->
      <div class="user-avatar">
        <img src="https://picsum.photos/seed/avatar/40/40" alt="用户头像" />
      </div>
      
      <!-- 互动按钮 -->
      <div class="action-buttons">
        <button class="action-btn like-btn" @click="handleLike">
          <svg class="action-icon" viewBox="0 0 24 24">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
          <span class="action-count">{{ currentVideo?.likes || 0 }}</span>
        </button>
        
        <button class="action-btn comment-btn" @click="handleComment">
          <svg class="action-icon" viewBox="0 0 24 24">
            <path d="M21.99 4c0-1.1-.89-2-1.99-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18zM18 14H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
          </svg>
          <span class="action-count">{{ currentVideo?.comments || 0 }}</span>
        </button>
        
        <button class="action-btn share-btn" @click="handleShare">
          <svg class="action-icon" viewBox="0 0 24 24">
            <path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92 1.61 0 2.92-1.31 2.92-2.92s-1.31-2.92-2.92-2.92z"/>
          </svg>
          <span class="action-count">{{ currentVideo?.shares || 0 }}</span>
        </button>
        
        <button class="action-btn collect-btn" @click="handleCollect">
          <svg class="action-icon" viewBox="0 0 24 24">
            <path d="M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2z"/>
          </svg>
        </button>
      </div>
      
      <!-- 音乐信息 -->
      <div class="music-info" v-if="currentVideo">
        <div class="music-cover">
          <img src="https://picsum.photos/seed/music/40/40" alt="音乐封面" />
        </div>
        <div class="music-details">
          <p class="music-title">{{ currentVideo.title }}</p>
          <p class="music-author">{{ currentVideo.author }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import VideoFeed from '../../../components/video/VideoFeed.vue'
import { mockVideoList } from '@/utils/mockData'

// 当前视频信息
const currentVideo = computed(() => {
  return mockVideoList[0] // 这里可以根据实际滚动位置获取当前视频
})

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

.sidebar-actions {
  position: absolute;
  right: 20px;
  bottom: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  z-index: 100;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  &:hover {
    transform: scale(1.1);
    border-color: rgba(255, 255, 255, 0.6);
  }
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.action-btn {
  background: transparent;
  border: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
    transform: translateY(-2px);
  }
}

.action-icon {
  width: 24px;
  height: 24px;
  fill: #fff;
}

.action-count {
  color: #fff;
  font-size: 12px;
  font-weight: 500;
}

.like-btn .action-icon {
  fill: #ff2d55;
}

.comment-btn .action-icon {
  fill: #4cd964;
}

.share-btn .action-icon {
  fill: #007aff;
}

.collect-btn .action-icon {
  fill: #ff9500;
}

.music-info {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 20px;
  padding: 8px 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: rgba(0, 0, 0, 0.8);
    border-color: rgba(255, 255, 255, 0.4);
    transform: translateX(-5px);
  }
}

.music-cover {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.music-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.music-title {
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.music-author {
  color: rgba(255, 255, 255, 0.7);
  font-size: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-actions {
    right: 10px;
    bottom: 80px;
    gap: 15px;
  }
  
  .user-avatar {
    width: 40px;
    height: 40px;
  }
  
  .action-buttons {
    gap: 12px;
  }
  
  .action-btn {
    padding: 6px;
  }
  
  .action-icon {
    width: 20px;
    height: 20px;
  }
  
  .action-count {
    font-size: 11px;
  }
  
  .music-info {
    padding: 6px 10px;
  }
  
  .music-cover {
    width: 32px;
    height: 32px;
  }
  
  .music-title {
    font-size: 11px;
    max-width: 100px;
  }
  
  .music-author {
    font-size: 9px;
  }
}
</style>