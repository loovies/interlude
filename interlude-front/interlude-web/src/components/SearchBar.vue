<template>
  <div class="search-root" :class="`mode-${mode}`">
    <button
      v-if="mode === 'page' && showBack"
      class="back-btn"
      type="button"
      @click="handleBack"
    >
      <el-icon><ArrowLeft /></el-icon>
    </button>

    <div ref="rootRef" class="search-shell">
      <div class="search-bar">
        <input
          ref="inputRef"
          v-model="keyword"
          type="text"
          class="search-input"
          :placeholder="placeholder"
          @focus="handleFocus"
          @keyup.enter="handleSubmit"
        />
        <div class="button-group">
          <button class="search-btn" type="button" @click="handleSubmit">{{ uiText.search }}</button>
        </div>
      </div>

      <div v-if="panelVisible" class="search-panel">
        <div v-if="panelLoading" class="panel-loading">{{ uiText.loading }}</div>
        <template v-else>
          <section v-if="showSearchHistorySection" class="panel-section">
            <div class="panel-head">
              <div class="panel-title">{{ uiText.searchHistory }}</div>
              <button
                v-if="panelData.recentKeywords.length"
                type="button"
                class="panel-clear-btn"
                @click="handleClearHistory"
              >
                {{ uiText.clearAll }}
              </button>
            </div>

            <div v-if="panelData.recentKeywords.length" class="history-keyword-list">
              <div
                v-for="recentKeyword in panelData.recentKeywords"
                :key="recentKeyword"
                class="history-keyword-item"
              >
                <button
                  type="button"
                  class="history-keyword-btn"
                  @click="handlePickKeyword(recentKeyword)"
                >
                  {{ recentKeyword }}
                </button>
                <button
                  type="button"
                  class="history-delete-btn"
                  :aria-label="`${uiText.deleteHistory} ${recentKeyword}`"
                  @click.stop="handleDeleteHistory(recentKeyword)"
                >
                  <el-icon><Close /></el-icon>
                </button>
              </div>
            </div>

            <div v-else class="panel-empty">{{ uiText.emptyHistory }}</div>
          </section>

          <section class="panel-section">
            <div class="panel-title">{{ uiText.hotKeywords }}</div>
            <div class="hot-list">
              <button
                v-for="hotKeyword in panelData.hotKeywords"
                :key="hotKeyword"
                type="button"
                class="hot-item"
                @click="handlePickKeyword(hotKeyword)"
              >
                {{ hotKeyword }}
              </button>
            </div>
          </section>
        </template>
      </div>
    </div>

    <div v-if="mode === 'home'" class="button-bar">
      <div class="button-list">
        <span>{{ uiText.following }}</span>
        <span>{{ uiText.alerts }}</span>
        <span>{{ uiText.messages }}</span>
        <span @click="handleUploadClick">{{ uiText.upload }}</span>
      </div>
      <button v-if="!authStore.isLoggedIn" class="login-btn" type="button" @click="handleLoginClick">
        {{ uiText.signIn }}
      </button>
      <el-popover v-else placement="bottom-end" trigger="click" :width="260" popper-class="top-user-popover">
        <template #reference>
          <button class="user-entry" type="button">
            <el-avatar :size="34" :src="avatarUrl" class="profile-jump" @click.stop="handleOpenCurrentProfile">
              {{ userInitial }}
            </el-avatar>
            <span class="user-name profile-jump" @click.stop="handleOpenCurrentProfile">{{ displayName }}</span>
            <span class="entry-more">﹀</span>
          </button>
        </template>
        <div class="user-card">
          <div class="user-main profile-jump" @click="handleOpenCurrentProfile">
            <el-avatar :size="44" :src="avatarUrl">
              {{ userInitial }}
            </el-avatar>
            <div class="user-text">
              <h4>{{ displayName }}</h4>
              <p>{{ authStore.currentUser?.email || uiText.account }}</p>
            </div>
          </div>
          <el-button type="primary" plain class="logout-btn" :loading="logoutLoading" @click="handleLogout">
            {{ uiText.signOut }}
          </el-button>
        </div>
      </el-popover>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeft, Close } from '@element-plus/icons-vue'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { clearSearchHistory, deleteSearchHistoryKeyword, fetchSearchPanel, type SearchPanelData } from '@/api/search'
import { useAuthStore } from '@/stores/auth'

type SearchBarMode = 'home' | 'page'

