<template>
  <div 
    class="douyin-video-player" 
    ref="playerContainer"
    @mousemove="showControls"
    @mouseleave="startHideTimer"
    @click="togglePlay"
  >
    <div class="video-stage" :style="videoStageStyle">
      <!-- 视频容器 -->
      <div class="video-container" ref="videoContainer"></div>

      <!-- 弹幕层 -->
      <DanmuLayer
        v-if="showDanmu"
        :danmu-list="danmuList"
        :current-time="currentTime"
        :is-playing="isPlaying"
        @send-danmu="handleSendDanmu"
      />

      <!-- 中央播放按钮（暂停时显示） -->
      <div class="center-play-button" v-if="!isPlaying && !loading && !error" @click.stop="togglePlay">
        <svg class="play-icon" viewBox="0 0 24 24">
          <path d="M8 5v14l11-7z" />
        </svg>
      </div>

      <!-- 视频信息区域（左下角） -->
      <div class="video-info" v-if="controlsVisible && videoData.author">
        <div class="author-info">
          <span class="author-name">@{{ videoData.author }}</span>
          <span class="publish-time">{{ formatPublishTime(videoData.createTime) }}</span>
        </div>
        <div class="video-description" v-if="videoData.description">
          {{ videoData.description }}
        </div>
      </div>

      <!-- 侧边栏操作区域 -->
      <div class="sidebar-actions" v-if="controlsVisible">
        <!-- 用户头像 -->
        <div class="user-avatar">
          <img src="https://picsum.photos/seed/avatar/40/40" alt="用户头像" />
        </div>
        
        <!-- 互动按钮 -->
        <div class="action-buttons">
          <button class="action-btn like-btn" @click.stop="handleLike">
            <svg class="action-icon" viewBox="0 0 24 24">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
            <span class="action-count">{{ videoData.likes || 0 }}</span>
          </button>
          
          <button class="action-btn comment-btn" @click.stop="handleComment">
            <svg class="action-icon" viewBox="0 0 24 24">
              <path d="M21.99 4c0-1.1-.89-2-1.99-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18zM18 14H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
            </svg>
            <span class="action-count">{{ videoData.comments || 0 }}</span>
          </button>
          
          <button class="action-btn share-btn" @click.stop="handleShare">
            <svg class="action-icon" viewBox="0 0 24 24">
              <path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92 1.61 0 2.92-1.31 2.92-2.92s-1.31-2.92-2.92-2.92z"/>
            </svg>
            <span class="action-count">{{ videoData.shares || 0 }}</span>
          </button>
          
          <button class="action-btn collect-btn" @click.stop="handleCollect">
            <svg class="action-icon" viewBox="0 0 24 24">
              <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
            </svg>
            <span class="action-count">{{ videoData.collects || 0 }}</span>
          </button>
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
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
          <p class="error-text">{{ error }}</p>
          <button class="retry-btn" @click.stop="retry">重试</button>
        </div>
      </div>
    </div>

    <!-- 底部控制栏 -->
    <div class="video-control-bar" v-if="controlsVisible" ref="controlBarRef">
      <!-- 第一行：进度条独占一行 -->
      <div class="progress-row">
        <div class="progress-bar" ref="progressBar" @click.stop="seekToPosition">
          <div class="progress-bg"></div>
          <div class="progress-filled" :style="{ width: progressPercent + '%' }"></div>
          <div class="progress-thumb" :style="{ left: progressPercent + '%' }"></div>
        </div>
      </div>

      <!-- 第二行：功能按钮 -->
      <div class="control-row">
        <!-- 左侧：播放/暂停 + 时间 -->
        <div class="control-left">
          <!-- 播放/暂停按钮 -->
          <button class="control-btn play-pause-btn" @click.stop="togglePlay">
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

        <!-- 中间：弹幕相关功能 -->
        <div class="control-center">
          <!-- 弹幕开关 -->
          <div class="danmu-switch-container" @click.stop>
            <span class="switch-label">弹幕</span>
            <ElSwitch
              v-model="showDanmu"
              :active-value="true"
              :inactive-value="false"
              @change="showControls"
              @click.native.stop
              size="small"
              style="--el-switch-on-color: #ff2d55; --el-switch-off-color: #666"
            />
          </div>

          <!-- 弹幕设置 -->
          <div class="danmu-settings" ref="danmuSettingsRef">
            <button class="control-btn settings-btn" @click.stop="toggleDanmuSettings">
              <svg class="settings-icon" viewBox="0 0 24 24">
                <path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"/>
              </svg>
              <span class="btn-text">弹幕设置</span>
            </button>

            <!-- 弹幕设置菜单 -->
            <div v-if="showDanmuSettings" class="danmu-settings-menu">
              <div class="settings-header">
                <span>弹幕设置</span>
                <button class="close-btn" @click.stop="closeDanmuSettings">
                  <svg class="close-icon" viewBox="0 0 24 24">
                    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                  </svg>
                </button>
              </div>
              <div class="settings-content">
                <div class="setting-item">
                  <label>字体大小</label>
                  <div class="slider-container">
                    <input 
                      type="range" 
                      min="12" 
                      max="24" 
                      step="2" 
                      v-model="danmuFontSize"
                      @input="updateDanmuSettings"
                    />
                    <span class="slider-value">{{ danmuFontSize }}px</span>
                  </div>
                </div>
                <div class="setting-item">
                  <label>弹幕透明度</label>
                  <div class="slider-container">
                    <input 
                      type="range" 
                      min="0.3" 
                      max="1" 
                      step="0.1" 
                      v-model="danmuOpacity"
                      @input="updateDanmuSettings"
                    />
                    <span class="slider-value">{{ Math.round(danmuOpacity * 100) }}%</span>
                  </div>
                </div>
                <div class="setting-item">
                  <label>弹幕速度</label>
                  <div class="slider-container">
                    <input 
                      type="range" 
                      min="0.5" 
                      max="2" 
                      step="0.1" 
                      v-model="danmuSpeed"
                      @input="updateDanmuSettings"
                    />
                    <span class="slider-value">{{ danmuSpeed }}x</span>
                  </div>
                </div>
                <div class="setting-item">
                  <label>显示区域</label>
                  <div class="radio-group">
                    <label class="radio-label">
                      <input 
                        type="radio" 
                        value="full" 
                        v-model="danmuArea"
                        @change="updateDanmuSettings"
                      />
                      全屏
                    </label>
                    <label class="radio-label">
                      <input 
                        type="radio" 
                        value="top" 
                        v-model="danmuArea"
                        @change="updateDanmuSettings"
                      />
                      顶部
                    </label>
                    <label class="radio-label">
                      <input 
                        type="radio" 
                        value="bottom" 
                        v-model="danmuArea"
                        @change="updateDanmuSettings"
                      />
                      底部
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 发送弹幕输入框（在弹幕设置右边） -->
          <div class="danmu-input-area">
            <div class="danmu-input-wrapper">
              <input
                ref="danmuInput"
                v-model="danmuInputText"
                type="text"
                placeholder="发个弹幕，开心一下~"
                maxlength="50"
                @keyup.enter="sendDanmuFromInput"
                @focus="showControls"
              />
              <button 
                class="send-danmu-btn" 
                @click.stop="sendDanmuFromInput"
                :disabled="!danmuInputText.trim()"
              >
                <svg class="send-icon" viewBox="0 0 24 24">
                  <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- 最右侧：其他功能按钮 -->
        <div class="control-right">
          <!-- 播放速率 -->
          <div class="playback-rate-selector">
            <button class="control-btn" @click.stop="togglePlaybackRateMenu">
              <svg class="speed-icon" viewBox="0 0 24 24">
                <path d="M13.5 4.5c0 1.1-.9 2-2 2s-2-.9-2-2 .9-2 2-2 2 .9 2 2zM10.5 22c-.55 0-1-.45-1-1v-6.14c-.31.11-.65.17-1 .17-1.1 0-2-.9-2-2V9c0-1.1.9-2 2-2 .35 0 .69.06 1 .17V5c0-1.1.9-2 2-2s2 .9 2 2v2.17c.31-.11.65-.17 1-.17 1.1 0 2 .9 2 2v4c0 1.1-.9 2-2 2-.35 0-.69-.06-1-.17V21c0 .55-.45 1-1 1s-1-.45-1-1v-6.14c-.31.11-.65.17-1 .17-1.1 0-2-.9-2-2V9c0-1.1.9-2 2-2 .35 0 .69.06 1 .17V21c0 .55-.45 1-1 1z"/>
              </svg>
              <span class="btn-text">{{ currentPlaybackRate }}x</span>
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
          <QualitySelector
            :qualities="sortedQualities"
            :current-quality="currentQuality"
            @change-quality="changeQuality"
          />

          <!-- 音量控制 -->
          <div class="volume-control">
            <button class="control-btn" @click.stop="toggleMute">
              <svg v-if="isMuted || volume === 0" class="volume-off-icon" viewBox="0 0 24 24">
                <path d="M16.5 12c0-1.77-1.02-3.29-2.5-4.03v2.21l2.45 2.45c.03-.2.05-.41.05-.63zm2.5 0c0 .94-.2 1.82-.54 2.64l1.51 1.51C20.63 14.91 21 13.5 21 12c0-4.28-2.99-7.86-7-8.77v2.06c2.89.86 5 3.54 5 6.71zM4.27 3L3 4.27 7.73 9H3v6h4l5 5v-6.73l4.25 4.25c-.67.52-1.42.93-2.25 1.18v2.06c1.38-.31 2.63-.95 3.69-1.81L19.73 21 21 19.73l-9-9L4.27 3zM12 4L9.91 6.09 12 8.18V4z"/>
              </svg>
              <svg v-else-if="volume <= 0.5" class="volume-low-icon" viewBox="0 0 24 24">
                <path d="M18.5 12c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM5 9v6h4l5 5V4L9 9H5z"/>
              </svg>
              <svg v-else class="volume-high-icon" viewBox="0 0 24 24">
                <path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z"/>
              </svg>
              <span class="btn-text">{{ Math.round(volume * 100) }}%</span>
            </button>

            <!-- 音量滑块（竖的） -->
            <div class="volume-slider-container vertical">
              <div class="volume-slider vertical" ref="volumeSlider" @click.stop="changeVolumeVertical">
                <div class="volume-track vertical"></div>
                <div
                  class="volume-level vertical"
                  :style="{ height: (isMuted ? 0 : volume) * 100 + '%' }"
                ></div>
                <div
                  class="volume-thumb vertical"
                  :style="{ bottom: (isMuted ? 0 : volume) * 100 + '%' }"
                ></div>
              </div>
            </div>
          </div>

          <!-- 全屏按钮 -->
          <button class="control-btn" @click.stop="toggleFullscreen">
            <svg v-if="isFullscreen" class="fullscreen-exit-icon" viewBox="0 0 24 24">
              <path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"/>
            </svg>
            <svg v-else class="fullscreen-icon" viewBox="0 0 24 24">
              <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
            </svg>
            <span class="btn-text">{{ isFullscreen ? '退出全屏' : '全屏' }}</span>
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import type { CSSProperties } from 'vue'
import Player from 'xgplayer'
import 'xgplayer/dist/index.min.css'
import { ElSwitch } from 'element-plus'
import 'element-plus/dist/index.css'
import DanmuLayer from './DanmuLayer.vue'
import QualitySelector from './QualitySelector.vue'
import { generateMockDanmu } from '@/utils/mockData'

