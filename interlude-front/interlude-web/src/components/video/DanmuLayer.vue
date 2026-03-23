<template>
  <div class="danmu-layer" ref="danmuLayer">
    <!-- 弹幕显示区域 -->
    <div class="danmu-container">
      <!-- 顶部弹幕轨道 -->
      <div class="danmu-track top-track">
        <div
          v-for="danmu in topDanmus"
          :key="danmu.id"
          class="danmu-item"
          :style="getDanmuStyle(danmu)"
          @click="handleDanmuClick(danmu)"
        >
          <span class="danmu-content">{{ danmu.content }}</span>
        </div>
      </div>

      <!-- 底部弹幕轨道 -->
      <div class="danmu-track bottom-track">
        <div
          v-for="danmu in bottomDanmus"
          :key="danmu.id"
          class="danmu-item"
          :style="getDanmuStyle(danmu)"
          @click="handleDanmuClick(danmu)"
        >
          <span class="danmu-content">{{ danmu.content }}</span>
        </div>
      </div>

      <!-- 滚动弹幕轨道（多个） -->
      <div
        v-for="(track, index) in scrollTracks"
        :key="index"
        class="danmu-track scroll-track"
        :class="`track-${index}`"
      >
        <div
          v-for="danmu in track.danmus"
          :key="danmu.id"
          class="danmu-item"
          :style="getScrollDanmuStyle(danmu, index)"
          @click="handleDanmuClick(danmu)"
        >
          <span class="danmu-content">{{ danmu.content }}</span>
        </div>
      </div>
    </div>

    <!-- 弹幕输入框 -->
    <div class="danmu-input-container" v-if="showInput">
      <div class="input-wrapper">
        <input
          ref="danmuInput"
          v-model="inputText"
          type="text"
          placeholder="发个弹幕，开心一下~"
          maxlength="50"
          @keyup.enter="sendDanmu"
          @keyup.esc="hideInput"
        />
        <button class="send-btn" @click="sendDanmu" :disabled="!inputText.trim()">
          <svg class="send-icon" viewBox="0 0 24 24">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
          </svg>
        </button>
      </div>
    </div>


  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'

interface Danmu {
  id: number | string
  content: string
  time: number
  color?: string
  type?: 'normal' | 'top' | 'bottom' | 'scroll'
  speed?: number
  userId?: string
  userName?: string
}

interface Props {
  danmuList: Danmu[]
  currentTime: number
}

const props = defineProps<Props>()
const emit = defineEmits(['send-danmu'])

const danmuLayer = ref<HTMLElement | null>(null)
const danmuInput = ref<HTMLInputElement | null>(null)
const showInput = ref(false)
const inputText = ref('')

// 弹幕轨道配置
const trackCount = 5 // 滚动弹幕轨道数量
const scrollTracks = ref(Array.from({ length: trackCount }, () => ({ danmus: [] as Danmu[] })))

// 计算当前时间范围内的弹幕
const currentDanmus = computed(() => {
  const timeWindow = 3 // 显示前后3秒的弹幕
  return props.danmuList.filter(
    (danmu) => Math.abs(danmu.time - props.currentTime) <= timeWindow
  )
})

// 顶部固定弹幕
const topDanmus = computed(() => {
  return currentDanmus.value.filter((danmu) => danmu.type === 'top')
})

// 底部固定弹幕
const bottomDanmus = computed(() => {
  return currentDanmus.value.filter((danmu) => danmu.type === 'bottom')
})

// 滚动弹幕
const scrollDanmus = computed(() => {
  return currentDanmus.value.filter(
    (danmu) => !danmu.type || danmu.type === 'normal' || danmu.type === 'scroll'
  )
})

// 分配弹幕到不同轨道
const assignDanmusToTracks = () => {
  // 清空所有轨道
  scrollTracks.value.forEach((track) => {
    track.danmus = []
  })

  // 将弹幕分配到不同轨道
  scrollDanmus.value.forEach((danmu, index) => {
    const trackIndex = index % trackCount
    const track = scrollTracks.value[trackIndex]
    if (track) {
      track.danmus.push(danmu)
    }
  })
}

// 获取弹幕样式
const getDanmuStyle = (danmu: Danmu) => {
  const style: any = {
    color: danmu.color || '#ffffff',
  }

  // 根据弹幕类型设置不同样式
  switch (danmu.type) {
    case 'top':
      style.position = 'absolute'
      style.top = '20%'
      style.left = '50%'
      style.transform = 'translateX(-50%)'
      style.fontSize = '18px'
      style.fontWeight = 'bold'
      style.textShadow = '2px 2px 4px rgba(0, 0, 0, 0.8)'
      style.backgroundColor = 'rgba(0, 0, 0, 0.5)'
      style.padding = '4px 12px'
      style.borderRadius = '20px'
      style.zIndex = '100'
      break

    case 'bottom':
      style.position = 'absolute'
      style.bottom = '20%'
      style.left = '50%'
      style.transform = 'translateX(-50%)'
      style.fontSize = '16px'
      style.fontWeight = 'bold'
      style.textShadow = '2px 2px 4px rgba(0, 0, 0, 0.8)'
      style.backgroundColor = 'rgba(0, 0, 0, 0.5)'
      style.padding = '4px 12px'
      style.borderRadius = '20px'
      style.zIndex = '100'
      break

    default:
      // 普通弹幕样式
      style.fontSize = '16px'
      style.textShadow = '1px 1px 2px rgba(0, 0, 0, 0.8)'
      style.fontWeight = 'normal'
  }

  return style
}

