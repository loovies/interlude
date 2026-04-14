<template>
  <div class="choiceness-page">
    <nav class="category-nav">
      <div class="nav-container">
        <div class="nav-scroll">
          <button
            v-for="category in visibleCategories"
            :key="category.id"
            class="category-btn"
            :class="{ active: activeCategory === category.id }"
            @click="switchCategory(category.id)"
          >
            {{ category.name }}
          </button>
          <div v-if="overflowCategories.length > 0" class="more-categories">
            <ElDropdown
              trigger="click"
              placement="bottom-end"
              popper-class="choiceness-more-dropdown"
              @visible-change="handleMoreDropdownVisibleChange"
            >
              <button
                class="category-btn more-btn"
                :class="{ active: isOverflowActive || showMoreCategories }"
                @click.stop
              >
                更多
              </button>
              <template #dropdown>
                <ElDropdownMenu class="more-dropdown-menu">
                  <ElDropdownItem
                    v-for="category in overflowCategories"
                    :key="category.id"
                    class="more-dropdown-item"
                    :class="{ 'is-active': activeCategory === category.id }"
                    @click="selectOverflowCategory(category.id)"
                  >
                    {{ category.name }}
                  </ElDropdownItem>
                </ElDropdownMenu>
              </template>
            </ElDropdown>
          </div>
        </div>
      </div>
    </nav>

    <main class="video-content">
      <template v-if="featuredVideo">
        <div class="video-grid">
          <div class="video-main">
            <VideoCard
              :video="featuredVideo"
              :is-featured="true"
              :data-video-id="featuredVideo.id"
              @click="handleVideoClick(featuredVideo)"
              @author-click="handleAuthorClick"
            />
          </div>

          <div class="video-sidebar">
            <div class="sidebar-header">
              <h3 class="sidebar-title">热门推荐</h3>
              <div class="sidebar-stats">
                <span class="stat-item">实时更新</span>
                <span class="stat-item">24小时热度</span>
              </div>
            </div>

            <div class="video-grid-small">
              <div
                v-for="video in smallVideos"
                :key="video.id"
                class="small-video-item"
                :data-video-id="video.id"
                @click="handleVideoClick(video)"
              >
                <div class="small-video-cover">
                  <img
                    :src="placeholder"
                    :data-src="video.cover"
                    :alt="video.title"
                    class="small-cover-image"
                    @error="handleSmallImageError($event, video)"
                  />
                  <div class="small-video-duration">{{ video.duration }}</div>
                  <div class="small-video-likes">
                    <span class="small-like-icon">♥</span>
                    <span class="small-like-count">{{ formatLikes(video.likes) }}</span>
                  </div>
                  <div class="small-play-overlay">
                    <div class="small-play-button">▶</div>
                  </div>
                </div>
                <div class="small-video-info">
                  <h4 class="small-video-title" :title="video.title">
                    {{ truncateSmallTitle(video.title) }}
                  </h4>
                  <div class="small-video-meta">
                    <button type="button" class="small-author-entry" @click.stop="handleAuthorClick(video)">
                      <img class="small-author-avatar" :src="resolveAuthorAvatar(video)" :alt="`${video.author || '用户'}头像`" />
                      <span class="small-video-author">{{ video.author }}</span>
                    </button>
                    <span class="small-video-views">{{ formatViews(video.views) }}观看</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="more-videos">
          <div class="section-header">
            <h2 class="section-title">更多推荐</h2>
            <div class="view-toggle">
              <button
                class="toggle-btn"
                :class="{ active: viewMode === 'grid' }"
                @click="viewMode = 'grid'"
              >
                <span class="icon-grid">▦</span>
              </button>
              <button
                class="toggle-btn"
                :class="{ active: viewMode === 'list' }"
                @click="viewMode = 'list'"
              >
                <span class="icon-list">☰</span>
              </button>
            </div>
          </div>

          <div class="video-list" :class="viewMode">
            <VideoCard
              v-for="video in moreVideos"
              :key="video.id"
              :video="video"
              :is-featured="false"
              :data-video-id="video.id"
              :class="viewMode === 'list' ? 'list-mode' : ''"
              @click="handleVideoClick(video)"
              @author-click="handleAuthorClick"
            />
          </div>

          <div v-if="loading" class="loading-state">加载中...</div>
          <div v-else-if="!hasMore && videoItems.length > 0" class="loading-state">没有更多了</div>
        </div>
      </template>

      <div v-else-if="loading" class="loading-state">加载中...</div>
      <div v-else class="empty-state">暂无视频数据</div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElDropdown, ElDropdownItem, ElDropdownMenu } from 'element-plus'
