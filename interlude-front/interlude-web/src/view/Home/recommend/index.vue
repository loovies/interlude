<template>
  <div class="recommend-page">
    <button
      v-if="showChoicenessBack"
      class="choiceness-exit-btn"
      type="button"
      @click="handleReturnToChoiceness"
    >
      <span class="close-icon">&times;</span>
    </button>
    <VideoFeed
      :key="routeKey"
      :feed-mode="feedMode"
      :initial-video-id="seedVideoId"
      @fullscreen-change="handleFullscreenChange"
      @active-video-change="handleActiveVideoChange"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import VideoFeed from '../../../components/video/VideoFeed.vue'

type FeedMode = 'recommend' | 'random'

const route = useRoute()
const router = useRouter()

// 褰撳墠瑙嗛淇℃伅
const currentVideo = ref<any>(null)
const seedVideoId = ref<string | null>(null)
const ignoreNextRouteSeedUpdate = ref(false)
const isFullscreen = ref(false)
const routeKey = computed(() => route.fullPath)

const feedMode = computed<FeedMode>(() => (route.name === 'recommendWithVideo' ? 'random' : 'recommend'))
const showChoicenessBack = computed(() => feedMode.value === 'random' && !isFullscreen.value)

const extractRouteVideoId = (): string | null => {
  const param = route.params.videoId
  if (Array.isArray(param)) {
    const value = param[0]
    return value === undefined || value === null ? null : String(value)
  }
  if (param === undefined || param === null) {
    return null
  }
  const value = String(param)
  return value.length === 0 ? null : value
}

const syncSeedFromRoute = () => {
  if (feedMode.value !== 'random') {
    seedVideoId.value = null
    ignoreNextRouteSeedUpdate.value = false
    return
  }
  if (ignoreNextRouteSeedUpdate.value) {
    ignoreNextRouteSeedUpdate.value = false
    return
  }
  seedVideoId.value = extractRouteVideoId()
}

watch(
  () => route.fullPath,
  () => {
    syncSeedFromRoute()
  },
  { immediate: true }
)

// 澶勭悊婵€娲昏棰戝彉鍖?
const handleActiveVideoChange = (index: number, video: any) => {
  currentVideo.value = video
  console.log('当前激活视频:', index, video)
  if (feedMode.value === 'random' && video?.videoId) {
    const nextVideoId = String(video.videoId)
    if (nextVideoId !== extractRouteVideoId()) {
      ignoreNextRouteSeedUpdate.value = true
      router.replace({
        name: 'recommendWithVideo',
        params: { videoId: nextVideoId },
      })
    }
  }
}

const handleFullscreenChange = (val: boolean) => {
  isFullscreen.value = val
}

const handleReturnToChoiceness = () => {
  router.push({ name: 'choiceness' })
}

// 澶勭悊鐐硅禐
const handleLike = () => {
  console.log('鐐硅禐瑙嗛:', currentVideo.value?.videoId)
  // 杩欓噷鍙互璋冪敤API杩涜鐐硅禐
}

// 澶勭悊璇勮
const handleComment = () => {
  console.log('璇勮瑙嗛:', currentVideo.value?.videoId)
  // 杩欓噷鍙互鎵撳紑璇勮闈㈡澘
}

// 澶勭悊鍒嗕韩
const handleShare = () => {
  console.log('鍒嗕韩瑙嗛:', currentVideo.value?.videoId)
  // 杩欓噷鍙互鎵撳紑鍒嗕韩闈㈡澘
}

// 澶勭悊鏀惰棌
const handleCollect = () => {
  console.log('鏀惰棌瑙嗛:', currentVideo.value?.videoId)
  // 杩欓噷鍙互璋冪敤API杩涜鏀惰棌
}
</script>

<style lang="scss" scoped>
.recommend-page {
  position: relative;
  height: calc(100vh - 80px);
  overflow: hidden;
}

.choiceness-exit-btn {
  position: absolute;
  top: 24px;
  left: 24px;
  width: 64px;
  height: 64px;
  border-radius: 32px;
  border: 0.5px solid rgba(255, 255, 255, 0.15);
  background-color: rgba(0, 0, 0, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 20;
  transition: opacity 0.2s ease, transform 0.2s ease;
  user-select: none;
  outline: none;
  caret-color: transparent;
}

.choiceness-exit-btn:hover {
  opacity: 0.85;
  transform: scale(0.98);
}

.choiceness-exit-btn .close-icon {
  font-size: 28px;
  color: #bfbfbf;
  line-height: 1;
  pointer-events: none;
  user-select: none;
}

@media (max-width: 768px) {
  .choiceness-exit-btn {
    top: 16px;
    left: 16px;
    width: 52px;
    height: 52px;
    border-radius: 26px;
  }

  .choiceness-exit-btn .close-icon {
    font-size: 24px;
  }
}
</style>

