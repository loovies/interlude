<template>
  <div class="choiceness-page">
    <!-- 导航栏 -->
    <nav class="category-nav">
      <div class="nav-container">
        <div class="nav-scroll">
          <button 
            v-for="category in categories" 
            :key="category.id"
            class="category-btn"
            :class="{ active: activeCategory === category.id }"
            @click="activeCategory = category.id"
          >
            {{ category.name }}
          </button>
        </div>
      </div>
    </nav>

    <!-- 视频内容区域 -->
    <main class="video-content">
      <div class="video-grid">
        <!-- 左侧大视频 -->
        <div class="video-main">
        <VideoCard 
          :video="featuredVideo" 
          :is-featured="true"
          :data-video-id="featuredVideo.id"
          @click="handleVideoClick(featuredVideo)"
        />
        </div>

      <!-- 右侧小视频网格 -->
      <div class="video-sidebar">
        <div class="sidebar-header">
          <h3 class="sidebar-title">热门推荐</h3>
          <div class="sidebar-stats">
            <span class="stat-item">🔥 实时更新</span>
            <span class="stat-item">👁️ 24小时热门</span>
          </div>
        </div>
        
        <div class="video-grid-small">
          <div 
            v-for="video in smallVideos" 
            :key="video.id"
            class="small-video-item"
            @click="handleVideoClick(video)"
          >
            <div class="small-video-cover">
              <img
                :src="placeholder"
                :data-src="video.cover"
                :alt="video.title"
                class="small-cover-image"
              />
              <div class="small-video-duration">{{ video.duration }}</div>
              <div class="small-video-likes">
                <span class="small-like-icon">❤️</span>
                <span class="small-like-count">{{ formatLikes(video.likes) }}</span>
              </div>
              <div class="small-play-overlay">
                <div class="small-play-button">▶</div>
              </div>
            </div>
            <div class="small-video-info">
              <h4 class="small-video-title" :title="video.title">
                {{ truncateSmallTitle(video.title) }}
              </h4>
              <div class="small-video-meta">
                <span class="small-video-author">{{ video.author }}</span>
                <span class="small-video-views">{{ formatViews(video.views) }}观看</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      </div>

      <!-- 更多视频区域 -->
      <div class="more-videos">
        <div class="section-header">
          <h2 class="section-title">更多推荐</h2>
          <div class="view-toggle">
            <button 
              class="toggle-btn" 
              :class="{ active: viewMode === 'grid' }"
              @click="viewMode = 'grid'"
            >
              <span class="icon-grid">▦</span>
            </button>
            <button 
              class="toggle-btn" 
              :class="{ active: viewMode === 'list' }"
              @click="viewMode = 'list'"
            >
              <span class="icon-list">☰</span>
            </button>
          </div>
        </div>
        <div class="video-list" :class="viewMode">
          <VideoCard 
            v-for="video in moreVideos" 
            :key="video.id"
            :video="video"
            :is-featured="false"
            :data-video-id="video.id"
            :class="viewMode === 'list' ? 'list-mode' : ''"
            @click="handleVideoClick(video)"
          />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import VideoCard from './components/VideoCard.vue'

interface VideoItem {
  id: number
  title: string
  category: string
  cover: string
  likes: number
  duration: string
  author: string
  views: number
  uploadTime: string
}

// 占位符图片（base64 小图）
const placeholder = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjYwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjJmMmYyIi8+PC9zdmc+'

// 分类名称映射
const categoryNames: Record<string, string> = {
  'all': '全部',
  'game': '游戏',
  'anime': '二次元',
  'music': '音乐',
  'dance': '舞蹈',
  'food': '美食',
  'travel': '旅行',
  'sports': '运动',
  'tech': '科技',
  'fashion': '时尚'
}

const formatLikes = (likes: number): string => {
  if (likes >= 10000) {
    return (likes / 10000).toFixed(1) + '万'
  }
  return likes.toString()
}

const formatViews = (views: number): string => {
  if (views >= 10000) {
    return (views / 10000).toFixed(1) + '万'
  }
  return views.toString()
}

const truncateSmallTitle = (title: string): string => {
  const maxLength = 30
  return title.length > maxLength ? title.substring(0, maxLength) + '...' : title
}

const getCategoryName = (categoryId: string): string => {
  return categoryNames[categoryId] || categoryId
}

// 模拟数据
const categories = ref([
  { id: 'all', name: '全部' },
  { id: 'game', name: '游戏' },
  { id: 'anime', name: '二次元' },
  { id: 'music', name: '音乐' },
  { id: 'dance', name: '舞蹈' },
  { id: 'food', name: '美食' },
  { id: 'travel', name: '旅行' },
  { id: 'sports', name: '运动' },
  { id: 'tech', name: '科技' },
  { id: 'fashion', name: '时尚' }
])