import VideoCard from './components/VideoCard.vue'
import {
  fetchChoicenessCategories,
  fetchChoicenessVideos,
  type ChoicenessCategory,
  type ChoicenessVideoItem,
} from '@/utils/mockData'

const placeholder = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjYwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjJmMmYyIi8+PC9zdmc+'
const router = useRouter()

const categories = ref<ChoicenessCategory[]>([{ id: 'all', name: '全部' }])
const activeCategory = ref('all')
const viewMode = ref<'grid' | 'list'>('grid')
const videoItems = ref<ChoicenessVideoItem[]>([])
const showMoreCategories = ref(false)
const loading = ref(false)
const hasMore = ref(true)
const pageNo = ref(1)
const pageSize = 12
const total = ref(0)
const observer = ref<IntersectionObserver | null>(null)
const scrollContainer = ref<HTMLElement | null>(null)
const loadedVideos = ref<Set<number>>(new Set())
const observedElements = new WeakSet<Element>()

const toggleChoicenessScrollbar = (hide: boolean) => {
  const container = document.querySelector('.content-container') as HTMLElement | null
  if (!container) {
    return
  }
  container.classList.toggle('choiceness-scrollbar-hidden', hide)
}

const featuredVideo = computed(() => videoItems.value[0] ?? null)
const smallVideos = computed(() => videoItems.value.slice(1, 5))
const moreVideos = computed(() => videoItems.value.slice(5))
const visibleCategories = computed(() => categories.value.slice(0, 16))
const overflowCategories = computed(() => categories.value.slice(16))
const isOverflowActive = computed(() =>
  overflowCategories.value.some((item) => item.id === activeCategory.value),
)

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

const normalizeUserId = (value: unknown): string => {
  if (value === null || value === undefined) {
    return ''
  }
  return String(value).trim()
}

const resolveAvatarUrl = (avatar?: string): string => {
  if (!avatar) {
    return ''
  }
  if (/^(https?:)?\/\//.test(avatar) || avatar.startsWith('data:image/')) {
    return avatar
  }
  if (avatar.startsWith('/file/')) {
    return avatar
  }
  const cleaned = avatar.replace(/^\/+/, '')
  if (cleaned.startsWith('file/')) {
    return `/${cleaned}`
  }
  return `/file/${cleaned}`
}

const resolveAuthorUserId = (video: ChoicenessVideoItem): string => {
  const authorId = normalizeUserId(video.authorId)
  if (authorId) {
    return authorId
  }
  const authorName = normalizeUserId(video.author)
  if (authorName) {
    return authorName
  }
  return `author-${video.id}`
}

const buildAuthorAvatarFallback = (video: ChoicenessVideoItem): string => {
  const seed = encodeURIComponent(resolveAuthorUserId(video) || video.author || 'author')
  return `https://picsum.photos/seed/choiceness-author-${seed}/64/64`
}

const resolveAuthorAvatar = (video: ChoicenessVideoItem): string => {
  const resolved = resolveAvatarUrl(video.authorAvatar)
  return resolved || buildAuthorAvatarFallback(video)
}

const openProfileInNewTab = (video: ChoicenessVideoItem) => {
  const userId = resolveAuthorUserId(video)
  if (!userId) {
    return
  }
  const target = router.resolve({
    path: '/mine',
    query: {
      userId,
      nickName: video.author || '',
      avatar: video.authorAvatar || '',
    },
  })
  window.open(target.href, '_blank', 'noopener,noreferrer')
}