interface Props {
  mode?: SearchBarMode
  modelValue?: string
  placeholder?: string
  showBack?: boolean
  navigateOnSearch?: boolean
  autoFocus?: boolean
}

const uiText = {
  placeholder: '\u641c\u7d22\u4f60\u611f\u5174\u8da3\u7684\u5185\u5bb9',
  search: '\u641c\u7d22',
  loading: '\u52a0\u8f7d\u4e2d...',
  searchHistory: '\u641c\u7d22\u5386\u53f2',
  clearAll: '\u5168\u90e8\u6e05\u7a7a',
  deleteHistory: '\u5220\u9664',
  emptyHistory: '\u6682\u65e0\u641c\u7d22\u5386\u53f2',
  hotKeywords: '\u70ed\u95e8\u641c\u7d22',
  following: '\u5173\u6ce8',
  alerts: '\u901a\u77e5',
  messages: '\u6d88\u606f',
  upload: '\u6295\u7a3f',
  signIn: '\u767b\u5f55',
  signOut: '\u9000\u51fa\u767b\u5f55',
  account: 'Interlude \u7528\u6237',
  user: '\u7528\u6237',
  logoutSuccess: '\u5df2\u9000\u51fa\u767b\u5f55',
  deleteHistorySuccess: '\u5df2\u5220\u9664\u641c\u7d22\u5386\u53f2',
  clearHistorySuccess: '\u5df2\u6e05\u7a7a\u641c\u7d22\u5386\u53f2',
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'home',
  modelValue: '',
  placeholder: '\u641c\u7d22\u4f60\u611f\u5174\u8da3\u7684\u5185\u5bb9',
  showBack: false,
  navigateOnSearch: true,
  autoFocus: false,
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'search', value: string): void
  (e: 'back'): void
}>()

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const keyword = ref(props.modelValue)
const panelVisible = ref(false)
const panelLoading = ref(false)
const logoutLoading = ref(false)
const panelData = ref<SearchPanelData>({
  playHistory: [],
  recentKeywords: [],
  hotKeywords: [],
})
const rootRef = ref<HTMLElement | null>(null)
const inputRef = ref<HTMLInputElement | null>(null)

const showSearchHistorySection = computed(
  () => authStore.isLoggedIn || panelData.value.recentKeywords.length > 0
)

watch(
  () => props.modelValue,
  (value) => {
    if (value !== keyword.value) {
      keyword.value = value
    }
  }
)

watch(keyword, (value) => {
  emit('update:modelValue', value)
})

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

const displayName = computed(() => {
  const user = authStore.currentUser
  return user?.nickName || user?.email || user?.phone || uiText.user
})

const userInitial = computed(() => displayName.value.slice(0, 1).toUpperCase())
const avatarUrl = computed(() => resolveAvatarUrl(authStore.currentUser?.avatar))

const loadPanel = async () => {
  panelLoading.value = true
  try {
    panelData.value = await fetchSearchPanel()
  } catch (error) {
    console.warn('Failed to load search panel data', error)
    panelData.value = {
      playHistory: [],
      recentKeywords: [],
      hotKeywords: [],
    }
  } finally {
    panelLoading.value = false
  }
}

const openSearchRoute = async (value: string) => {
  const targetQuery = {
    keyword: value,
    tab: 'all',
    pageNo: '1',
  }
  if (route.path === '/search') {
    await router.replace({ path: '/search', query: targetQuery })
    return
  }
  await router.push({ path: '/search', query: targetQuery })
}

const submitKeyword = async (value: string) => {
  const normalized = value.trim()
  if (!normalized) {
    return
  }
  keyword.value = normalized
  panelVisible.value = false
  emit('search', normalized)
  if (props.navigateOnSearch) {
    await openSearchRoute(normalized)
  }
}

const handleFocus = async () => {
  panelVisible.value = true
  await loadPanel()
}

const handleSubmit = async () => {
  await submitKeyword(keyword.value)
}

const handlePickKeyword = async (value: string) => {
  await submitKeyword(value)
}

const handleDeleteHistory = async (value: string) => {
  const normalized = value.trim()
  if (!normalized) {
    return
  }
  await deleteSearchHistoryKeyword(normalized)
  panelData.value.recentKeywords = panelData.value.recentKeywords.filter((item) => item !== normalized)
  ElMessage.success(uiText.deleteHistorySuccess)
}

const handleClearHistory = async () => {
  await clearSearchHistory()
  panelData.value.recentKeywords = []
  ElMessage.success(uiText.clearHistorySuccess)
}

