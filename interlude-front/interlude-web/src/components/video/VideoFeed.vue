<template>
  <div class="video-feed" ref="feedRef" @scroll="onScroll">
    <DouyinVideoPlayer
      v-for="(video, index) in videoList"
      :key="video.videoId"
      :video-data="video"
      :autoplay="activeIndexInternal === index"
      ref="videoPlayers"
      @play="handleVideoPlay(index)"
      @pause="handleVideoPause(index)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import DouyinVideoPlayer from './DouyinVideoPlayer.vue'
import { fetchVideoList } from '@/utils/mockData'

const feedRef = ref<HTMLElement | null>(null)
const videoPlayers = ref<InstanceType<typeof DouyinVideoPlayer>[]>([])

// 视频列表数据
const videoList = ref<any[]>([])
const loading = ref(false)

// activeIndex 用于滚动计算，activeIndexInternal 用于实际控制播放
const activeIndex = ref(0)
const activeIndexInternal = ref(0)
const isFullScreen = ref(false)
const lastActiveIndexBeforeFullscreen = ref(0) // 记录进入全屏前的索引

// 标志位，用于区分是用户滚动还是程序化滚动
let isProgrammaticScroll = false

// 更新内部激活索引（只有非全屏时才同步）
const updateActiveIndexInternal = (index: number) => {
  if (!isFullScreen.value) {
    activeIndexInternal.value = index
  }
}

// 根据滚动位置计算当前索引
const updateActiveIndexFromScroll = () => {
  if (!feedRef.value) return
  const scrollTop = feedRef.value.scrollTop
  const itemHeight = window.innerHeight
  const newIndex = Math.round(scrollTop / itemHeight)
  if (newIndex !== activeIndex.value && newIndex >= 0 && newIndex < videoList.value.length) {
    activeIndex.value = newIndex
    updateActiveIndexInternal(newIndex)
  }
}

// 加载视频列表
const loadVideoList = async () => {
  if (loading.value) return
  
  loading.value = true
  try {
    const result = await fetchVideoList(1, 10)
    videoList.value = result.data
  } catch (error) {
    console.error('加载视频列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 滚动事件处理
const onScroll = () => {
  if (isFullScreen.value || isProgrammaticScroll) return // 全屏或程序化滚动时忽略
  updateActiveIndexFromScroll()
}

// 监听内部索引变化，暂停上一个视频
watch(activeIndexInternal, (newVal, oldVal) => {
  if (oldVal !== undefined && videoPlayers.value[oldVal]) {
    videoPlayers.value[oldVal].pause()
  }
  // 自动播放新视频已在 DouyinVideoPlayer 中通过 autoplay prop 处理
})

// 处理视频播放
const handleVideoPlay = (index: number) => {
  // 确保只有当前视频在播放
  videoPlayers.value.forEach((player, i) => {
    if (i !== index && player) {
      player.pause()
    }
  })
}

// 处理视频暂停
const handleVideoPause = (index: number) => {
  // 可以在这里处理暂停逻辑
}

// 键盘事件
const handleKeyDown = (e: KeyboardEvent) => {
  if (isFullScreen.value) return // 全屏时禁用按键切换
  if (e.key === 'ArrowUp' || e.key === 'ArrowDown') {
    e.preventDefault()
  }

  const itemHeight = window.innerHeight
  if (e.key === 'ArrowUp') {
    const newIndex = activeIndex.value - 1
    if (newIndex >= 0) {
      isProgrammaticScroll = true
      feedRef.value?.scrollTo({
        top: newIndex * itemHeight,
        behavior: 'smooth'
      })
      // 由于 smooth 滚动是异步的，需要在一段时间后重置标志
      setTimeout(() => {
        isProgrammaticScroll = false
      }, 300) // 大致与动画时间匹配
    }
  } else if (e.key === 'ArrowDown') {
    const newIndex = activeIndex.value + 1
    if (newIndex < videoList.value.length) {
      isProgrammaticScroll = true
      feedRef.value?.scrollTo({
        top: newIndex * itemHeight,
        behavior: 'smooth'
      })
      setTimeout(() => {
        isProgrammaticScroll = false
      }, 300)
    }
  }
}

// 全屏状态变化处理（由播放器内部处理）
const handleFullscreenChange = (val: boolean) => {
  const feed = feedRef.value
  if (!feed) return

  if (val) {
    // 进入全屏：记录当前激活索引，禁用滚动，暂停其他视频
    lastActiveIndexBeforeFullscreen.value = activeIndexInternal.value
    feed.style.overflow = 'hidden'
    videoPlayers.value.forEach((player, idx) => {
      if (idx !== activeIndexInternal.value && player) {
        player.pause()
      }
    })
  } else {
    // 退出全屏：恢复滚动，并强制滚动到之前激活的视频位置
    feed.style.overflow = 'auto'
    const targetTop = lastActiveIndexBeforeFullscreen.value * window.innerHeight
    // 程序化滚动到目标位置
    isProgrammaticScroll = true
    feed.scrollTo({
      top: targetTop,
      behavior: 'auto' // 立即滚动，避免动画导致错位
    })
    // 直接设置 activeIndex 和 activeIndexInternal 为之前的值
    activeIndex.value = lastActiveIndexBeforeFullscreen.value
    activeIndexInternal.value = lastActiveIndexBeforeFullscreen.value
    // 重置标志
    isProgrammaticScroll = false
  }
  isFullScreen.value = val
}

// IntersectionObserver 精确监听视频可见性
let observer: IntersectionObserver | null = null

onMounted(() => {
  // 加载视频列表
  loadVideoList()

  // 初始计算
  updateActiveIndexFromScroll()

  // 监听键盘
  window.addEventListener('keydown', handleKeyDown)

  // 创建 IntersectionObserver
  observer = new IntersectionObserver((entries) => {
    if (isFullScreen.value) return // 全屏时忽略
    for (const entry of entries) {
      if (entry.isIntersecting) {
        const index = Array.from(feedRef.value?.children || []).indexOf(entry.target)
        if (index !== -1 && index !== activeIndex.value) {
          activeIndex.value = index
          updateActiveIndexInternal(index)
        }
        break
      }
    }
  }, {
    root: feedRef.value,
    threshold: 0.6 // 60% 可见时认为激活
  })

  // 等待 DOM 渲染完成后观察每个视频项
  watch(() => videoList.value, () => {
    nextTick(() => {
      if (feedRef.value && observer) {
        Array.from(feedRef.value.children).forEach((child) => {
          observer?.observe(child)
        })
      }
    })
  }, { immediate: true })
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
  observer?.disconnect()
})
</script>

<style scoped>
.video-feed {
  height: calc(100vh - 80px);
  overflow-y: scroll;
  scroll-snap-type: y mandatory; /* 启用垂直滚动捕捉 */
  -webkit-overflow-scrolling: touch; /* 提升 iOS 滚动流畅度 */
  scroll-behavior: smooth;
  scrollbar-width: none;
  -ms-overflow-style: none;
  border-radius: 20px;
}

.video-feed::-webkit-scrollbar {
  display: none;
}

.video-feed > * {
  width: 100%;
  height: calc(100vh - 80px); /* 每个视频占满视口高度 */
  scroll-snap-align: start; /* 用于滚动捕捉 */
}

/* 加载状态 */
.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
}
</style>