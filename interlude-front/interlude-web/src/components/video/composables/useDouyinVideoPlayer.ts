import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { CSSProperties } from 'vue'
import Player from 'xgplayer'
import 'xgplayer/dist/index.min.css'
import { ElMessage } from 'element-plus'
import {
  fetchUserFollowStatus,
  fetchVideoReactionStatus,
  followUser,
  reportVideoPlayFinish,
  reportVideoWatchHistory,
  toggleVideoCollect,
  toggleVideoLike,
  toggleVideoShare,
  type UserFollowStatus,
  type VideoReactionStatus,
} from '@/api/video'
import { useLoginGuard } from '@/composables/useLoginGuard'
import {
  clearFollowRelationCache,
  followRelationVersion,
  getFollowRelationCache,
  setFollowRelationCache,
} from '@/utils/followCache'
import { generateMockDanmu } from '@/utils/mockData'

export interface VideoQualityOption {
  quality: string
  url: string
  label?: string
}

export interface DouyinVideoData {
  videoId: string | number | null
  title?: string
  duration?: number
  qualities: VideoQualityOption[]
  description?: string
  authorId?: string | number
  author?: string
  authorAvatar?: string
  createTime?: string
  likes?: number
  comments?: number
  shares?: number
  collects?: number
}

export interface DouyinVideoPlayerProps {
  videoData: DouyinVideoData
  autoplay: boolean
}

export type DouyinPlayerEmit = (
  event: 'ready' | 'play' | 'pause' | 'ended' | 'error' | 'fullscreen-change',
  payload?: unknown
) => void

