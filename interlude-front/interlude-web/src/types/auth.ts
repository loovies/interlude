export interface TokenUserInfo {
  token: string
  userId: string
  nickName?: string
  avatar?: string
  email?: string
  phone?: string
  expireAt?: number
  roleId?: number
  fansCount?: number
  focusCount?: number
  likeCount?: number
  mutualCount?: number
}

export interface RememberedCredential {
  account: string
  password: string
}

export type LoginIntent =
  | 'manual'
  | 'like'
  | 'comment'
  | 'follow'
  | 'collect'
  | 'share'
  | 'danmu'
  | 'favorite'
  | 'bullet'
  | 'publish'

export interface PendingAuthAction<T = unknown> {
  intent: LoginIntent
  runner: () => T | Promise<T>
  resolve: (value: T) => void
  reject: (reason?: any) => void
}
