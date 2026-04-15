<template>
  <div class="follow-page">
    <section v-if="!authStore.isLoggedIn" class="follow-empty">
      <h2>登录后查看关注内容</h2>
      <p>这里会展示你关注用户发布的视频</p>
      <button type="button" class="primary-btn" @click="authStore.openLoginDialog('follow')">立即登录</button>
    </section>

    <div v-else class="follow-layout" :class="{ 'avatar-only': isListAvatarOnly }">
      <aside class="follow-list-panel">
        <div class="follow-tools">
          <button type="button" class="list-toggle-btn" @click="toggleManualCollapse">
            <svg v-if="isListAvatarOnly" class="toggle-icon" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M9 6l6 6-6 6" />
            </svg>
            <svg v-else class="toggle-icon" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M15 6l-6 6 6 6" />
            </svg>
            <span class="toggle-text">列表</span>
          </button>
          <div v-if="!isListAvatarOnly" ref="sortMenuRef" class="sort-filter-wrap">
            <span class="sort-label">综合排序</span>
            <button type="button" class="sort-filter-btn" @click.stop="toggleSortMenu">
              <svg class="sort-filter-icon" viewBox="0 0 24 24" aria-hidden="true">
                <path d="M3 5h18l-7 8v5l-4 2v-7z" />
              </svg>
            </button>
            <div v-if="isSortMenuOpen" class="sort-dropdown" @click.stop>
              <button
                v-for="option in sortOptions"
                :key="option.value"
                type="button"
                class="sort-dropdown-item"
                :class="{ active: followSortType === option.value }"
                @click="handleSortSelect(option.value)"
              >
                {{ option.label }}
              </button>
            </div>
          </div>
        </div>

        <header v-if="!isListAvatarOnly" class="follow-list-header">
          <span>我的关注</span>
          <span class="follow-count">{{ followUsers.length }}</span>
        </header>

        <div v-if="loading" class="follow-list-empty">加载中...</div>
        <div v-else-if="followUsers.length === 0" class="follow-list-empty">还没有关注用户</div>
        <ul v-else class="follow-list">
          <li v-for="item in displayedFollowUsers" :key="item.userId" class="follow-item">
            <img class="follow-avatar" :src="resolveFollowAvatar(item)" :alt="`${item.nickName || item.userId}头像`" />
            <span class="follow-name">{{ item.nickName || item.userId }}</span>
          </li>
        </ul>
      </aside>

      <section class="follow-feed-panel">
        <div v-if="!loading && followVideoList.length === 0" class="feed-empty">
          <p>暂时没有关注用户发布的视频</p>
          <button type="button" class="feed-empty-btn" @click="goRecommend">去推荐页看看</button>
        </div>
        <VideoFeed
          v-else
          :external-video-list="followVideoList"
          @comment-panel-change="handleCommentPanelChange"
        />
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import VideoFeed from '@/components/video/VideoFeed.vue'
import { useAuthStore } from '@/stores/auth'
import { fetchUserFollowList, type UserFollowListItem } from '@/api/video'
import { fetchVideoList, type VideoData } from '@/utils/mockData'

type FollowUserViewItem = UserFollowListItem & {
  __order: number
  __followTimestamp: number
}

const authStore = useAuthStore()
const router = useRouter()

const followUsers = ref<FollowUserViewItem[]>([])
const followVideoList = ref<VideoData[]>([])
const loading = ref(false)
const isCommentPanelOpen = ref(false)
const isManualCollapsed = ref(false)
const followSortType = ref<'default' | 'latest' | 'earliest'>('default')
const isSortMenuOpen = ref(false)
const sortMenuRef = ref<HTMLElement | null>(null)

const FOLLOW_USER_LIMIT = 300
const VIDEO_PAGE_SIZE = 20
const VIDEO_PAGE_LIMIT = 12
const VIDEO_TARGET_COUNT = 80
const sortOptions: Array<{ value: 'default' | 'latest' | 'earliest'; label: string }> = [
  { value: 'default', label: '综合排序' },
  { value: 'latest', label: '最新关注' },
  { value: 'earliest', label: '最早关注' },
]

const isListAvatarOnly = computed(() => isCommentPanelOpen.value || isManualCollapsed.value)

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

const buildAvatarFallback = (seed: string): string => {
  const safeSeed = encodeURIComponent(seed || 'follow-user')
  return `https://picsum.photos/seed/${safeSeed}/96/96`
}

const resolveFollowAvatar = (item: UserFollowListItem): string => {
  return resolveAvatarUrl(item.avatar) || buildAvatarFallback(item.userId || item.nickName || 'follow-user')
}

const goRecommend = () => {
  router.push({ name: 'recommend' })
}

const toggleManualCollapse = () => {
  isManualCollapsed.value = !isManualCollapsed.value
}

const toggleSortMenu = () => {
  isSortMenuOpen.value = !isSortMenuOpen.value
}

const handleSortSelect = (value: 'default' | 'latest' | 'earliest') => {
  followSortType.value = value
  isSortMenuOpen.value = false
}