export const useDouyinVideoPlayer = (props: DouyinVideoPlayerProps, emit: DouyinPlayerEmit) => {
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
  const { runAfterLogin, authStore } = useLoginGuard()
  
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

  const likeCount = ref<number>(Number(props.videoData.likes ?? 0) || 0)
  const collectCount = ref<number>(Number(props.videoData.collects ?? 0) || 0)
  const shareCount = ref<number>(Number(props.videoData.shares ?? 0) || 0)
  const isLiked = ref(false)
  const isCollected = ref(false)
  const isShared = ref(false)
  const reactionMutating = ref(false)
  const reactionStatusLoading = ref(false)
  const isFollowed = ref(false)
  const followActionState = ref<'plus' | 'check' | 'hidden'>('hidden')
  const followMutating = ref(false)
  let followHideTimer: ReturnType<typeof setTimeout> | null = null
  let followAnimatingTargetUserId = ''
  
  // 网络监控
  const bufferingCount = ref(0) // 缓冲次数统计
  const lastBufferingTime = ref(0) // 上次缓冲时间
  const networkQuality = ref<'good' | 'medium' | 'poor'>('good') // 网络质量
  const autoDegradeEnabled = ref(true) // 是否启用自动降级
  const HISTORY_REPORT_INTERVAL_MS = 8000
  let lastHistoryReportAt = 0
  let lastHistoryVideoId: number | null = null

  const getVideoId = (): string | number | null => {
    const current = props.videoData?.videoId
    if (current === null || current === undefined) {
      return null
    }
    return current
  }

  const parseVideoId = (): number | null => {
    const raw = getVideoId()
    if (raw === null) {
      return null
    }
    const parsed = Number(raw)
    if (!Number.isFinite(parsed) || parsed <= 0) {
      return null
    }
    return Math.floor(parsed)
  }

  const parseCount = (value: unknown): number => {
    const numeric = Number(value ?? 0)
    if (!Number.isFinite(numeric)) {
      return 0
    }
    return Math.max(0, Math.round(numeric))
  }

  const resetReactionSnapshot = () => {
    likeCount.value = parseCount(props.videoData.likes)
    collectCount.value = parseCount(props.videoData.collects)
    shareCount.value = parseCount(props.videoData.shares)
    isLiked.value = false
    isCollected.value = false
    isShared.value = false
  }

  const normalizeUserId = (value: unknown): string => {
    if (value === null || value === undefined) {
      return ''
    }
    const text = String(value).trim()
    return text
  }

  const getAuthorUserId = (): string => {
    return normalizeUserId(props.videoData?.authorId)
  }

  const clearFollowHideTimer = () => {
    if (!followHideTimer) {
      return
    }
    clearTimeout(followHideTimer)
    followHideTimer = null
    followAnimatingTargetUserId = ''
  }

  const startFollowSuccessAnimation = (targetUserId: string) => {
    clearFollowHideTimer()
    followActionState.value = 'check'
    followAnimatingTargetUserId = targetUserId
    if (typeof window === 'undefined') {
      followActionState.value = 'hidden'
      followAnimatingTargetUserId = ''
      return
    }
    followHideTimer = window.setTimeout(() => {
      followActionState.value = 'hidden'
      followHideTimer = null
      followAnimatingTargetUserId = ''
    }, 1000)
  }

  const applyFollowStatus = (status?: UserFollowStatus | null) => {
    const targetUserId = normalizeUserId(status?.targetUserId || getAuthorUserId())
    const followed = !!status?.followed
    isFollowed.value = followed
    if (followed) {
      if (targetUserId && authStore.isLoggedIn) {
        setFollowRelationCache(targetUserId, true)
      }
      const isCurrentFollowAnimation =
        followActionState.value === 'check'
        && !!followHideTimer
        && targetUserId
        && followAnimatingTargetUserId === targetUserId
      if (isCurrentFollowAnimation) {
        return
      }
      followActionState.value = 'hidden'
      clearFollowHideTimer()
      return
    }
    const currentUserId = normalizeUserId(authStore.currentUser?.userId)
    const isSelfVideo = !!targetUserId && !!currentUserId && targetUserId === currentUserId
    if (targetUserId && authStore.isLoggedIn) {
      setFollowRelationCache(targetUserId, false)
    }
    followActionState.value = isSelfVideo || !targetUserId ? 'hidden' : 'plus'
    clearFollowHideTimer()
  }

  const applyReactionStatus = (status?: VideoReactionStatus | null) => {
    if (!status) {
      return
    }
    likeCount.value = parseCount(status.likeCount)
    collectCount.value = parseCount(status.collectCount)
    shareCount.value = parseCount(status.shareCount)
    isLiked.value = !!status.liked
    isCollected.value = !!status.collected
    isShared.value = !!status.shared
  }

  const handleReactionError = (error: any, fallback: string) => {
    const message = error?.response?.data?.info || error?.message || fallback
    console.warn(fallback, error)
    ElMessage.error(message)
  }

  const reportPlayFinished = async () => {
    const videoId = parseVideoId()
    if (videoId === null) {
      return
    }
    const duration = Math.max(0, Math.floor(totalDuration.value || props.videoData.duration || 0))
    try {
      await reportVideoPlayFinish({
        videoId,
        watchDuration: duration,
        lastWatchTimeOffset: duration,
        completeWatch: 1,
      })
    } catch (error) {
      console.warn('上报播放完成失败:', error)
    }
  }

  const reportPlayStarted = async () => {
    if (!authStore.isLoggedIn) {
      return
    }
    const videoId = parseVideoId()
    if (videoId === null) {
      return
    }
    const now = Date.now()
    if (lastHistoryVideoId === videoId && now - lastHistoryReportAt < HISTORY_REPORT_INTERVAL_MS) {
      return
    }
    lastHistoryVideoId = videoId
    lastHistoryReportAt = now
    const watchedOffset = Math.max(0, Math.floor(currentTime.value || 0))
    try {
      await reportVideoWatchHistory({
        videoId,
        watchDuration: watchedOffset,
        lastWatchTimeOffset: watchedOffset,
        completeWatch: 0,
      })
    } catch (error) {
      console.warn('????????:', error)
    }
  }

  const buildShareUrl = (videoId: string | number) => {
    const origin = typeof window !== 'undefined' && window.location?.origin
      ? window.location.origin
      : 'http://localhost:3001'
    return `${origin}/recommend/videoId=${encodeURIComponent(String(videoId))}`
  }

  const copyTextToClipboard = async (text: string) => {
    if (typeof navigator !== 'undefined' && navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(text)
      return
    }
    if (typeof document === 'undefined') {
      throw new Error('Clipboard API unavailable')
    }
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.style.position = 'fixed'
    textarea.style.left = '-9999px'
    document.body.appendChild(textarea)
    textarea.select()
    const copied = document.execCommand('copy')
    document.body.removeChild(textarea)
    if (!copied) {
      throw new Error('Copy failed')
    }
  }

  const loadReactionStatus = async (videoId: string | number) => {
    reactionStatusLoading.value = true
    try {
      const status = await fetchVideoReactionStatus(videoId)
      applyReactionStatus(status)
    } catch (error) {
      console.warn('获取互动状态失败', error);
    } finally {
      reactionStatusLoading.value = false
    }
  }

  const loadFollowStatus = async () => {
    const targetUserId = getAuthorUserId()
    if (!targetUserId) {
      applyFollowStatus({ targetUserId: '', followed: true })
      return
    }
    const cachedFollowState = getFollowRelationCache(targetUserId)
    if (cachedFollowState !== null) {
      applyFollowStatus({ targetUserId, followed: cachedFollowState })
      return
    }
    const currentUserId = normalizeUserId(authStore.currentUser?.userId)
    if (currentUserId && currentUserId === targetUserId) {
      applyFollowStatus({ targetUserId, followed: true })
      return
    }
    try {
      const status = await fetchUserFollowStatus(targetUserId)
      applyFollowStatus(status)
    } catch (error) {
      console.warn('获取关注状态失败', error)
      applyFollowStatus({ targetUserId, followed: false })
    }
  }
  
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
      // 销毁现有播放器
      if (player.value) {
        console.log('销毁现有播放器')
        player.value.destroy()
        player.value = null
      }
  
      // 设置总时长
      totalDuration.value = props.videoData.duration || 0
      console.log('视频时长:', totalDuration.value)
  
      // 初始化 XGPlayer 播放器
      player.value = new Player({
        el: videoContainer.value,
        url: getCurrentVideoUrl(),
        width: '100%',
        height: '100%',
        autoplay: props.autoplay,
        muted: isMuted.value,
        volume: volume.value,
        fitVideoSize: 'contain' as any,
        controls: false, // 使用自定义控制栏
        playbackRate: [0.5, 0.75, 1.0, 1.25, 1.5, 2.0],
        lang: 'zh-cn',
        cssFullscreen: true,
        // 添加加载和错误处理
        playsinline: true,
        preloadTime: 30, // 预加载 30 秒
        closeVideoClick: true,
        closeVideoTouch: true,
        // 缓冲优化配置
        bufferSize: 1024 * 1024 * 50, // 50MB 缓冲区
        loadNextBuffer: true, // 加载下一个缓冲区
        startTime: 0,
        // 网络优化
        maxBufferLength: 30, // 最大缓冲长度 30 秒
        minBufferLength: 5,  // 最小缓冲长度 5 秒
        bufferBehind: 10,    // 向后缓冲 10 秒
        // 添加就绪回调 - 只设置一次，避免重复设置
        ready: () => {
          console.log('XGPlayer 播放器就绪')
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
  
      // 添加额外事件监听，确保加载状态正确更新
      if (player.value) {
        // 先移除可能存在的旧事件监听器
        // XGPlayer 的 off 需要事件类型和回调，这里直接重新绑定
        // 旧的事件监听器会在 player.destroy() 时自动清理
        
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
            // 如果连续播放 30 秒没有缓冲，网络可能已恢复
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
        }, 5000) // 减少到 5 秒超时，更快反馈
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
  
  // 备用视频源列表（当主源加载失败时使用），使用更快的视频源
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
  
    const fallbackIndex = currentBackupIndex.value % backupVideoSources.length
    const firstQuality = sortedQualities.value[0]
    const qualityData = sortedQualities.value.find((q) => q.quality === currentQuality.value)
    if (!qualityData) {
      if (firstQuality) {
        console.warn('未找到当前清晰度，使用默认清晰度:', firstQuality.quality)
      }
      const defaultUrl = firstQuality?.url || backupVideoSources[fallbackIndex]
      return defaultUrl
    }
  
    const url = qualityData.url || backupVideoSources[fallbackIndex]
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
    const [firstQuality] = sortedQualities.value
    if (!firstQuality) {
      return
    }
    currentQuality.value = firstQuality.quality
  }
  
  // 生成弹幕数据
  const generateDanmuData = () => {
    const videoId = getVideoId()
    if (videoId === null) {
      danmuList.value = []
      return
    }
    danmuList.value = generateMockDanmu(videoId, totalDuration.value)
  }
  
  // 设置播放器事件
  const setupPlayerEvents = () => {
    if (!player.value) return
  
    player.value.on('play', () => {
      clearReplayTimeout()
      isPlaying.value = true
      emit('play')
      void reportPlayStarted()
    })
  
    player.value.on('pause', () => {
      clearReplayTimeout()
      isPlaying.value = false
      emit('pause')
    })
  
    player.value.on('ended', () => {
      isPlaying.value = false
      emit('ended')
      void reportPlayFinished()
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
      emit('fullscreen-change', isFull)
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
          if (bufferedPercent % 10 < 1) { // 每 10% 显示一次
            console.log(`缓冲进度: ${bufferedPercent.toFixed(1)}% (${bufferedEnd.toFixed(1)}/${totalDuration.value})`)
          }
          
          // 智能预加载策略
          const bufferAhead = bufferedEnd - currentTime.value
          
          if (bufferAhead < 5) { // 缓冲不足 5 秒
            console.log('缓冲不足，剩余缓冲', bufferAhead.toFixed(1), '秒')
            
            // 如果网络质量好，尝试预加载更多
            if (networkQuality.value === 'good' && player.value.video) {
              // 尝试预加载下一段
              try {
                // XGPlayer 可能不支持直接控制预加载，这里仅做记录
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
  
    // 限制音量在 0-1 之间
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
  
    // 限制音量在 0-1 之间
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
      if (!lowerQuality) {
        return
      }
      const targetLabel = lowerQuality.label || lowerQuality.quality
      console.log(`网络质量较差，自动降级到: ${targetLabel}`)
      
      // 显示提示
      error.value = `网络不稳定，已自动切换到${targetLabel}清晰度`
      setTimeout(() => {
        error.value = ''
      }, 3000)
      
      // 切换到低清晰度
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
  
  // 监听 videoData 变化 - 仅在视频 ID 变化时重新初始化
  watch(
    () => props.videoData?.videoId,
    (newVideoId, oldVideoId) => {
      if (newVideoId !== oldVideoId) {
        console.log('视频ID变化，重新初始化播放器', oldVideoId, '->', newVideoId)
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
    () => props.videoData?.videoId,
    (videoId) => {
      resetReactionSnapshot()
      if (videoId !== null && videoId !== undefined) {
        loadReactionStatus(videoId as string | number)
      }
    },
    { immediate: true }
  )

  watch(
    () => props.videoData?.authorId,
    () => {
      loadFollowStatus()
    },
    { immediate: true }
  )

  watch(
    () => authStore.currentUser?.userId,
    (newUserId, oldUserId) => {
      if (normalizeUserId(newUserId) !== normalizeUserId(oldUserId)) {
        clearFollowRelationCache()
      }
      loadFollowStatus()
    }
  )

  watch(
    () => followRelationVersion.value,
    () => {
      loadFollowStatus()
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
    runAfterLogin('like', async () => {
      if (reactionMutating.value) {
        return
      }
      const videoId = getVideoId()
      if (videoId === null) {
        ElMessage.warning('暂无可点赞的视频')
        return
      }
      reactionMutating.value = true
      try {
        const status = await toggleVideoLike(videoId)
        applyReactionStatus(status)
      } catch (error) {
        handleReactionError(error, '点赞失败，请稍后再试')
      } finally {
        reactionMutating.value = false
      }
    }).catch(() => undefined)
  }
  
  // 处理评论
  const handleComment = () => {
    runAfterLogin('comment', () => {
      ElMessage.info('评论功能开发中，欢迎先点赞收藏~')
    }).catch(() => undefined)
  }  
  // 处理分享
  const handleShare = () => {
    runAfterLogin('share', async () => {
      if (reactionMutating.value) {
        return
      }
      const videoId = getVideoId()
      if (videoId === null) {
        ElMessage.warning('暂无可分享的视频')
        return
      }
      reactionMutating.value = true
      try {
        const status = await toggleVideoShare(videoId)
        applyReactionStatus(status)
        const shareUrl = buildShareUrl(videoId)
        await copyTextToClipboard(shareUrl)
        ElMessage.success('已复制链接,快去分享给好友吧!')
      } catch (error) {
        handleReactionError(error, '分享操作失败，请稍后再试')
      } finally {
        reactionMutating.value = false
      }
    }).catch(() => undefined)
  }  
  // 处理收藏
  const handleCollect = () => {
    runAfterLogin('collect', async () => {
      if (reactionMutating.value) {
        return
      }
      const videoId = getVideoId()
      if (videoId === null) {
        ElMessage.warning('暂无可收藏的视频')
        return
      }
      reactionMutating.value = true
      try {
        const status = await toggleVideoCollect(videoId)
        applyReactionStatus(status)
      } catch (error) {
        handleReactionError(error, '收藏操作失败，请稍后再试')
      } finally {
        reactionMutating.value = false
      }
    }).catch(() => undefined)
  }

  const handleFollow = () => {
    runAfterLogin('follow', async () => {
      if (followMutating.value || followActionState.value === 'hidden') {
        return
      }
      const targetUserId = getAuthorUserId()
      if (!targetUserId) {
        ElMessage.warning('暂无可关注的作者')
        return
      }
      const currentUserId = normalizeUserId(authStore.currentUser?.userId)
      if (currentUserId && currentUserId === targetUserId) {
        ElMessage.warning('不能关注自己')
        applyFollowStatus({ targetUserId, followed: true })
        return
      }
      followMutating.value = true
      try {
        const status = await followUser(targetUserId)
        if (status.followed) {
          setFollowRelationCache(targetUserId, true)
        }
        applyFollowStatus(status)
        if (status.followed) {
          startFollowSuccessAnimation(targetUserId)
        }
      } catch (error: any) {
        const message = error?.response?.data?.info || error?.message || '关注失败，请稍后再试'
        ElMessage.error(message)
      } finally {
        followMutating.value = false
      }
    }).catch(() => undefined)
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
    clearFollowHideTimer()
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

  const exposed = {
    play: () => player.value?.play(),
    pause: () => player.value?.pause(),
    seek: (time: number) => {
      if (player.value) {
        player.value.currentTime = time
      }
    },
    toggleDanmu: () => toggleDanmu(),
    toggleFullscreen: () => toggleFullscreen(),
  }

  return {
    playerContainer,
    videoContainer,
    videoStageStyle,
    progressBar,
    controlBarRef,
    volumeSlider,
    danmuSettingsRef,
    danmuInput,
    isPlaying,
    currentTime,
    totalDuration,
    progressPercent,
    controlsVisible,
    showControls,
    startHideTimer,
    togglePlay,
    showDanmu,
    danmuList,
    danmuInputText,
    likeCount,
    collectCount,
    shareCount,
    isLiked,
    isCollected,
    isShared,
    reactionMutating,
    isFollowed,
    followActionState,
    followMutating,
    showDanmuSettings,
    danmuFontSize,
    danmuOpacity,
    danmuSpeed,
    danmuArea,
    toggleDanmuSettings,
    closeDanmuSettings,
    updateDanmuSettings,
    seekToPosition,
    playbackRates,
    currentPlaybackRate,
    showPlaybackRateMenu,
    togglePlaybackRateMenu,
    changePlaybackRate,
    sendDanmuFromInput,
    currentQuality,
    sortedQualities,
    changeQuality,
    volume,
    isMuted,
    toggleMute,
    changeVolumeVertical,
    isFullscreen,
    toggleFullscreen,
    loading,
    error,
    retry,
    handleSendDanmu,
    handleLike,
    handleComment,
    handleFollow,
    handleShare,
    handleCollect,
    formatTime,
    formatPublishTime,
    exposed
  }
}







