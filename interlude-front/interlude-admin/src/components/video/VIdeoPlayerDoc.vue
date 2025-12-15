<template>
  <!-- 视频播放器模态框 -->
  <div
    v-if="visible"
    class="video-modal-overlay"
    @click.self="close"
    @mousemove="showControls"
    @mouseleave="startHideTimer"
  >
    <!-- 播放器容器 -->
    <div class="video-modal-container" :class="{ 'controls-visible': controlsVisible }">
      <!-- 播放器头部 -->
      <div class="modal-header">
        <div class="video-title">{{ videoTitle + '.mp4' }}</div>
        <button class="modal-close-btn" @click="close">
          <svg class="close-icon" viewBox="0 0 24 24">
            <path
              d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"
            />
          </svg>
        </button>
      </div>

      <!-- 视频播放区域 -->
      <div class="video-content">
        <div class="video-wrapper" @click="heandleWrapperPause()" ref="videoContainer"></div>

        <!-- 中央播放按钮（暂停时显示） -->
        <div class="center-play-button" v-if="!isPlaying && !loading && !error" @click="playVideo">
          <svg class="play-icon" viewBox="0 0 24 24">
            <path d="M8 5v14l11-7z" />
          </svg>
        </div>

        <!-- 底部控制栏 -->
        <div class="video-control-bar" v-if="controlsVisible">
          <!-- 左侧：播放/暂停 + 时间 -->
          <div class="control-left">
            <!-- 播放/暂停按钮 -->
            <button class="control-btn play-pause-btn" @click="togglePlay">
              <svg v-if="isPlaying" class="pause-icon" viewBox="0 0 24 24">
                <path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z" />
              </svg>
              <svg v-else class="play-icon" viewBox="0 0 24 24">
                <path d="M8 5v14l11-7z" />
              </svg>
            </button>

            <!-- 时间显示 -->
            <div class="time-display">
              <span class="current-time">{{ formatTime(currentTime) }}</span>
              <span class="time-separator">/</span>
              <span class="duration">{{ formatTime(totalDuration) }}</span>
            </div>
          </div>

          <!-- 中间：进度条 -->
          <div class="control-center">
            <div class="progress-bar" ref="progressBar" @click="seekToPosition">
              <div class="progress-bg"></div>
              <div class="progress-filled" :style="{ width: progressPercent + '%' }"></div>
              <div class="progress-thumb" :style="{ left: progressPercent + '%' }"></div>
            </div>
          </div>

          <!-- 右侧：功能按钮 -->
          <div class="control-right">
            <!-- 播放速率 -->
            <div class="playback-rate-selector">
              <button class="control-btn" @click="togglePlaybackRateMenu">
                <svg class="speed-icon" viewBox="0 0 24 24">
                  <path
                    d="M13.5 4.5c0 1.1-.9 2-2 2s-2-.9-2-2 .9-2 2-2 2 .9 2 2zM10.5 22c-.55 0-1-.45-1-1v-6.14c-.31.11-.65.17-1 .17-1.1 0-2-.9-2-2V9c0-1.1.9-2 2-2 .35 0 .69.06 1 .17V5c0-1.1.9-2 2-2s2 .9 2 2v2.17c.31-.11.65-.17 1-.17 1.1 0 2 .9 2 2v4c0 1.1-.9 2-2 2-.35 0-.69-.06-1-.17V21c0 .55-.45 1-1 1s-1-.45-1-1v-6.14c-.31.11-.65.17-1 .17-1.1 0-2-.9-2-2V9c0-1.1.9-2 2-2 .35 0 .69.06 1 .17V21c0 .55-.45 1-1 1z"
                  />
                </svg>
                <span class="rate-text">{{ currentPlaybackRate }}x</span>
              </button>

              <!-- 播放速率菜单 -->
              <div v-if="showPlaybackRateMenu" class="dropdown-menu rate-menu">
                <div
                  v-for="rate in playbackRates"
                  :key="rate"
                  class="dropdown-item"
                  :class="{ active: currentPlaybackRate === rate }"
                  @click="changePlaybackRate(rate)"
                >
                  {{ rate }}x
                </div>
              </div>
            </div>

            <!-- 清晰度选择 -->
            <div class="quality-selector">
              <button class="control-btn" @click="toggleQualityMenu">
                <svg class="quality-icon" viewBox="0 0 24 24">
                  <path
                    d="M12 7c2.76 0 5 2.24 5 5s-2.24 5-5 5-5-2.24-5-5 2.24-5 5-5zm0-5C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z"
                  />
                  <path d="M12 17l1.5-4.5h3L12 7 9.5 12.5h3z" />
                </svg>
                <span class="quality-text">{{ currentQuality }}</span>
              </button>

              <!-- 清晰度菜单 -->
              <div v-if="showQualityMenu" class="dropdown-menu quality-menu">
                <div
                  v-for="quality in sortedQualities"
                  :key="quality.quality"
                  class="dropdown-item"
                  :class="{ active: currentQuality === quality.quality }"
                  @click="changeQuality(quality.quality)"
                >
                  <span>{{ quality.quality }}</span>
                  <span class="quality-resolution">{{ quality.resolution }}</span>
                </div>
              </div>
            </div>

            <!-- 音量控制 -->
            <div class="volume-control">
              <button class="control-btn" @click="toggleMute">
                <svg v-if="isMuted || volume === 0" class="volume-off-icon" viewBox="0 0 24 24">
                  <path
                    d="M16.5 12c0-1.77-1.02-3.29-2.5-4.03v2.21l2.45 2.45c.03-.2.05-.41.05-.63zm2.5 0c0 .94-.2 1.82-.54 2.64l1.51 1.51C20.63 14.91 21 13.5 21 12c0-4.28-2.99-7.86-7-8.77v2.06c2.89.86 5 3.54 5 6.71zM4.27 3L3 4.27 7.73 9H3v6h4l5 5v-6.73l4.25 4.25c-.67.52-1.42.93-2.25 1.18v2.06c1.38-.31 2.63-.95 3.69-1.81L19.73 21 21 19.73l-9-9L4.27 3zM12 4L9.91 6.09 12 8.18V4z"
                  />
                </svg>
                <svg v-else-if="volume <= 0.5" class="volume-low-icon" viewBox="0 0 24 24">
                  <path
                    d="M18.5 12c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM5 9v6h4l5 5V4L9 9H5z"
                  />
                </svg>
                <svg v-else class="volume-high-icon" viewBox="0 0 24 24">
                  <path
                    d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z"
                  />
                </svg>
              </button>

              <!-- 音量滑块 -->
              <div class="volume-slider-container">
                <div class="volume-slider" ref="volumeSlider" @click="changeVolume">
                  <div class="volume-track"></div>
                  <div
                    class="volume-level"
                    :style="{ width: (isMuted ? 0 : volume) * 100 + '%' }"
                  ></div>
                  <div
                    class="volume-thumb"
                    :style="{ left: (isMuted ? 0 : volume) * 100 + '%' }"
                  ></div>
                </div>
              </div>
            </div>

            <!-- 全屏按钮 -->
            <button class="control-btn" @click="toggleFullscreen">
              <svg v-if="isFullscreen" class="fullscreen-exit-icon" viewBox="0 0 24 24">
                <path
                  d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"
                />
              </svg>
              <svg v-else class="fullscreen-icon" viewBox="0 0 24 24">
                <path
                  d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 加载遮罩 -->
      <div v-if="loading" class="loading-overlay">
        <div class="spinner"></div>
        <p class="loading-text">视频加载中...</p>
      </div>

      <!-- 错误提示 -->
      <div v-if="error" class="error-overlay">
        <div class="error-content">
          <svg class="error-icon" viewBox="0 0 24 24">
            <path
              d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"
            />
          </svg>
          <p class="error-text">{{ error }}</p>
          <button class="retry-btn" @click="retry">重试</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'