// 定义props
const props = defineProps({
  videoData: {
    type: Object,
    default: () => ({
      videoId: null,
      title: '',
      duration: 0,
      qualities: [],
    }),
  },
  autoplay: {
    type: Boolean,
    default: true,
  },
})

const emit = defineEmits(['ready', 'play', 'pause', 'ended', 'error'])

// 响应式数据
const playerContainer = ref<HTMLElement | null>(null)
const videoContainer = ref<HTMLElement | null>(null)
const progressBar = ref<HTMLElement | null>(null)
const controlBarRef = ref<HTMLElement | null>(null)
const volumeSlider = ref<HTMLElement | null>(null)

const VIDEO_BOTTOM_GAP = 2
const CONTROL_BAR_VISUAL_OFFSET = 60
const videoStageStyle = ref<CSSProperties>({
  width: '100%',
  height: '100%',
})

let lastStageScale = 1
let containerResizeObserver: ResizeObserver | null = null
let replayTimeout: ReturnType<typeof setTimeout> | null = null

const player = ref<Player | null>(null)

// 播放状态
const isPlaying = ref(false)
const currentTime = ref(0)
const totalDuration = ref(0)
const progressPercent = ref(0)

// 控制条状态
const controlsVisible = ref(true)
const hideTimer = ref<number | null>(null)

