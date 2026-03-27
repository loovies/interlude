// 模拟视频数据
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
}

export interface VideoQuality {
  quality: string
  url: string
  resolution?: string
  bitrate?: number
  recommended?: boolean
  disabled?: boolean
}

export interface DanmuData {
  id: number | string
  content: string
  time: number
  color?: string
  type?: 'normal' | 'top' | 'bottom' | 'scroll'
  speed?: number
  userId?: string
  userName?: string
}

// 快速加载的视频源（更小的文件，更快的CDN）
const fastVideoSources: string[] = [
  'https://vjs.zencdn.net/v/oceans.mp4', // 小文件，快速加载
  'https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4', // 较短视频
  'https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', // 较短视频
]

// 模拟视频列表数据
export const mockVideoList: VideoData[] = [
  {
    videoId: 1,
    title: '超美风景航拍，大自然的鬼斧神工',
    description: '带你领略世界各地的壮丽风景，感受大自然的魅力',
    duration: 596, // Big Buck Bunny 时长
    author: '旅行摄影师',
    likes: 12500,
    comments: 856,
    shares: 324,
    collects: 789,
    createTime: '2024-01-15',
    thumbnailUrl: 'https://picsum.photos/seed/video1/400/700',
    qualities: [
      {
        quality: '1080p',
        url: fastVideoSources[0]!!, // 使用快速加载的视频源
        resolution: '1920×1080',
        bitrate: 8000,
        recommended: true,
      },
      {
        quality: '720p',
        url: fastVideoSources[0]!, // 使用快速加载的视频源
        resolution: '1280×720',
        bitrate: 4000,
      },
      {
        quality: '480p',
        url: fastVideoSources[0]!, // 使用快速加载的视频源
        resolution: '854×480',
        bitrate: 2000,
      },
    ],
  },
  {
    videoId: 2,
    title: '萌宠日常，治愈你的不开心',
    description: '可爱猫咪的日常，看完心情都变好了',
    duration: 152,
    author: '宠物日记',
    likes: 8920,
    comments: 1245,
    shares: 567,
    collects: 432,
    createTime: '2024-01-14',
    thumbnailUrl: 'https://picsum.photos/seed/video2/400/700',
    qualities: [
      {
        quality: '1080p',
        url: fastVideoSources[1]!, // 使用快速加载的视频源
        resolution: '1920×1080',
        bitrate: 8000,
        recommended: true,
      },
      {
        quality: '720p',
        url: fastVideoSources[1]!, // 使用快速加载的视频源
        resolution: '1280×720',
        bitrate: 4000,
      },
      {
        quality: '480p',
        url: fastVideoSources[1]!, // 使用快速加载的视频源
        resolution: '854×480',
        bitrate: 2000,
      },
    ],
  },
  {
    videoId: 3,
    title: '美食制作教程，简单易学',
    description: '手把手教你制作美味甜点，零失败教程',
    duration: 245,
    author: '美食家小厨',
    likes: 15680,
    comments: 2345,
    shares: 890,
    collects: 678,
    createTime: '2024-01-13',
    thumbnailUrl: 'https://picsum.photos/seed/video3/400/700',
    qualities: [
      {
        quality: '1080p',
        url: fastVideoSources[2]!, // 使用快速加载的视频源
        resolution: '1920×1080',
        bitrate: 8000,
        recommended: true,
      },
      {
        quality: '720p',
        url: fastVideoSources[2]!, // 使用快速加载的视频源
        resolution: '1280×720',
        bitrate: 4000,
      },
      {
        quality: '480p',
        url: fastVideoSources[2]!, // 使用快速加载的视频源
        resolution: '854×480',
        bitrate: 2000,
      },
    ],
  },
  {
    videoId: 4,
    title: '健身教学，打造完美身材',
    description: '专业教练指导，在家也能高效健身',
    duration: 198,
    author: '健身达人',
    likes: 9870,
    comments: 1567,
    shares: 432,
    collects: 321,
    createTime: '2024-01-12',
    thumbnailUrl: 'https://picsum.photos/seed/video4/400/700',
    qualities: [
      {
        quality: '720p',
        url: fastVideoSources[0]!, // 使用快速加载的视频源
        resolution: '1280×720',
        bitrate: 4000,
        recommended: true,
      },
      {
        quality: '480p',
        url: fastVideoSources[0]!, // 使用快速加载的视频源
        resolution: '854×480',
        bitrate: 2000,
      },
      {
        quality: '360p',
        url: fastVideoSources[0]!, // 使用快速加载的视频源
        resolution: '640×360',
        bitrate: 1000,
      },
    ],
  },
  {
    videoId: 5,
    title: '搞笑合集，笑到肚子疼',
    description: '精选搞笑片段，专治各种不开心',
    duration: 167,
    author: '欢乐制造机',
    likes: 23450,
    comments: 3456,
    shares: 1234,
    collects: 890,
    createTime: '2024-01-11',
    thumbnailUrl: 'https://picsum.photos/seed/video5/400/700',
    qualities: [
      {
        quality: '1080p',
        url: fastVideoSources[1]!, // 使用快速加载的视频源
        resolution: '1920×1080',
        bitrate: 8000,
        recommended: true,
      },
      {
        quality: '720p',
        url: fastVideoSources[1]!, // 使用快速加载的视频源
        resolution: '1280×720',
        bitrate: 4000,
      },
      {
        quality: '480p',
        url: fastVideoSources[1]!, // 使用快速加载的视频源
        resolution: '854×480',
        bitrate: 2000,
      },
    ],
  },
]