const handleAuthorClick = (video: ChoicenessVideoItem) => {
  openProfileInNewTab(video)
}
const truncateSmallTitle = (title: string): string => {
  const maxLength = 30
  return title.length > maxLength ? `${title.substring(0, maxLength)}...` : title
}

const getCurrentCategoryParams = () => {
  const currentCategory = categories.value.find((item) => item.id === activeCategory.value)
  if (!currentCategory || currentCategory.id === 'all') {
    return {}
  }

  const resolvedCategoryId = Number(currentCategory.categoryId ?? currentCategory.id)
  if (!Number.isFinite(resolvedCategoryId) || resolvedCategoryId <= 0) {
    return {}
  }

  if (currentCategory.pCategoryId === 0 || currentCategory.pCategoryId === undefined) {
    return {
      categoryId: resolvedCategoryId,
    }
  }

  return {
    categoryId: resolvedCategoryId,
    pCategoryId: currentCategory.pCategoryId,
  }
}

const initLazyLoad = () => {
  if (!observer.value) {
    observer.value = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) {
          return
        }

        const videoCard = entry.target as HTMLElement
        const videoId = Number(videoCard.getAttribute('data-video-id') || '0')
        if (videoId > 0 && loadedVideos.value.has(videoId)) {
          observer.value?.unobserve(videoCard)
          return
        }

        const images = videoCard.querySelectorAll('img[data-src]')
        images.forEach((imgNode) => {
          const img = imgNode as HTMLImageElement
          const dataSrc = img.getAttribute('data-src')
          if (!dataSrc || dataSrc === placeholder) {
            img.removeAttribute('data-src')
            return
          }

          img.src = dataSrc
          img.removeAttribute('data-src')
        })

        if (videoId > 0) {
          loadedVideos.value.add(videoId)
        }
        observer.value?.unobserve(videoCard)
      })
    }, {
      rootMargin: '120px',
      threshold: 0.01,
    })
  }

  nextTick(() => {
    document.querySelectorAll<HTMLElement>('[data-video-id]').forEach((card) => {
      if (observedElements.has(card)) {
        return
      }
      observedElements.add(card)
      observer.value?.observe(card)
    })
  })
}

