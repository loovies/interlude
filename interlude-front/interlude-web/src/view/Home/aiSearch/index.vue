<template>
  <div class="ai-search-page" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <div v-if="sidebarCollapsed" class="sidebar-rail">
      <button
        type="button"
        class="sidebar-toggle-btn rail-toggle-btn"
        title="展开对话栏"
        @click="sidebarCollapsed = false"
      >
        <el-icon><Expand /></el-icon>
      </button>
    </div>

    <aside v-else class="session-panel">
      <div class="session-panel-head">
        <button
          type="button"
          class="sidebar-toggle-btn"
          title="收起对话栏"
          @click="sidebarCollapsed = true"
        >
          <el-icon><Fold /></el-icon>
        </button>

        <button type="button" class="new-session-btn" @click="handleCreateSession">
          <span class="new-session-icon">+</span>
          <span>新对话</span>
        </button>
      </div>

      <div class="session-list">
        <button
          v-for="session in sessions"
          :key="session.sessionId"
          type="button"
          class="session-item"
          :class="{ active: session.sessionId === activeSessionId }"
          @click="handleSelectSession(session.sessionId)"
        >
          <h3>{{ session.title || '新对话' }}</h3>
          <p>{{ session.lastMessage || '开始新的提问' }}</p>
        </button>
      </div>
    </aside>

    <section class="chat-panel">
      <div ref="messageListRef" class="message-list" :class="{ empty: messages.length === 0 }">
        <template v-if="messages.length === 0">
          <div class="chat-empty">
            <h1>你好，欢迎使用AI搜索</h1>
            <div class="suggestion-list">
              <button
                v-for="suggestion in suggestions"
                :key="suggestion"
                type="button"
                class="suggestion-item"
                @click="submitSearch(suggestion)"
              >
                {{ suggestion }}
              </button>
            </div>
          </div>
        </template>

        <template v-else>
          <article
            v-for="message in messages"
            :key="message.id"
            class="message-row"
            :class="`role-${message.role}`"
          >
            <div class="message-avatar" :class="{ 'user-avatar-wrap': message.role === 'user' }">
              <template v-if="message.role === 'assistant'">
                <span>AI</span>
              </template>
              <template v-else-if="currentUserAvatar">
                <img class="message-avatar-image" :src="currentUserAvatar" alt="当前用户头像" />
              </template>
              <template v-else>
                <span>{{ currentUserInitial }}</span>
              </template>
            </div>

            <div class="message-main">
              <div class="message-bubble">
                <p class="message-text">{{ message.content }}</p>
              </div>

              <div
                v-if="message.role === 'assistant' && getMessageVideos(message).length"
                class="video-card-grid"
              >
                <button
                  v-for="video in getMessageVideos(message)"
                  :key="video.videoId"
                  type="button"
                  class="video-card"
                  @click="openVideo(video.videoId)"
                >
                  <img
                    class="video-cover"
                    :src="resolveVideoCover(video.videoId, video.coverUrl)"
                    :alt="video.title"
                    @error="handleVideoCoverError($event, video.coverUrl)"
                  />
                  <div class="video-card-body">
                    <div class="video-stats">
                      <span>{{ formatCount(video.likeCount) }} 点赞</span>
                    </div>
                    <h3>{{ video.title }}</h3>
                    <p>{{ video.author?.nickName || '未知作者' }}</p>
                  </div>
                </button>
              </div>
            </div>
          </article>

          <article v-if="loading" class="message-row role-assistant">
            <div class="message-avatar">AI</div>
            <div class="message-main">
              <div class="message-bubble loading-bubble">
                <p class="message-text">正在思考中...</p>
              </div>
            </div>
          </article>
        </template>
      </div>

      <form class="composer" @submit.prevent="handleSubmit">
        <div class="composer-input-wrap">
          <el-input
            v-model="draftMessage"
            type="textarea"
            resize="none"
            :autosize="{ minRows: 4, maxRows: 8 }"
            placeholder="问AI，找答案"
            @keydown.enter.exact.prevent="handleSubmit"
          />
          <button type="submit" class="send-btn" :disabled="submitDisabled">
            <el-icon v-if="!loading"><Promotion /></el-icon>
            <span>{{ loading ? '发送中...' : '发送' }}</span>
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Expand, Fold, Promotion } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import {
  createAiSession,
  fetchAiSessionDetail,
  fetchAiSessions,
  postAiSearch,
  type AiChatMessageItem,
  type AiChatSessionItem,
  type AiSearchResponse,
} from '@/api/chat'
import type { SearchVideoItem } from '@/api/search'

