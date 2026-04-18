<template>
  <div class="video-feed" ref="feedRef">
    <div class="video-feed-track" :class="{ dragging: isDragging }" :style="trackStyle">
      <div v-for="(video, index) in videoList" :key="video.videoId" class="video-feed-item">
        <DouyinVideoPlayer
          :video-data="video"
          :autoplay="activeIndexInternal === index"
          ref="videoPlayers"
          @play="handleVideoPlay(index)"
          @pause="handleVideoPause(index)"
          @fullscreen-change="handleFullscreenChange"
          @comment-panel-change="handleCommentPanelChange(index, $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import DouyinVideoPlayer from './DouyinVideoPlayer.vue'
import { fetchVideoData, fetchRandomVideoList, fetchVideoList } from '@/utils/mockData'

type FeedMode = 'recommend' | 'random'

const SWITCH_ANIMATION_MS = 380
const TOUCH_SWIPE_RATIO_THRESHOLD = 0.14
const TOUCH_VELOCITY_THRESHOLD = 0.42
const WHEEL_SWITCH_THRESHOLD = 95
const WHEEL_RESET_MS = 180

const props = withDefaults(defineProps<{
  feedMode?: FeedMode
  initialVideoId?: string | number | null
  externalVideoList?: any[] | null
}>(), {
  feedMode: 'recommend',
  initialVideoId: null,
  externalVideoList: null,
})

const emit = defineEmits<{
  activeVideoChange: [index: number, video: any]
  fullscreenChange: [val: boolean]
  commentPanelChange: [visible: boolean]
}>()

const feedRef = ref<HTMLElement | null>(null)
const videoPlayers = ref<InstanceType<typeof DouyinVideoPlayer>[]>([])
const videoList = ref<any[]>([])
const loading = ref(false)

const FEED_PAGE_SIZE = 10
const isRandomMode = computed(() => props.feedMode === 'random')
const hasExternalVideoList = computed(() => Array.isArray(props.externalVideoList))

const seedHandled = ref(false)
const lastAppliedSeedId = ref<string | null>(null)
const pendingSeedId = computed(() => {
  if (props.initialVideoId === null || props.initialVideoId === undefined) {
    return null
  }
  const value = String(props.initialVideoId)
  return value.length === 0 ? null : value
})

const activeIndex = ref(0)
const activeIndexInternal = ref(0)
const isFullScreen = ref(false)
const isCommentPanelVisible = ref(false)
const lastActiveIndexBeforeFullscreen = ref(0)

const itemHeight = ref(Math.max(window.innerHeight - 80, 1))
const isAnimating = ref(false)
const transitionEnabled = ref(true)
const isDragging = ref(false)
const dragOffsetY = ref(0)

let animationTimer: ReturnType<typeof setTimeout> | null = null
let wheelDeltaAccumulator = 0
let wheelResetTimer: ReturnType<typeof setTimeout> | null = null

let touchStartY = 0
let touchStartTime = 0
let lastTouchY = 0
let lastTouchTime = 0

const trackStyle = computed(() => {
  const translateY = -activeIndex.value * itemHeight.value + dragOffsetY.value
  return {
    transform: `translate3d(0, ${translateY}px, 0)`,
    transition: transitionEnabled.value
      ? `transform ${SWITCH_ANIMATION_MS}ms cubic-bezier(0.22, 1, 0.36, 1)`
      : 'none',
  }
})

const clampIndex = (index: number): number => {
  if (videoList.value.length === 0) {
    return 0
  }
  return Math.min(Math.max(index, 0), videoList.value.length - 1)
}

const clearAnimationTimer = () => {
  if (animationTimer) {
    clearTimeout(animationTimer)
    animationTimer = null
  }
}

const clearWheelResetTimer = () => {
  if (wheelResetTimer) {
    clearTimeout(wheelResetTimer)
    wheelResetTimer = null
  }
}

const finishAnimationAfterDelay = (duration = SWITCH_ANIMATION_MS) => {
  clearAnimationTimer()
  isAnimating.value = true
  animationTimer = setTimeout(() => {
    isAnimating.value = false
    animationTimer = null
  }, duration)
}

const emitActiveVideoChange = () => {
  const index = activeIndexInternal.value
  if (index >= 0 && videoList.value[index]) {
    emit('activeVideoChange', index, videoList.value[index])
  }
}

const updateActiveIndexInternal = (index: number) => {
  activeIndexInternal.value = index
}

const snapBackToCurrent = () => {
  transitionEnabled.value = true
  dragOffsetY.value = 0
  finishAnimationAfterDelay(260)
}

const goToIndex = (targetIndex: number): boolean => {
  if (videoList.value.length === 0 || isFullScreen.value || isAnimating.value) {
    return false
  }
  const nextIndex = clampIndex(targetIndex)
  if (nextIndex === activeIndex.value) {
    return false
  }

  transitionEnabled.value = true
  dragOffsetY.value = 0
  activeIndex.value = nextIndex
  updateActiveIndexInternal(nextIndex)
  finishAnimationAfterDelay()
  return true
}

