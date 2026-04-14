<template>
  <div class="danmu-layer">
    <div class="danmu-container">
      <div class="danmu-track top-track">
        <div
          v-for="(danmu, index) in topDanmus"
          :key="`top-${danmu.id}-${index}`"
          class="danmu-item fixed-item"
          :style="getFixedDanmuStyle(danmu, 'top', index)"
        >
          <span class="danmu-content">{{ danmu.content }}</span>
        </div>
      </div>

      <div class="danmu-track bottom-track">
        <div
          v-for="(danmu, index) in bottomDanmus"
          :key="`bottom-${danmu.id}-${index}`"
          class="danmu-item fixed-item"
          :style="getFixedDanmuStyle(danmu, 'bottom', index)"
        >
          <span class="danmu-content">{{ danmu.content }}</span>
        </div>
      </div>

      <div
        v-for="(track, index) in scrollTracks"
        :key="`scroll-track-${index}-${safeArea}`"
        class="danmu-track scroll-track"
        :style="{ '--play-state': isPlaying ? 'running' : 'paused' }"
      >
        <div
          v-for="danmu in track.danmus"
          :key="`scroll-${danmu.id}`"
          class="danmu-item"
          :style="getScrollDanmuStyle(danmu, index)"
          @animationend="handleDanmuAnimationEnd(danmu, index)"
        >
          <span class="danmu-content">{{ danmu.content }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'

type DanmuType = 'normal' | 'top' | 'bottom' | 'scroll'
type DanmuArea = 'full' | 'top' | 'bottom'

interface Danmu {
  id: number | string
  content: string
  time: number
  color?: string
  type?: DanmuType
  speed?: number
  fontSize?: number
  opacity?: number
}

interface Props {
  danmuList: Danmu[]
  currentTime: number
  isPlaying?: boolean
  fontSize?: number
  opacity?: number
  speedMultiplier?: number
  area?: DanmuArea
}

const props = withDefaults(defineProps<Props>(), {
  isPlaying: true,
  fontSize: 16,
  opacity: 0.8,
  speedMultiplier: 1,
  area: 'full',
})

defineEmits<{
  (e: 'send-danmu', content: string): void
}>()

const TRACK_HEIGHT = 30
const TIME_WINDOW_SECONDS = 3

const clampNumber = (value: unknown, min: number, max: number, fallback: number): number => {
  const numeric = Number(value)
  if (!Number.isFinite(numeric)) {
    return fallback
  }
  return Math.max(min, Math.min(max, numeric))
}

const safeArea = computed<DanmuArea>(() => {
  return props.area === 'top' || props.area === 'bottom' ? props.area : 'full'
})

const safeFontSize = computed(() => Math.round(clampNumber(props.fontSize, 12, 42, 16)))
const safeOpacity = computed(() => clampNumber(props.opacity, 0.2, 1, 0.8))
const safeSpeedMultiplier = computed(() => clampNumber(props.speedMultiplier, 0.5, 2, 1))
const trackCount = computed(() => (safeArea.value === 'full' ? 8 : 5))

const isPlaying = computed(() => props.isPlaying !== false)
const activeDanmuIds = ref<Set<number | string>>(new Set())
const scrollTracks = ref<Array<{ danmus: Danmu[] }>>([])

const resetTracks = () => {
  scrollTracks.value = Array.from({ length: trackCount.value }, () => ({ danmus: [] as Danmu[] }))
  activeDanmuIds.value = new Set()
}

const isScrollType = (danmuType?: DanmuType) => {
  return !danmuType || danmuType === 'normal' || danmuType === 'scroll'
}

const currentDanmus = computed(() => {
  return props.danmuList.filter((danmu) => {
    const danmuTime = Number(danmu.time ?? 0)
    if (!Number.isFinite(danmuTime)) {
      return false
    }
    return activeDanmuIds.value.has(danmu.id) || Math.abs(danmuTime - props.currentTime) <= TIME_WINDOW_SECONDS
  })
})

const topDanmus = computed(() => {
  if (safeArea.value === 'bottom') {
    return []
  }
  return currentDanmus.value.filter((danmu) => danmu.type === 'top')
})

const bottomDanmus = computed(() => {
  if (safeArea.value === 'top') {
    return []
  }
  return currentDanmus.value.filter((danmu) => danmu.type === 'bottom')
})

const scrollDanmus = computed(() => {
  return currentDanmus.value.filter((danmu) => isScrollType(danmu.type))
})

const assignDanmusToTracks = () => {
  if (!scrollTracks.value.length) {
    resetTracks()
  }
  const currentIds = new Set(scrollDanmus.value.map((item) => item.id))
  scrollTracks.value.forEach((track) => {
    track.danmus = track.danmus.filter(
      (danmu) => currentIds.has(danmu.id) || activeDanmuIds.value.has(danmu.id),
    )
  })

  const trackLoads = scrollTracks.value.map((track) => track.danmus.length)
  scrollDanmus.value.forEach((danmu) => {
    const exists = scrollTracks.value.some((track) => track.danmus.some((item) => item.id === danmu.id))
    if (exists) {
      return
    }
    let selectedTrackIndex = 0
    let minLoad = Number.POSITIVE_INFINITY
    trackLoads.forEach((load, index) => {
      if (load < minLoad) {
        minLoad = load
        selectedTrackIndex = index
      }
    })
    const selectedTrack = scrollTracks.value[selectedTrackIndex]
    if (!selectedTrack) {
      return
    }
    selectedTrack.danmus.push(danmu)
    trackLoads[selectedTrackIndex] = (trackLoads[selectedTrackIndex] ?? 0) + 1
    activeDanmuIds.value.add(danmu.id)
  })
}

const resolveTrackTop = (trackIndex: number) => {
  if (safeArea.value === 'top') {
    return `${16 + trackIndex * TRACK_HEIGHT}px`
  }
  if (safeArea.value === 'bottom') {
    return `calc(55% + ${trackIndex * TRACK_HEIGHT}px)`
  }
  return `${16 + trackIndex * TRACK_HEIGHT}px`
}

const getDanmuColor = (danmu: Danmu): string => {
  const color = String(danmu.color || '#FFFFFF')
  return /^#[0-9a-fA-F]{6}$/.test(color) ? color.toUpperCase() : '#FFFFFF'
}

const getDanmuFontSize = (danmu: Danmu): string => {
  const size = Math.round(clampNumber(danmu.fontSize, 12, 42, safeFontSize.value))
  return `${size}px`
}

const getDanmuOpacity = (danmu: Danmu): number => {
  return clampNumber(danmu.opacity, 0.2, 1, safeOpacity.value)
}

const getFixedDanmuStyle = (danmu: Danmu, position: 'top' | 'bottom', index: number) => {
  const style: Record<string, string | number> = {
    color: getDanmuColor(danmu),
    fontSize: getDanmuFontSize(danmu),
    opacity: getDanmuOpacity(danmu),
    textShadow: '1px 1px 2px rgba(0, 0, 0, 0.8)',
    left: '50%',
    transform: 'translateX(-50%)',
    maxWidth: '78%',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  }
  const offset = 14 + (index % 3) * 8
  if (position === 'top') {
    style.top = `${offset}%`
  } else {
    style.bottom = `${offset}%`
  }
  return style
}

const getScrollDanmuStyle = (danmu: Danmu, trackIndex: number) => {
  const danmuSpeed = clampNumber(danmu.speed, 0.5, 2, 1) * safeSpeedMultiplier.value
  const speedFactor = 0.85 + (trackIndex % 3) * 0.15
  const duration = Math.max(6, 20 / (danmuSpeed * speedFactor))
  return {
    color: getDanmuColor(danmu),
    fontSize: getDanmuFontSize(danmu),
    opacity: getDanmuOpacity(danmu),
    textShadow: '1px 1px 2px rgba(0, 0, 0, 0.8)',
    animationDuration: `${duration}s`,
    top: resolveTrackTop(trackIndex),
  }
}

const handleDanmuAnimationEnd = (danmu: Danmu, trackIndex: number) => {
  activeDanmuIds.value.delete(danmu.id)
  const track = scrollTracks.value[trackIndex]
  if (!track) {
    return
  }
  const index = track.danmus.findIndex((item) => item.id === danmu.id)
  if (index >= 0) {
    track.danmus.splice(index, 1)
  }
}

watch(trackCount, () => {
  resetTracks()
  assignDanmusToTracks()
}, { immediate: true })

watch(
  () => props.danmuList,
  () => {
    assignDanmusToTracks()
  },
  { deep: true },
)

watch(
  () => props.currentTime,
  () => {
    assignDanmusToTracks()
  },
)

watch(
  () => props.area,
  () => {
    resetTracks()
    assignDanmusToTracks()
  },
)

onMounted(() => {
  assignDanmusToTracks()
})

onUnmounted(() => {
  activeDanmuIds.value.clear()
  scrollTracks.value = []
})
</script>

<style scoped>
.danmu-layer {
  position: absolute;
  inset: 0;
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
  inset: 0;
}

.danmu-item {
  position: absolute;
  left: 100%;
  white-space: nowrap;
  user-select: none;
}

.fixed-item {
  left: auto;
  animation: fixedFadeIn 0.25s ease-out;
}

.scroll-track .danmu-item {
  animation-name: scrollRightToLeft;
  animation-timing-function: linear;
  animation-iteration-count: 1;
  animation-fill-mode: forwards;
  animation-play-state: var(--play-state, running);
}

.danmu-content {
  display: inline-block;
  line-height: 1.35;
  padding: 1px 6px;
  border-radius: 10px;
  backdrop-filter: blur(2px);
}

@keyframes scrollRightToLeft {
  from {
    left: 100%;
  }
  to {
    left: -115%;
  }
}

@keyframes fixedFadeIn {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

@media (max-width: 768px) {
  .danmu-content {
    padding: 1px 5px;
  }
}
</style>