// 弹幕内容库
const danmuContents = [
  '前方高能预警！',
  '这个视频太棒了！',
  '哈哈哈哈笑死我了',
  '泪目了😭',
  '这个特效太酷了',
  '求BGM名字',
  '已三连支持',
  '这个up主太有才了',
  '看到这里我哭了',
  '这个转场绝了',
  '求更新！',
  '这个教程太实用了',
  '收藏了慢慢看',
  '这个角度拍得真好',
  '音乐配得太好了',
  '这个梗我能笑一年',
  '太治愈了',
  '这个技巧学到了',
  'up主声音好好听',
  '这个运镜太专业了',
  '期待下一期',
  '这个美食看着就好吃',
  '这个宠物太可爱了',
  '这个风景太美了',
  '这个教学太详细了',
  '这个健身动作学会了',
  '这个搞笑片段绝了',
  '这个特效怎么做的',
  '求同款',
  '这个转场怎么拍的',
  '这个音乐叫什么',
  '这个up主我关注了',
  '这个视频我看了10遍',
  '这个教程拯救了我',
  '这个宠物成精了',
  '这个风景想去',
  '这个美食想学',
  '这个健身有效果',
  '这个搞笑停不下来',
  '这个特效值百万',
]

// 弹幕颜色库
const danmuColors = [
  '#ffffff', // 白色
  '#ff2d55', // 红色
  '#4cd964', // 绿色
  '#007aff', // 蓝色
  '#ff9500', // 橙色
  '#ffcc00', // 黄色
  '#5856d6', // 紫色
  '#ff3b30', // 亮红
  '#34c759', // 亮绿
  '#5ac8fa', // 亮蓝
]

// 用户名称库
const userNames = [
  '旅行爱好者',
  '美食家',
  '健身达人',
  '宠物控',
  '摄影迷',
  '音乐发烧友',
  '电影爱好者',
  '游戏玩家',
  '读书人',
  '科技迷',
  '时尚达人',
  '运动健将',
  '艺术青年',
  '历史爱好者',
  '科学探索者',
]