const activeCategory = ref('all')

const viewMode = ref<'grid' | 'list'>('grid')

// 模拟视频数据
const generateVideo = (id: number, title: string, category: string) => ({
  id,
  title,
  category,
  cover: `https://picsum.photos/seed/video${id}/400/600`,
  likes: Math.floor(Math.random() * 10000) + 1000,
  duration: `${Math.floor(Math.random() * 4) + 1}:${Math.floor(Math.random() * 60).toString().padStart(2, '0')}`,
  author: `用户${id}`,
  views: Math.floor(Math.random() * 100000) + 10000,
  uploadTime: `${Math.floor(Math.random() * 30) + 1}天前`
})

// 生成模拟数据
const allVideos = ref(
  Array.from({ length: 50 }, (_, i) => {
    const categoryIndex = Math.floor(Math.random() * categories.value.length)
    const category = categories.value[categoryIndex]
    if (category) {
      return generateVideo(
        i + 1,
        `精彩视频标题 ${i + 1} - ${category.name}`,
        category.id
      )
    }
    return null
  }).filter(Boolean) as VideoItem[]
)

// 精选视频（左侧大视频）
const featuredVideo = computed(() => allVideos.value[0]!)

// 右侧小视频（4个）
const smallVideos = computed(() => allVideos.value.slice(1, 5))

// 更多视频（过滤当前分类）
const moreVideos = computed(() => 
  allVideos.value.slice(5).filter(video => 
    activeCategory.value === 'all' || video.category === activeCategory.value
  )
)

// 懒加载相关
const observer = ref<IntersectionObserver | null>(null)
const loadedVideos = ref<Set<number>>(new Set())

const handleVideoClick = (video: VideoItem) => {
  console.log('点击视频:', video)
  // 这里可以跳转到视频播放页面
}

// 初始化懒加载
const initLazyLoad = () => {
  observer.value = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const videoCard = entry.target as HTMLElement
        const videoId = parseInt(videoCard.getAttribute('data-video-id') || '0')
        
        if (!loadedVideos.value.has(videoId)) {
        const img = videoCard.querySelector('img')
        if (img) {
          const dataSrc = img.getAttribute('data-src')
          if (dataSrc) {
            img.src = dataSrc
            img.removeAttribute('data-src')
            loadedVideos.value.add(videoId)
          }
        }
        }
      }
    })
  }, {
    rootMargin: '100px',
    threshold: 0.01
  })

  // 观察所有视频卡片
  nextTick(() => {
    if (observer.value) {
      document.querySelectorAll('.video-card').forEach(card => {
        observer.value!.observe(card)
      })
    }
  })
}

// 加载更多视频
const loadMoreVideos = () => {
  const newVideos = Array.from({ length: 10 }, (_, i) => {
    const id = allVideos.value.length + i + 1
    const categoryIndex = Math.floor(Math.random() * categories.value.length)
    const category = categories.value[categoryIndex]
    if (category) {
      return generateVideo(
        id,
        `更多视频 ${id} - ${category.name}`,
        category.id
      )
    }
    return null
  }).filter(Boolean) as VideoItem[]
  
  allVideos.value.push(...newVideos)
  
  // 重新初始化懒加载
  nextTick(() => {
    if (observer.value) {
      document.querySelectorAll('.video-card').forEach(card => {
        const videoIdAttr = card.getAttribute('data-video-id')
        if (videoIdAttr && !loadedVideos.value.has(parseInt(videoIdAttr))) {
          observer.value!.observe(card)
        }
      })
    }
  })
}

// 滚动加载更多
const handleScroll = () => {
  const scrollTop = window.scrollY || document.documentElement.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight
  
  if (scrollTop + windowHeight >= documentHeight - 200) {
    loadMoreVideos()
  }
}

