import axios from 'axios'

export interface VideoQuality {
  quality: string
  url: string
  resolution?: string
  bitrate?: number
  recommended?: boolean
  disabled?: boolean
}

export interface VideoData {
  videoId: string | number
  title: string
  description?: string
  duration: number
  qualities: VideoQuality[]
  author?: string
  likes?: number
  comments?: number
  shares?: number
  collects?: number
  createTime?: string
  thumbnailUrl?: string
  views?: number
  categoryId?: number
  pCategoryId?: number
  categoryName?: string
  pCategoryName?: string
}

export interface ChoicenessCategory {
  id: string
  name: string
  pCategoryId?: number
  categoryId?: number
}

export interface ChoicenessVideoItem {
  id: number
  title: string
  category: string
  categoryName: string
  cover: string
  coverFallback?: string
  likes: number
  duration: string
  author: string
  views: number
  uploadTime: string
  description?: string
}

export interface DanmuData {
  id: string
  content: string
  time: number
  color?: string
  type?: 'scroll' | 'top' | 'bottom'
  speed?: number
  userId?: string
  userName?: string
}

interface WebApiResponse<T> {
  status?: string
  code?: number
  info?: string
  data?: T
}

interface WebPage<T> {
  totalCount?: number
  pageSize?: number
  pageNo?: number
  pageTotal?: number
  list?: T[]
}

interface WebVideoAuthor {
  userId?: string | number
  nickName?: string
  avatar?: string
}

interface WebVideoQuality {
  quality?: string
  url?: string
  m3u8Url?: string
  resolution?: string
  bitrate?: number | string
  recommended?: boolean
  disabled?: boolean
}

interface WebVideoItem {
  videoId: number | string
  title?: string
  description?: string
  duration?: number
  qualities?: WebVideoQuality[]
  author?: WebVideoAuthor
  likeCount?: number
  commentCount?: number
  shareCount?: number
  collectCount?: number
  playCount?: number
  publishTime?: string
  coverUrl?: string
  pCategoryId?: number
  pCategoryName?: string
  categoryId?: number
  categoryName?: string
}

function buildInlinePlaceholder(label: string): string {
  const safeLabel = encodeURIComponent(label || 'Interlude')
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="600" viewBox="0 0 400 600"><defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#1f2937"/><stop offset="1" stop-color="#374151"/></linearGradient></defs><rect width="400" height="600" fill="url(#g)"/><circle cx="310" cy="120" r="72" fill="rgba(255,255,255,0.08)"/><circle cx="86" cy="498" r="96" fill="rgba(255,255,255,0.06)"/><text x="32" y="500" fill="#f9fafb" font-size="28" font-family="Arial, sans-serif">Interlude</text><text x="32" y="538" fill="#d1d5db" font-size="20" font-family="Arial, sans-serif">${safeLabel}</text></svg>`
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}

interface WebVideoCategory {
  categoryId?: number
  categoryCode?: string
  categoryName?: string
  pCategoryId?: number
  sort?: number
  children?: WebVideoCategory[]
}

interface WebPlayListInfo {
  videoId?: number | string
  title?: string
  duration?: number
  qualities?: Array<Record<string, unknown>>
}

const DEFAULT_VIDEO_SOURCES: string[] = [
  'https://vjs.zencdn.net/v/oceans.mp4',
  'https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4',
  'https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4',
]

