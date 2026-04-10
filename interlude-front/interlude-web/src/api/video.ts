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
