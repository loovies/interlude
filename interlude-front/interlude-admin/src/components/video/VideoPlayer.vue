<template>
  <div class="video-player-container">
    <!-- 播放器容器 -->
    <div class="video-wrapper">
      <video
        ref="videoPlayer"
        class="video-js vjs-big-play-centered vjs-fluid"
        controls
        preload="auto"
      ></video>
    </div>

    <!-- 清晰度选择 -->
    <div v-if="qualities.length > 1" class="quality-selector">
      <span class="quality-label">清晰度：</span>
      <button
        v-for="quality in qualities"
        :key="quality.quality"
        :class="['quality-btn', { active: currentQuality === quality.quality }]"
        @click="changeQuality(quality)"
      >
        {{ quality.quality }} ({{ quality.resolution }})
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
      <span>视频加载中...</span>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error-message">
      {{ error }}
      <button @click="retry" class="retry-btn">重试</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'

// 定义props
const props = defineProps({
  // 视频数据
  videoData: {
    type: Object,
    required: true,
    default: () => ({
      qualities: [], // 格式: [{ quality: '480p', m3u8Url: '...', resolution: '854×480' }]
    }),
  },
  // 默认清晰度
  defaultQuality: {
    type: String,
    default: '720p',
  },
  // 播放器配置
  playerOptions: {
    type: Object,
    default: () => ({
      controls: true,
      autoplay: false,
      preload: 'auto',
      fluid: true,
      playbackRates: [0.5, 1, 1.5, 2],
      controlBar: {
        children: [
          'playToggle',
          'volumePanel',
          'currentTimeDisplay',
          'timeDivider',
          'durationDisplay',
          'progressControl',
          'remainingTimeDisplay',
          'playbackRateMenuButton',
          'fullscreenToggle',
        ],
      },
    }),
  },
})

const emit = defineEmits(['ready', 'play', 'pause', 'ended', 'error', 'quality-change'])

// 响应式数据
const videoPlayer = ref(null)
const player = ref(null)
const currentQuality = ref('')
const loading = ref(true)
const error = ref('')
const qualities = ref([])

// 初始化播放器
const initPlayer = () => {
  if (!videoPlayer.value) return

  loading.value = true
  error.value = ''

  try {
    // 合并默认配置
    const options = {
      ...props.playerOptions,
      sources: [],
    }

    // 初始化video.js播放器
    player.value = videojs(videoPlayer.value, options, function () {
      // 播放器就绪回调
      console.log('播放器已初始化')
      emit('ready', player.value)
    })

    // 设置事件监听
    setupPlayerEvents()

    // 设置初始清晰度
    setInitialQuality()
  } catch (err) {
    error.value = `播放器初始化失败: ${err.message}`
    console.error('播放器初始化错误:', err)
    loading.value = false
  }
}

// 设置播放器事件
const setupPlayerEvents = () => {
  if (!player.value) return

  const events = {
    play: () => emit('play'),
    pause: () => emit('pause'),
    ended: () => emit('ended'),
    error: handlePlayerError,
  }

  Object.entries(events).forEach(([event, handler]) => {
    player.value.on(event, handler)
  })
}

// 设置初始清晰度
const setInitialQuality = () => {
  qualities.value = props.videoData.qualities || []

  // 找到默认清晰度
  let targetQuality = qualities.value.find((q) => q.quality === props.defaultQuality)

  // 如果没有找到默认清晰度，使用第一个
  if (!targetQuality && qualities.value.length > 0) {
    targetQuality = qualities.value[0]
  }

  if (targetQuality) {
    currentQuality.value = targetQuality.quality
    loadVideoSource(targetQuality.m3u8Url)
  } else {
    error.value = '没有可用的视频源'
    loading.value = false
  }
}

// 加载视频源
const loadVideoSource = (sourceUrl) => {
  if (!player.value) return

  loading.value = true

  // 保存当前播放状态
  const currentTime = player.value.currentTime()
  const wasPaused = player.value.paused()

  // 设置新的视频源
  player.value.src({
    src: sourceUrl,
    type: 'application/x-mpegURL',
  })

  // 恢复播放状态
  player.value.ready(() => {
    player.value.currentTime(currentTime)
    if (!wasPaused) {
      player.value.play().catch((err) => {
        console.warn('自动播放失败:', err)
      })
    }
    loading.value = false
  })
}