const shouldIgnoreGesture = (target: EventTarget | null): boolean => {
  if (!(target instanceof HTMLElement)) {
    return false
  }
  const blockSelector = [
    'input',
    'textarea',
    'select',
    '[contenteditable="true"]',
    '.comment-panel',
    '.comment-panel-body',
    '.comment-list',
    '.comment-panel-input',
    '.el-overlay',
    '.el-dialog',
    '.el-drawer',
    '.el-select-dropdown',
    '.el-popover',
    '.el-picker-panel',
    '.el-scrollbar',
  ].join(',')
  return !!target.closest(blockSelector)
}

const updateItemHeight = () => {
  if (feedRef.value) {
    itemHeight.value = Math.max(feedRef.value.clientHeight, 1)
  } else {
    itemHeight.value = Math.max(window.innerHeight - 80, 1)
  }
}

const applyEdgeResistance = (deltaY: number): number => {
  const atTop = activeIndex.value <= 0 && deltaY > 0
  const atBottom = activeIndex.value >= videoList.value.length - 1 && deltaY < 0
  if (atTop || atBottom) {
    return deltaY * 0.22
  }
  return deltaY * 0.9
}

const triggerSwitchByDirection = (direction: 1 | -1): boolean => {
  const target = activeIndex.value + direction
  return goToIndex(target)
}

const onWheel = (event: WheelEvent) => {
  if (isFullScreen.value || isCommentPanelVisible.value || videoList.value.length === 0) {
    return
  }
  if (shouldIgnoreGesture(event.target)) {
    return
  }
  if (Math.abs(event.deltaY) < 2) {
    return
  }

  event.preventDefault()

  if (isAnimating.value) {
    return
  }

  wheelDeltaAccumulator += event.deltaY
  clearWheelResetTimer()
  wheelResetTimer = setTimeout(() => {
    wheelDeltaAccumulator = 0
    wheelResetTimer = null
  }, WHEEL_RESET_MS)

  if (Math.abs(wheelDeltaAccumulator) < WHEEL_SWITCH_THRESHOLD) {
    return
  }

  const direction: 1 | -1 = wheelDeltaAccumulator > 0 ? 1 : -1
  wheelDeltaAccumulator = 0
  triggerSwitchByDirection(direction)
}

const onTouchStart = (event: TouchEvent) => {
  if (isFullScreen.value || isCommentPanelVisible.value || videoList.value.length === 0 || isAnimating.value) {
    return
  }
  if (event.touches.length !== 1 || shouldIgnoreGesture(event.target)) {
    return
  }

  const touch = event.touches[0]
  if (!touch) {
    return
  }
  touchStartY = touch.clientY
  touchStartTime = Date.now()
  lastTouchY = touch.clientY
  lastTouchTime = touchStartTime

  isDragging.value = true
  transitionEnabled.value = false
  dragOffsetY.value = 0
}

const onTouchMove = (event: TouchEvent) => {
  if (!isDragging.value || event.touches.length !== 1) {
    return
  }
  const touch = event.touches[0]
  if (!touch) {
    return
  }
  const deltaY = touch.clientY - touchStartY
  lastTouchY = touch.clientY
  lastTouchTime = Date.now()
  dragOffsetY.value = applyEdgeResistance(deltaY)

  if (Math.abs(deltaY) > 4) {
    event.preventDefault()
  }
}

const finalizeTouch = () => {
  if (!isDragging.value) {
    return
  }

  const deltaY = lastTouchY - touchStartY
  const duration = Math.max(lastTouchTime - touchStartTime, 1)
  const velocity = deltaY / duration
  const distanceThreshold = itemHeight.value * TOUCH_SWIPE_RATIO_THRESHOLD

  isDragging.value = false
  transitionEnabled.value = true

  const shouldPrev = deltaY > distanceThreshold || velocity > TOUCH_VELOCITY_THRESHOLD
  const shouldNext = deltaY < -distanceThreshold || velocity < -TOUCH_VELOCITY_THRESHOLD

  if (shouldNext) {
    const switched = triggerSwitchByDirection(1)
    if (!switched) {
      snapBackToCurrent()
    }
    return
  }

  if (shouldPrev) {
    const switched = triggerSwitchByDirection(-1)
    if (!switched) {
      snapBackToCurrent()
    }
    return
  }

  snapBackToCurrent()
}

const onTouchEnd = () => {
  finalizeTouch()
}

const onTouchCancel = () => {
  finalizeTouch()
}

const resetFeedState = () => {
  clearAnimationTimer()
  clearWheelResetTimer()
  wheelDeltaAccumulator = 0
  activeIndex.value = 0
  activeIndexInternal.value = 0
  dragOffsetY.value = 0
  isDragging.value = false
  isAnimating.value = false
  transitionEnabled.value = true
  isCommentPanelVisible.value = false
  lastActiveIndexBeforeFullscreen.value = 0
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
    console.warn('Failed to load seed video detail:', error)
  }
  return list
}