// 获取滚动弹幕样式
const getScrollDanmuStyle = (danmu: Danmu, trackIndex: number) => {
  const baseSpeed = danmu.speed || 1
  const speedFactor = 0.8 + (trackIndex % 3) * 0.2 // 不同轨道速度略有差异
  
  return {
    color: danmu.color || '#ffffff',
    fontSize: '16px',
    textShadow: '1px 1px 2px rgba(0, 0, 0, 0.8)',
    animationDuration: `${20 / (baseSpeed * speedFactor)}s`, // 速度控制
    top: `${20 + trackIndex * 24}px`, // 轨道位置
  }
}

// 发送弹幕
const sendDanmu = () => {
  const content = inputText.value.trim()
  if (!content) return

  emit('send-danmu', content)
  inputText.value = ''
  showInput.value = false
}

// 隐藏输入框
const hideInput = () => {
  showInput.value = false
  inputText.value = ''
}

// 处理弹幕点击
const handleDanmuClick = (danmu: Danmu) => {
  console.log('弹幕点击:', danmu)
  // 可以在这里实现弹幕点赞、回复等功能
}

// 监听弹幕列表变化
watch(
  () => props.danmuList,
  () => {
    assignDanmusToTracks()
  },
  { deep: true }
)

// 监听当前时间变化
watch(
  () => props.currentTime,
  () => {
    assignDanmusToTracks()
  }
)

// 监听输入框显示状态
watch(showInput, (val) => {
  if (val) {
    nextTick(() => {
      danmuInput.value?.focus()
    })
  }
})

  // 点击外部隐藏输入框
  const handleClickOutside = (event: MouseEvent) => {
    if (showInput.value && danmuLayer.value) {
      const target = event.target as Node | null
      if (target && !danmuLayer.value.contains(target)) {
        hideInput()
      }
    }
  }

onMounted(() => {
  assignDanmusToTracks()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.danmu-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 10;
  overflow: hidden;
}

.danmu-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.danmu-track {
  position: absolute;
  width: 100%;
  pointer-events: auto;
}

.scroll-track {
  height: 24px;
}

.danmu-item {
  position: absolute;
  white-space: nowrap;
  pointer-events: auto;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.scroll-track .danmu-item {
  animation-name: scrollRightToLeft;
  animation-timing-function: linear;
  animation-iteration-count: 1;
  animation-fill-mode: forwards;
  left: 100%;
}

.danmu-content {
  padding: 2px 8px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(4px);
  line-height: 1.4;
}

.danmu-item:hover .danmu-content {
  background: rgba(0, 0, 0, 0.5);
  transform: scale(1.05);
}

.danmu-input-container {
  position: absolute;
  bottom: 80px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  pointer-events: auto;
  z-index: 100;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: rgba(0, 0, 0, 0.8);
  border-radius: 24px;
  padding: 8px 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  min-width: 300px;
  max-width: 500px;
}

.input-wrapper input {
  flex: 1;
  background: transparent;
  border: none;
  color: #fff;
  font-size: 14px;
  padding: 4px 8px;
  outline: none;
}

.input-wrapper input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.send-btn {
  background: #ff2d55;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin-left: 8px;
  transition: all 0.2s;
}

.send-btn:disabled {
  background: rgba(255, 255, 255, 0.2);
  cursor: not-allowed;
}

.send-btn:not(:disabled):hover {
  background: #ff1a45;
  transform: scale(1.1);
}

.send-icon {
  width: 18px;
  height: 18px;
  fill: #fff;
}



@keyframes scrollRightToLeft {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-100vw);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .input-wrapper {
    min-width: 250px;
    max-width: 350px;
  }



  .danmu-content {
    font-size: 14px;
    padding: 1px 6px;
  }

  .scroll-track {
    height: 20px;
  }

  .scroll-track .danmu-item {
    top: 16px !important;
  }
}

/* 弹幕类型样式增强 */
.danmu-item[data-type="top"] .danmu-content {
  background: linear-gradient(135deg, rgba(255, 45, 85, 0.8), rgba(255, 107, 157, 0.8));
  border: 2px solid rgba(255, 255, 255, 0.3);
  padding: 6px 20px;
  font-size: 18px;
}

.danmu-item[data-type="bottom"] .danmu-content {
  background: linear-gradient(135deg, rgba(0, 122, 255, 0.8), rgba(0, 199, 190, 0.8));
  border: 2px solid rgba(255, 255, 255, 0.3);
  padding: 6px 20px;
  font-size: 16px;
}

/* 弹幕动画优化 */
@keyframes floatIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.top-track .danmu-item,
.bottom-track .danmu-item {
  animation: floatIn 0.3s ease-out;
}
</style>