export const mockVideoList: VideoData[] = [
  {
    videoId: 1,
    title: '清晨城市航拍',
    description: '城市日出延时画面，适合测试播放器滚动切换。',
    duration: 45,
    author: 'Interlude官方',
    likes: 1250,
    comments: 86,
    shares: 32,
    collects: 79,
    createTime: '2026-03-01 09:20:00',
    thumbnailUrl: 'https://picsum.photos/seed/interlude1/400/700',
    views: 18200,
    categoryId: 101,
    pCategoryId: 1,
    categoryName: '推荐',
    pCategoryName: '全部',
    qualities: [
      {
        quality: '720p',
        url: DEFAULT_VIDEO_SOURCES[0]!,
        resolution: '1280x720',
        bitrate: 3500,
        recommended: true,
      },
    ],
  },
  {
    videoId: 2,
    title: '街头篮球瞬间',
    description: '高光动作合集，测试快节奏视频切换。',
    duration: 32,
    author: '热血剪辑社',
    likes: 3421,
    comments: 210,
    shares: 117,
    collects: 203,
    createTime: '2026-03-02 13:35:00',
    thumbnailUrl: 'https://picsum.photos/seed/interlude2/400/700',
    views: 49300,
    categoryId: 102,
    pCategoryId: 1,
    categoryName: '热门',
    pCategoryName: '全部',
    qualities: [
      {
        quality: '720p',
        url: DEFAULT_VIDEO_SOURCES[1]!,
        resolution: '1280x720',
        bitrate: 3500,
        recommended: true,
      },
    ],
  },
  {
    videoId: 3,
    title: '夜景慢镜头',
    description: '灯光与人流，适合测试评论和弹幕叠加效果。',
    duration: 58,
    author: '镜头观察员',
    likes: 2780,
    comments: 160,
    shares: 91,
    collects: 140,
    createTime: '2026-03-03 21:10:00',
    thumbnailUrl: 'https://picsum.photos/seed/interlude3/400/700',
    views: 26800,
    categoryId: 103,
    pCategoryId: 1,
    categoryName: '精选',
    pCategoryName: '全部',
    qualities: [
      {
        quality: '720p',
        url: DEFAULT_VIDEO_SOURCES[2]!,
        resolution: '1280x720',
        bitrate: 3500,
        recommended: true,
      },
    ],
  },
]

const danmuContents = [
  '这个镜头太稳了',
  '转场好丝滑',
  '节奏很舒服',
  '这个画面有电影感',
  '继续更，没看够',
  '卡点很准',
  '收藏了，回头学习',
  '这个运镜绝了',
  '这条会火',
  'BGM很搭',
]

const danmuColors = [
  '#ffffff',
  '#ff2d55',
  '#4cd964',
  '#007aff',
  '#ff9500',
  '#ffcc00',
  '#5856d6',
  '#ff3b30',
  '#34c759',
  '#5ac8fa',
]

const userNames = ['用户A', '用户B', '剪辑爱好者', '路人甲', '路人乙', '今天也在刷', '视频控']

function toNumber(value: unknown, defaultValue: number = 0): number {
  if (typeof value === 'number' && !Number.isNaN(value)) {
    return value
  }

  if (typeof value === 'string') {
    const parsed = Number(value)
    return Number.isNaN(parsed) ? defaultValue : parsed
  }

  return defaultValue
}

function formatDuration(seconds: number): string {
  const safeSeconds = Math.max(0, toNumber(seconds, 0))
  const hours = Math.floor(safeSeconds / 3600)
  const minutes = Math.floor((safeSeconds % 3600) / 60)
  const remainSeconds = safeSeconds % 60

  if (hours > 0) {
    return `${hours}:${String(minutes).padStart(2, '0')}:${String(remainSeconds).padStart(2, '0')}`
  }

  return `${minutes}:${String(remainSeconds).padStart(2, '0')}`
}

function normalizeQualities(
  qualities: WebVideoQuality[] | undefined,
  videoId: string | number,
): VideoQuality[] {
  const normalized = (qualities || [])
    .map<VideoQuality | null>((item) => {
      const url = item.url || item.m3u8Url
      if (!url) {
        return null
      }

      return {
        quality: item.quality || '标清',
        url,
        resolution: item.resolution,
        bitrate: toNumber(item.bitrate, 0) || undefined,
        recommended: item.recommended,
        disabled: item.disabled,
      }
    })
    .filter((item): item is VideoQuality => item !== null)

  if (normalized.length > 0) {
    return normalized
  }

  const source = DEFAULT_VIDEO_SOURCES[toNumber(videoId, 1) % DEFAULT_VIDEO_SOURCES.length] || DEFAULT_VIDEO_SOURCES[0]!
  return [
    {
      quality: '720p',
      url: source,
      resolution: '1280x720',
      bitrate: 3500,
      recommended: true,
    },
  ]
}

