<template>
  <div class="dashboard-page">
    <div class="page-head">
      <div>
        <h2>首页</h2>
        <p>视频、审核、互动、用户的实时概览</p>
      </div>
      <el-button :icon="Refresh" :loading="refreshing" plain @click="loadDashboardData">
        刷新数据
      </el-button>
    </div>

    <el-row :gutter="16" class="metric-row">
      <el-col v-for="item in metricCards" :key="item.key" :xs="12" :sm="8" :lg="4">
        <el-card class="metric-card" shadow="never">
          <div class="metric-top">
            <div class="metric-icon" :style="{ background: item.color }">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <span class="metric-label">{{ item.label }}</span>
          </div>
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-foot">{{ item.desc }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="14">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>近 7 日发布趋势</span>
              <span class="panel-sub">按创建时间统计</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>稿件状态分布</span>
              <span class="panel-sub">待审、已发布、已下线</span>
            </div>
          </template>
          <div ref="statusChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>分类热度</span>
              <span class="panel-sub">来自最近作品列表</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>最近作品</span>
              <span class="panel-sub">最新内容排在前面</span>
            </div>
          </template>
          <div class="mini-list">
            <div v-for="item in recentVideos" :key="item.videoId" class="mini-item">
              <div class="mini-main">
                <div class="mini-title">{{ item.videoName || '未命名作品' }}</div>
                <div class="mini-sub">
                  {{ item.nickName || '-' }} · {{ formatTime(item.publishTime || item.createTime) }}
                </div>
              </div>
              <el-tag :type="statusTagType(item.status)" size="small">
                {{ statusText(item.status) }}
              </el-tag>
            </div>
            <el-empty v-if="recentVideos.length === 0" description="暂无数据" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>待办概览</span>
              <span class="panel-sub">需要尽快处理的内容</span>
            </div>
          </template>
          <div class="todo-list">
            <div v-for="item in todoList" :key="item.label" class="todo-item">
              <div class="todo-label">{{ item.label }}</div>
              <div class="todo-value">{{ item.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>数据说明</span>
              <span class="panel-sub">当前首页的取数方式</span>
            </div>
          </template>
          <div class="summary-text">
            <p>1. 统计卡片和图表优先使用现有管理端接口返回的真实数据。</p>
            <p>2. 刷新数据使用容错加载，单个接口失败不会阻塞整页更新。</p>
            <p>3. 这个首页只替换 `/home` 页面内容，不影响你现有后台路由结构。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import {
  Refresh,
  Document,
  Tickets,
  ChatDotRound,
  User,
  VideoCamera,
  Stamp,
} from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance() as any

type DashboardMetric = {
  key: string
  label: string
  value: string
  desc: string
  icon: any
  color: string
}

type VideoItem = Record<string, any>

const trendChartRef = ref<HTMLDivElement | null>(null)
const statusChartRef = ref<HTMLDivElement | null>(null)
const categoryChartRef = ref<HTMLDivElement | null>(null)

const trendChart = ref<echarts.ECharts | null>(null)
const statusChart = ref<echarts.ECharts | null>(null)
const categoryChart = ref<echarts.ECharts | null>(null)
const refreshing = ref(false)

const metrics = ref({
  videoTotal: 0,
  videoPublished: 0,
  videoPending: 0,
  videoOffline: 0,
  auditPending: 0,
  userTotal: 0,
  commentTotal: 0,
  danmuTotal: 0,
  todayPublished: 0,
})

const recentVideos = ref<VideoItem[]>([])
const recentVideoSamples = ref<VideoItem[]>([])
const videoStatusSamples = ref<Array<{ label: string; value: number }>>([])

const metricCards = computed<DashboardMetric[]>(() => [
  {
    key: 'videoTotal',
    label: '作品总数',
    value: String(metrics.value.videoTotal),
    desc: '当前累计入库视频',
    icon: VideoCamera,
    color: 'linear-gradient(135deg, #4f8cff, #2d62ff)',
  },
  {
    key: 'videoPublished',
    label: '已发布',
    value: String(metrics.value.videoPublished),
    desc: '已进入公开视频池',
    icon: Document,
    color: 'linear-gradient(135deg, #21c87a, #0ea45f)',
  },
  {
    key: 'videoPending',
    label: '待处理',
    value: String(metrics.value.videoPending),
    desc: '草稿或待审核内容',
    icon: Tickets,
    color: 'linear-gradient(135deg, #ffb84d, #f08a24)',
  },
  {
    key: 'auditPending',
    label: '待审核',
    value: String(metrics.value.auditPending),
    desc: '等待处理的稿件',
    icon: Stamp,
    color: 'linear-gradient(135deg, #ff7a59, #ff4d4f)',
  },
  {
    key: 'commentTotal',
    label: '评论总数',
    value: String(metrics.value.commentTotal),
    desc: '内容互动沉淀',
    icon: ChatDotRound,
    color: 'linear-gradient(135deg, #7b61ff, #5b4bdb)',
  },
  {
    key: 'userTotal',
    label: '用户总数',
    value: String(metrics.value.userTotal),
    desc: '平台注册用户',
    icon: User,
    color: 'linear-gradient(135deg, #10b6c7, #0a8f9f)',
  },
])

const todoList = computed(() => [
  { label: '待审稿件', value: metrics.value.auditPending },
  { label: '待处理内容', value: metrics.value.videoPending },
  { label: '弹幕总数', value: metrics.value.danmuTotal },
  { label: '今日新增', value: metrics.value.todayPublished },
])

const formatTime = (value: any): string => {
  if (!value) return '-'
  const text = String(value).replace('T', ' ').replace('.0', '')
  return text.length > 19 ? text.slice(0, 19) : text
}

const statusText = (status: any): string => {
  if (status === 0 || status === '0') return '待审核'
  if (status === 1 || status === '1') return '已发布'
  if (status === 2 || status === '2') return '已下线'
  if (status === 3 || status === '3') return '审核失败'
  return '未知'
}

const statusTagType = (status: any): 'warning' | 'success' | 'info' | 'danger' => {
  if (status === 0 || status === '0') return 'warning'
  if (status === 1 || status === '1') return 'success'
  if (status === 2 || status === '2') return 'info'
  return 'danger'
}

const getList = (result: any): VideoItem[] => {
  if (!result || !result.data) return []
  if (Array.isArray(result.data)) return result.data
  return result.data.list || []
}

const getTotalCount = (result: any): number => {
  if (!result || !result.data) return 0
  if (typeof result.data.totalCount === 'number') return result.data.totalCount
  if (typeof result.data.total === 'number') return result.data.total
  if (Array.isArray(result.data)) return result.data.length
  return 0
}

const pickResult = (result: PromiseSettledResult<any>) => {
  return result.status === 'fulfilled' ? result.value : null
}

const loadDashboardData = async (): Promise<void> => {
  refreshing.value = true
  try {
    const results = await Promise.allSettled([
      proxy.$Request({ url: proxy.$Api.getLoadVideoInfo, params: { pageNo: 1, pageSize: 1 } }),
      proxy.$Request({
        url: proxy.$Api.getLoadVideoInfo,
        params: { pageNo: 1, pageSize: 1, status: 1 },
      }),
      proxy.$Request({
        url: proxy.$Api.getLoadVideoInfo,
        params: { pageNo: 1, pageSize: 1, status: 0 },
      }),
      proxy.$Request({
        url: proxy.$Api.getLoadVideoInfo,
        params: { pageNo: 1, pageSize: 1, status: 2 },
      }),
      proxy.$Request({
        url: proxy.$Api.getVideoAuditInfo,
        params: { pageNo: 1, pageSize: 1, status: 1 },
      }),
      proxy.$Request({
        url: proxy.$Api.getLoadDataList,
        params: { pageNo: 1, pageSize: 1, isCreatTimeDesc: 'desc' },
      }),
      proxy.$Request({ url: proxy.$Api.getLoadCommentList, params: { pageNo: 1, pageSize: 1 } }),
      proxy.$Request({ url: proxy.$Api.getLoadDanmuList, params: { pageNo: 1, pageSize: 1 } }),
      proxy.$Request({
        url: proxy.$Api.getLoadVideoInfo,
        params: {
          pageNo: 1,
          pageSize: 30,
          isDescOrAscCreateTime: 'desc',
        },
      }),
    ])

    const videoAllRes = pickResult(results[0])
    const videoPublishedRes = pickResult(results[1])
    const videoPendingRes = pickResult(results[2])
    const videoOfflineRes = pickResult(results[3])
    const auditPendingRes = pickResult(results[4])
    const userRes = pickResult(results[5])
    const commentRes = pickResult(results[6])
    const danmuRes = pickResult(results[7])
    const recentRes = pickResult(results[8])

    metrics.value = {
      videoTotal: getTotalCount(videoAllRes),
      videoPublished: getTotalCount(videoPublishedRes),
      videoPending: getTotalCount(videoPendingRes),
      videoOffline: getTotalCount(videoOfflineRes),
      auditPending: getTotalCount(auditPendingRes),
      userTotal: getTotalCount(userRes),
      commentTotal: getTotalCount(commentRes),
      danmuTotal: getTotalCount(danmuRes),
      todayPublished: 0,
    }

    recentVideos.value = getList(recentRes).slice(0, 6)
    recentVideoSamples.value = getList(recentRes)
    metrics.value.todayPublished = countTodayVideos(recentVideoSamples.value)

    videoStatusSamples.value = [
      { label: '待审核', value: metrics.value.videoPending },
      { label: '已发布', value: metrics.value.videoPublished },
      { label: '已下线', value: metrics.value.videoOffline },
      { label: '待审稿件', value: metrics.value.auditPending },
    ]

    await nextTick()
    renderCharts(true)
  } finally {
    refreshing.value = false
  }
}

const countTodayVideos = (list: VideoItem[]): number => {
  const today = new Date()
  const y = today.getFullYear()
  const m = today.getMonth()
  const d = today.getDate()
  return list.filter((item) => {
    const value = item.publishTime || item.createTime || item.submitTime
    if (!value) return false
    const date = new Date(value)
    return (
      date.getFullYear() === y &&
      date.getMonth() === m &&
      date.getDate() === d
    )
  }).length
}

const disposeChart = (instanceRef: typeof trendChart): void => {
  if (instanceRef.value) {
    instanceRef.value.dispose()
    instanceRef.value = null
  }
}

const renderCharts = (forceRecreate = false): void => {
  if (forceRecreate) {
    // 每次手动刷新都强制销毁并重建图表，确保相同数据也会重新渲染。
    disposeChart(trendChart)
    disposeChart(statusChart)
    disposeChart(categoryChart)
  }
  renderTrendChart()
  renderStatusChart()
  renderCategoryChart()
}

const ensureChart = (
  targetRef: typeof trendChartRef,
  instanceRef: typeof trendChart
): echarts.ECharts | null => {
  if (!targetRef.value) return null
  if (!instanceRef.value) {
    instanceRef.value = echarts.init(targetRef.value)
  }
  return instanceRef.value
}

const getLast7Days = (): string[] => {
  const list: string[] = []
  for (let i = 6; i >= 0; i -= 1) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    list.push(`${date.getMonth() + 1}/${date.getDate()}`)
  }
  return list
}

const renderTrendChart = (): void => {
  const chart = ensureChart(trendChartRef, trendChart)
  if (!chart) return

  const days = getLast7Days()
  const counts = days.map((_, index) => {
    const date = new Date()
    date.setDate(date.getDate() - (6 - index))
    return recentVideoSamples.value.filter((item) => {
      const value = item.publishTime || item.createTime || item.submitTime
      if (!value) return false
      const source = new Date(value)
      return (
        source.getFullYear() === date.getFullYear() &&
        source.getMonth() === date.getMonth() &&
        source.getDate() === date.getDate()
      )
    }).length
  })

  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 24, top: 35, bottom: 25 },
    xAxis: {
      type: 'category',
      data: days,
      axisLine: { lineStyle: { color: '#d9dce3' } },
      axisLabel: { color: '#667085' },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#edf0f5' } },
      axisLabel: { color: '#667085' },
    },
    series: [
      {
        name: '发布量',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        data: counts,
        lineStyle: { width: 3, color: '#4f8cff' },
        itemStyle: { color: '#4f8cff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(79, 140, 255, 0.35)' },
            { offset: 1, color: 'rgba(79, 140, 255, 0.03)' },
          ]),
        },
      },
    ],
  })
}