const loadVideos = async (reset: boolean = false) => {
  if (loading.value) {
    return
  }

  loading.value = true
  const targetPage = reset ? 1 : pageNo.value

  try {
    const result = await fetchChoicenessVideos({
      page: targetPage,
      pageSize,
      ...getCurrentCategoryParams(),
    })

    total.value = result.total
    hasMore.value = videoItems.value.length + result.list.length < result.total

    if (reset) {
      videoItems.value = result.list
      pageNo.value = 2
      loadedVideos.value = new Set()
      observer.value?.disconnect()
    } else {
      videoItems.value.push(...result.list)
      pageNo.value += 1
    }

    if (result.list.length === 0) {
      hasMore.value = false
    }

    initLazyLoad()
  } catch (error) {
    console.error('加载精选页视频失败:', error)
    if (reset) {
      videoItems.value = []
    }
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

const switchCategory = (categoryId: string) => {
  if (activeCategory.value === categoryId) {
    showMoreCategories.value = false
    return
  }

  activeCategory.value = categoryId
  showMoreCategories.value = false
}

const selectOverflowCategory = (categoryId: string) => {
  switchCategory(categoryId)
}

const handleMoreDropdownVisibleChange = (value: boolean) => {
  showMoreCategories.value = value
}

const handleVideoClick = (video: ChoicenessVideoItem) => {
  if (!video?.id) {
    return
  }
  router.push({
    name: 'recommendWithVideo',
    params: {
      videoId: String(video.id),
    },
    state: {
      from: 'choiceness',
    },
  })
}

const handleSmallImageError = (event: Event, video: ChoicenessVideoItem) => {
  const img = event.target as HTMLImageElement

  if (img.dataset.fallbackApplied === 'true') {
    img.src = placeholder
    return
  }

  if (video.coverFallback) {
    img.dataset.fallbackApplied = 'true'
    img.src = video.coverFallback
    return
  }

  img.src = placeholder
}

const handleScroll = () => {
  if (loading.value || !hasMore.value) {
    return
  }

  if (scrollContainer.value) {
    const scrollTop = scrollContainer.value.scrollTop
    const viewportHeight = scrollContainer.value.clientHeight
    const contentHeight = scrollContainer.value.scrollHeight
    if (scrollTop + viewportHeight >= contentHeight - 200) {
      loadVideos()
    }
    return
  }

  const scrollTop = window.scrollY || document.documentElement.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight

  if (scrollTop + windowHeight >= documentHeight - 200) {
    loadVideos()
  }
}

watch(activeCategory, () => {
  pageNo.value = 1
  hasMore.value = true
  loadVideos(true)
})

onMounted(async () => {
  try {
    categories.value = await fetchChoicenessCategories()
  } catch (error) {
    console.error('加载精选分类失败:', error)
  }

  await loadVideos(true)
  scrollContainer.value = document.querySelector('.content-container') as HTMLElement | null
  if (scrollContainer.value) {
    scrollContainer.value.addEventListener('scroll', handleScroll, { passive: true })
  } else {
    window.addEventListener('scroll', handleScroll, { passive: true })
  }
  toggleChoicenessScrollbar(true)
})

onUnmounted(() => {
  observer.value?.disconnect()
  if (scrollContainer.value) {
    scrollContainer.value.removeEventListener('scroll', handleScroll)
  } else {
    window.removeEventListener('scroll', handleScroll)
  }
  toggleChoicenessScrollbar(false)
})

watch(activeCategory, () => {
  showMoreCategories.value = false
})
</script>

<style lang="scss" scoped>
.choiceness-page {
  padding: 0 12px;
  max-width: 100%;
  margin: 0 auto;
  background-color: var(--bg-color);
  min-height: 100%;
}

.category-nav {
  position: sticky;
  top: -10px;
  z-index: 100;
  background-color: var(--bg-color);
  border-bottom: 1px solid var(--border-color);
  margin: 0 -12px 8px;
  padding: 10px 12px;

  .nav-container {
    overflow-x: auto;
    overflow-y: visible;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .nav-scroll {
    display: flex;
    gap: 16px;
    white-space: nowrap;
    padding-bottom: 4px;
    align-items: flex-start;
    overflow: visible;
  }

  .category-btn {
    padding: 8px 20px;
    border: none;
    border-radius: 20px;
    background-color: var(--hover-bg);
    color: var(--text-secondary);
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    flex-shrink: 0;

    &:hover {
      background-color: var(--border-color);
    }

    &.active {
      background-color: var(--primary-color);
      color: #fff;
    }
  }

  .more-categories {
    position: relative;
    flex-shrink: 0;
  }

  .more-btn {
    min-width: 76px;
  }
}

:deep(.choiceness-more-dropdown) {
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: 0 12px 28px var(--shadow-color);
  padding: 8px;
}

:deep(.choiceness-more-dropdown .el-dropdown-menu) {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  border: none;
  box-shadow: none;
  padding: 0;
  min-width: 240px;
  max-width: min(360px, 80vw);
  background: var(--bg-color);
}

:deep(.choiceness-more-dropdown .el-dropdown-menu__item) {
  border-radius: 10px;
  padding: 8px 12px;
  line-height: 1.4;
  color: var(--text-secondary);
  background: var(--hover-bg);
}

:deep(.choiceness-more-dropdown .el-dropdown-menu__item:hover) {
  background: var(--border-color);
  color: var(--text-color);
}

:deep(.choiceness-more-dropdown .el-dropdown-menu__item.is-active) {
  background: var(--primary-color);
  color: #fff;
}

.video-content {
  padding-top: 12px;
}

.video-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 40px;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.video-main {
  .video-card {
    height: 480px;

    @media (max-width: 768px) {
      height: 400px;
    }
  }
}

.video-sidebar {
  position: sticky;
  top: 60px;
  align-self: start;

  @media (max-width: 1024px) {
    position: static;
  }

  .sidebar-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    padding: 10px 12px 12px;
    background: linear-gradient(135deg, var(--primary-color) 0%, #ff6b6b 100%);
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);

    .sidebar-title {
      font-size: 15px;
      font-weight: 700;
      color: #fff;
      margin: 0;
    }

    .sidebar-stats {
      display: flex;
      gap: 8px;

      .stat-item {
        font-size: 11px;
        color: rgba(255, 255, 255, 0.9);
        background: rgba(255, 255, 255, 0.2);
        padding: 3px 8px;
        border-radius: 10px;
        backdrop-filter: blur(4px);
      }
    }
  }

  .video-grid-small {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 17px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  .small-video-item {
    display: flex;
    flex-direction: column;
    background: var(--bg-color);
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    overflow: hidden;
    box-shadow: 0 2px 6px var(--shadow-color);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px var(--shadow-color);
    }
  }

  .small-video-cover {
    position: relative;
    width: 100%;
    height: 150px;
    overflow: hidden;
  }

  .small-cover-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .small-video-duration,
  .small-video-likes {
    position: absolute;
    bottom: 6px;
    background: rgba(0, 0, 0, 0.75);
    color: #fff;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 11px;
  }

  .small-video-duration {
    right: 6px;
  }

  .small-video-likes {
    left: 6px;
    display: flex;
    align-items: center;
    gap: 2px;
  }

  .small-play-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.2s ease;
  }

  .small-play-button {
    width: 28px;
    height: 28px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    color: #000;
  }

  .small-video-item:hover .small-play-overlay {
    opacity: 1;
  }

  .small-video-info {
    padding: 8px 10px;
  }

  .small-video-title {
    font-size: 13px;
    font-weight: 600;
    color: var(--text-color);
    margin: 0 0 4px;
    line-height: 1.3;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .small-video-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
    font-size: 11px;
    color: var(--text-secondary);
  }

  .small-author-entry {
    border: none;
    background: transparent;
    padding: 0;
    margin: 0;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    max-width: calc(100% - 70px);
    cursor: pointer;
    color: inherit;
  }

  .small-author-avatar {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    object-fit: cover;
    flex: 0 0 18px;
  }

  .small-video-author {
    color: var(--text-color);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .small-author-entry:hover .small-video-author {
    color: var(--primary-color);
  }
}

.more-videos {
  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid var(--border-color);
  }

  .section-title {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-color);
    margin: 0;
  }

  .view-toggle {
    display: flex;
    gap: 4px;
    background: var(--hover-bg);
    padding: 4px;
    border-radius: 8px;
  }

  .toggle-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 28px;
    border: none;
    background: transparent;
    color: var(--text-secondary);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: var(--border-color);
    }

    &.active {
      background: var(--primary-color);
      color: #fff;
    }
  }

  .video-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;

    @media (max-width: 768px) {
      grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      gap: 12px;
    }

    @media (max-width: 480px) {
      grid-template-columns: 1fr;
    }

    &.list {
      grid-template-columns: 1fr;
      gap: 12px;

      :deep(.video-card) {
        display: flex;
        flex-direction: row;
        height: auto;

        .video-cover {
          width: 200px;
          height: 120px;
          flex-shrink: 0;
        }

        .video-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: center;
        }
      }
    }
  }
}

.loading-state,
.empty-state {
  padding: 32px 0;
  text-align: center;
  color: var(--text-secondary);
}

:global(.content-container.choiceness-scrollbar-hidden) {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

:global(.content-container.choiceness-scrollbar-hidden::-webkit-scrollbar) {
  width: 0;
  height: 0;
  display: none;
}
</style>