const handleDocumentMouseDown = (event: MouseEvent) => {
  if (!panelVisible.value) {
    return
  }
  const target = event.target as Node | null
  if (rootRef.value && target && !rootRef.value.contains(target)) {
    panelVisible.value = false
  }
}

const handleLoginClick = () => {
  authStore.openLoginDialog('manual')
}

const handleUploadClick = async () => {
  if (!authStore.isLoggedIn) {
    authStore.openLoginDialog('publish')
    await router.replace({
      path: route.path,
      query: {
        ...route.query,
        loginRedirect: '/creator/publish',
      },
    })
    return
  }
  const target = router.resolve('/creator/publish')
  window.open(target.href, '_blank', 'noopener,noreferrer')
}

const handleOpenCurrentProfile = () => {
  const user = authStore.currentUser
  const userId = user?.userId ? String(user.userId).trim() : ''
  if (!userId) {
    return
  }
  const target = router.resolve({
    path: '/mine',
    query: {
      userId,
      nickName: user?.nickName || '',
      avatar: user?.avatar || '',
      focusCount: user?.focusCount,
      fansCount: user?.fansCount,
      likeCount: user?.likeCount,
    },
  })
  window.open(target.href, '_blank', 'noopener,noreferrer')
}

const handleLogout = async () => {
  if (logoutLoading.value) {
    return
  }
  logoutLoading.value = true
  try {
    await authStore.logout()
    ElMessage.success(uiText.logoutSuccess)
  } finally {
    logoutLoading.value = false
  }
}

const handleBack = () => {
  emit('back')
}