const renderStatusChart = (): void => {
  const chart = ensureChart(statusChartRef, statusChart)
  if (!chart) return

  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 4,
      textStyle: { color: '#667085' },
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 3 },
        label: { color: '#344054' },
        data: videoStatusSamples.value,
      },
    ],
  })
}

const renderCategoryChart = (): void => {
  const chart = ensureChart(categoryChartRef, categoryChart)
  if (!chart) return

  const categoryMap = new Map<string, number>()
  recentVideoSamples.value.forEach((item) => {
    const category = item.categoryName || item.pCategoryName || item.category || '未分类'
    const parent = item.pCategoryName ? `${item.pCategoryName}/${item.categoryName || ''}` : category
    const key = parent.replace(/\/$/, '')
    categoryMap.set(key, (categoryMap.get(key) || 0) + 1)
  })

  const entries = [...categoryMap.entries()].sort((a, b) => b[1] - a[1]).slice(0, 6)
  const names = entries.map(([name]) => name)
  const values = entries.map(([, value]) => value)

  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 70, right: 20, top: 20, bottom: 20 },
    xAxis: {
      type: 'value',
      axisLabel: { color: '#667085' },
      splitLine: { lineStyle: { color: '#edf0f5' } },
    },
    yAxis: {
      type: 'category',
      data: names,
      axisLabel: { color: '#667085' },
    },
    series: [
      {
        type: 'bar',
        data: values,
        barWidth: 18,
        itemStyle: {
          borderRadius: [0, 8, 8, 0],
          color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
            { offset: 0, color: '#4f8cff' },
            { offset: 1, color: '#7b61ff' },
          ]),
        },
      },
    ],
  })
}