function mapWebVideoItemToVideoData(item: WebVideoItem): VideoData {
  return {
    videoId: item.videoId,
    title: item.title || '未命名视频',
    description: item.description,
    duration: toNumber(item.duration, 15),
    qualities: normalizeQualities(item.qualities, item.videoId),
    author: item.author?.nickName || '匿名作者',
    likes: toNumber(item.likeCount, 0),
    comments: toNumber(item.commentCount, 0),
    shares: toNumber(item.shareCount, 0),
    collects: toNumber(item.collectCount, 0),
    createTime: item.publishTime,
    thumbnailUrl: item.coverUrl,
    views: toNumber(item.playCount, 0),
    categoryId: item.categoryId,
    pCategoryId: item.pCategoryId,
    categoryName: item.categoryName,
    pCategoryName: item.pCategoryName,
  }
}

function mapVideoDataToChoicenessItem(item: VideoData): ChoicenessVideoItem {
  const fallbackCover = item.thumbnailUrl || placeholderCover(item.videoId, item.title)
  const cover = item.thumbnailUrl || fallbackCover

  return {
    id: toNumber(item.videoId, 0),
    title: item.title,
    category: String(item.categoryId ?? item.pCategoryId ?? 'all'),
    categoryName: item.categoryName || item.pCategoryName || '未分类',
    cover,
    coverFallback: fallbackCover,
    likes: toNumber(item.likes, 0),
    duration: formatDuration(item.duration),
    author: item.author || '匿名作者',
    views: toNumber(item.views, 0),
    uploadTime: item.createTime || '',
    description: item.description,
  }
}

function placeholderCover(videoId: string | number, title?: string): string {
  const safeId = String(videoId ?? '').trim()
  if (!safeId) {
    return buildInlinePlaceholder(title || 'Video')
  }
  return buildInlinePlaceholder(title || `Video ${safeId}`)
}

function getFallbackVideoPage(page: number, pageSize: number): { data: VideoData[]; total: number } {
  const safePage = Math.max(1, page)
  const safePageSize = Math.max(1, pageSize)
  const start = (safePage - 1) * safePageSize
  const end = start + safePageSize

  return {
    data: mockVideoList.slice(start, end),
    total: mockVideoList.length,
  }
}

function getFallbackChoicenessPage(categoryId?: number, page: number = 1, pageSize: number = 10) {
  const filtered = typeof categoryId === 'number'
    ? mockVideoList.filter((item) => item.categoryId === categoryId || item.pCategoryId === categoryId)
    : mockVideoList

  const safePage = Math.max(1, page)
  const safePageSize = Math.max(1, pageSize)
  const start = (safePage - 1) * safePageSize
  const end = start + safePageSize
  const list = filtered.slice(start, end).map(mapVideoDataToChoicenessItem)

  return {
    list,
    total: filtered.length,
  }
}

function shuffleArray<T>(items: T[]): T[] {
  const list = [...items]
  for (let i = list.length - 1; i > 0; i -= 1) {
    const j = Math.floor(Math.random() * (i + 1))
    const current = list[i]
    list[i] = list[j] as T
    list[j] = current as T
  }
  return list
}

function getFallbackRandomList(page: number, pageSize: number) {
  const safePage = Math.max(1, page)
  const safePageSize = Math.max(1, pageSize)
  const shuffled = shuffleArray(mockVideoList)
  const start = (safePage - 1) * safePageSize
  const end = start + safePageSize
  return {
    data: shuffled.slice(start, end),
    total: shuffled.length,
  }
}

export function generateMockDanmu(videoId: string | number, duration: number): DanmuData[] {
  const safeDuration = Math.max(5, duration || 15)
  const danmuCount = Math.floor(Math.random() * 50) + 30
  const danmus: DanmuData[] = []

  for (let i = 0; i < danmuCount; i++) {
    const roll = Math.random()
    const type: 'scroll' | 'top' | 'bottom' = roll < 0.05 ? 'top' : roll < 0.1 ? 'bottom' : 'scroll'

    danmus.push({
      id: `${videoId}_${Date.now()}_${i}`,
      content: danmuContents[Math.floor(Math.random() * danmuContents.length)] || '666',
      time: Math.random() * safeDuration,
      color: type === 'top' ? '#ff2d55' : type === 'bottom' ? '#007aff' : danmuColors[Math.floor(Math.random() * danmuColors.length)],
      type,
      speed: 1 + Math.random(),
      userId: `user_${Math.floor(Math.random() * 10000)}`,
      userName: userNames[Math.floor(Math.random() * userNames.length)] || '匿名用户',
    })
  }

  return danmus.sort((a, b) => a.time - b.time)
}