import Hls from 'hls.js'

// 定义props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  videoData: {
    type: Object,
    default: () => ({
      videoId: null,
      title: '',
      duration: 0,
      qualities: [],
    }),
  },
})

const emit = defineEmits(['close', 'ready', 'play', 'pause', 'ended'])

// 响应式数据
const videoContainer = ref(null)
const progressBar = ref(null)
const volumeSlider = ref(null)

const player = ref(null)
const hls = ref(null)

// 播放状态
const isPlaying = ref(false)
const currentTime = ref(0)
const totalDuration = ref(0)
const progressPercent = ref(0)

// 控制条状态
const controlsVisible = ref(true)
const hideTimer = ref(null)

// 清晰度相关
const currentQuality = ref('')
const showQualityMenu = ref(false)

// 播放速率相关
const playbackRates = [0.5, 0.75, 1.0, 1.25, 1.5, 2.0]
const currentPlaybackRate = ref(1.0)
const showPlaybackRateMenu = ref(false)

// 音量相关
const volume = ref(0.7)
const isMuted = ref(false)

// 全屏相关
const isFullscreen = ref(false)

// 加载和错误状态
const loading = ref(false)
const error = ref('')
const isInitialized = ref(false)

// 计算属性
const videoTitle = computed(() => props.videoData.title || '视频播放')

