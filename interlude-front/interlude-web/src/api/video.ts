import http from './http'
import type { ResponseVO } from './types'
import { unwrapResponse } from './types'

export interface VideoReactionStatus {
  videoId: number
  liked: boolean
  collected: boolean
  shared: boolean
  likeCount: number
  collectCount: number
  shareCount: number
}

export interface UserFollowStatus {
  targetUserId: string
  followed: boolean
}

export interface VideoPlayFinishPayload {
  videoId: VideoIdentifier
  watchDuration?: number
  lastWatchTimeOffset?: number
  completeWatch?: number
}

type VideoIdentifier = number | string

export const fetchVideoReactionStatus = async (videoId: VideoIdentifier): Promise<VideoReactionStatus> => {
  const res = await http.get<ResponseVO<VideoReactionStatus>>('/video/reaction/status', {
    params: { videoId },
  })
  return unwrapResponse(res)
}

export const toggleVideoLike = async (videoId: VideoIdentifier): Promise<VideoReactionStatus> => {
  const res = await http.post<ResponseVO<VideoReactionStatus>>('/video/reaction/like', { videoId })
  return unwrapResponse(res)
}

export const toggleVideoCollect = async (videoId: VideoIdentifier): Promise<VideoReactionStatus> => {
  const res = await http.post<ResponseVO<VideoReactionStatus>>('/video/reaction/collect', { videoId })
  return unwrapResponse(res)
}

export const toggleVideoShare = async (videoId: VideoIdentifier): Promise<VideoReactionStatus> => {
  const res = await http.post<ResponseVO<VideoReactionStatus>>('/video/reaction/share', { videoId })
  return unwrapResponse(res)
}

export const reportVideoPlayFinish = async (payload: VideoPlayFinishPayload): Promise<boolean> => {
  const res = await http.post<ResponseVO<boolean>>('/video/play/finish', payload)
  return unwrapResponse(res)
}

export const reportVideoWatchHistory = async (payload: VideoPlayFinishPayload): Promise<boolean> => {
  const res = await http.post<ResponseVO<boolean>>('/video/play/history', payload)
  return unwrapResponse(res)
}

export const fetchUserFollowStatus = async (targetUserId: VideoIdentifier): Promise<UserFollowStatus> => {
  const res = await http.get<ResponseVO<UserFollowStatus>>('/video/follow/status', {
    params: { targetUserId },
  })
  return unwrapResponse(res)
}

export const followUser = async (targetUserId: VideoIdentifier): Promise<UserFollowStatus> => {
  const res = await http.post<ResponseVO<UserFollowStatus>>('/video/follow/add', { targetUserId })
  return unwrapResponse(res)
}

export const unfollowUser = async (targetUserId: VideoIdentifier): Promise<UserFollowStatus> => {
  const payload = { targetUserId }
  const fallbackEndpoints = [
    '/video/follow/cancel',
    '/video/follow/unfollow',
    '/video/follow/delete',
    '/video/follow/remove',
    '/video/follow/cancelFollow',
  ] as const

  const requestTasks: Array<() => Promise<UserFollowStatus>> = [
    ...fallbackEndpoints.map((endpoint) => async () => {
      const res = await http.post<ResponseVO<UserFollowStatus>>(endpoint, payload)
      return unwrapResponse(res)
    }),
    async () => {
      const res = await http.delete<ResponseVO<UserFollowStatus>>('/video/follow/cancel', {
        data: payload,
        params: payload,
      })
      return unwrapResponse(res)
    },
    async () => {
      const res = await http.get<ResponseVO<UserFollowStatus>>('/video/follow/cancel', {
        params: payload,
      })
      return unwrapResponse(res)
    },
    async () => {
      const res = await http.get<ResponseVO<UserFollowStatus>>('/video/follow/unfollow', {
        params: payload,
      })
      return unwrapResponse(res)
    },
  ]

  let lastError: any = null
  for (const runRequest of requestTasks) {
    try {
      const status = await runRequest()
      return {
        targetUserId: String(status?.targetUserId ?? targetUserId),
        followed: !!status?.followed,
      }
    } catch (error: any) {
      lastError = error
      const statusCode = error?.response?.status
      if (statusCode !== 404 && statusCode !== 405) {
        throw error
      }
    }
  }

  throw lastError
}