interface ViewMessage extends AiChatMessageItem {
  id: string
}

const FALLBACK_COVER =
  'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzIwIiBoZWlnaHQ9IjE4MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMzIwIiBoZWlnaHQ9IjE4MCIgZmlsbD0iI2VlZWVlZSIvPjwvc3ZnPg=='
const DEFAULT_USER_AVATAR = '/file/avatar/202603/Uh8Hatmr0k8cXbDHuYuP1sNWSxe8Dc.png'

const router = useRouter()
const authStore = useAuthStore()
const messageListRef = ref<HTMLElement | null>(null)
const sessions = ref<AiChatSessionItem[]>([])
const messages = ref<ViewMessage[]>([])
const activeSessionId = ref('')
const draftMessage = ref('')
const loading = ref(false)
const sidebarCollapsed = ref(false)

const suggestions = [
  '诸葛亮为什么娶“丑”老婆',
  '好评TOP5的新能源汽车都有什么',
  '父母该怎么处理孩子早恋的问题',
  '怎么回复女朋友说“你不爱我了”',
  '我想看csgo的视频',
]

const submitDisabled = computed(
  () => loading.value || !draftMessage.value.trim() || !activeSessionId.value,
)

const buildMessageId = (): string => `msg-${Date.now()}-${Math.random().toString(16).slice(2, 8)}`

const resolveMediaUrl = (value?: string): string => {
  if (!value) {
    return ''
  }
  if (/^(https?:)?\/\//.test(value) || value.startsWith('data:image/')) {
    return value
  }
  if (value.startsWith('/file/')) {
    return value
  }
  const cleaned = value.replace(/^\/+/, '')
  if (cleaned.startsWith('file/')) {
    return `/${cleaned}`
  }
  return `/file/${cleaned}`
}

const currentUserAvatar = computed(() => resolveMediaUrl(authStore.currentUser?.avatar || DEFAULT_USER_AVATAR))

const currentUserInitial = computed(() => {
  const source =
    authStore.currentUser?.nickName ||
    authStore.currentUser?.userId ||
    authStore.currentUser?.email ||
    '我'
  return source.slice(0, 1).toUpperCase()
})

const resolveVideoCover = (videoId?: number, fallback?: string): string => {
  if (videoId) {
    return `/api/video/discover/${videoId}/cover`
  }
  return resolveMediaUrl(fallback) || FALLBACK_COVER
}

const handleVideoCoverError = (event: Event, fallback?: string) => {
  const img = event.target as HTMLImageElement
  const fallbackSrc = resolveMediaUrl(fallback)
  if (fallbackSrc && img.dataset.fallbackApplied !== 'true') {
    img.dataset.fallbackApplied = 'true'
    img.src = fallbackSrc
    return
  }
  img.src = FALLBACK_COVER
}

const formatCount = (value?: number): string => {
  if (!value || value <= 0) {
    return '0'
  }
  if (value >= 10000) {
    return `${(value / 10000).toFixed(1)}万`
  }
  return String(value)
}

const toViewMessages = (list: AiChatMessageItem[]): ViewMessage[] =>
  (list || []).map((item) => ({
    ...item,
    id: item.messageId ? `msg-${item.messageId}` : buildMessageId(),
  }))

const getMessageVideos = (message: ViewMessage): SearchVideoItem[] => {
  return message.searchResult?.videoResult?.list || []
}

const scrollToBottom = async () => {
  await nextTick()
  const container = messageListRef.value
  if (!container) {
    return
  }
  container.scrollTop = container.scrollHeight
}

const loadSessions = async () => {
  sessions.value = await fetchAiSessions()
}

const loadSessionDetail = async (sessionId: string) => {
  if (!sessionId) {
    messages.value = []
    return
  }
  const detail = await fetchAiSessionDetail(sessionId)
  activeSessionId.value = detail.sessionId
  messages.value = toViewMessages(detail.messages || [])
  await scrollToBottom()
}

const prependSessionIfMissing = (sessionId: string, title: string) => {
  const existing = sessions.value.find((item) => item.sessionId === sessionId)
  if (existing) {
    return
  }
  sessions.value.unshift({
    sessionId,
    title,
    lastMessage: '',
    messageCount: 0,
    lastActiveTime: '',
  })
}

const handleCreateSession = async () => {
  try {
    const response = await createAiSession()
    activeSessionId.value = response.sessionId
    messages.value = []
    prependSessionIfMissing(response.sessionId, '新对话')
  } catch (error) {
    console.error('Create session failed', error)
    ElMessage.error('新建对话失败')
  }
}