const handleResize = (): void => {
  trendChart.value?.resize()
  statusChart.value?.resize()
  categoryChart.value?.resize()
}

onMounted(() => {
  loadDashboardData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart.value?.dispose()
  statusChart.value?.dispose()
  categoryChart.value?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 4px 2px 12px;
  min-height: 100%;
}

.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  padding: 4px 2px 0;

  h2 {
    margin: 0;
    font-size: 22px;
    font-weight: 700;
    color: #1f2937;
  }

  p {
    margin: 6px 0 0;
    color: #6b7280;
    font-size: 13px;
  }
}

.metric-row,
.chart-row {
  width: 100%;
}

.metric-card {
  border: 1px solid #edf0f5;
  border-radius: 12px;
  height: 132px;
  background: #fff;
}

.metric-top {
  display: flex;
  align-items: center;
  gap: 10px;
}

.metric-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  color: #fff;
  flex: 0 0 auto;
}

.metric-label {
  font-size: 14px;
  color: #6b7280;
}

.metric-value {
  margin-top: 16px;
  font-size: 30px;
  font-weight: 700;
  color: #111827;
  line-height: 1;
}

.metric-foot {
  margin-top: 10px;
  font-size: 12px;
  color: #8a94a6;
}

.panel-card {
  border: 1px solid #edf0f5;
  border-radius: 12px;
  background: #fff;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
  color: #111827;
  font-weight: 600;
}

.panel-sub {
  font-size: 12px;
  color: #8a94a6;
  font-weight: 400;
}

.chart-box {
  width: 100%;
  height: 320px;
}

.mini-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 320px;
}

.mini-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border: 1px solid #edf0f5;
  border-radius: 10px;
  background: #fafbfc;
}

.mini-main {
  min-width: 0;
}

.mini-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mini-sub {
  margin-top: 5px;
  font-size: 12px;
  color: #8a94a6;
}

.todo-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.todo-item {
  padding: 14px;
  border-radius: 10px;
  border: 1px solid #edf0f5;
  background: #fafbfc;
}

.todo-label {
  font-size: 13px;
  color: #6b7280;
}

.todo-value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 700;
  color: #111827;
}

.summary-text {
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: #4b5563;
  font-size: 13px;
  line-height: 1.7;
}

:deep(.el-card__header) {
  padding: 14px 16px;
}

:deep(.el-card__body) {
  padding: 16px;
}

@media (max-width: 768px) {
  .page-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .chart-box {
    height: 260px;
  }
}
</style>