const handleCommentPanelChange = (visible: boolean) => {
  isCommentPanelOpen.value = visible
}

const parseFollowTime = (item: UserFollowListItem): number => {
  const source = item as UserFollowListItem & {
    followTime?: string | number
    createTime?: string | number
    updateTime?: string | number
    followAt?: string | number
    gmtCreate?: string | number
    gmtModified?: string | number
  }
  const candidates = [
    source.followTime,
    source.createTime,
    source.updateTime,
    source.followAt,
    source.gmtCreate,
    source.gmtModified,
  ]

  for (const value of candidates) {
    if (value === null || value === undefined || value === '') {
      continue
    }
    if (typeof value === 'number' && Number.isFinite(value)) {
      if (value <= 0) {
        continue
      }
      return value > 1e12 ? value : value * 1000
    }
    if (typeof value === 'string') {
      const numeric = Number(value)
      if (Number.isFinite(numeric) && numeric > 0) {
        return numeric > 1e12 ? numeric : numeric * 1000
      }
      const parsed = Date.parse(value)
      if (Number.isFinite(parsed) && parsed > 0) {
        return parsed
      }
    }
  }

  return 0
}

const displayedFollowUsers = computed<FollowUserViewItem[]>(() => {
  const list = [...followUsers.value]
  if (followSortType.value === 'default') {
    return list
  }

  list.sort((a, b) => {
    const aTime = Number(a.__followTimestamp || 0)
    const bTime = Number(b.__followTimestamp || 0)
    if (aTime === bTime) {
      if (followSortType.value === 'latest') {
        return a.__order - b.__order
      }
      return b.__order - a.__order
    }
    if (followSortType.value === 'latest') {
      return bTime - aTime
    }
    return aTime - bTime
  })
  return list
})

const loadFollowVideos = async (followedUserIds: Set<string>) => {
  const videos: VideoData[] = []
  const seenVideoIds = new Set<string>()
  let total = 0

  for (let pageNo = 1; pageNo <= VIDEO_PAGE_LIMIT; pageNo += 1) {
    const result = await fetchVideoList(pageNo, VIDEO_PAGE_SIZE)
    const pageVideos = Array.isArray(result?.data) ? result.data : []
    total = Number(result?.total || 0)

    pageVideos.forEach((video) => {
      const authorId = normalizeUserId(video.authorId)
      if (!authorId || !followedUserIds.has(authorId)) {
        return
      }
      const key = String(video.videoId)
      if (seenVideoIds.has(key)) {
        return
      }
      seenVideoIds.add(key)
      videos.push(video)
    })

    if (videos.length >= VIDEO_TARGET_COUNT) {
      break
    }
    if (pageVideos.length < VIDEO_PAGE_SIZE) {
      break
    }
    if (total > 0 && pageNo * VIDEO_PAGE_SIZE >= total) {
      break
    }
  }

  followVideoList.value = videos
}