const sortedQualities = computed(() => {
  const qualities = [...props.videoData.qualities]
  // 按清晰度排序（从高到低）
  const order = {
    '4k': 1,
    '2160p': 2,
    '1440p': 3,
    '1080p': 4,
    '720p': 5,
    '480p': 6,
    '360p': 7,
    '240p': 8,
  }
  return qualities.sort((a, b) => {
    const orderA = order[a.quality.toLowerCase()] || 999
    const orderB = order[b.quality.toLowerCase()] || 999
    return orderA - orderB
  })
})

// 初始化播放器
const initPlayer = () => {
  console.log('初始化播放器开始')

  if (!videoContainer.value) {
    console.error('视频容器未找到')
    return
  }

  loading.value = true
  error.value = ''
  controlsVisible.value = true
  isInitialized.value = false

  try {
    // 销毁现有的播放器
    if (player.value) {
      console.log('销毁现有播放器')
      player.value.dispose()
      player.value = null
    }

    if (hls.value) {
      hls.value.destroy()
      hls.value = null
    }

    // 设置总时长
    totalDuration.value = props.videoData.duration || 0
    console.log('视频时长:', totalDuration.value)

    // 创建video元素
    const videoElement = document.createElement('video')
    videoElement.className = 'video-js vjs-default-skin vjs-big-play-centered'
    videoElement.setAttribute('playsinline', '')
    videoElement.setAttribute('webkit-playsinline', '')
    videoElement.setAttribute('controlsList', 'nodownload')

    // 清空容器并添加video元素
    videoContainer.value.innerHTML = ''
    videoContainer.value.appendChild(videoElement)

    // 初始化video.js播放器
    player.value = videojs(
      videoElement,
      {
        controls: false,
        autoplay: true, // 改为false，由用户手动触发
        preload: 'auto',
        fluid: true,
        aspectRatio: '16:9',
        muted: isMuted.value,
        volume: volume.value,
        playbackRates: playbackRates,
        userActions: {
          doubleClick: true,
          hotkeys: true,
        },
        html5: {
          vhs: {
            overrideNative: !Hls.isSupported(),
            withCredentials: false,
          },
          nativeVideoTracks: false,
          nativeAudioTracks: false,
          nativeTextTracks: false,
        },
        sources: [],
      },
      function () {
        console.log('Video.js播放器初始化完成')
        isInitialized.value = true

        // 设置初始清晰度
        setInitialQuality()

        // 设置播放速率
        player.value.playbackRate(currentPlaybackRate.value)

        // 设置事件监听
        setupPlayerEvents()

        // 开始隐藏控制条的计时器
        startHideTimer()

        // 触发ready事件
        emit('ready')
      }
    )

    console.log('播放器初始化完成')
  } catch (err) {
    console.error('播放器初始化失败:', err)
    error.value = `播放器初始化失败: ${err.message}`
    loading.value = false
  }
}

// 设置初始清晰度
const setInitialQuality = () => {
  if (sortedQualities.value.length > 0) {
    // 默认选择最高清晰度
    const defaultQuality = sortedQualities.value[0].quality
    currentQuality.value = defaultQuality
    loadVideoSource(defaultQuality)
  }
}