export async function fetchVideoData(videoId: string | number): Promise<VideoData> {
  try {
    const [detailResponse, playlistResponse] = await Promise.all([
      axios.get<WebApiResponse<WebVideoItem>>(`/api/video/play/${videoId}`),
      axios.get<WebApiResponse<WebPlayListInfo>>(`/api/video/play/${videoId}/playlist`),
    ])

    const detail = detailResponse.data?.data
    if (!detail) {
      throw new Error('视频详情为空')
    }

    const playlist = playlistResponse.data?.data
    return {
      ...mapWebVideoItemToVideoData(detail),
      duration: toNumber(playlist?.duration, detail.duration ?? 15),
      qualities: normalizeQualities((playlist?.qualities || []) as WebVideoQuality[], detail.videoId),
      title: playlist?.title || detail.title || '未命名视频',
    }
  } catch (error) {
    const fallback = mockVideoList.find((item) => String(item.videoId) === String(videoId))
    if (fallback) {
      return fallback
    }
    throw error
  }
}

export async function fetchVideoList(
  page: number = 1,
  pageSize: number = 10,
): Promise<{ data: VideoData[]; total: number }> {
  try {
    const response = await axios.get<WebApiResponse<WebPage<WebVideoItem>>>('/api/video/feed/recommend', {
      params: {
        pageNo: page,
        pageSize,
      },
    })

    const pageData = response.data?.data
    const list = (pageData?.list || []).map(mapWebVideoItemToVideoData)

    if (list.length === 0) {
      return getFallbackVideoPage(page, pageSize)
    }

    return {
      data: list,
      total: toNumber(pageData?.totalCount, list.length),
    }
  } catch (error) {
    console.error('加载推荐视频流失败，使用本地兜底数据：', error)
    return getFallbackVideoPage(page, pageSize)
  }
}

export async function fetchRandomVideoList(options?: {
  page?: number
  pageSize?: number
  seedVideoId?: string | number
}): Promise<{ data: VideoData[]; total: number }> {
  const page = Math.max(1, options?.page ?? 1)
  const pageSize = Math.max(1, options?.pageSize ?? 10)
  const seedVideoId = options?.seedVideoId

  try {
    const params: Record<string, unknown> = {
      pageNo: page,
      pageSize,
    }
    if (seedVideoId !== undefined && seedVideoId !== null && seedVideoId !== '') {
      params.seedVideoId = seedVideoId
    }

    const response = await axios.get<WebApiResponse<WebPage<WebVideoItem>>>('/api/video/feed/random', {
      params,
    })

    const pageData = response.data?.data
    const list = (pageData?.list || []).map(mapWebVideoItemToVideoData)

    if (list.length === 0) {
      const recommend = await fetchVideoList(page, pageSize)
      return {
        data: shuffleArray(recommend.data).slice(0, pageSize),
        total: recommend.total,
      }
    }

    return {
      data: list,
      total: toNumber(pageData?.totalCount, list.length),
    }
  } catch (error) {
    console.error('加载随机播放列表失败，尝试使用推荐流作为兜底：', error)
    try {
      const recommend = await fetchVideoList(page, pageSize)
      return {
        data: shuffleArray(recommend.data).slice(0, pageSize),
        total: recommend.total,
      }
    } catch (innerError) {
      console.error('推荐流兜底失败，使用本地随机列表：', innerError)
      return getFallbackRandomList(page, pageSize)
    }
  }
}

