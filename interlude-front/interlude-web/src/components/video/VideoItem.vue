<template>
  <div class="video-item" :id="'video-' + videoId" ref="containerRef"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import Player from 'xgplayer'
import 'xgplayer/dist/index.min.css'
import DemoBasePlugin from '@/utils/demoBasePlugin.js'
import DemoPlugin from '@/utils/demoPlugin.js'

const props = defineProps<{
  src: string
  videoId: string | number
  autoplay?: boolean
}>()

const emit = defineEmits(['fullscreenchange'])

const containerRef = ref<HTMLElement | null>(null)
let player: Player | null = null

const initPlayer = () => {
  if (!containerRef.value) return

  player = new Player({
    el: containerRef.value,
    url: props.src,
    plugins: [DemoBasePlugin, DemoPlugin],
    fitVideoSize: 'fixWidth',
    width: '100%',
    height: '100%',
    autoplay: props.autoplay,
    muted: true, // 静音自动播放，避免浏览器限制
    // 移除全屏按钮
    controls: {
      // 只保留需要的控件，去掉 fullscreen
      mode: 'normal',
      // 或者直接禁用全屏功能：
      fullscreen: false
    }
  })

  // 监听全屏变化（如果控件被移除，此事件可能不会触发，但保留以防万一）
  player.on('fullscreenchange', (isFull: boolean) => {
    emit('fullscreenchange', isFull)
  })
}

const destroyPlayer = () => {
  if (player) {
    player.destroy()
    player = null
  }
}

// 暴露控制方法
const play = () => player?.play()
const pause = () => player?.pause()

defineExpose({ play, pause })

onMounted(() => {
  initPlayer()
})

onUnmounted(() => {
  destroyPlayer()
})

watch(() => props.src, () => {
  destroyPlayer()
  initPlayer()
})

// 监听 autoplay 变化，动态控制播放/暂停
watch(() => props.autoplay, (val) => {
  if (val) {
    play()
  } else {
    pause()
  }
})
</script>

<style scoped>
.video-item {
  width: 100%;
  height: calc(100vh-80px); /* 每个视频占满视口高度 */
  scroll-snap-align: start; /* 用于滚动捕捉 */
}
</style>