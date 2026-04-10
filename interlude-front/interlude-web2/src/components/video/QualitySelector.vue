<template>
  <div class="quality-selector" ref="selectorRef">
    <!-- 清晰度选择按钮 -->
    <button class="control-btn quality-btn" @click.stop="toggleMenu">
      <svg class="quality-icon" viewBox="0 0 24 24">
        <path d="M12 7c2.76 0 5 2.24 5 5s-2.24 5-5 5-5-2.24-5-5 2.24-5 5-5zm0-5C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z"/>
        <path d="M12 17l1.5-4.5h3L12 7 9.5 12.5h3z"/>
      </svg>
      <span class="quality-text">{{ currentQualityDisplay }}</span>
      <svg class="arrow-icon" :class="{ 'arrow-up': showMenu }" viewBox="0 0 24 24">
        <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/>
      </svg>
    </button>

    <!-- 清晰度菜单（向上弹出） -->
    <div v-if="showMenu" class="quality-menu">
      <div class="menu-header">
        <span class="menu-title">清晰度选择</span>
        <button class="close-btn" @click.stop="closeMenu">
          <svg class="close-icon" viewBox="0 0 24 24">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      
      <div class="menu-content">
        <div
          v-for="quality in sortedQualities"
          :key="quality.quality"
          class="quality-item"
          :class="{ 
            active: currentQuality === quality.quality,
            recommended: quality.recommended,
            disabled: quality.disabled
          }"
          @click.stop="selectQuality(quality.quality)"
        >
          <div class="quality-info">
            <span class="quality-label">{{ getQualityDisplayName(quality.quality) }}</span>
            <span class="quality-resolution">{{ quality.resolution || getResolution(quality.quality) }}</span>
          </div>
          
          <div class="quality-status">
            <span v-if="currentQuality === quality.quality" class="current-badge">当前</span>
            <span v-if="quality.recommended && currentQuality !== quality.quality" class="recommended-badge">推荐</span>
            <span v-if="quality.disabled" class="disabled-badge">不可用</span>
          </div>
        </div>
      </div>

      <!-- 网络状态提示 -->
      <div v-if="networkStatus" class="network-status">
        <svg class="network-icon" viewBox="0 0 24 24">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l5-5-5-5v10z"/>
        </svg>
        <span class="network-text">{{ networkStatus }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

interface Quality {
  quality: string
  url?: string
  resolution?: string
  bitrate?: number
  recommended?: boolean
  disabled?: boolean
}

interface Props {
  qualities: Quality[]
  currentQuality: string
}

const props = defineProps<Props>()
const emit = defineEmits(['change-quality'])

const selectorRef = ref<HTMLElement | null>(null)
const showMenu = ref(false)
const menuPosition = ref<'down' | 'up'>('down')

// 清晰度显示名称映射
const qualityDisplayMap: Record<string, string> = {
  '4k': '4K 超清',
  '2160p': '4K',
  '1440p': '2K',
  '1080p': '1080P',
  '720p': '720P',
  '480p': '480P',
  '360p': '360P',
  '240p': '240P',
  'auto': '自动',
  'hd': '高清',
  'sd': '标清',
  'ld': '流畅',
}

// 分辨率映射
const resolutionMap: Record<string, string> = {
  '4k': '3840×2160',
  '2160p': '3840×2160',
  '1440p': '2560×1440',
  '1080p': '1920×1080',
  '720p': '1280×720',
  '480p': '854×480',
  '360p': '640×360',
  '240p': '426×240',
}

// 网络状态
const networkStatus = ref<string>('')

// 计算属性
const currentQualityDisplay = computed(() => {
  return getQualityDisplayName(props.currentQuality)
})

const sortedQualities = computed(() => {
  const qualities = [...props.qualities]
  
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
    'auto': 9,
    'hd': 10,
    'sd': 11,
    'ld': 12,
  }
  
  return qualities.sort((a, b) => {
    const orderA = order[a.quality.toLowerCase()] || 999
    const orderB = order[b.quality.toLowerCase()] || 999
    return orderA - orderB
  })
})

// 获取清晰度显示名称
const getQualityDisplayName = (quality: string) => {
  return qualityDisplayMap[quality.toLowerCase()] || quality.toUpperCase()
}

// 获取分辨率
const getResolution = (quality: string) => {
  return resolutionMap[quality.toLowerCase()] || ''
}

// 切换菜单显示
const toggleMenu = () => {
  showMenu.value = !showMenu.value
  if (showMenu.value) {
    updateMenuPosition()
    document.addEventListener('click', handleClickOutside)
    checkNetworkStatus()
  } else {
    document.removeEventListener('click', handleClickOutside)
  }
}