// 生成模拟弹幕数据
export function generateMockDanmu(videoId: string | number, duration: number): DanmuData[] {
  const danmuCount = Math.floor(Math.random() * 50) + 30 // 30-80条弹幕
  const danmus: DanmuData[] = []

  for (let i = 0; i < danmuCount; i++) {
    // 随机弹幕时间（在视频时长范围内）
    const time = Math.random() * duration
    
    // 随机弹幕类型
    const typeRoll = Math.random()
    let type: 'top' | 'bottom' | 'scroll' = 'scroll'
    if (typeRoll < 0.05) {
      type = 'top' // 5%顶部弹幕
    } else if (typeRoll < 0.1) {
      type = 'bottom' // 5%底部弹幕
    } else {
      type = 'scroll' // 90%滚动弹幕
    }

    // 随机弹幕内容
    const content = danmuContents[Math.floor(Math.random() * danmuContents.length)] || '666'
    
    // 随机颜色（普通弹幕才有随机颜色，特殊弹幕用固定颜色）
    let color = '#ffffff'
    if (type === 'scroll') {
      color = danmuColors[Math.floor(Math.random() * danmuColors.length)] || '#ffffff'
    } else if (type === 'top') {
      color = '#ff2d55' // 顶部弹幕用红色
    } else if (type === 'bottom') {
      color = '#007aff' // 底部弹幕用蓝色
    }

    // 随机速度（1-2倍速）
    const speed = 1 + Math.random()

    // 随机用户
    const userId = `user_${Math.floor(Math.random() * 10000)}`
    const userName = userNames[Math.floor(Math.random() * userNames.length)] ?? '匿名用户'

    danmus.push({
      id: `${videoId}_${i}_${Date.now()}`,
      content,
      time,
      color,
      type,
      speed,
      userId,
      userName,
    })
  }

  // 按时间排序
  return danmus.sort((a, b) => a.time - b.time)
}

// 获取视频数据（模拟API调用）
export function fetchVideoData(videoId: string | number): Promise<VideoData> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const video = mockVideoList.find(v => v.videoId === videoId)
      if (video) {
        resolve(video)
      } else {
        reject(new Error('视频不存在'))
      }
    }, 500) // 模拟网络延迟
  })
}

// 获取视频列表（模拟API调用）
export function fetchVideoList(page: number = 1, pageSize: number = 10): Promise<{ data: VideoData[], total: number }> {
  return new Promise((resolve) => {
    setTimeout(() => {
      const start = (page - 1) * pageSize
      const end = start + pageSize
      const data = mockVideoList.slice(start, end)
      
      resolve({
        data,
        total: mockVideoList.length,
      })
    }, 800) // 模拟网络延迟
  })
}

// 发送弹幕（模拟API调用）
export function sendDanmu(videoId: string | number, content: string): Promise<{ success: boolean, danmuId: string }> {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log(`发送弹幕到视频 ${videoId}: ${content}`)
      resolve({
        success: true,
        danmuId: `danmu_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      })
    }, 300) // 模拟网络延迟
  })
}

// 获取视频弹幕（模拟API调用）
export function fetchVideoDanmu(videoId: string | number): Promise<DanmuData[]> {
  return new Promise((resolve) => {
    setTimeout(() => {
      const video = mockVideoList.find(v => v.videoId === videoId)
      if (video) {
        const danmus = generateMockDanmu(videoId, video.duration)
        resolve(danmus)
      } else {
        resolve([])
      }
    }, 600) // 模拟网络延迟
  })
}

// 模拟点赞/收藏/分享
export function likeVideo(videoId: string | number): Promise<{ success: boolean, likes: number }> {
  return new Promise((resolve) => {
    setTimeout(() => {
      const video = mockVideoList.find(v => v.videoId === videoId)
      if (video) {
        video.likes = (video.likes || 0) + 1
        resolve({
          success: true,
          likes: video.likes,
        })
      } else {
        resolve({
          success: false,
          likes: 0,
        })
      }
    }, 200)
  })
}

export function collectVideo(videoId: string | number): Promise<{ success: boolean }> {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log(`收藏视频 ${videoId}`)
      resolve({ success: true })
    }, 200)
  })
}

export function shareVideo(videoId: string | number): Promise<{ success: boolean, shares: number }> {
  return new Promise((resolve) => {
    setTimeout(() => {
      const video = mockVideoList.find(v => v.videoId === videoId)
      if (video) {
        video.shares = (video.shares || 0) + 1
        resolve({
          success: true,
          shares: video.shares,
        })
      } else {
        resolve({
          success: false,
          shares: 0,
        })
      }
    }, 200)
  })
}

// 导出默认
export default {
  mockVideoList,
  generateMockDanmu,
  fetchVideoData,
  fetchVideoList,
  sendDanmu,
  fetchVideoDanmu,
  likeVideo,
  collectVideo,
  shareVideo,
}