// 加载视频源
const loadVideoSource = (quality) => {
  console.log('loadVideoSource 被调用，quality:', quality)

  if (!player.value) {
    return
  }

  const qualityData = props.videoData.qualities.find((q) => q.quality === quality)
  if (!qualityData) {
    console.error('未找到清晰度数据:', quality)
    return
  }

  loading.value = true
  error.value = ''

  // 清除之前的HLS实例
  if (hls.value) {
    console.log('销毁之前的HLS实例')
    hls.value.destroy()
    hls.value = null
  }

  const videoSrc = 'http://localhost:4000' + qualityData.m3u8Url
  console.log('完整视频URL:', videoSrc)

  // 测试URL是否可访问
  fetch(videoSrc)
    .then((response) => {
      console.log('M3U8文件响应状态:', response.status, response.statusText)
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`)
      }
      return response.text()
    })
    .then((text) => {
      console.log('M3U8文件内容前200字符:', text.substring(0, 200))
      console.log('M3U8文件总长度:', text.length)

      if (!text.includes('#EXTM3U')) {
        console.error('不是有效的M3U8文件')
        error.value = '视频格式错误：不是有效的M3U8文件'
        loading.value = false
        return
      }

      // 使用HLS.js播放视频
      playVideoWithHls(videoSrc)
    })
    .catch((err) => {
      console.error('无法获取M3U8文件:', err)
      error.value = `无法加载视频: ${err.message}`
      loading.value = false
    })
}

// 使用HLS.js播放视频
const playVideoWithHls = (videoSrc) => {
  console.log('playVideoWithHls 开始，videoSrc:', videoSrc)

  if (Hls.isSupported()) {
    console.log('浏览器支持hls.js，使用hls.js播放')

    hls.value = new Hls({
      enableWorker: true,
      lowLatencyMode: true,
      backBufferLength: 90,
      maxBufferLength: 30,
      debug: true,
      xhrSetup: (xhr, url) => {
        xhr.withCredentials = false
        console.log('加载资源:', url)
      },
    })

    hls.value.loadSource(videoSrc)

    // 获取video元素 - 使用原生video元素
    let videoElement
    if (player.value && player.value.el()) {
      videoElement = player.value.el()
      // 确保是video元素，如果被包装了则找到真正的video元素
      if (videoElement.tagName !== 'VIDEO') {
        videoElement = videoElement.querySelector('video')
      }
    }

    if (!videoElement) {
      console.error('未找到video元素')
      error.value = '播放器初始化失败'
      loading.value = false
      return
    }

    console.log('将hls.js附加到video元素:', videoElement)
    hls.value.attachMedia(videoElement)

    // HLS事件监听
    hls.value.on(Hls.Events.MANIFEST_PARSED, (event, data) => {
      console.log('HLS manifest解析完成，可以开始播放', data)

      // 重要：设置正确的视频时长
      if (data.levels && data.levels.length > 0) {
        const level = data.levels[0]
        const duration = level.duration || level.totalduration || 0
        if (duration > 0) {
          totalDuration.value = duration
          console.log('从HLS设置视频时长:', duration)
        }
      }

      // 等待一小段时间确保视频就绪
      setTimeout(() => {
        loading.value = false
        console.log('加载状态设为false，显示播放按钮')

        // 确保视频元素有正确的状态
        if (videoElement.readyState >= 2) {
          // HAVE_CURRENT_DATA 或更高
          console.log('视频已就绪，可以播放')
        } else {
          console.log('视频准备状态:', videoElement.readyState)
          // 监听视频就绪事件
          videoElement.addEventListener(
            'loadeddata',
            () => {
              console.log('视频数据加载完成')
            },
            { once: true }
          )
        }
      }, 500)
    })

    hls.value.on(Hls.Events.LEVEL_LOADED, (event, data) => {
      console.log('HLS 清晰度加载完成:', data)
    })

    hls.value.on(Hls.Events.ERROR, (event, data) => {
      console.error('HLS错误详情:', data)

      if (data.fatal) {
        loading.value = false

        switch (data.type) {
          case Hls.ErrorTypes.NETWORK_ERROR:
            console.error('网络错误详情:', data.details)
            error.value = `网络错误: ${data.details}`
            if (data.response && data.response.code === 404) {
              error.value = '视频文件不存在 (404)'
            }
            break
          case Hls.ErrorTypes.MEDIA_ERROR:
            console.error('媒体错误详情:', data.details)
            error.value = '视频格式错误'
            // 尝试恢复
            hls.value.recoverMediaError()
            break
          default:
            console.error('其他HLS错误:', data)
            error.value = '视频播放错误'
            degradeQuality()
            break
        }
      }
    })
  } else {
    // 浏览器原生支持HLS
    console.log('浏览器原生支持HLS，使用原生播放')

    const videoElement = player.value.el()
    if (videoElement.canPlayType('application/vnd.apple.mpegurl')) {
      // 设置视频源
      player.value.src({
        src: videoSrc,
        type: 'application/x-mpegURL',
        withCredentials: false,
      })

      // 监听加载完成事件
      player.value.ready(() => {
        console.log('原生HLS播放器就绪')
        loading.value = false

        // 监听视频数据加载
        videoElement.addEventListener(
          'loadeddata',
          () => {
            console.log('视频数据加载完成')
            if (videoElement.duration && videoElement.duration > 0) {
              totalDuration.value = videoElement.duration
              console.log('设置视频时长:', videoElement.duration)
            }
          },
          { once: true }
        )
      })

      // 监听错误
      player.value.on('error', () => {
        const playerError = player.value.error()
        console.error('原生HLS播放错误:', playerError)
        loading.value = false
        error.value = `播放错误: ${getErrorMessage(playerError)}`
      })
    } else {
      error.value = '您的浏览器不支持HLS视频播放'
      loading.value = false
      console.error('浏览器不支持HLS')
    }
  }
}

// 手动播放视频
const playVideo = () => {
  if (!player.value) return

  try {
    // 直接调用 video.js 的 play 方法
    const playPromise = player.value.play()

    if (playPromise !== undefined) {
      playPromise
        .then(() => {
          console.log('视频播放成功')
          isPlaying.value = true
          error.value = '' // 清除任何错误
        })
        .catch((err) => {
          console.log('自动播放被阻止:', err)
          // 尝试静音播放
          player.value.muted(true)
          player.value
            .play()
            .then(() => {
              console.log('静音播放成功')
              isPlaying.value = true
              error.value = '' // 清除任何错误
            })
            .catch((err2) => {
              console.error('静音播放也失败:', err2)
              error.value = '播放失败，请点击播放按钮重试'
            })
        })
    }
  } catch (err) {
    console.error('播放异常:', err)
    error.value = '播放失败，请重试'
  }
}

// 设置播放器事件
const setupPlayerEvents = () => {
  if (!player.value) return

  // 使用 video.js 的事件监听
  player.value.on('play', () => {
    console.log('视频开始播放')
    isPlaying.value = true
    emit('play')
  })

  player.value.on('pause', () => {
    console.log('视频暂停')
    isPlaying.value = false
    emit('pause')
  })

  player.value.on('ended', () => {
    console.log('视频播放结束')
    isPlaying.value = false
    emit('ended')
  })

  player.value.on('error', () => {
    const playerError = player.value.error()
    console.error('视频播放错误:', playerError)
    error.value = getErrorMessage(playerError)
    loading.value = false
  })

  player.value.on('timeupdate', () => {
    const current = player.value.currentTime()
    const duration = player.value.duration()

    if (current !== undefined) {
      currentTime.value = current
    }

    if (duration && duration > 0) {
      totalDuration.value = duration

      if (totalDuration.value > 0) {
        progressPercent.value = (currentTime.value / totalDuration.value) * 100
      }
    }
  })

  player.value.on('loadedmetadata', () => {
    const duration = player.value.duration()
    if (duration && duration > 0) {
      totalDuration.value = duration
      console.log('从视频获取到时长:', duration)
    }
  })

  player.value.on('volumechange', () => {
    volume.value = player.value.volume()
    isMuted.value = player.value.muted()
  })

  player.value.on('fullscreenchange', () => {
    isFullscreen.value = player.value.isFullscreen()
  })

  // 监听键盘事件
  document.addEventListener('keydown', handleKeydown)

  // 添加一个超时检查，防止视频一直加载
  setTimeout(() => {
    if (loading.value) {
      console.log('视频加载超时，尝试强制显示播放按钮')
      loading.value = false
      // 检查视频元素状态
      const videoElement = player.value.el()
      if (videoElement.readyState >= 1) {
        // HAVE_ENOUGH_DATA 或更高
        console.log('视频已有足够数据，应该可以播放')
      }
    }
  }, 10000) // 10秒超时
}

// 显示控制条
const showControls = () => {
  controlsVisible.value = true
  clearTimeout(hideTimer.value)

  // 如果不是播放状态，保持显示
  if (isPlaying.value) {
    hideTimer.value = setTimeout(() => {
      controlsVisible.value = false
    }, 5000) // 5秒后隐藏
  }
}

// 开始隐藏计时器
const startHideTimer = () => {
  clearTimeout(hideTimer.value)

  if (isPlaying.value) {
    hideTimer.value = setTimeout(() => {
      controlsVisible.value = false
    }, 5000) // 5秒后隐藏
  }
}

// 点击进度条跳转
const seekToPosition = (event) => {
  if (!player.value || !progressBar.value) return

  const rect = progressBar.value.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const width = rect.width
  const percent = clickX / width

  if (totalDuration.value > 0) {
    const seekTime = totalDuration.value * percent
    player.value.currentTime(seekTime)
    currentTime.value = seekTime
  }
}

// 改变音量
const changeVolume = (event) => {
  if (!player.value || !volumeSlider.value) return

  const rect = volumeSlider.value.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const width = rect.width
  let newVolume = clickX / width

  // 限制音量在0-1之间
  newVolume = Math.max(0, Math.min(1, newVolume))

  volume.value = newVolume
  player.value.volume(newVolume)
  player.value.muted(newVolume === 0)
}

// 切换播放/暂停
const togglePlay = () => {
  if (!player.value) return

  if (player.value.paused()) {
    playVideo()
  } else {
    player.value.pause()
  }

  // 显示控制条
  showControls()
}

// 切换静音
const toggleMute = () => {
  if (!player.value) return

  player.value.muted(!player.value.muted())
  isMuted.value = player.value.muted()

  // 显示控制条
  showControls()
}

// 切换清晰度菜单
const toggleQualityMenu = () => {
  showQualityMenu.value = !showQualityMenu.value
  showPlaybackRateMenu.value = false

  // 显示控制条
  showControls()
}

// 切换播放速率菜单
const togglePlaybackRateMenu = () => {
  showPlaybackRateMenu.value = !showPlaybackRateMenu.value
  showQualityMenu.value = false

  // 显示控制条
  showControls()
}

// 改变清晰度
const changeQuality = (quality) => {
  if (currentQuality.value === quality) {
    showQualityMenu.value = false
    return
  }

  currentQuality.value = quality
  showQualityMenu.value = false
  loadVideoSource(quality)

  // 显示控制条
  showControls()
}

// 改变播放速率
const changePlaybackRate = (rate) => {
  if (currentPlaybackRate.value === rate) {
    showPlaybackRateMenu.value = false
    return
  }

  currentPlaybackRate.value = rate
  showPlaybackRateMenu.value = false

  if (player.value) {
    player.value.playbackRate(rate)
  }

  // 显示控制条
  showControls()
}

// 切换全屏
const toggleFullscreen = () => {
  if (!player.value) return

  if (!isFullscreen.value) {
    if (player.value.requestFullscreen) {
      player.value.requestFullscreen()
    }
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }

  // 显示控制条
  showControls()
}

// 降级清晰度
const degradeQuality = () => {
  const currentIndex = sortedQualities.value.findIndex((q) => q.quality === currentQuality.value)
  if (currentIndex < sortedQualities.value.length - 1) {
    const lowerQuality = sortedQualities.value[currentIndex + 1]
    currentQuality.value = lowerQuality.quality
    setTimeout(() => loadVideoSource(lowerQuality.quality), 500)
  }
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

// 重试
const retry = () => {
  error.value = ''
  loadVideoSource(currentQuality.value)
}

// 关闭播放器
const close = () => {
  emit('close')
}

// 格式化时间
const formatTime = (seconds) => {
  if (!seconds && seconds !== 0) return '00:00'

  const hrs = Math.floor(seconds / 3600)
  const mins = Math.floor((seconds % 3600) / 60)
  const secs = Math.floor(seconds % 60)

  if (hrs > 0) {
    return `${hrs.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs
      .toString()
      .padStart(2, '0')}`
  } else {
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
}

const heandleWrapperPause = (event) => {
  togglePlay()
}

// 键盘快捷键
const handleKeydown = (e) => {
  if (!props.visible) return

  switch (e.key) {
    case 'Escape':
      e.preventDefault()
      if (isFullscreen.value) {
        toggleFullscreen()
      } else {
        close()
      }
      break
    case ' ':
    case 'Spacebar':
      e.preventDefault()
      togglePlay()
      break
    case 'f':
    case 'F':
      e.preventDefault()
      toggleFullscreen()
      break
    case 'm':
    case 'M':
      e.preventDefault()
      toggleMute()
      break
    case 'ArrowLeft':
      e.preventDefault()
      if (player.value) {
        player.value.currentTime(Math.max(0, player.value.currentTime() - 5))
      }
      showControls()
      break
    case 'ArrowRight':
      e.preventDefault()
      if (player.value) {
        player.value.currentTime(Math.min(totalDuration.value, player.value.currentTime() + 5))
      }
      showControls()
      break
    case 'ArrowUp':
      e.preventDefault()
      if (player.value) {
        player.value.volume(Math.min(1, player.value.volume() + 0.1))
      }
      showControls()
      break
    case 'ArrowDown':
      e.preventDefault()
      if (player.value) {
        player.value.volume(Math.max(0, player.value.volume() - 0.1))
      }
      showControls()
      break
  }
}

// 监听visible变化
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      nextTick(() => {
        initPlayer()
      })
    } else {
      // 清理播放器
      if (player.value) {
        player.value.dispose()
        player.value = null
      }
      if (hls.value) {
        hls.value.destroy()
        hls.value = null
      }
      clearTimeout(hideTimer.value)
      document.removeEventListener('keydown', handleKeydown)
      isInitialized.value = false
    }
  }
)

// 监听videoData变化
watch(
  () => props.videoData,
  (newData) => {
    if (isInitialized.value && newData.qualities && newData.qualities.length > 0) {
      setInitialQuality()
    }
  },
  { deep: true }
)

// 组件卸载
onBeforeUnmount(() => {
  if (player.value) {
    player.value.dispose()
  }
  if (hls.value) {
    hls.value.destroy()
  }
  clearTimeout(hideTimer.value)
  document.removeEventListener('keydown', handleKeydown)
})

// 暴露方法给父组件
defineExpose({
  play: () => player.value?.play(),
  pause: () => player.value?.pause(),
  seek: (time) => {
    if (player.value) {
      player.value.currentTime(time)
    }
  },
})
</script>

<style scoped>
.video-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.video-modal-container {
  width: 90%;
  max-width: 1300px;
  background: #1a1a1a;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.3s ease;
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  position: relative;
}

@keyframes slideUp {
  from {
    transform: translateY(50px) scale(0.95);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #2a2a2a;
  border-bottom: 1px solid #3a3a3a;
  position: absolute;
  width: 100%;
  z-index: 40;
}

.video-title {
  color: #fff;
  font-size: 18px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-align: center;
  flex: 1;
}

.modal-close-btn {
  background: transparent;
  border: none;
  color: #fff;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  transition: background 0.2s;
  flex-shrink: 0;
  margin-left: 16px;
  cursor: pointer;
}

.modal-close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.modal-close-btn .close-icon {
  width: 20px;
  height: 20px;
  fill: #fff;
}

.video-content {
  position: relative;
  flex: 1;
  overflow: hidden;
}

.video-wrapper {
  width: 100%;
  height: 100%;
  min-height: 300px;
  background: #000;
}

.video-wrapper :deep(.video-js) {
  width: 100%;
  height: 100%;
  background: #000;
}

.video-wrapper :deep(.vjs-big-play-button) {
  display: none !important;
}

.center-play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 70px;
  height: 70px;
  background: rgba(0, 161, 214, 0.8);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 10;
}

.center-play-button:hover {
  background: rgba(0, 161, 214, 1);
  transform: translate(-50%, -50%) scale(1.1);
}

.center-play-button .play-icon {
  width: 40px;
  height: 40px;
  fill: #fff;
  margin-left: 4px;
}

.video-control-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  padding: 12px 20px;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  z-index: 20;
}

.video-modal-container:not(.controls-visible) .video-control-bar {
  transform: translateY(100%);
  opacity: 0;
}

.control-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.control-btn {
  background: transparent;
  border: none;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.control-btn svg {
  width: 24px;
  height: 24px;
  fill: #fff;
}

.play-pause-btn svg {
  width: 20px;
  height: 20px;
}

.time-display {
  color: #fff;
  font-size: 14px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  display: flex;
  align-items: center;
  gap: 2px;
}

.current-time {
  color: #fff;
}

.time-separator {
  color: rgba(255, 255, 255, 0.6);
  margin: 0 2px;
}

.duration {
  color: rgba(255, 255, 255, 0.8);
}

.control-center {
  flex: 1;
  padding: 0 20px;
}

.progress-bar {
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  cursor: pointer;
  position: relative;
}

.progress-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.progress-filled {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #00a1d6;
  border-radius: 2px;
  z-index: 1;
}

.progress-thumb {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background: #00a1d6;
  border-radius: 50%;
  z-index: 2;
  opacity: 0;
  transition: opacity 0.2s;
}

.progress-bar:hover .progress-thumb {
  opacity: 1;
}

.control-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.playback-rate-selector,
.quality-selector,
.volume-control {
  position: relative;
}

.control-btn .rate-text,
.control-btn .quality-text {
  font-size: 14px;
  margin-left: 4px;
  color: #fff;
}

.dropdown-menu {
  position: absolute;
  bottom: 100%;
  right: 0;
  background: rgba(0, 0, 0, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  min-width: 120px;
  margin-bottom: 8px;
  backdrop-filter: blur(10px);
  z-index: 30;
}

.rate-menu {
  min-width: 80px;
}

.dropdown-item {
  padding: 8px 16px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dropdown-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.dropdown-item.active {
  color: #00a1d6;
}

.quality-resolution {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

.volume-control {
  display: flex;
  align-items: center;
}

.volume-slider-container {
  position: absolute;
  bottom: 100%;
  right: 0;
  width: 80px;
  background: rgba(0, 0, 0, 0.9);
  border-radius: 4px;
  padding: 16px 8px;
  margin-bottom: 8px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.volume-control:hover .volume-slider-container {
  opacity: 1;
  visibility: visible;
}

.volume-slider {
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  cursor: pointer;
  position: relative;
}

.volume-track {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.volume-level {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #00a1d6;
  border-radius: 2px;
  z-index: 1;
}

.volume-thumb {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background: #00a1d6;
  border-radius: 50%;
  z-index: 2;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 15;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #00a1d6;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  color: #fff;
  font-size: 16px;
}

.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 15;
}

.error-content {
  text-align: center;
  color: #fff;
}

.error-icon {
  width: 60px;
  height: 60px;
  fill: #ff4757;
  margin-bottom: 20px;
}

.error-text {
  margin-bottom: 20px;
  max-width: 300px;
}

.retry-btn {
  background: #00a1d6;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 8px 20px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}

.retry-btn:hover {
  background: #0088b5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .video-modal-container {
    width: 95%;
    max-height: 80vh;
  }

  .modal-header {
    padding: 12px 16px;
  }

  .video-title {
    font-size: 16px;
  }

  .modal-close-btn {
    width: 28px;
    height: 28px;
  }

  .modal-close-btn .close-icon {
    width: 18px;
    height: 18px;
  }

  .video-control-bar {
    padding: 8px 12px;
  }

  .control-left {
    gap: 8px;
  }

  .control-center {
    padding: 0 12px;
  }

  .control-right {
    gap: 8px;
  }

  .control-btn svg {
    width: 20px;
    height: 20px;
  }

  .play-pause-btn svg {
    width: 18px;
    height: 18px;
  }

  .time-display {
    font-size: 12px;
  }

  .control-btn .rate-text,
  .control-btn .quality-text {
    font-size: 12px;
  }

  .dropdown-menu {
    min-width: 100px;
  }

  .dropdown-item {
    padding: 6px 12px;
    font-size: 12px;
  }

  .center-play-button {
    width: 60px;
    height: 60px;
  }

  .center-play-button .play-icon {
    width: 25px;
    height: 25px;
  }
}

/* 在视频区域添加固定的宽高比 */
.video-content {
  aspect-ratio: 16/9;
}
</style>