// 关闭菜单
const closeMenu = () => {
  showMenu.value = false
  document.removeEventListener('click', handleClickOutside)
}

// 选择清晰度
const selectQuality = (quality: string) => {
  if (props.currentQuality === quality) {
    closeMenu()
    return
  }

  const selectedQuality = props.qualities.find(q => q.quality === quality)
  if (selectedQuality?.disabled) {
    return
  }

  emit('change-quality', quality)
  closeMenu()
}

// 更新菜单位置
const updateMenuPosition = () => {
  if (!selectorRef.value) return

  const rect = selectorRef.value.getBoundingClientRect()
  const viewportHeight = window.innerHeight
  
  // 如果按钮靠近底部，菜单向上弹出
  if (rect.bottom > viewportHeight - 200) {
    menuPosition.value = 'up'
  } else {
    menuPosition.value = 'down'
  }
}

// 检查网络状态
const checkNetworkStatus = () => {
  const nav = navigator as any
  if (nav.connection) {
    const connection = nav.connection
    const effectiveType = connection.effectiveType
    
    switch (effectiveType) {
      case 'slow-2g':
      case '2g':
        networkStatus.value = '网络较差，建议选择低清晰度'
        break
      case '3g':
        networkStatus.value = '网络一般，建议选择标清或高清'
        break
      case '4g':
        networkStatus.value = '网络良好，可选择高清或超清'
        break
      default:
        networkStatus.value = ''
    }
  } else {
    networkStatus.value = ''
  }
}

// 点击外部关闭菜单
const handleClickOutside = (event: MouseEvent) => {
  if (selectorRef.value && !selectorRef.value.contains(event.target as Node)) {
    closeMenu()
  }
}

// 监听窗口大小变化
const handleResize = () => {
  if (showMenu.value) {
    updateMenuPosition()
  }
}

// 监听清晰度变化
watch(() => props.currentQuality, () => {
  if (showMenu.value) {
    closeMenu()
  }
})

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  closeMenu()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.quality-selector {
  position: relative;
  display: inline-block;
}

.control-btn {
  background: transparent;
  border: none;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.quality-btn {
  gap: 4px;
}

.quality-icon {
  width: 20px;
  height: 20px;
  fill: #fff;
}

.quality-text {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}

.arrow-icon {
  width: 16px;
  height: 16px;
  fill: #fff;
  transition: transform 0.2s;
}

.arrow-up {
  transform: rotate(180deg);
}

.quality-menu {
  position: absolute;
  right: 0;
  bottom: 100%;
  background: rgba(0, 0, 0, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  min-width: 200px;
  margin-bottom: 8px;
  backdrop-filter: blur(20px);
  z-index: 1000;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  overflow: hidden;
}

.quality-menu.menu-up {
  bottom: auto;
  top: 100%;
  margin-bottom: 0;
  margin-top: 8px;
}

.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
}

.menu-title {
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

.menu-content {
  max-height: 300px;
  overflow-y: auto;
  padding: 8px 0;
}

.quality-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}

.quality-item:hover {
  background: rgba(255, 255, 255, 0.05);
}

.quality-item.active {
  background: rgba(255, 45, 85, 0.1);
  border-left-color: #ff2d55;
}

.quality-item.recommended:not(.active) {
  border-left-color: #4cd964;
}

.quality-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quality-item.disabled:hover {
  background: transparent;
}

.quality-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.quality-label {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.quality-resolution {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

.quality-status {
  display: flex;
  gap: 4px;
}

.current-badge {
  background: #ff2d55;
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 500;
}

.recommended-badge {
  background: #4cd964;
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 500;
}

.disabled-badge {
  background: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.7);
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 500;
}

.network-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.03);
}

.network-icon {
  width: 16px;
  height: 16px;
  fill: #4cd964;
}

.network-text {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  flex: 1;
}

/* 滚动条样式 */
.menu-content::-webkit-scrollbar {
  width: 4px;
}

.menu-content::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
}

.menu-content::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.menu-content::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quality-menu {
    min-width: 180px;
    right: -50%;
    transform: translateX(50%);
  }

  .quality-menu.menu-up {
    right: -50%;
    transform: translateX(50%);
  }

  .quality-text {
    font-size: 12px;
  }

  .quality-icon {
    width: 18px;
    height: 18px;
  }

  .arrow-icon {
    width: 14px;
    height: 14px;
  }

  .quality-item {
    padding: 8px 12px;
  }

  .quality-label {
    font-size: 13px;
  }

  .quality-resolution {
    font-size: 11px;
  }
}

/* 动画效果 */
.quality-menu {
  animation: slideIn 0.2s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.quality-menu.menu-up {
  animation: slideInUp 0.2s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>