const applyExternalVideoList = (list: any[]) => {
  resetFeedState()
  videoList.value = Array.isArray(list) ? [...list] : []
  nextTick(() => {
    emit('commentPanelChange', false)
    emitActiveVideoChange()
  })
}

const loadVideoList = async (options?: { forceSeed?: boolean }) => {
  if (hasExternalVideoList.value) {
    applyExternalVideoList(props.externalVideoList || [])
    return
  }
  if (loading.value) {
    return
  }

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
    nextTick(() => {
      updateItemHeight()
      emitActiveVideoChange()
    })
  } catch (error) {
    console.error('Failed to load video list:', error)
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

const reloadVideos = (forceSeed = false) => {
  resetFeedState()
  loadVideoList({ forceSeed })
}

watch(activeIndexInternal, (newVal, oldVal) => {
  if (oldVal !== undefined && videoPlayers.value[oldVal]) {
    videoPlayers.value[oldVal].pause()
  }
  if (newVal !== undefined) {
    isCommentPanelVisible.value = false
    emitActiveVideoChange()
    emit('commentPanelChange', false)
  }
})

watch(() => props.feedMode, () => {
  if (hasExternalVideoList.value) {
    return
  }
  seedHandled.value = false
  lastAppliedSeedId.value = null
  reloadVideos(true)
})

watch(() => props.initialVideoId, (newVal, oldVal) => {
  if (hasExternalVideoList.value || !isRandomMode.value) {
    return
  }
  if (newVal === oldVal || newVal === null || newVal === undefined) {
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

watch(
  () => props.externalVideoList,
  (list) => {
    if (!hasExternalVideoList.value) {
      return
    }
    applyExternalVideoList(list || [])
  },
)

const handleVideoPlay = (index: number) => {
  videoPlayers.value.forEach((player, i) => {
    if (i !== index && player) {
      player.pause()
    }
  })
}

const handleVideoPause = (_index: number) => {
  // Reserved for future behavior
}

const handleCommentPanelChange = (index: number, visible: boolean) => {
  if (index !== activeIndexInternal.value) {
    return
  }
  isCommentPanelVisible.value = visible
  emit('commentPanelChange', visible)
}

const handleKeyDown = (event: KeyboardEvent) => {
  if (isFullScreen.value || isCommentPanelVisible.value) {
    return
  }
  if (event.key === 'ArrowUp' || event.key === 'ArrowDown') {
    event.preventDefault()
  }
  if (event.key === 'ArrowUp') {
    triggerSwitchByDirection(-1)
  } else if (event.key === 'ArrowDown') {
    triggerSwitchByDirection(1)
  }
}

const handleFullscreenChange = (val: boolean) => {
  if (val) {
    lastActiveIndexBeforeFullscreen.value = activeIndexInternal.value
    videoPlayers.value.forEach((player, idx) => {
      if (idx !== activeIndexInternal.value && player) {
        player.pause()
      }
    })
  } else {
    const target = clampIndex(lastActiveIndexBeforeFullscreen.value)
    activeIndex.value = target
    activeIndexInternal.value = target
    dragOffsetY.value = 0
    transitionEnabled.value = false
    requestAnimationFrame(() => {
      transitionEnabled.value = true
    })
  }
  isFullScreen.value = val
  emit('fullscreenChange', val)
}

const handleResize = () => {
  updateItemHeight()
}

onMounted(() => {
  if (hasExternalVideoList.value) {
    applyExternalVideoList(props.externalVideoList || [])
  } else {
    reloadVideos(true)
  }

  updateItemHeight()
  window.addEventListener('resize', handleResize)
  window.addEventListener('keydown', handleKeyDown)

  const feed = feedRef.value
  if (feed) {
    feed.addEventListener('wheel', onWheel, { passive: false })
    feed.addEventListener('touchstart', onTouchStart, { passive: true })
    feed.addEventListener('touchmove', onTouchMove, { passive: false })
    feed.addEventListener('touchend', onTouchEnd, { passive: true })
    feed.addEventListener('touchcancel', onTouchCancel, { passive: true })
  }
})

onUnmounted(() => {
  clearAnimationTimer()
  clearWheelResetTimer()
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('keydown', handleKeyDown)

  const feed = feedRef.value
  if (feed) {
    feed.removeEventListener('wheel', onWheel)
    feed.removeEventListener('touchstart', onTouchStart)
    feed.removeEventListener('touchmove', onTouchMove)
    feed.removeEventListener('touchend', onTouchEnd)
    feed.removeEventListener('touchcancel', onTouchCancel)
  }
})
</script>

<style scoped>
.video-feed {
  position: relative;
  width: 100%;
  height: calc(100vh - 80px);
  overflow: hidden;
  border-radius: 20px;
  background: #000;
  will-change: transform;
  backface-visibility: hidden;
}

.video-feed-track {
  width: 100%;
  height: 100%;
  will-change: transform;
}

.video-feed-track.dragging {
  transition: none !important;
}

.video-feed-item {
  width: 100%;
  height: calc(100vh - 80px);
  transform: translateZ(0);
  will-change: transform;
}

.video-feed-item > .douyin-video-player {
  contain: strict;
}
</style>