const handleSelectSession = async (sessionId: string) => {
  if (!sessionId || sessionId === activeSessionId.value) {
    return
  }
  try {
    await loadSessionDetail(sessionId)
  } catch (error) {
    console.error('Load session detail failed', error)
    ElMessage.error('加载对话失败')
  }
}

const appendAssistantMessage = (response: AiSearchResponse) => {
  messages.value.push({
    id: buildMessageId(),
    role: 'assistant',
    content: response.answer || '',
    keyword: response.keyword || null,
    searchResult: response.searchResult || null,
  })
}

const submitSearch = async (message: string) => {
  const normalized = message.trim()
  if (!normalized || loading.value || !activeSessionId.value) {
    return
  }

  messages.value.push({
    id: buildMessageId(),
    role: 'user',
    content: normalized,
    searchResult: null,
  })
  draftMessage.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const response = await postAiSearch({
      message: normalized,
      sessionId: activeSessionId.value,
    })
    appendAssistantMessage(response)
    await loadSessions()
    await scrollToBottom()
  } catch (error) {
    console.error('AI search request failed', error)
    ElMessage.error('AI搜索失败，请稍后再试')
    messages.value.push({
      id: buildMessageId(),
      role: 'assistant',
      content: '这次没有成功返回内容，你可以换个说法再问一次。',
      searchResult: null,
    })
    await scrollToBottom()
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  await submitSearch(draftMessage.value)
}

const openVideo = async (videoId?: number) => {
  if (!videoId) {
    return
  }
  await router.push({
    name: 'recommendWithVideo',
    params: {
      videoId: String(videoId),
    },
  })
}

onMounted(async () => {
  try {
    await loadSessions()
    const firstSession = sessions.value[0]
    if (firstSession?.sessionId) {
      await loadSessionDetail(firstSession.sessionId)
      return
    }
    await handleCreateSession()
  } catch (error) {
    console.error('Init ai search page failed', error)
    ElMessage.error('初始化AI搜索失败')
  }
})
</script>

<style scoped lang="scss">
.ai-search-page {
  display: grid;
  grid-template-columns: 236px minmax(0, 1fr);
  gap: 16px;
  height: 100%;
  min-height: 0;
  padding: 6px 4px 12px;
  background: var(--bg-color);
  overflow: hidden;
}

.ai-search-page.sidebar-collapsed {
  grid-template-columns: 44px minmax(0, 1fr);
}

.sidebar-rail {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  height: 100%;
  min-height: 0;
  padding-top: 10px;
}

.session-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  background: color-mix(in srgb, var(--sidebar-bg) 96%, transparent);
  border: 1px solid color-mix(in srgb, var(--border-color) 82%, transparent);
  border-radius: 16px;
  overflow: hidden;
}

.session-panel-head {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 0 0 auto;
  padding: 14px 12px;
  border-bottom: 1px solid color-mix(in srgb, var(--border-color) 78%, transparent);
}

.sidebar-toggle-btn,
.new-session-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 38px;
  border-radius: 999px;
  background: transparent;
  color: var(--text-color);
  cursor: pointer;
}

.sidebar-toggle-btn {
  width: 38px;
  flex: 0 0 38px;
  border: 1px solid color-mix(in srgb, var(--border-color) 80%, transparent);
}

.rail-toggle-btn {
  background: color-mix(in srgb, var(--sidebar-bg) 96%, transparent);
}

.new-session-btn {
  flex: 1;
  min-width: 0;
  padding: 0 14px;
  border: 1px solid color-mix(in srgb, var(--primary-color) 50%, transparent);
}

.new-session-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 1px solid currentColor;
  font-size: 14px;
  line-height: 1;
}

.session-list {
  flex: 1;
  min-height: 0;
  padding: 10px;
  overflow-y: auto;
}

.session-item {
  width: 100%;
  padding: 12px;
  border: 1px solid transparent;
  border-radius: 12px;
  background: transparent;
  color: var(--text-color);
  text-align: left;
  cursor: pointer;
}

.session-item + .session-item {
  margin-top: 8px;
}

.session-item:hover,
.session-item.active {
  background: color-mix(in srgb, var(--hover-bg) 88%, transparent);
  border-color: color-mix(in srgb, var(--border-color) 76%, transparent);
}

.session-item h3 {
  margin: 0;
  font-size: 14px;
  line-height: 1.4;
}

