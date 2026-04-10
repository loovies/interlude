import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { LoginIntent, PendingAuthAction, RememberedCredential, TokenUserInfo } from '@/types/auth'
import { logoutFromServer, requestAutoLogin } from '@/api/auth'

const REMEMBER_STORAGE_KEY = 'interlude-web-remember'

const canUseStorage = (): boolean => {
  try {
    return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined'
  } catch (error) {
    return false
  }
}

const encodeBase64 = (value: string): string => {
  try {
    return btoa(value)
  } catch (error) {
    return value
  }
}

const decodeBase64 = (value: string): string => {
  try {
    return atob(value)
  } catch (error) {
    return value
  }
}

export const useAuthStore = defineStore('auth', () => {
  const currentUser = ref<TokenUserInfo | null>(null)
  const loginDialogVisible = ref(false)
  const loginIntent = ref<LoginIntent>('manual')
  const rememberMe = ref(false)
  const rememberedCredential = ref<RememberedCredential | null>(null)
  const pendingActions = ref<PendingAuthAction<any>[]>([])
  const autoLoginFinished = ref(false)

  const isLoggedIn = computed(() => !!currentUser.value?.token)

  const openLoginDialog = (intent: LoginIntent = 'manual') => {
    loginIntent.value = intent
    loginDialogVisible.value = true
  }

  const hideLoginDialog = () => {
    loginDialogVisible.value = false
    loginIntent.value = 'manual'
  }

  const cancelLoginFlow = () => {
    hideLoginDialog()
    rejectPendingActions(new Error('login cancelled'))
  }

  const runAfterLogin = async <T>(intent: LoginIntent, runner: () => T | Promise<T>): Promise<T> => {
    if (isLoggedIn.value) {
      return Promise.resolve().then(runner)
    }
    if (!loginDialogVisible.value) {
      openLoginDialog(intent)
    } else {
      loginIntent.value = intent
    }
    return new Promise<T>((resolve, reject) => {
      pendingActions.value = [
        ...pendingActions.value,
        {
          intent,
          runner,
          resolve,
          reject,
        },
      ]
    })
  }

  const resolvePendingActions = async (): Promise<void> => {
    if (!pendingActions.value.length) {
      return
    }
    const queue = [...pendingActions.value]
    pendingActions.value = []
    for (const task of queue) {
      try {
        const result = await task.runner()
        task.resolve(result as any)
      } catch (error) {
        task.reject(error)
      }
    }
  }

  const rejectPendingActions = (reason?: any) => {
    if (!pendingActions.value.length) {
      return
    }
    const queue = [...pendingActions.value]
    pendingActions.value = []
    const fallbackError = reason ?? new Error('login cancelled')
    queue.forEach((task) => task.reject(fallbackError))
  }

  const completeLogin = (user: TokenUserInfo) => {
    currentUser.value = user
    hideLoginDialog()
    resolvePendingActions()
  }

  const setCurrentUser = (user: TokenUserInfo | null) => {
    currentUser.value = user
  }

  const persistRemembered = (credential: RememberedCredential | null) => {
    if (!canUseStorage()) {
      rememberMe.value = !!credential
      rememberedCredential.value = credential
      return
    }
    if (credential) {
      const serialized = JSON.stringify(credential)
      localStorage.setItem(REMEMBER_STORAGE_KEY, encodeBase64(serialized))
      rememberMe.value = true
      rememberedCredential.value = credential
    } else {
      localStorage.removeItem(REMEMBER_STORAGE_KEY)
      rememberMe.value = false
      rememberedCredential.value = null
    }
  }

  const hydrateRemembered = () => {
    if (!canUseStorage()) {
      return
    }
    const raw = localStorage.getItem(REMEMBER_STORAGE_KEY)
    if (!raw) {
      return
    }
    try {
      const decoded = decodeBase64(raw)
      const parsed = JSON.parse(decoded) as RememberedCredential
      if (parsed?.account && typeof parsed.password === 'string') {
        rememberedCredential.value = parsed
        rememberMe.value = true
      }
    } catch (error) {
      console.warn('Failed to parse remembered credential', error)
      localStorage.removeItem(REMEMBER_STORAGE_KEY)
    }
  }

  const runAutoLogin = async (): Promise<TokenUserInfo | null> => {
    if (autoLoginFinished.value) {
      return currentUser.value
    }
    try {
      const result = await requestAutoLogin()
      currentUser.value = result || null
    } catch (error) {
      console.warn('Auto login check failed', error)
      currentUser.value = null
    } finally {
      autoLoginFinished.value = true
    }
    return currentUser.value
  }

  const logout = async (): Promise<void> => {
    try {
      await logoutFromServer()
    } catch (error) {
      console.warn('Logout request failed', error)
    } finally {
      currentUser.value = null
      hideLoginDialog()
    }
  }

  if (canUseStorage()) {
    hydrateRemembered()
  }

  return {
    // state
    currentUser,
    loginDialogVisible,
    loginIntent,
    rememberMe,
    rememberedCredential,
    autoLoginFinished,
    isLoggedIn,
    // actions
    openLoginDialog,
    hideLoginDialog,
    cancelLoginFlow,
    runAfterLogin,
    completeLogin,
    setCurrentUser,
    persistRemembered,
    hydrateRemembered,
    runAutoLogin,
    logout,
  }
})