export async function fetchChoicenessCategories(): Promise<ChoicenessCategory[]> {
  try {
    const response = await axios.get<WebApiResponse<WebVideoCategory[]>>('/api/video/discover/categories')
    const categories = response.data?.data || []

    const mappedRoots = categories
      .filter((item) => toNumber(item.pCategoryId, 0) === 0)
      .map((item) => ({
        id: String(item.categoryId ?? item.categoryCode ?? ''),
        name: item.categoryName || '未命名分类',
        pCategoryId: item.pCategoryId,
        categoryId: item.categoryId,
      }))
      .filter((item) => item.id)

    if (mappedRoots.length > 0) {
      return [{ id: 'all', name: '全部' }, ...mappedRoots]
    }
  } catch (error) {
    console.error('加载精选分类失败，使用本地兜底分类：', error)
  }

  return [
    { id: 'all', name: '全部' },
    { id: '1', name: '推荐', pCategoryId: 0, categoryId: 1 },
    { id: '2', name: '热门', pCategoryId: 0, categoryId: 2 },
    { id: '3', name: '精选', pCategoryId: 0, categoryId: 3 },
  ]
}

export async function fetchChoicenessVideos(options?: {
  page?: number
  pageSize?: number
  categoryId?: number
  pCategoryId?: number
}): Promise<{ list: ChoicenessVideoItem[]; total: number }> {
  const page = Math.max(1, options?.page ?? 1)
  const pageSize = Math.max(1, options?.pageSize ?? 10)
  const categoryId = options?.categoryId
  const pCategoryId = options?.pCategoryId

  try {
    const response = await axios.get<WebApiResponse<WebPage<WebVideoItem>>>('/api/video/feed/category', {
      params: {
        pageNo: page,
        pageSize,
        categoryId,
        pCategoryId,
      },
    })

    const pageData = response.data?.data
    const list = (pageData?.list || [])
      .map(mapWebVideoItemToVideoData)
      .map(mapVideoDataToChoicenessItem)

    if (list.length === 0) {
      return getFallbackChoicenessPage(categoryId ?? pCategoryId, page, pageSize)
    }

    return {
      list,
      total: toNumber(pageData?.totalCount, list.length),
    }
  } catch (error) {
    console.error('加载精选视频失败，使用本地兜底数据：', error)
    return getFallbackChoicenessPage(categoryId ?? pCategoryId, page, pageSize)
  }
}

export function fetchVideoDanmu(videoId: string | number): Promise<DanmuData[]> {
  return new Promise((resolve) => {
    setTimeout(() => {
      const video = mockVideoList.find((item) => String(item.videoId) === String(videoId))
      resolve(generateMockDanmu(videoId, video?.duration || 15))
    }, 200)
  })
}

export function sendDanmu(videoId: string | number, content: string): Promise<{ success: boolean; danmuId: string }> {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log(`发送弹幕 videoId=${videoId}, content=${content}`)
      resolve({
        success: true,
        danmuId: `danmu_${Date.now()}_${Math.random().toString(36).slice(2, 11)}`,
      })
    }, 150)
  })
}

export function likeVideo(videoId: string | number): Promise<{ success: boolean; likes: number }> {
  return new Promise((resolve) => {
    const video = mockVideoList.find((item) => String(item.videoId) === String(videoId))
    if (!video) {
      resolve({ success: false, likes: 0 })
      return
    }

    video.likes = (video.likes || 0) + 1
    resolve({ success: true, likes: video.likes })
  })
}

export function collectVideo(videoId: string | number): Promise<{ success: boolean }> {
  return new Promise((resolve) => {
    const exists = mockVideoList.some((item) => String(item.videoId) === String(videoId))
    resolve({ success: exists })
  })
}

export function shareVideo(videoId: string | number): Promise<{ success: boolean; shares: number }> {
  return new Promise((resolve) => {
    const video = mockVideoList.find((item) => String(item.videoId) === String(videoId))
    if (!video) {
      resolve({ success: false, shares: 0 })
      return
    }

    video.shares = (video.shares || 0) + 1
    resolve({ success: true, shares: video.shares })
  })
}

export default {
  mockVideoList,
  generateMockDanmu,
  fetchVideoData,
  fetchVideoList,
  fetchRandomVideoList,
  fetchChoicenessCategories,
  fetchChoicenessVideos,
  sendDanmu,
  fetchVideoDanmu,
  likeVideo,
  collectVideo,
  shareVideo,
}
