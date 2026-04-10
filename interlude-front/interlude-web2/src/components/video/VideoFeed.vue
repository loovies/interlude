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
      @fullscreen-change="handleFullscreenChange"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch, withDefaults } from 'vue'
import DouyinVideoPlayer from './DouyinVideoPlayer.vue'
import { fetchVideoData, fetchVideoList, fetchRandomVideoList } from '@/utils/mockData'

// 防抖函数
function debounce<T extends (...args: any[]) => any>(fn: T, delay: number): T {
  let timer: ReturnType<typeof setTimeout> | null = null
  return function(this: any, ...args: any[]) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  } as T
}

// 节流函数
function throttle<T extends (...args: any[]) => any>(fn: T, interval: number): T {
  let lastTime = 0
  let timer: ReturnType<typeof setTimeout> | null = null
  return function(this: any, ...args: any[]) {
    const now = Date.now()
    if (now - lastTime >= interval) {
      lastTime = now
      fn.apply(this, args)
    } else if (timer === null) {
      timer = setTimeout(() => {
        lastTime = Date.now()
        timer = null
        fn.apply(this, args)
      }, interval - (now - lastTime))
    }
  } as T
}

type FeedMode = 'recommend' | 'random'

const props = withDefaults(defineProps<{
  feedMode?: FeedMode
  initialVideoId?: string | number | null
}>(), {
  feedMode: 'recommend',
  initialVideoId: null,
})

const emit = defineEmits<{
  activeVideoChange: [index: number, video: any]
  fullscreenChange: [val: boolean]
}>()

const feedRef = ref<HTMLElement | null>(null)
const videoPlayers = ref<InstanceType<typeof DouyinVideoPlayer>[]>([])

// 视频列表数据
const videoList = ref<any[]>([])
const loading = ref(false)
const FEED_PAGE_SIZE = 10
const isRandomMode = computed(() => props.feedMode === 'random')
const seedHandled = ref(false)
const lastAppliedSeedId = ref<string | null>(null)
const pendingSeedId = computed(() => {
  if (props.initialVideoId === null || props.initialVideoId === undefined) {
    return null
  }
  const value = String(props.initialVideoId)
  return value.length === 0 ? null : value
})

// activeIndex 用于滚动计算，activeIndexInternal 用于实际控制播放
const activeIndex = ref(0)
const activeIndexInternal = ref(0)
const isFullScreen = ref(false)
const lastActiveIndexBeforeFullscreen = ref(0) // 记录进入全屏前的索引

// 缓存项目高度，避免重复计算
const itemHeight = ref(window.innerHeight)

// 标志位，用于区分是用户滚动还是程序化滚动
let isProgrammaticScroll = false

const resetFeedState = () => {
  activeIndex.value = 0
  activeIndexInternal.value = 0
  lastActiveIndexBeforeFullscreen.value = 0
  if (!feedRef.value) {
    return
  }
  isProgrammaticScroll = true
  feedRef.value.scrollTo({
    top: 0,
    behavior: 'auto',
  })
  isProgrammaticScroll = false
}

const ensureSeedAtTop = async (list: any[], seedId: string | null) => {
  if (!seedId) {
    return list
  }
  const targetIndex = list.findIndex((item) => item && String(item.videoId) === seedId)
  if (targetIndex === 0) {
    return list
  }
  if (targetIndex > 0) {
    const [seedItem] = list.splice(targetIndex, 1)
    list.unshift(seedItem)
    return list
  }
  try {
    const detail = await fetchVideoData(seedId)
    if (detail) {
      list.unshift(detail)
    }
  } catch (error) {
    console.warn('根据 videoId 获取详情失败:', error)
  }
  return list
}

const reloadVideos = (forceSeed: boolean = false) => {
  resetFeedState()
  loadVideoList({ forceSeed })
}

// 更新内部激活索引（只有非全屏时才同步）
const updateActiveIndexInternal = (index: number) => {
  if (!isFullScreen.value) {
    activeIndexInternal.value = index
  }
}

// 根据滚动位置计算当前索引（优化版）
const updateActiveIndexFromScroll = () => {
  if (!feedRef.value) return
  const scrollTop = feedRef.value.scrollTop
  const height = itemHeight.value
  const newIndex = Math.round(scrollTop / height)
  if (newIndex !== activeIndex.value && newIndex >= 0 && newIndex < videoList.value.length) {
    activeIndex.value = newIndex
    updateActiveIndexInternal(newIndex)
  }
}