onMounted(() => {
  initLazyLoad()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  if (observer.value) {
    observer.value.disconnect()
  }
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss" scoped>
.choiceness-page {
  padding: 0 12px;
  max-width: 100%;
  margin: 0 auto;
  background-color: var(--bg-color);
  min-height: 100vh;
}

// 导航栏样式
.category-nav {
  position: sticky;
  top: -10px;
  z-index: 100;
  background-color: var(--bg-color);
  border-bottom: 1px solid var(--border-color);
  padding: 10px 0;
  margin: 0 -12px 8px -12px;
  padding-left: 12px;
  padding-right: 12px;

  .nav-container {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .nav-scroll {
    display: flex;
    gap: 16px;
    white-space: nowrap;
    padding-bottom: 4px;
  }

  .category-btn {
    padding: 8px 20px;
    border: none;
    border-radius: 20px;
    background-color: var(--hover-bg);
    color: var(--text-secondary);
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    flex-shrink: 0;

    &:hover {
      background-color: var(--border-color);
    }

    &.active {
      background-color: var(--primary-color);
      color: white;
    }
  }
}

// 视频内容区域
.video-content {
  padding-top: 12px;
}

.video-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 40px;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

.video-main {
  .video-card {
    height: 480px;

    @media (max-width: 768px) {
      height: 400px;
    }
  }
}

.video-sidebar {
  position: sticky;
  top: 60px;
  align-self: start;
  overflow: visible;

  @media (max-width: 1024px) {
    position: static;
  }
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 2px;
  }

  .sidebar-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    padding: 10px 12px;
    background: linear-gradient(135deg, var(--primary-color) 0%, #ff6b6b 100%);
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
    padding-bottom: 12px;

    .sidebar-title {
      font-size: 15px;
      font-weight: 700;
      color: white;
      margin: 0;
      text-shadow: 0 1px 2px var(--shadow-color);
    }

    .sidebar-stats {
      display: flex;
      gap: 8px;

      .stat-item {
        font-size: 11px;
        color: rgba(255, 255, 255, 0.9);
        background: rgba(255, 255, 255, 0.2);
        padding: 3px 8px;
        border-radius: 10px;
        backdrop-filter: blur(4px);
      }
    }
  }

  .video-grid-small {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  .small-video-item {
    display: flex;
    flex-direction: column;
    background: var(--bg-color);
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    overflow: hidden;
    box-shadow: 0 2px 6px var(--shadow-color);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px var(--shadow-color);
    }

    .small-video-cover {
      position: relative;
      width: 100%;
      height: 150px;
      overflow: hidden;

      .small-cover-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .small-video-duration {
        position: absolute;
        bottom: 6px;
        right: 6px;
        background: rgba(0, 0, 0, 0.75);
        color: white;
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 500;
      }

      .small-video-likes {
        position: absolute;
        bottom: 6px;
        left: 6px;
        display: flex;
        align-items: center;
        gap: 2px;
        background: rgba(0, 0, 0, 0.75);
        color: white;
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 11px;

        .small-like-icon {
          font-size: 10px;
        }
      }

      .small-play-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.3);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.2s ease;

        .small-play-button {
          width: 28px;
          height: 28px;
          background: rgba(255, 255, 255, 0.9);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
          color: #000;
        }
      }
    }

    &:hover .small-play-overlay {
      opacity: 1;
    }

    .small-video-info {
      padding: 8px 10px;

      .small-video-title {
        font-size: 13px;
        font-weight: 600;
        color: var(--text-color);
        margin: 0 0 4px 0;
        line-height: 1.3;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .small-video-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 11px;
        color: var(--text-secondary);

        .small-video-author {
          font-weight: 500;
        }
      }
    }

      .small-video-tags {
        margin-top: 6px;

        .small-video-tag {
          display: inline-block;
          padding: 2px 8px;
          background: var(--hover-bg);
          color: var(--primary-color);
          border-radius: 10px;
          font-size: 10px;
          font-weight: 500;
        }
      }
    }
  }


// 更多视频区域
.more-videos {
  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid var(--border-color);

    .section-title {
      font-size: 20px;
      font-weight: 600;
      color: var(--text-color);
      margin: 0;
    }

    .view-toggle {
      display: flex;
      gap: 4px;
      background: var(--hover-bg);
      padding: 4px;
      border-radius: 8px;

      .toggle-btn {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 32px;
        height: 28px;
        border: none;
        background: transparent;
        color: var(--text-secondary);
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          background: var(--border-color);
        }

        &.active {
          background: var(--primary-color);
          color: white;
        }

        .icon-grid, .icon-list {
          font-size: 16px;
        }
      }
    }
  }

  .video-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;

    @media (max-width: 768px) {
      grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      gap: 12px;
    }

    @media (max-width: 480px) {
      grid-template-columns: 1fr;
    }

    &.list {
      grid-template-columns: 1fr;
      gap: 12px;

      :deep(.video-card) {
        display: flex;
        flex-direction: row;
        height: auto;

        .video-cover {
          width: 200px;
          height: 120px;
          flex-shrink: 0;
        }

        .video-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: center;
        }
      }
    }
  }
}
</style>