onMounted(() => {
  document.addEventListener('mousedown', handleDocumentMouseDown)
  if (props.autoFocus) {
    nextTick(() => inputRef.value?.focus())
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', handleDocumentMouseDown)
})
</script>

<style scoped lang="scss">
.search-root {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 48px;
}

.mode-home {
  justify-content: flex-end;
  padding: 0 16px;
  z-index: 220;
}

.mode-page {
  justify-content: flex-start;
  width: 100%;
}

.search-shell {
  position: relative;
  width: min(640px, calc(100% - 420px));
}

.mode-home .search-shell {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  z-index: 220;
}

.mode-page .search-shell {
  position: relative;
  transform: none;
  left: auto;
  width: min(780px, 100%);
  flex: 1;
}

.search-bar {
  display: flex;
  align-items: center;
  width: 100%;
  border: 1px solid color-mix(in srgb, var(--border-color) 85%, transparent);
  border-radius: 18px;
  padding: 4px 4px 4px 16px;
  background: color-mix(in srgb, var(--sidebar-bg) 90%, transparent);
  box-shadow: 0 8px 20px color-mix(in srgb, var(--shadow-color) 35%, transparent);
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: var(--text-color);
  font-size: 15px;
  min-width: 0;
}

.search-input::placeholder {
  color: color-mix(in srgb, var(--text-secondary) 85%, transparent);
}

.button-group {
  margin-left: 8px;
}

.search-btn {
  border: none;
  border-radius: 14px;
  padding: 8px 18px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  background: linear-gradient(135deg, var(--primary-color), #ff8f63);
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.search-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.search-panel {
  position: absolute;
  top: calc(100% + 10px);
  left: 0;
  right: 0;
  z-index: 260;
  border: 1px solid color-mix(in srgb, var(--border-color) 85%, transparent);
  border-radius: 18px;
  background: color-mix(in srgb, var(--sidebar-bg) 96%, #ffffff 4%);
  box-shadow: 0 18px 36px color-mix(in srgb, var(--shadow-color) 42%, transparent);
  padding: 18px;
}

.panel-loading {
  color: var(--text-secondary);
  font-size: 14px;
  padding: 14px 0;
}

.panel-section + .panel-section {
  margin-top: 20px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color);
}

.panel-clear-btn {
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  padding: 0;
  white-space: nowrap;
  transition: color 0.2s ease;
}

.panel-clear-btn:hover {
  color: var(--primary-color);
}

.panel-empty {
  font-size: 13px;
  color: var(--text-secondary);
  padding: 6px 0 2px;
}

.history-keyword-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.history-keyword-item {
  display: inline-flex;
  align-items: center;
  border: 1px solid color-mix(in srgb, var(--border-color) 70%, transparent);
  border-radius: 999px;
  background: color-mix(in srgb, var(--hover-bg) 82%, #ffffff 18%);
  box-shadow: 0 6px 14px color-mix(in srgb, var(--shadow-color) 10%, transparent);
  transition: border-color 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease,
    transform 0.2s ease;
}

.history-keyword-item:hover {
  border-color: color-mix(in srgb, var(--primary-color) 55%, transparent);
  background: color-mix(in srgb, var(--hover-bg) 74%, #ffffff 26%);
  box-shadow: 0 10px 18px color-mix(in srgb, var(--shadow-color) 16%, transparent);
  transform: translateY(-1px);
}

.history-keyword-btn {
  border: none;
  background: transparent;
  color: var(--text-color);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  padding: 8px 6px 8px 14px;
  max-width: 220px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-delete-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  margin: 0 5px 0 0;
  transition: color 0.2s ease, background-color 0.2s ease;
}

.history-delete-btn:hover {
  color: var(--primary-color);
  background: color-mix(in srgb, var(--hover-bg) 88%, transparent);
}

.hot-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 10px;
}

.hot-item {
  border: 1px solid color-mix(in srgb, var(--border-color) 82%, transparent);
  border-radius: 999px;
  background: transparent;
  padding: 8px 14px;
  color: var(--text-color);
  font-size: 13px;
  cursor: pointer;
  transition: border-color 0.2s ease, color 0.2s ease, background-color 0.2s ease;
}

.hot-item:hover {
  border-color: color-mix(in srgb, var(--primary-color) 55%, transparent);
  color: var(--primary-color);
  background: color-mix(in srgb, var(--hover-bg) 84%, transparent);
}

.button-bar {
  position: relative;
  z-index: 1;
  margin-left: auto;
  min-width: fit-content;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
}

.button-list {
  display: flex;
  align-items: center;
  gap: 16px;
  justify-content: flex-end;
  text-align: right;
}

.button-list span {
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s ease;
}

.button-list span:hover {
  color: var(--text-color);
}

.login-btn {
  width: 82px;
  border-radius: 99px;
  border: 1px solid var(--border-color);
  background: transparent;
  color: var(--text-color);
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.login-btn:hover {
  background: var(--hover-bg);
  border-color: color-mix(in srgb, var(--text-secondary) 65%, transparent);
}

.user-entry {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border: 1px solid color-mix(in srgb, var(--border-color) 85%, transparent);
  background: color-mix(in srgb, var(--sidebar-bg) 90%, transparent);
  border-radius: 999px;
  padding: 4px 12px 4px 5px;
  cursor: pointer;
  color: var(--text-color);
  max-width: 188px;
}

.user-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.entry-more {
  font-size: 12px;
  opacity: 0.72;
}

.profile-jump {
  cursor: pointer;
}

.user-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.user-main {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-text {
  min-width: 0;
}

.user-text h4 {
  margin: 0;
  font-size: 15px;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-text p {
  margin: 3px 0 0;
  font-size: 12px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout-btn {
  width: 100%;
}

.back-btn {
  width: 42px;
  height: 42px;
  border: 1px solid color-mix(in srgb, var(--border-color) 82%, transparent);
  border-radius: 50%;
  background: color-mix(in srgb, var(--sidebar-bg) 92%, transparent);
  color: var(--text-color);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  transition: transform 0.2s ease, border-color 0.2s ease, background-color 0.2s ease;
}

.back-btn:hover {
  transform: translateX(-1px);
  border-color: color-mix(in srgb, var(--primary-color) 55%, transparent);
  background: color-mix(in srgb, var(--hover-bg) 84%, transparent);
}

:global(.top-user-popover.el-popover) {
  border-radius: 14px;
  border: 1px solid color-mix(in srgb, var(--border-color) 85%, transparent);
  background: color-mix(in srgb, var(--sidebar-bg) 96%, #ffffff 4%);
  box-shadow: 0 16px 30px color-mix(in srgb, var(--shadow-color) 45%, transparent);
}

@media (max-width: 1100px) {
  .mode-home .search-shell {
    width: min(560px, calc(100% - 240px));
  }

  .button-list {
    display: none;
  }

  .button-bar {
    min-width: fit-content;
  }
}

@media (max-width: 768px) {
  .mode-home {
    gap: 10px;
  }

  .mode-home .search-shell {
    position: static;
    left: auto;
    transform: none;
    width: auto;
    max-width: none;
    flex: 1;
  }

  .button-bar {
    min-width: auto;
  }

  .user-name {
    display: none;
  }

  .search-panel {
    padding: 14px;
  }

  .mode-page {
    gap: 10px;
  }

  .history-keyword-btn {
    max-width: 180px;
  }
}
</style>
