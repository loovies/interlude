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

export interface UserFollowListItem {
  userId: string
  nickName?: string
  avatar?: string
  school?: string
  personIntroduction?: string
  followed?: boolean
  mutualFollow?: boolean
}

export interface UserProfileStats {
  focusCount: number
  fansCount: number
  likeCount: number
}

export interface VideoPlayFinishPayload {
  videoId: VideoIdentifier
  watchDuration?: number
  lastWatchTimeOffset?: number
  completeWatch?: number
}

export interface VideoCommentItem {
  commentId: number
  videoId: number
  authorId: string
  authorName: string
  authorAvatar?: string
  parentCommentId?: number | null
  replyToUserId?: string | null
  replyToUserName?: string | null
  content: string
  likeCount: number
  replyCount: number
  createdAt: string
  liked?: boolean
}

export interface PageResult<T> {
  totalCount: number
  pageSize: number
  pageNo: number
  pageTotal: number
  list: T[]
}

export interface VideoCommentPayload {
  videoId: VideoIdentifier
  content: string
  parentCommentId?: number
  replyCommentId?: number
}

export interface VideoCommentLikePayload {
  commentId: number
}

export interface VideoCommentLikeStatus {
  commentId: number
  liked: boolean
  likeCount: number
}

export interface VideoDanmuItem {
  danmuId: number
  videoId: number
  userId: string
  userName?: string
  content: string
  timeOffset: number
  color?: string
  fontSize?: number
  position?: number
  createdAt?: string
}

export interface VideoDanmuPayload {
  videoId: VideoIdentifier
  content: string
  timeOffset: number
  color?: string
  fontSize?: number
  position?: number
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

export const fetchVideoComments = async (
  videoId: VideoIdentifier,
  pageNo: number = 1,
  pageSize: number = 20,
): Promise<PageResult<VideoCommentItem>> => {
  const res = await http.get<ResponseVO<PageResult<VideoCommentItem>>>('/video/comment/list', {
    params: { videoId, pageNo, pageSize },
  })
  return unwrapResponse(res)
}

export const createVideoComment = async (payload: VideoCommentPayload): Promise<VideoCommentItem> => {
  const res = await http.post<ResponseVO<VideoCommentItem>>('/video/comment/add', payload)
  return unwrapResponse(res)
}

export const likeVideoComment = async (payload: VideoCommentLikePayload): Promise<VideoCommentLikeStatus> => {
  const res = await http.post<ResponseVO<VideoCommentLikeStatus>>('/video/comment/like', payload)
  return unwrapResponse(res)
}

export const fetchVideoDanmus = async (
  videoId: VideoIdentifier,
  limit: number = 1200,
): Promise<VideoDanmuItem[]> => {
  const res = await http.get<ResponseVO<VideoDanmuItem[]>>('/video/danmu/list', {
    params: { videoId, limit },
  })
  return unwrapResponse(res)
}

export const createVideoDanmu = async (payload: VideoDanmuPayload): Promise<VideoDanmuItem> => {
  const res = await http.post<ResponseVO<VideoDanmuItem>>('/video/danmu/add', payload)
  return unwrapResponse(res)
}

export const fetchUserFollowStatus = async (targetUserId: VideoIdentifier): Promise<UserFollowStatus> => {
  const res = await http.get<ResponseVO<UserFollowStatus>>('/video/follow/status', {
    params: { targetUserId },
  })
  return unwrapResponse(res)
}

export const fetchUserFollowList = async (
  userId: VideoIdentifier,
  type: 'following' | 'followers',
  limit: number = 200,
): Promise<UserFollowListItem[]> => {
  const res = await http.get<ResponseVO<UserFollowListItem[]>>('/video/follow/list', {
    params: { userId, type, limit },
  })
  return unwrapResponse(res)
}

export const fetchUserProfileStats = async (userId: VideoIdentifier): Promise<UserProfileStats> => {
  const res = await http.get<ResponseVO<UserProfileStats>>('/video/follow/stats', {
    params: { userId },
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
