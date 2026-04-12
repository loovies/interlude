<template>
  <div class="top-box">
    <div class="search-bar">
      <input type="text" class="search-input" :placeholder="uiText.placeholder" />
      <div class="button-group">
        <button class="search-btn" type="button">{{ uiText.search }}</button>
      </div>
    </div>
    <div class="button-bar">
      <div class="button-list">
        <span>{{ uiText.following }}</span>
        <span>{{ uiText.alerts }}</span>
        <span>{{ uiText.messages }}</span>
        <span>{{ uiText.upload }}</span>
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
            <span class="entry-more">▾</span>
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
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const router = useRouter()
const logoutLoading = ref(false)
const uiText = {
  placeholder: '\u641c\u7d22\u4f60\u611f\u5174\u8da3\u7684\u5185\u5bb9',
  search: '\u641c\u7d22',
  following: '\u5173\u6ce8',
  alerts: '\u901a\u77e5',
  messages: '\u6d88\u606f',
  upload: '\u6295\u7a3f',
  signIn: '\u767b\u5f55',
  signOut: '\u9000\u51fa\u767b\u5f55',
  account: 'Interlude \u7528\u6237',
  user: '\u7528\u6237',
  logoutSuccess: '\u5df2\u9000\u51fa\u767b\u5f55',
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

const displayName = computed(() => {
  const user = authStore.currentUser
  return user?.nickName || user?.email || user?.phone || uiText.user
})

const userInitial = computed(() => displayName.value.slice(0, 1).toUpperCase())
const avatarUrl = computed(() => resolveAvatarUrl(authStore.currentUser?.avatar))

const handleLoginClick = () => {
  authStore.openLoginDialog('manual')
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
</script>

<style scoped lang="scss">
.top-box {
  position: relative;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 0 16px;
  align-items: center;
  min-height: 48px;
}

.search-bar {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  width: min(640px, calc(100% - 420px));
  min-width: 280px;
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

  span {
    color: var(--text-secondary);
    font-size: 14px;
    cursor: pointer;
    transition: color 0.2s ease;
  }

  span:hover {
    color: var(--text-color);
  }
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

.user-entry .user-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.user-entry .entry-more {
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

  h4 {
    margin: 0;
    font-size: 15px;
    color: var(--text-color);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  p {
    margin: 3px 0 0;
    font-size: 12px;
    color: var(--text-secondary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.logout-btn {
  width: 100%;
}

:global(.top-user-popover.el-popover) {
  border-radius: 14px;
  border: 1px solid color-mix(in srgb, var(--border-color) 85%, transparent);
  background: color-mix(in srgb, var(--sidebar-bg) 96%, #ffffff 4%);
  box-shadow: 0 16px 30px color-mix(in srgb, var(--shadow-color) 45%, transparent);
}

@media (max-width: 1100px) {
  .search-bar {
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
  .top-box {
    gap: 10px;
  }

  .search-bar {
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

  .user-entry .user-name {
    display: none;
  }
}
</style>