// 切换清晰度
const changeQuality = (quality) => {
  if (currentQuality.value === quality.quality) return

  console.log(`切换到 ${quality.quality} 清晰度`)

  // 保存之前的状态
  const previousQuality = currentQuality.value

  // 更新当前清晰度
  currentQuality.value = quality.quality

  try {
    loadVideoSource(quality.m3u8Url)
    emit('quality-change', quality)
  } catch (err) {
    // 如果切换失败，恢复之前的清晰度
    currentQuality.value = previousQuality
    error.value = `清晰度切换失败: ${err.message}`
    console.error('清晰度切换错误:', err)
  }
}

// 处理播放器错误
const handlePlayerError = () => {
  const playerError = player.value?.error()
  console.error('视频播放错误:', playerError)

  error.value = getErrorMessage(playerError)

  // 尝试自动降级到低清晰度
  autoDegradeQuality()
}

// 获取错误信息
const getErrorMessage = (error) => {
  if (!error) return '视频播放出错'

  switch (error.code) {
    case 1:
      return '视频加载中断'
    case 2:
      return '网络错误，请检查网络连接'
    case 3:
      return '视频解码错误'
    case 4:
      return '视频格式不支持'
    default:
      return '视频播放出错'
  }
}

// 自动降级清晰度
const autoDegradeQuality = () => {
  const qualityOrder = ['1080p', '720p', '480p', '360p']
  const currentIndex = qualityOrder.indexOf(currentQuality.value)

  if (currentIndex > 0) {
    const lowerQuality = qualityOrder[currentIndex - 1]
    const qualityData = qualities.value.find((q) => q.quality === lowerQuality)

    if (qualityData) {
      setTimeout(() => {
        changeQuality(qualityData)
        error.value = `已自动切换到 ${lowerQuality} 清晰度`
      }, 1000)
    }
  }
}

// 重试
const retry = () => {
  error.value = ''
  const qualityData = qualities.value.find((q) => q.quality === currentQuality.value)
  if (qualityData) {
    loadVideoSource(qualityData.m3u8Url)
  }
}

// 组件挂载时初始化
onMounted(() => {
  initPlayer()
})

// 组件卸载前清理
onBeforeUnmount(() => {
  if (player.value) {
    player.value.dispose()
    player.value = null
  }
})

// 监听videoData变化
watch(
  () => props.videoData,
  (newData) => {
    qualities.value = newData.qualities || []
    if (qualities.value.length > 0) {
      setInitialQuality()
    }
  },
  { deep: true }
)

// 暴露方法给父组件
defineExpose({
  play: () => player.value?.play(),
  pause: () => player.value?.pause(),
  togglePlay: () => {
    if (player.value) {
      player.value.paused() ? player.value.play() : player.value.pause()
    }
  },
  setCurrentTime: (time) => player.value?.currentTime(time),
  getCurrentTime: () => player.value?.currentTime(),
  getDuration: () => player.value?.duration(),
  getPlayer: () => player.value,
})
</script>

<style scoped>
.video-player-container {
  position: relative;
  max-width: 100%;
  margin: 0 auto;
  background-color: #000;
}

.video-wrapper {
  position: relative;
  padding-bottom: 56.25%; /* 16:9 比例 */
  height: 0;
  overflow: hidden;
}

.video-wrapper :deep(.video-js) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.quality-selector {
  padding: 10px;
  background: rgba(0, 0, 0, 0.8);
  text-align: center;
}

.quality-label {
  color: #fff;
  font-size: 14px;
  margin-right: 10px;
}

.quality-btn {
  padding: 6px 12px;
  margin: 0 4px;
  border: 1px solid #666;
  border-radius: 4px;
  background: #333;
  color: #fff;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quality-btn:hover {
  background: #444;
  border-color: #888;
}

.quality-btn.active {
  background: #00a1d6;
  border-color: #00a1d6;
  color: #fff;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  z-index: 10;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-message {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 20px;
  background: rgba(0, 0, 0, 0.9);
  color: #fff;
  border-radius: 8px;
  text-align: center;
  z-index: 10;
}

.retry-btn {
  display: block;
  margin-top: 10px;
  padding: 8px 16px;
  background: #00a1d6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.retry-btn:hover {
  background: #0088b5;
}
</style>