// 弹幕相关
const showDanmu = ref(true)
const danmuList = ref<any[]>([])
const danmuInput = ref<HTMLInputElement | null>(null)
const danmuInputText = ref('')
const danmuSettingsRef = ref<HTMLElement | null>(null)

// 弹幕设置
const showDanmuSettings = ref(false)
const danmuFontSize = ref(16)
const danmuOpacity = ref(0.8)
const danmuSpeed = ref(1.0)
const danmuArea = ref('full')

// 播放速率相关
const playbackRates = [0.5, 0.75, 1.0, 1.25, 1.5, 2.0]
const currentPlaybackRate = ref(1.0)
const showPlaybackRateMenu = ref(false)

// 清晰度相关
const currentQuality = ref('')
const sortedQualities = computed(() => {
  const qualities = [...props.videoData.qualities]
  // 按清晰度排序（从高到低）
  const order: Record<string, number> = {
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

// 音量相关
const volume = ref(0.7)
const isMuted = ref(false)

// 全屏相关
const isFullscreen = ref(false)

// 加载和错误状态
const loading = ref(false)
const error = ref('')

// 网络监控
const bufferingCount = ref(0) // 缓冲次数统计
const lastBufferingTime = ref(0) // 上次缓冲时间
const networkQuality = ref<'good' | 'medium' | 'poor'>('good') // 网络质量
const autoDegradeEnabled = ref(true) // 是否启用自动降级

const clearReplayTimeout = () => {
  if (replayTimeout) {
    clearTimeout(replayTimeout)
    replayTimeout = null
  }
}

const scheduleAutoReplay = () => {
  clearReplayTimeout()
  if (typeof window === 'undefined') return
  replayTimeout = window.setTimeout(() => {
    if (player.value) {
      try {
        player.value.currentTime = 0
      } catch (e) {
        console.log('重置播放时间失败:', e)
      }
      player.value
        .play()
        .then(() => {
          isPlaying.value = true
        })
        .catch((e: any) => {
        console.log('自动重播失败:', e)
        isPlaying.value = false
      })
    }
  }, 2000)
}

const updateVideoStageSize = () => {
  const containerEl = playerContainer.value
  if (!containerEl) return

  const containerRect = containerEl.getBoundingClientRect()
  if (!containerRect.width || !containerRect.height) return

  let scale = lastStageScale

  let referenceTop: number | null = null
  if (progressBar.value) {
    referenceTop = progressBar.value.getBoundingClientRect().top
  } else if (controlBarRef.value) {
    referenceTop = controlBarRef.value.getBoundingClientRect().top + CONTROL_BAR_VISUAL_OFFSET
  }

  if (referenceTop !== null && referenceTop !== undefined) {
    const availableHeight = Math.max(referenceTop - containerRect.top - VIDEO_BOTTOM_GAP, 0)
    if (availableHeight > 0) {
      scale = Math.min(availableHeight / containerRect.height, 1)
      lastStageScale = scale
    }
  } else {
    scale = 1
    lastStageScale = 1
  }

  if (!Number.isFinite(scale) || scale <= 0) {
    scale = 1
    lastStageScale = 1
  }

  const stageHeight = containerRect.height * scale
  const stageWidth = containerRect.width * scale

  videoStageStyle.value = {
    width: `${stageWidth}px`,
    height: `${stageHeight}px`,
  }
}

// 初始化播放器
const initPlayer = () => {
  console.log('初始化抖音风格播放器')

  if (!videoContainer.value) {
    console.error('视频容器未找到')
    return
  }

  loading.value = true
  error.value = ''
  controlsVisible.value = true
  clearReplayTimeout()
  
  // 预加载优化：如果之前有播放器，先暂停并清理
  if (player.value) {
    try {
      player.value.pause()
      // 不立即销毁，先尝试重用
    } catch (e) {
      console.log('暂停旧播放器失败:', e)
    }
  }

  try {
    // 销毁现有的播放器
    if (player.value) {
      console.log('销毁现有播放器')
      player.value.destroy()
      player.value = null
    }

    // 设置总时长
    totalDuration.value = props.videoData.duration || 0
    console.log('视频时长:', totalDuration.value)

    // 初始化XGPlayer播放器
    player.value = new Player({
      el: videoContainer.value,
      url: getCurrentVideoUrl(),
      width: '100%',
      height: '100%',
      autoplay: props.autoplay,
      muted: isMuted.value,
      volume: volume.value,
      fitVideoSize: 'contain',
      controls: false, // 使用自定义控制栏
      playbackRate: [0.5, 0.75, 1.0, 1.25, 1.5, 2.0],
      lang: 'zh-cn',
      cssFullscreen: true,
      // 添加加载和错误处理
      playsinline: true,
      preloadTime: 30, // 预加载30秒
      closeVideoClick: true,
      closeVideoTouch: true,
      // 缓冲优化配置
      bufferSize: 1024 * 1024 * 50, // 50MB缓冲区
      loadNextBuffer: true, // 加载下一个缓冲区
      startTime: 0,
      // 网络优化
      maxBufferLength: 30, // 最大缓冲长度30秒
      minBufferLength: 5,  // 最小缓冲长度5秒
      bufferBehind: 10,    // 向后缓冲10秒
      // 添加就绪回调 - 只设置一次，避免重复设置
      ready: () => {
        console.log('XGPlayer播放器就绪')
        // 这里不设置 loading.value = false，由 canplay 事件处理
      },
      // 添加错误处理
      error: (err: any) => {
        console.error('XGPlayer播放错误:', err)
        loading.value = false
        
        // 尝试切换到备用源
        if (currentBackupIndex.value < backupVideoSources.length - 1) {
          error.value = '视频加载失败，正在尝试备用源...'
          setTimeout(() => {
            switchToNextBackupSource()
          }, 2000)
        } else {
          error.value = '视频加载失败，请检查网络连接'
        }
      }
    })

    // 添加额外的事件监听，确保加载状态正确更新
    if (player.value) {
      // 先移除可能存在的旧事件监听器
      // XGPlayer的off方法需要事件类型和回调函数，这里我们直接重新绑定
      // 旧的事件监听器会在player.destroy()时自动清理
      
      player.value.on('canplay', () => {
        console.log('视频可以播放')
        loading.value = false
      })

      player.value.on('waiting', () => {
        console.log('视频等待加载')
        loading.value = true
        
        // 监控缓冲频率
        const now = Date.now()
        bufferingCount.value++
        
        // 如果10秒内缓冲超过3次，网络质量较差
        if (now - lastBufferingTime.value < 10000) {
          if (bufferingCount.value >= 3) {
            networkQuality.value = 'poor'
            console.log('网络质量较差，考虑降级清晰度')
            
            // 如果启用自动降级且当前不是最低清晰度
            if (autoDegradeEnabled.value && sortedQualities.value.length > 1) {
              const currentIndex = sortedQualities.value.findIndex(q => q.url === getCurrentVideoUrl())
              if (currentIndex > 0) { // 不是最低清晰度
                setTimeout(() => {
                  degradeQuality()
                }, 2000) // 2秒后尝试降级
              }
            }
          } else if (bufferingCount.value >= 2) {
            networkQuality.value = 'medium'
          }
        } else {
          // 重置计数
          bufferingCount.value = 1
        }
        
        lastBufferingTime.value = now
      })

      player.value.on('playing', () => {
        console.log('视频开始播放')
        loading.value = false
        
        // 监控网络恢复
        if (networkQuality.value === 'poor') {
          // 如果连续播放30秒没有缓冲，网络可能恢复了
          setTimeout(() => {
            if (isPlaying.value && !loading.value && networkQuality.value === 'poor') {
              console.log('网络可能已恢复，重置网络质量')
              networkQuality.value = 'good'
              bufferingCount.value = 0
            }
          }, 30000)
        }
      })

      // 设置超时，防止一直显示加载中
      const loadingTimeout = setTimeout(() => {
        if (loading.value) {
          console.log('视频加载超时')
          loading.value = false
          error.value = '视频加载超时，请检查网络或视频地址'
        }
      }, 5000) // 减少到5秒超时，更快反馈
    }

    // 设置初始清晰度
    setInitialQuality()

    // 设置事件监听
    setupPlayerEvents()

    // 开始隐藏控制条的计时器
    startHideTimer()

    // 生成模拟弹幕数据
    generateDanmuData()
    
    console.log('抖音风格播放器初始化完成')
    nextTick(() => updateVideoStageSize())
  } catch (err: any) {
    console.error('播放器初始化失败:', err)
    error.value = `播放器初始化失败: ${err.message}`
    loading.value = false
  }
}

// 备用视频源列表（当主源加载失败时使用）- 使用更快的视频源
const backupVideoSources = [
  'https://vjs.zencdn.net/v/oceans.mp4', // 小文件，快速加载
  'https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4', // 较短视频，快速加载
  'https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', // 较短视频，快速加载
  'https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', // 较短视频
  'https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4', // 较短视频
  'https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', // 长视频，放在后面
  'https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', // 长视频，放在后面
]

// 当前使用的备用源索引
const currentBackupIndex = ref(0)

// 获取当前视频URL
const getCurrentVideoUrl = () => {
  if (sortedQualities.value.length === 0) {
    console.warn('没有可用的清晰度数据，使用备用源')
    // 返回备用视频URL
    return backupVideoSources[currentBackupIndex.value % backupVideoSources.length]
  }

  const qualityData = sortedQualities.value.find((q) => q.quality === currentQuality.value)
  if (!qualityData) {
    console.warn('未找到当前清晰度，使用默认清晰度:', sortedQualities.value[0].quality)
    const defaultUrl = sortedQualities.value[0].url || backupVideoSources[currentBackupIndex.value % backupVideoSources.length]
    return defaultUrl
  }

  const url = qualityData.url || backupVideoSources[currentBackupIndex.value % backupVideoSources.length]
  console.log('当前视频URL:', url)
  return url
}

// 切换到下一个备用源
const switchToNextBackupSource = () => {
  currentBackupIndex.value = (currentBackupIndex.value + 1) % backupVideoSources.length
  console.log('切换到备用视频源:', backupVideoSources[currentBackupIndex.value])
  
  // 重新初始化播放器
  if (player.value) {
    player.value.destroy()
    player.value = null
    setTimeout(() => {
      initPlayer()
    }, 500)
  }
}

// 设置初始清晰度
const setInitialQuality = () => {
  if (sortedQualities.value.length > 0) {
    // 默认选择最高清晰度
    const defaultQuality = sortedQualities.value[0].quality
    currentQuality.value = defaultQuality
  }
}

// 生成弹幕数据
const generateDanmuData = () => {
  danmuList.value = generateMockDanmu(props.videoData.videoId, totalDuration.value)
}

// 设置播放器事件
const setupPlayerEvents = () => {
  if (!player.value) return

  player.value.on('play', () => {
    clearReplayTimeout()
    isPlaying.value = true
    emit('play')
  })

  player.value.on('pause', () => {
    clearReplayTimeout()
    isPlaying.value = false
    emit('pause')
  })

  player.value.on('ended', () => {
    isPlaying.value = false
    emit('ended')
    scheduleAutoReplay()
  })

  player.value.on('error', (err: any) => {
    console.error('视频播放错误:', err)
    error.value = getErrorMessage(err)
    emit('error', err)
  })

  player.value.on('timeupdate', () => {
    if (player.value) {
      currentTime.value = player.value.currentTime
      totalDuration.value = player.value.duration || props.videoData.duration || 0

      if (totalDuration.value > 0) {
        progressPercent.value = (currentTime.value / totalDuration.value) * 100
      }
    }
  })

  player.value.on('volumechange', () => {
    if (player.value) {
      volume.value = player.value.volume
      isMuted.value = player.value.muted
    }
  })

  player.value.on('fullscreenchange', (isFull: boolean) => {
    isFullscreen.value = isFull
    nextTick(() => updateVideoStageSize())
  })

  // 缓冲监控
  player.value.on('progress', () => {
    if (player.value) {
      const buffered = player.value.buffered
      if (buffered && buffered.length > 0) {
        const bufferedEnd = buffered.end(buffered.length - 1)
        const bufferedPercent = (bufferedEnd / totalDuration.value) * 100
        
        // 只在调试时显示
        if (bufferedPercent % 10 < 1) { // 每10%显示一次
          console.log(`缓冲进度: ${bufferedPercent.toFixed(1)}% (${bufferedEnd.toFixed(1)}/${totalDuration.value})`)
        }
        
        // 智能预加载策略
        const bufferAhead = bufferedEnd - currentTime.value
        
        if (bufferAhead < 5) { // 缓冲不足5秒
          console.log('缓冲不足，剩余缓冲:', bufferAhead.toFixed(1), '秒')
          
          // 如果网络质量好，尝试预加载更多
          if (networkQuality.value === 'good' && player.value.video) {
            // 尝试预加载下一段
            try {
              // XGPlayer 可能不支持直接控制预加载，这里只是记录
              console.log('尝试预加载更多数据...')
            } catch (e) {
              console.log('预加载失败:', e)
            }
          }
        }
      }
    }
  })

  // 监听键盘事件
  document.addEventListener('keydown', handleKeydown)
}

// 显示控制条
const showControls = () => {
  const wasHidden = !controlsVisible.value
  controlsVisible.value = true
  if (hideTimer.value) {
    clearTimeout(hideTimer.value)
  }

  // 如果不是播放状态，保持显示
  if (isPlaying.value) {
    hideTimer.value = setTimeout(() => {
      controlsVisible.value = false
    }, 5000) // 5秒后隐藏
  }

  if (wasHidden) {
    nextTick(() => updateVideoStageSize())
  }
}

// 开始隐藏计时器
const startHideTimer = () => {
  if (hideTimer.value) {
    clearTimeout(hideTimer.value)
  }

  if (isPlaying.value) {
    hideTimer.value = setTimeout(() => {
      controlsVisible.value = false
    }, 5000) // 5秒后隐藏
  }
}

// 点击进度条跳转
const seekToPosition = (event: MouseEvent) => {
  if (!player.value || !progressBar.value) return

  const rect = progressBar.value.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const width = rect.width
  const percent = clickX / width

  if (totalDuration.value > 0) {
    const seekTime = totalDuration.value * percent
    player.value.currentTime = seekTime
    currentTime.value = seekTime
  }
}

// 改变音量（横的）
const changeVolume = (event: MouseEvent) => {
  if (!player.value || !volumeSlider.value) return

  const rect = volumeSlider.value.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const width = rect.width
  let newVolume = clickX / width

  // 限制音量在0-1之间
  newVolume = Math.max(0, Math.min(1, newVolume))

  volume.value = newVolume
  player.value.volume = newVolume
  player.value.muted = newVolume === 0
}

// 改变音量（竖的）
const changeVolumeVertical = (event: MouseEvent) => {
  if (!player.value || !volumeSlider.value) return

  const rect = volumeSlider.value.getBoundingClientRect()
  const clickY = event.clientY - rect.top
  const height = rect.height
  let newVolume = 1 - (clickY / height) // 从下往上增加

  // 限制音量在0-1之间
  newVolume = Math.max(0, Math.min(1, newVolume))

  volume.value = newVolume
  player.value.volume = newVolume
  player.value.muted = newVolume === 0
}

// 切换播放/暂停
const togglePlay = () => {
  console.log('togglePlay called, player exists:', !!player.value)
  if (!player.value) return

  console.log('player paused:', player.value.paused)
  if (player.value.paused) {
    player.value.play()
  } else {
    player.value.pause()
  }

  // 显示控制条
  showControls()
}

// 切换静音
const toggleMute = () => {
  if (!player.value) return

  player.value.muted = !player.value.muted
  isMuted.value = player.value.muted

  // 显示控制条
  showControls()
}

// 切换弹幕
const toggleDanmu = () => {
  showDanmu.value = !showDanmu.value
  showControls()
}

// 切换弹幕设置菜单
const toggleDanmuSettings = () => {
  showDanmuSettings.value = !showDanmuSettings.value
  showPlaybackRateMenu.value = false
  showControls()
}

// 关闭弹幕设置菜单
const closeDanmuSettings = () => {
  showDanmuSettings.value = false
}

// 更新弹幕设置
const updateDanmuSettings = () => {
  // 这里可以发送弹幕设置到弹幕组件
  console.log('弹幕设置更新:', {
    fontSize: danmuFontSize.value,
    opacity: danmuOpacity.value,
    speed: danmuSpeed.value,
    area: danmuArea.value
  })
  // 实际应用中可以通过事件或状态管理传递给弹幕组件
}

// 切换播放速率菜单
const togglePlaybackRateMenu = () => {
  showPlaybackRateMenu.value = !showPlaybackRateMenu.value
  showDanmuSettings.value = false
  showControls()
}

// 改变播放速率
const changePlaybackRate = (rate: number) => {
  if (currentPlaybackRate.value === rate) {
    showPlaybackRateMenu.value = false
    return
  }

  currentPlaybackRate.value = rate
  showPlaybackRateMenu.value = false

  if (player.value) {
    player.value.playbackRate = rate
  }

  showControls()
}

// 从输入框发送弹幕
const sendDanmuFromInput = () => {
  const content = danmuInputText.value.trim()
  if (!content) return

  handleSendDanmu(content)
  danmuInputText.value = ''
  
  // 聚焦到输入框以便继续输入
  nextTick(() => {
    danmuInput.value?.focus()
  })
}

// 改变清晰度
const changeQuality = (quality: string) => {
  if (currentQuality.value === quality) {
    return
  }

  currentQuality.value = quality
  showControls()

  // 重新加载视频
  if (player.value) {
    loading.value = true
    const currentTime = player.value.currentTime
    const wasPlaying = !player.value.paused

    player.value.src = getCurrentVideoUrl()
    player.value.currentTime = currentTime

    if (wasPlaying) {
        player.value.play().catch((e: any) => {
          console.log('切换清晰度后播放失败:', e)
          isPlaying.value = false
        })
    }
  }
}

// 自动降级清晰度
const degradeQuality = () => {
  if (sortedQualities.value.length <= 1) {
    console.log('没有更低清晰度可降级')
    return
  }
  
  const currentUrl = getCurrentVideoUrl()
  const currentIndex = sortedQualities.value.findIndex(q => q.url === currentUrl)
  
  if (currentIndex > 0) { // 不是最低清晰度
    const lowerQuality = sortedQualities.value[currentIndex - 1]
    console.log(`网络质量较差，自动降级到: ${lowerQuality.label}`)
    
    // 显示提示
    error.value = `网络不稳定，已自动切换到${lowerQuality.label}清晰度`
    setTimeout(() => {
      error.value = ''
    }, 3000)
    
    // 切换到低清晰度
    changeQuality(lowerQuality.label)
  }
}

// 切换全屏
const toggleFullscreen = () => {
  if (!player.value) return

  if (!isFullscreen.value) {
    // XGPlayer 全屏控制 - 使用原生HTML元素
    const videoElement = player.value?.video
    if (videoElement && 'requestFullscreen' in videoElement) {
      (videoElement as HTMLElement).requestFullscreen()
    }
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }

  // 显示控制条
  showControls()
}

// 获取错误信息
const getErrorMessage = (error: any) => {
  if (!error) return '视频播放出错'

  if (error.code) {
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

  return error.message || '视频播放出错'
}

// 重试
const retry = () => {
  error.value = ''
  initPlayer()
}

// 处理发送弹幕
const handleSendDanmu = (content: string) => {
  const newDanmu = {
    id: Date.now(),
    content,
    time: currentTime.value,
    color: '#ffffff',
    type: 'normal',
    userId: 'user_' + Math.floor(Math.random() * 1000),
    userName: '用户' + Math.floor(Math.random() * 1000),
  }
  
  danmuList.value.push(newDanmu)
  showControls()
}

// 格式化时间
const formatTime = (seconds: number) => {
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

// 格式化发布时间
const formatPublishTime = (timeString?: string) => {
  if (!timeString) return ''
  
  try {
    const publishTime = new Date(timeString)
    const now = new Date()
    const diffMs = now.getTime() - publishTime.getTime()
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
    
    if (diffDays === 0) {
      return '今天发布'
    } else if (diffDays === 1) {
      return '昨天发布'
    } else if (diffDays < 7) {
      return `${diffDays}天前发布`
    } else if (diffDays < 30) {
      const weeks = Math.floor(diffDays / 7)
      return `${weeks}周前发布`
    } else if (diffDays < 365) {
      const months = Math.floor(diffDays / 30)
      return `${months}个月前发布`
    } else {
      const years = Math.floor(diffDays / 365)
      return `${years}年前发布`
    }
  } catch (error) {
    return timeString
  }
}

// 键盘快捷键
const handleKeydown = (e: KeyboardEvent) => {
  switch (e.key) {
    case 'Escape':
      e.preventDefault()
      if (isFullscreen.value) {
        toggleFullscreen()
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
    case 'd':
    case 'D':
      e.preventDefault()
      toggleDanmu()
      break
    case 'ArrowLeft':
      e.preventDefault()
      if (player.value) {
        player.value.currentTime = Math.max(0, player.value.currentTime - 5)
      }
      showControls()
      break
    case 'ArrowRight':
      e.preventDefault()
      if (player.value) {
        player.value.currentTime = Math.min(totalDuration.value, player.value.currentTime + 5)
      }
      showControls()
      break
    case 'ArrowUp':
      e.preventDefault()
      if (player.value) {
        player.value.volume = Math.min(1, player.value.volume + 0.1)
      }
      showControls()
      break
    case 'ArrowDown':
      e.preventDefault()
      if (player.value) {
        player.value.volume = Math.max(0, player.value.volume - 0.1)
      }
      showControls()
      break
  }
}

// 监听videoData变化 - 优化：只有当视频ID变化时才重新初始化
watch(
  () => props.videoData?.videoId,
  (newVideoId, oldVideoId) => {
    if (newVideoId !== oldVideoId) {
      console.log('视频ID变化，重新初始化播放器:', oldVideoId, '->', newVideoId)
      if (props.videoData.qualities && props.videoData.qualities.length > 0) {
        setInitialQuality()
        initPlayer()
      }
    }
  }
)

// 监听autoplay变化
watch(
  () => props.autoplay,
  (newVal) => {
    if (player.value) {
    if (newVal && !isPlaying.value) {
      player.value.play().catch((e: any) => console.log('自动播放失败:', e))
    }
    }
  }
)

watch(
  () => progressBar.value,
  (bar) => {
    if (bar) {
      nextTick(() => updateVideoStageSize())
    }
  }
)

watch(
  () => controlBarRef.value,
  (bar) => {
    if (bar) {
      nextTick(() => updateVideoStageSize())
    }
  }
)

watch(
  () => controlsVisible.value,
  () => {
    nextTick(() => updateVideoStageSize())
  }
)

// 点击外部关闭菜单
const handleClickOutside = (event: MouseEvent) => {
  // 关闭弹幕设置菜单
  if (showDanmuSettings.value && danmuSettingsRef.value && !danmuSettingsRef.value.contains(event.target as Node)) {
    closeDanmuSettings()
  }
  
  // 关闭播放速率菜单
  if (showPlaybackRateMenu.value) {
    showPlaybackRateMenu.value = false
  }
}

// 处理点赞
const handleLike = () => {
  console.log('点赞视频:', props.videoData.videoId)
  // 这里可以调用API进行点赞
}

// 处理评论
const handleComment = () => {
  console.log('评论视频:', props.videoData.videoId)
  // 这里可以打开评论面板
}

// 处理分享
const handleShare = () => {
  console.log('分享视频:', props.videoData.videoId)
  // 这里可以打开分享面板
}

// 处理收藏
const handleCollect = () => {
  console.log('收藏视频:', props.videoData.videoId)
  // 这里可以调用API进行收藏
}

// 组件挂载
onMounted(() => {
  nextTick(() => {
    initPlayer()
    updateVideoStageSize()

    if (playerContainer.value && typeof ResizeObserver !== 'undefined') {
      containerResizeObserver = new ResizeObserver(() => {
        updateVideoStageSize()
      })
      containerResizeObserver.observe(playerContainer.value)
    }
  })
  
  // 添加点击外部关闭菜单的监听
  document.addEventListener('click', handleClickOutside)
  if (typeof window !== 'undefined') {
    window.addEventListener('resize', updateVideoStageSize)
  }
})

// 组件卸载
onBeforeUnmount(() => {
  if (player.value) {
    player.value.destroy()
  }
  if (hideTimer.value) {
    clearTimeout(hideTimer.value)
  }
  clearReplayTimeout()
  document.removeEventListener('keydown', handleKeydown)
  document.removeEventListener('click', handleClickOutside)
  if (typeof window !== 'undefined') {
    window.removeEventListener('resize', updateVideoStageSize)
  }
  if (containerResizeObserver) {
    containerResizeObserver.disconnect()
    containerResizeObserver = null
  }
})

// 暴露方法给父组件
defineExpose({
  play: () => player.value?.play(),
  pause: () => player.value?.pause(),
  seek: (time: number) => {
    if (player.value) {
      player.value.currentTime = time
    }
  },
  toggleDanmu: () => toggleDanmu(),
  toggleFullscreen: () => toggleFullscreen(),
})
</script>

<style scoped>
.douyin-video-player {
  position: relative;
  width: 100%;
  height: 100%;
  background: #000;
  overflow: hidden;
  cursor: pointer;
}

.video-stage {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  z-index: 5;
  max-width: 100%;
  max-height: 100%;
  transition: width 0.2s ease, height 0.2s ease;
}

.video-container {
  width: 100%;
  height: 100%;
  background: #000;
}

.video-container :deep(.xgplayer) {
  width: 100%;
  height: 100%;
  background: #000;
}

/* 隐藏XGPlayer默认的加载指示器 */
.video-container :deep(.xgplayer-loading),
.video-container :deep(.xgplayer-spinner),
.video-container :deep(.xgplayer-poster),
.video-container :deep(.xgplayer-start) {
  display: none !important;
}

/* 隐藏XGPlayer默认的控制栏 */
.video-container :deep(.xgplayer-controls) {
  display: none !important;
}

.center-play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 70px;
  height: 70px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 10;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.center-play-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%) scale(1.1);
}

.center-play-button .play-icon {
  width: 30px;
  height: 30px;
  fill: #fff;
  margin-left: 4px;
}

/* 视频信息区域 */
.video-info {
  position: absolute;
  bottom: 20px; /* 向下移动更多 */
  left: 0px;
  max-width: 70%;
  color: #fff;
  z-index: 20;
  /* 移除背景色和边框 */
  padding: 0;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px; /* 增加间距 */
}

.author-name {
  font-size: 25px; /* 增大字体 */
  font-weight: 700; /* 加粗 */
  color: #fff;
}

.publish-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.video-description {
  font-size: 18px; /* 增大字体 */
  line-height: 1.6; /* 增加行高 */
  color: rgba(255, 255, 255, 0.95);
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-top: 6px; /* 增加上边距 */
}

/* 底部控制栏 */
.video-control-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.9));
  padding: 0 20px 10px;
  transition: all 0.3s ease;
  z-index: 20;
}

/* 进度条行 */
.progress-row {
  margin-bottom: 6px;
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
  background: #ff2d55;
  border-radius: 2px;
  z-index: 1;
}

.progress-thumb {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background: #ff2d55;
  border-radius: 50%;
  z-index: 2;
  opacity: 0;
  transition: opacity 0.2s;
}

.progress-bar:hover .progress-thumb {
  opacity: 1;
}

/* 控制行 */
.control-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
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
  padding: 4px 6px;
  border-radius: 6px;
  transition: all 0.2s;
  gap: 4px;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.control-btn svg {
  width: 18px;
  height: 18px;
  fill: #fff;
}

.btn-text {
  font-size: 11px;
  color: #fff;
  white-space: nowrap;
}

.play-pause-btn svg {
  width: 18px;
  height: 18px;
}

.time-display {
  color: #fff;
  font-size: 13px;
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

/* 中间区域：弹幕相关 */
.control-center {
  display: flex;
  align-items: center;
  gap: 12px; /* 增加间距 */
}

/* 弹幕开关容器 */
.danmu-switch-container {
  display: flex;
  align-items: center;
  gap: 6px;
}

.switch-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  white-space: nowrap;
}

.settings-btn {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 弹幕设置 */
.danmu-settings {
  position: relative;
}

.danmu-settings-menu {
  position: absolute;
  bottom: 100%;
  left: 0;
  background: rgba(0, 0, 0, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  min-width: 240px;
  margin-bottom: 8px;
  backdrop-filter: blur(20px);
  z-index: 1000;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  overflow: hidden;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.close-btn {
  background: transparent;
  border: none;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.close-icon {
  width: 16px;
  height: 16px;
  fill: rgba(255, 255, 255, 0.7);
}

.settings-content {
  padding: 12px 16px;
}

.setting-item {
  margin-bottom: 16px;
}

.setting-item label {
  display: block;
  color: rgba(255, 255, 255, 0.9);
  font-size: 13px;
  margin-bottom: 6px;
}

.slider-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.slider-container input[type="range"] {
  flex: 1;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  outline: none;
  -webkit-appearance: none;
}

.slider-container input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 16px;
  height: 16px;
  background: #ff2d55;
  border-radius: 50%;
  cursor: pointer;
}

.slider-value {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  min-width: 40px;
  text-align: right;
}

.radio-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 4px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  cursor: pointer;
}

.radio-label input[type="radio"] {
  margin: 0;
}

/* 弹幕输入区域 */
.danmu-input-area {
  flex: 1 1 auto;      /* 允许放大和缩小 */
  max-width: 650px;
  width: 500px;
  min-width: 300px;
}

.danmu-input-wrapper {
  display: flex;
  align-items: center;
  background: rgba(0, 0, 0, 0.8);
  border-radius: 20px;
  padding: 4px 10px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  /* margin-left 已移到父元素 */
}

.danmu-input-wrapper input {
  flex: 1;
  background: transparent;
  border: none;
  color: #fff;
  font-size: 12px;
  padding: 2px 10px;
  outline: none;
  width: 100%;
}

.danmu-input-wrapper input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.send-danmu-btn {
  background: #ff2d55;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.send-danmu-btn:disabled {
  background: rgba(255, 255, 255, 0.2);
  cursor: not-allowed;
}

.send-danmu-btn:not(:disabled):hover {
  background: #ff1a45;
  transform: scale(1.1);
}

.send-icon {
  width: 16px;
  height: 16px;
  fill: #fff;
}

/* 右侧功能区域 */
.control-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.playback-rate-selector,
.volume-control {
  position: relative;
}

.dropdown-menu {
  position: absolute;
  bottom: 100%;
  right: 0;
  background: rgba(0, 0, 0, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  min-width: 80px;
  margin-bottom: 8px;
  backdrop-filter: blur(10px);
  z-index: 30;
}

.rate-menu {
  min-width: 60px;
}

.dropdown-item {
  padding: 8px 12px;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.dropdown-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.dropdown-item.active {
  color: #ff2d55;
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

.volume-slider-container.vertical {
  width: 40px;
  height: 120px;
  padding: 8px 16px;
  margin-bottom: 8px;
  right: 50%;
  transform: translateX(50%);
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

.volume-slider.vertical {
  width: 4px;
  height: 100px;
  margin: 0 auto;
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

.volume-track.vertical {
  width: 100%;
  height: 100%;
}

.volume-level {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #ff2d55;
  border-radius: 2px;
  z-index: 1;
}

.volume-level.vertical {
  width: 100%;
  height: 0%;
  bottom: 0;
  top: auto;
}

.volume-thumb {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background: #ff2d55;
  border-radius: 50%;
  z-index: 2;
}

.volume-thumb.vertical {
  left: 50%;
  top: auto;
  bottom: 0%;
  transform: translate(-50%, 50%);
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
  border-top-color: #ff2d55;
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
  background: #ff2d55;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 8px 20px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}

.retry-btn:hover {
  background: #ff1a45;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .video-info {
    bottom: 190px;
    left: 15px;
    max-width: 80%;
    padding: 10px 14px;
  }
  
  .author-name {
    font-size: 15px;
  }
  
  .video-description {
    font-size: 13px;
  }
  
  .danmu-input-area {
    max-width: 250px;
  }
}

@media (max-width: 768px) {
  .video-control-bar {
    padding: 10px 15px;
  }
  
  .video-info {
    bottom: 210px;
    left: 10px;
    max-width: 85%;
    padding: 8px 12px;
  }
  
  .author-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .author-name {
    font-size: 14px;
  }
  
  .publish-time {
    font-size: 11px;
  }
  
  .video-description {
    font-size: 12px;
    -webkit-line-clamp: 1;
  }
  
  .control-row {
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .control-left,
  .control-center,
  .danmu-input-area,
  .control-right {
    flex: 1 1 auto;
  }
  
  .danmu-input-area {
    order: 1;
    max-width: 100%;
    margin-top: 8px;
  }
  
  .control-left,
  .control-center,
  .control-right {
    order: 2;
  }
  
  .control-btn {
    padding: 4px 6px;
  }
  
  .btn-text {
    font-size: 11px;
  }
  
  .control-btn svg {
    width: 18px;
    height: 18px;
  }
  
  .play-pause-btn svg {
    width: 16px;
    height: 16px;
  }
  
  .time-display {
    font-size: 11px;
  }
  
  .danmu-input-wrapper {
    padding: 4px 10px;
  }
  
  .danmu-input-wrapper input {
    font-size: 12px;
  }
  
  .send-danmu-btn {
    width: 24px;
    height: 24px;
  }
  
  .send-icon {
    width: 14px;
    height: 14px;
  }
  
  .danmu-settings-menu {
    min-width: 200px;
    left: -80px;
  }
  
  .center-play-button {
    width: 50px;
    height: 50px;
  }
  
  .center-play-button .play-icon {
    width: 20px;
    height: 20px;
  }
}

@media (max-width: 480px) {
  .video-info {
    bottom: 230px;
    max-width: 90%;
  }
  
  .control-row {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .control-left,
  .control-center,
  .control-right {
    display: flex;
    justify-content: space-between;
    width: 100%;
  }
  
  .danmu-input-area {
    margin-top: 0;
  }
  
  .danmu-settings-menu {
    min-width: 180px;
    left: -60px;
  }
  
  .settings-content {
    padding: 10px 12px;
  }
  
  .setting-item {
    margin-bottom: 12px;
  }
  
  .setting-item label {
    font-size: 12px;
  }
  
  .slider-value {
    font-size: 11px;
  }
  
  .radio-group {
    gap: 8px;
  }
  
  .radio-label {
    font-size: 11px;
  }
}

/* 抖音风格优化 */
.douyin-video-player {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.progress-filled {
  background: linear-gradient(90deg, #ff2d55, #ff6b9d);
}

.volume-level {
  background: linear-gradient(90deg, #ff2d55, #ff6b9d);
}

.retry-btn {
  background: linear-gradient(135deg, #ff2d55, #ff6b9d);
}

.retry-btn:hover {
  background: linear-gradient(135deg, #ff1a45, #ff5a8c);
}

/* 侧边栏操作区域 */
.sidebar-actions {
  position: absolute;
  right: 30px;
  bottom: 70px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  z-index: 100;
}

.user-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid rgba(255, 255, 255, 0.35);
  cursor: pointer;
  transition: all 0.3s;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar:hover {
  transform: scale(1.1);
  border-color: rgba(255, 255, 255, 0.6);
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.action-btn {
  background: transparent;
  border: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 10px;
  border-radius: 10px;
  transition: all 0.2s;
  min-width: 72px;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.action-icon {
  width: 32px;
  height: 32px;
  fill: #fff;
}

.action-count {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
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
  fill: #ffcc00;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-actions {
    right: 12px;
    bottom: 95px;
    gap: 16px;
  }
  
  .user-avatar {
    width: 48px;
    height: 48px;
  }
  
  .action-buttons {
    gap: 12px;
  }
  
  .action-btn {
    padding: 6px;
  }
  
  .action-icon {
    width: 24px;
    height: 24px;
  }
  
  .action-count {
    font-size: 12px;
  }
}
</style>