// 触发当前激活视频变化事件
const emitActiveVideoChange = () => {
  const index = activeIndexInternal.value
  if (index >= 0 && videoList.value[index]) {
    emit('activeVideoChange', index, videoList.value[index])
  }
}

// 加载视频列表
const loadVideoList = async (options?: { forceSeed?: boolean }) => {
  if (loading.value) return

  loading.value = true
  const shouldInjectSeed = isRandomMode.value && (options?.forceSeed || (!seedHandled.value && !!pendingSeedId.value))
  const normalizedSeed = shouldInjectSeed ? pendingSeedId.value : null

  try {
    let fetchedVideos: any[] = []
    if (isRandomMode.value) {
      const result = await fetchRandomVideoList({
        page: 1,
        pageSize: FEED_PAGE_SIZE,
        seedVideoId: normalizedSeed ?? undefined,
      })
      fetchedVideos = result.data || []
      await ensureSeedAtTop(fetchedVideos, normalizedSeed)
    } else {
      const result = await fetchVideoList(1, FEED_PAGE_SIZE)
      fetchedVideos = result.data || []
    }

    videoList.value = fetchedVideos
    // 通知第一个视频激活
    nextTick(() => {
      emitActiveVideoChange()
    })
  } catch (error) {
    console.error('加载视频列表失败:', error)
  } finally {
    if (isRandomMode.value) {
      if (normalizedSeed) {
        seedHandled.value = true
        lastAppliedSeedId.value = normalizedSeed
      } else if (!pendingSeedId.value) {
        seedHandled.value = false
        lastAppliedSeedId.value = null
      }
    } else {
      seedHandled.value = false
      lastAppliedSeedId.value = null
    }
    loading.value = false
  }
}

// 使用 requestAnimationFrame 优化滚动处理
let scrollRafId: number | null = null

// 滚动事件处理
const onScroll = () => {
  if (isFullScreen.value || isProgrammaticScroll) return // 全屏或程序化滚动时忽略
  
  if (scrollRafId !== null) {
    cancelAnimationFrame(scrollRafId)
  }
  
  scrollRafId = requestAnimationFrame(() => {
    updateActiveIndexFromScroll()
    scrollRafId = null
  })
}

// 监听内部索引变化，暂停上一个视频并通知父组件
watch(activeIndexInternal, (newVal, oldVal) => {
  if (oldVal !== undefined && videoPlayers.value[oldVal]) {
    videoPlayers.value[oldVal].pause()
  }
  // 自动播放新视频已在 DouyinVideoPlayer 中通过 autoplay prop 处理
  
  // 通知父组件当前激活的视频变化
  if (newVal !== undefined) {
    emitActiveVideoChange()
  }
})

watch(() => props.feedMode, () => {
  seedHandled.value = false
  lastAppliedSeedId.value = null
  reloadVideos(true)
})

watch(() => props.initialVideoId, (newVal, oldVal) => {
  if (!isRandomMode.value) {
    return
  }
  if (newVal === oldVal) {
    return
  }
  if (newVal === null || newVal === undefined) {
    return
  }
  const normalized = String(newVal)
  if (normalized.length === 0) {
    return
  }
  if (seedHandled.value && lastAppliedSeedId.value === normalized) {
    return
  }
  reloadVideos(true)
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

  const height = itemHeight.value
  if (e.key === 'ArrowUp') {
    const newIndex = activeIndex.value - 1
    if (newIndex >= 0) {
      isProgrammaticScroll = true
      feedRef.value?.scrollTo({
        top: newIndex * height,
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
        top: newIndex * height,
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
    const targetTop = lastActiveIndexBeforeFullscreen.value * itemHeight.value
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
  emit('fullscreenChange', val)
}

// IntersectionObserver 精确监听视频可见性
let observer: IntersectionObserver | null = null

onMounted(() => {
  // 加载视频列表
  reloadVideos(true)

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
  overflow-x: hidden;
  scroll-snap-type: y mandatory;
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;
  scrollbar-width: none;
  -ms-overflow-style: none;
  border-radius: 20px;
  will-change: scroll-position;
  backface-visibility: hidden;
}

.video-feed::-webkit-scrollbar {
  display: none;
}

.video-feed > * {
  width: 100%;
  height: calc(100vh - 80px);
  scroll-snap-align: start;
  scroll-snap-stop: always;
  transform: translateZ(0);
  will-change: transform;
}

.video-feed > .douyin-video-player {
  contain: strict;
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
