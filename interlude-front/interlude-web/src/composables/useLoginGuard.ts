import type { LoginIntent } from '@/types/auth'
import { useAuthStore } from '@/stores/auth'

export const useLoginGuard = () => {
  const authStore = useAuthStore()

  const runAfterLogin = async <T>(intent: LoginIntent, action: () => T | Promise<T>): Promise<T> => {
    return authStore.runAfterLogin(intent, action)
  }

  const promptLogin = (intent: LoginIntent = 'manual'): void => {
    authStore.openLoginDialog(intent)
  }

  return {
    runAfterLogin,
    promptLogin,
    authStore,
  }
}

export type UseLoginGuardReturn = ReturnType<typeof useLoginGuard>
