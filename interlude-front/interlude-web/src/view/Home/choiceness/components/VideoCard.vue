<template>
  <div
    class="video-card"
    :class="{ featured: isFeatured }"
    :data-video-id="dataVideoId"
    @click="emit('click', video)"
  >
    <div class="video-cover">
      <img
        :src="placeholder"
        :data-src="video.cover"
        :alt="video.title"
        class="cover-image"
        @load="handleImageLoad"
        @error="handleImageError"
      />

      <div class="video-duration">
        {{ video.duration }}
      </div>

      <div class="video-likes">
        <span class="like-icon">♥</span>
        <span class="like-count">{{ formatLikes(video.likes) }}</span>
      </div>

      <div class="play-overlay">
        <div class="play-button">▶</div>
      </div>
    </div>

    <div class="video-info">
      <h3 class="video-title" :title="video.title">
        {{ truncateTitle(video.title) }}
      </h3>

      <div class="video-meta">
        <span class="video-author">{{ video.author }}</span>
        <span class="video-views">{{ formatViews(video.views) }}观看</span>
        <span class="video-time">{{ video.uploadTime }}</span>
      </div>

      <div class="video-tags">
        <span class="video-tag">{{ video.categoryName }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ChoicenessVideoItem } from '@/utils/mockData'

interface Props {
  video: ChoicenessVideoItem
  isFeatured?: boolean
  'data-video-id'?: number
}

const props = withDefaults(defineProps<Props>(), {
  isFeatured: false,
  'data-video-id': undefined,
})

const emit = defineEmits<{
  click: [video: ChoicenessVideoItem]
}>()

const placeholder = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjYwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjJmMmYyIi8+PC9zdmc+'
const dataVideoId = computed(() => props['data-video-id'] || props.video.id)

const formatLikes = (likes: number): string => {
  if (likes >= 10000) {
    return `${(likes / 10000).toFixed(1)}万`
  }
  return likes.toString()
}

const formatViews = (views: number): string => {
  if (views >= 10000) {
    return `${(views / 10000).toFixed(1)}万`
  }
  return views.toString()
}

const truncateTitle = (title: string): string => {
  const maxLength = props.isFeatured ? 40 : 30
  return title.length > maxLength ? `${title.substring(0, maxLength)}...` : title
}

const handleImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.classList.add('loaded')
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  const fallback = props.video.coverFallback

  if (img.dataset.fallbackApplied === 'true') {
    img.src = placeholder
    img.classList.add('loaded')
    return
  }

  if (fallback) {
    img.dataset.fallbackApplied = 'true'
    img.src = fallback
    return
  }

  img.src = placeholder
  img.classList.add('loaded')
}
</script>

<style lang="scss" scoped>
.video-card {
  background-color: var(--bg-color);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px var(--shadow-color);
  position: relative;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

    .play-overlay {
      opacity: 1;
    }
  }

  &.featured {
    .video-cover {
      height: 100%;
    }

    .video-title {
      font-size: 18px;
      line-height: 1.4;
    }
  }
}

.video-cover {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background-color: var(--hover-bg);
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s ease;
  opacity: 0;

  &.loaded {
    opacity: 1;
  }
}

.video-duration {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background-color: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  backdrop-filter: blur(4px);
}

.video-likes {
  position: absolute;
  bottom: 10px;
  left: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
  background-color: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  backdrop-filter: blur(4px);
}

.play-overlay {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.play-button {
  width: 60px;
  height: 60px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #000;
  transition: transform 0.2s ease;

  &:hover {
    transform: scale(1.1);
  }
}

.video-info {
  padding: 16px;
  background-color: var(--bg-color);
}

.video-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.video-author {
  font-weight: 500;
  color: var(--text-color);
}

.video-views,
.video-time {
  opacity: 0.8;
}

.video-tag {
  display: inline-block;
  padding: 4px 12px;
  background-color: var(--hover-bg);
  color: var(--text-secondary);
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s ease;

  &:hover {
    background-color: var(--border-color);
  }
}
</style>