const loadFollowPageData = async () => {
  if (!authStore.isLoggedIn) {
    followUsers.value = []
    followVideoList.value = []
    return
  }

  const currentUserId = normalizeUserId(authStore.currentUser?.userId)
  if (!currentUserId) {
    followUsers.value = []
    followVideoList.value = []
    return
  }

  loading.value = true
  try {
    const list = await fetchUserFollowList(currentUserId, 'following', FOLLOW_USER_LIMIT)
    const normalizedList = (Array.isArray(list) ? list : []).filter((item) => normalizeUserId(item.userId))
    const deduplicated: FollowUserViewItem[] = []
    const seenUserIds = new Set<string>()

    normalizedList.forEach((item) => {
      const userId = normalizeUserId(item.userId)
      if (!userId || seenUserIds.has(userId)) {
        return
      }
      seenUserIds.add(userId)
      const followTs = parseFollowTime(item)
      deduplicated.push({
        ...item,
        userId,
        __order: deduplicated.length,
        __followTimestamp: followTs,
      })
    })

    followUsers.value = deduplicated
    if (!seenUserIds.size) {
      followVideoList.value = []
      return
    }

    await loadFollowVideos(seenUserIds)
  } catch (error: any) {
    followUsers.value = []
    followVideoList.value = []
    const message = error?.response?.data?.info || error?.message || '加载关注页失败，请稍后重试'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}

watch(
  () => [authStore.isLoggedIn, normalizeUserId(authStore.currentUser?.userId)],
  () => {
    isCommentPanelOpen.value = false
    isManualCollapsed.value = false
    isSortMenuOpen.value = false
    void loadFollowPageData()
  },
  { immediate: true },
)

watch(
  () => isListAvatarOnly.value,
  (value) => {
    if (value) {
      isSortMenuOpen.value = false
    }
  },
)

const handleDocumentMouseDown = (event: MouseEvent) => {
  if (!isSortMenuOpen.value) {
    return
  }
  const target = event.target as Node | null
  if (!target) {
    return
  }
  if (sortMenuRef.value && !sortMenuRef.value.contains(target)) {
    isSortMenuOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('mousedown', handleDocumentMouseDown)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', handleDocumentMouseDown)
})
</script>

<style scoped lang="scss">
.follow-page {
  height: calc(100vh - 80px);
  overflow: hidden;
  color: var(--text-color);
}

.follow-empty {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  text-align: center;

  h2 {
    margin: 0;
    font-size: 22px;
    font-weight: 700;
  }

  p {
    margin: 0;
    font-size: 14px;
    color: var(--text-secondary);
  }
}

.primary-btn {
  border: none;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  padding: 10px 18px;
  border-radius: 999px;
  background: linear-gradient(120deg, #fe2c55 0%, #ff5f47 100%);
}

.follow-layout {
  height: 100%;
  display: flex;
  gap: 8px;
}

.follow-list-panel {
  width: 192px;
  flex: 0 0 192px;
  height: 100%;
  border: none;
  background: transparent;
  overflow: hidden;
  transition: width 0.24s ease, flex-basis 0.24s ease;
}

.follow-layout.avatar-only .follow-list-panel {
  width: 86px;
  flex-basis: 86px;
}

.follow-tools {
  height: 36px;
  padding: 2px 6px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.follow-layout.avatar-only .follow-tools {
  justify-content: flex-start;
  padding-left: 2px;
}

.follow-layout.avatar-only .list-toggle-btn {
  padding-left: 0;
}

.list-toggle-btn {
  border: none;
  background: transparent;
  color: var(--text-color);
  cursor: pointer;
  height: 30px;
  padding: 0 4px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
}

.toggle-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.toggle-text {
  line-height: 1;
}

.sort-filter-wrap {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
  font-size: 12px;
}

.sort-label {
  white-space: nowrap;
}

.sort-filter-btn {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-color);
  background: color-mix(in srgb, var(--bg-color) 86%, #97a2be 14%);
}

.sort-filter-icon {
  width: 14px;
  height: 14px;
  fill: currentColor;
}

.sort-dropdown {
  position: absolute;
  top: 34px;
  right: 0;
  z-index: 20;
  width: 118px;
  border-radius: 10px;
  border: 1px solid var(--border-color);
  background: color-mix(in srgb, var(--bg-color) 90%, #a0abc4 10%);
  box-shadow: 0 10px 24px color-mix(in srgb, var(--shadow-color) 35%, transparent);
  padding: 4px;
}

.sort-dropdown-item {
  width: 100%;
  border: none;
  cursor: pointer;
  border-radius: 8px;
  height: 28px;
  padding: 0 10px;
  text-align: left;
  font-size: 12px;
  color: var(--text-color);
  background: transparent;
}

.sort-dropdown-item:hover {
  background: color-mix(in srgb, var(--bg-color) 84%, #97a2be 16%);
}

.sort-dropdown-item.active {
  color: #fff;
  background: linear-gradient(120deg, #fe2c55 0%, #ff5f47 100%);
}

.follow-list-header {
  height: 42px;
  padding: 0 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  font-weight: 600;
}

.follow-count {
  min-width: 26px;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #fff;
  background: linear-gradient(120deg, #fe2c55 0%, #ff5f47 100%);
}

.follow-list {
  margin: 0;
  padding: 4px 0;
  list-style: none;
  height: calc(100% - 78px);
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none;
}

.follow-list::-webkit-scrollbar {
  width: 0;
  height: 0;
  display: none;
}

.follow-item {
  height: 54px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 6px;
}

.follow-item + .follow-item {
  margin-top: 2px;
}

.follow-item:hover {
  background: color-mix(in srgb, var(--bg-color) 92%, #8f9abc 8%);
}

.follow-avatar {
  width: 40px;
  height: 40px;
  flex: 0 0 40px;
  border-radius: 50%;
  object-fit: cover;
  background: #151515;
}

.follow-name {
  flex: 1;
  min-width: 0;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  opacity: 1;
  max-width: 150px;
  transition: opacity 0.2s ease, max-width 0.2s ease, margin 0.2s ease;
}

.follow-layout.avatar-only .follow-name {
  opacity: 0;
  max-width: 0;
  margin: 0;
  pointer-events: none;
}

.follow-list-empty {
  height: calc(100% - 78px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  font-size: 13px;
}

.follow-layout.avatar-only .follow-list,
.follow-layout.avatar-only .follow-list-empty {
  height: calc(100% - 36px);
}

.follow-feed-panel {
  flex: 1;
  min-width: 0;
  height: 100%;
  overflow: hidden;
}

.feed-empty {
  height: 100%;
  border-radius: 16px;
  border: 1px dashed var(--border-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: var(--text-secondary);
}

.feed-empty-btn {
  border: none;
  height: 36px;
  padding: 0 16px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  cursor: pointer;
  background: linear-gradient(120deg, #fe2c55 0%, #ff5f47 100%);
}

@media (max-width: 980px) {
  .follow-layout {
    gap: 8px;
  }

  .follow-list-panel {
    width: 86px;
    flex-basis: 86px;
  }

  .sort-filter-wrap {
    display: none;
  }

  .follow-name {
    opacity: 0;
    max-width: 0;
    margin: 0;
    pointer-events: none;
  }
}
</style>