.session-item p {
  margin: 6px 0 0;
  color: var(--text-secondary);
  font-size: 12px;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  background: color-mix(in srgb, var(--sidebar-bg) 94%, transparent);
  border: 1px solid color-mix(in srgb, var(--border-color) 82%, transparent);
  border-radius: 18px;
  overflow: hidden;
}

.message-list {
  flex: 1;
  min-height: 0;
  padding: 24px;
  overflow-y: auto;
}

.message-list.empty {
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-empty {
  width: min(780px, 100%);
}

.chat-empty h1 {
  margin: 0 0 22px;
  font-size: 32px;
  line-height: 1.25;
  color: var(--text-color);
}

.suggestion-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.suggestion-item {
  padding: 12px 16px;
  border: 1px solid color-mix(in srgb, var(--border-color) 80%, transparent);
  border-radius: 14px;
  background: color-mix(in srgb, var(--bg-color) 92%, transparent);
  color: var(--text-color);
  font-size: 14px;
  line-height: 1.6;
  cursor: pointer;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.message-row + .message-row {
  margin-top: 18px;
}

.role-user {
  justify-content: flex-end;
}

.role-user .message-avatar {
  order: 2;
}

.role-user .message-main {
  order: 1;
  align-items: flex-end;
}

.message-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  flex: 0 0 36px;
  border-radius: 50%;
  background: color-mix(in srgb, var(--primary-color) 14%, transparent);
  color: var(--text-color);
  font-size: 13px;
  font-weight: 700;
}

.user-avatar-wrap {
  padding: 3px;
  background: color-mix(in srgb, var(--border-color) 58%, transparent);
}

.message-avatar-image {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.message-main {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: min(960px, calc(100% - 48px));
}

.message-bubble {
  max-width: 100%;
  padding: 16px 18px;
  border-radius: 18px;
  background: color-mix(in srgb, var(--bg-color) 92%, transparent);
}

.role-user .message-bubble {
  background: #1890ff;
  color: #fff;
}

.loading-bubble {
  opacity: 0.82;
}

.message-text {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.video-card-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.video-card {
  overflow: hidden;
  border: 1px solid color-mix(in srgb, var(--border-color) 78%, transparent);
  border-radius: 14px;
  background: color-mix(in srgb, var(--bg-color) 96%, transparent);
  color: var(--text-color);
  text-align: left;
  cursor: pointer;
}

.video-cover {
  display: block;
  width: 100%;
  aspect-ratio: 4 / 5;
  object-fit: cover;
  background: var(--hover-bg);
}

.video-card-body {
  padding: 10px 12px 12px;
}

.video-stats {
  color: var(--text-secondary);
  font-size: 12px;
}

.video-card-body h3 {
  display: -webkit-box;
  margin: 8px 0 0;
  overflow: hidden;
  font-size: 14px;
  line-height: 1.5;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-card-body p {
  margin: 8px 0 0;
  color: var(--text-secondary);
  font-size: 13px;
}

.composer {
  flex: 0 0 auto;
  padding: 18px 20px 20px;
  border-top: 1px solid color-mix(in srgb, var(--border-color) 78%, transparent);
  background: color-mix(in srgb, var(--bg-color) 94%, transparent);
}

.composer-input-wrap {
  position: relative;
}

.composer-input-wrap :deep(.el-textarea__inner) {
  min-height: 132px;
  padding: 18px 20px 72px;
  border: 1px solid color-mix(in srgb, var(--border-color) 78%, transparent);
  border-radius: 24px;
  background: color-mix(in srgb, var(--bg-color) 100%, transparent);
  color: var(--text-color);
  box-shadow: none;
}

.send-btn {
  position: absolute;
  right: 16px;
  bottom: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 108px;
  height: 42px;
  border: none;
  border-radius: 999px;
  background: color-mix(in srgb, #1890ff 92%, #ffffff 8%);
  color: #fff;
  cursor: pointer;
  z-index: 1;
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 1180px) {
  .video-card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 860px) {
  .ai-search-page,
  .ai-search-page.sidebar-collapsed {
    grid-template-columns: 1fr;
  }

  .sidebar-rail {
    display: none;
  }

  .session-panel {
    height: 180px;
  }

  .chat-empty h1 {
    font-size: 28px;
  }
}

@media (max-width: 640px) {
  .message-list {
    padding: 16px;
  }

  .chat-empty h1 {
    font-size: 26px;
  }

  .composer {
    padding: 14px 14px 16px;
  }

  .composer-input-wrap :deep(.el-textarea__inner) {
    min-height: 120px;
    padding: 16px 16px 68px;
  }

  .send-btn {
    right: 12px;
    bottom: 12px;
    min-width: 92px;
  }

  .video-card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
