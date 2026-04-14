<template>
  <div class="video-panel">
    <div class="user-search" v-if="userSearchDisplay">
      <el-card :class="['user-search-card', isShrink ? 'user-shrink-show' : 'user-shrink']">
        <el-form :model="formData" ref="formDataRef" class="form-style" label-width="95px" @submit.prevent>
          <div class="show-form">
            <el-form-item class="input" label="视频标题" prop="videoNameFuzzy">
              <el-input
                clearable
                placeholder="请输入视频标题"
                v-model.trim="formData.videoNameFuzzy"
              ></el-input>
            </el-form-item>
            <el-form-item class="input" label="发送用户" prop="userKeyword">
              <el-input
                clearable
                placeholder="请输入用户ID或昵称"
                v-model.trim="formData.userKeyword"
              ></el-input>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select style="width: 130px" v-model="formData.status">
                <el-option label="全部" :value="-1"></el-option>
                <el-option label="显示中" :value="1"></el-option>
                <el-option label="已屏蔽" :value="0"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadDanmuList()" class="search-btn mainbtn" style="width: 100px">
                <i class="iconfont icon-sousuo"></i>
              </el-button>
              <el-button type="primary" @click="cheanFrom()" class="search-btn">
                <i class="iconfont icon-rubber"></i>
              </el-button>
              <el-button type="primary" @click="changeShrink()" class="search-btn" style="width: 35px">
                <i class="iconfont icon-kuoda"></i>
              </el-button>
            </el-form-item>
          </div>

          <div class="hidden-from">
            <el-form-item class="input" label="弹幕内容" prop="contentFuzzy">
              <el-input
                clearable
                placeholder="请输入弹幕关键字"
                v-model.trim="formData.contentFuzzy"
              ></el-input>
            </el-form-item>
            <el-form-item label="位置" prop="position">
              <el-select style="width: 130px" v-model="formData.position">
                <el-option label="全部" :value="-1"></el-option>
                <el-option label="滚动" :value="1"></el-option>
                <el-option label="顶部" :value="2"></el-option>
                <el-option label="底部" :value="3"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="发送时间" prop="createTimeRange">
              <el-date-picker
                v-model="formData.createTimeRange"
                type="daterange"
                value-format="YYYY-MM-DD"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              />
            </el-form-item>
          </div>
        </el-form>
      </el-card>
    </div>

    <div class="userCenter" v-if="userSearchDisplay">
      <h2 class="user-title">弹幕管理</h2>
      <div class="user-controll">
        <div :class="['contrual-btn jia', tableListLength <= 0 ? 'disable' : '']" @click="exportClick()">
          <i class="iconfont icon-xiazai"></i>
        </div>
        <div :class="['contrual-btn jia', tableListLength <= 0 ? 'disable' : '']" @click="changeSort()">
          <i class="iconfont icon-paixu"></i>
        </div>
        <div :class="['contrual-btn del', rowSelectedList.length <= 0 ? 'disable' : '']" @click="deleteSelectedRows">
          <i class="iconfont icon-lajitong"></i>
        </div>
      </div>
    </div>

    <div class="user-table">
      <el-card class="user-table-card">
        <span class="iconfont icon-kuoda1 upstep" @click="handleUpStepClick"></span>
        <Table
          ref="dataTableRef"
          :columns="columns"
          :dataSource="tableData"
          :fetch="loadDanmuList"
          :initFetch="true"
          :options="tableOptions"
          :MaxHeight="MaxHeight"
          @rowSelected="rowSelected"
        >
          <template #videoInfo="{ row }">
            <div class="video-info">
              <span class="video-name">{{ row.videoName || '-' }}</span>
              <span class="video-id">视频ID: {{ row.videoId }}</span>
            </div>
          </template>

          <template #sender="{ row }">
            <div class="author-info">
              <span class="author-name">{{ row.userName || row.userId }}</span>
              <span class="author-id">@{{ row.userId }}</span>
            </div>
          </template>

          <template #content="{ row }">
            <div class="danmu-content">
              <span class="color-dot" :style="{ backgroundColor: row.color || '#FFFFFF' }"></span>
              <span>{{ row.content }}</span>
            </div>
          </template>

          <template #position="{ row }">
            <span>{{ getPositionText(row.position) }}</span>
          </template>

          <template #timeOffset="{ row }">
            <span>{{ formatOffset(row.timeOffset) }}</span>
          </template>

          <template #status="{ row }">
            <div class="enabled">
              <span :class="['comment', Number(row.status) === 1 ? 'useing' : 'audit']">
                {{ Number(row.status) === 1 ? '显示中' : '已屏蔽' }}
              </span>
            </div>
          </template>

          <template #slotOperation="{ row }">
            <div class="row-op-panel">
              <a class="a-link" @click="toggleDanmuStatus(row)">
                {{ Number(row.status) === 1 ? '屏蔽' : '恢复' }}
              </a>
              <a class="a-link" @click="deleteDanmu(row)">删除</a>
            </div>
          </template>
        </Table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const { proxy } = getCurrentInstance() as any

const formDataRef = ref()
const formData = ref<Record<string, any>>({
  videoNameFuzzy: '',
  userKeyword: '',
  contentFuzzy: '',
  status: -1,
  position: -1,
  createTimeRange: [],
})

const tableOptions = {
  exHeight: 0,
  showIndex: false,
  selectType: 'checkbox',
  tableHeight: 720,
}

const columns = [
  {
    label: '弹幕ID',
    prop: 'danmuId',
    width: 120,
    align: 'center',
  },
  {
    label: '视频信息',
    prop: 'videoInfo',
    scopedSlots: 'videoInfo',
    width: 280,
    align: 'left',
  },
  {
    label: '发送用户',
    prop: 'sender',
    scopedSlots: 'sender',
    width: 180,
    align: 'left',
  },
  {
    label: '弹幕内容',
    prop: 'content',
    scopedSlots: 'content',
    align: 'left',
  },
  {
    label: '时间点',
    prop: 'timeOffset',
    scopedSlots: 'timeOffset',
    width: 120,
    align: 'center',
  },
  {
    label: '位置',
    prop: 'position',
    scopedSlots: 'position',
    width: 90,
    align: 'center',
  },
  {
    label: '字号',
    prop: 'fontSize',
    width: 80,
    align: 'center',
  },
  {
    label: '状态',
    prop: 'status',
    scopedSlots: 'status',
    width: 100,
    align: 'center',
  },
  {
    label: '发送时间',
    prop: 'createdAt',
    width: 180,
    align: 'center',
  },
  {
    label: '操作',
    prop: 'slotOperation',
    scopedSlots: 'slotOperation',
    width: 140,
    align: 'center',
  },
]

const tableData = ref<Record<string, any>>({
  list: [],
  pageNo: 1,
  pageSize: 15,
  totalCount: 0,
  pageTotal: 0,
})

const tableListLength = computed(() => {
  return (tableData.value?.list || []).length
})

const flag = ref('desc')
const MaxHeight = ref(565)
const isShrink = ref(false)
const userSearchDisplay = ref(true)
const rowSelectedList = ref<Array<Record<string, any>>>([])
const dataTableRef = ref()

const buildParams = () => {
  const params: Record<string, any> = {
    pageNo: tableData.value.pageNo,
    pageSize: tableData.value.pageSize,
    videoNameFuzzy: formData.value.videoNameFuzzy,
    userKeyword: formData.value.userKeyword,
    contentFuzzy: formData.value.contentFuzzy,
    status: formData.value.status,
    position: formData.value.position,
    isDescOrAscCreateTime: flag.value,
  }

  if (params.status === -1) {
    params.status = null
  }
  if (params.position === -1) {
    params.position = null
  }
  const range = formData.value.createTimeRange
  if (Array.isArray(range) && range.length === 2) {
    params.createTimeStart = range[0]
    params.createTimeEnd = range[1]
  }
  return params
}

const loadDanmuList = async (): Promise<void> => {
  const result = await proxy.$Request({
    url: proxy.$Api.getLoadDanmuList,
    params: buildParams(),
  })
  if (!result) {
    return
  }
  tableData.value = result.data || {
    list: [],
    pageNo: 1,
    pageSize: 15,
    totalCount: 0,
    pageTotal: 0,
  }
}

const rowSelected = async (selectedRows: Array<Record<string, any>>): Promise<void> => {
  rowSelectedList.value = selectedRows || []
}

const exportClick = (): void => {
  if (tableListLength.value <= 0) {
    return
  }
  dataTableRef.value.exportClick()
  proxy.$Message.success('导出成功')
}

const changeSort = (): void => {
  flag.value = flag.value === 'desc' ? 'asc' : 'desc'
  loadDanmuList()
}

const cheanFrom = () => {
  formData.value = {
    videoNameFuzzy: '',
    userKeyword: '',
    contentFuzzy: '',
    status: -1,
    position: -1,
    createTimeRange: [],
  }
  loadDanmuList()
}

const changeShrink = (): void => {
  isShrink.value = !isShrink.value
  MaxHeight.value = isShrink.value ? 510 : 565
}

const handleUpStepClick = (): void => {
  userSearchDisplay.value = !userSearchDisplay.value
  if (userSearchDisplay.value) {
    isShrink.value = false
    MaxHeight.value = 565
  } else {
    MaxHeight.value = 800
    tableOptions.tableHeight = 800
  }
}

const getPositionText = (position: number): string => {
  if (Number(position) === 2) {
    return '顶部'
  }
  if (Number(position) === 3) {
    return '底部'
  }
  return '滚动'
}

const formatOffset = (timeOffset: number): string => {
  const total = Math.max(0, Number(timeOffset || 0))
  const seconds = Math.floor(total / 1000)
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

const toggleDanmuStatus = async (row: Record<string, any>) => {
  const nextStatus = Number(row.status) === 1 ? 0 : 1
  const res = await proxy.$Request({
    url: proxy.$Api.updateDanmuStatus,
    params: {
      danmuId: row.danmuId,
      status: nextStatus,
    },
  })
  if (!res) {
    return
  }
  ElMessage.success(nextStatus === 1 ? '弹幕已恢复' : '弹幕已屏蔽')
  loadDanmuList()
}

const deleteDanmu = (row: Record<string, any>) => {
  ElMessageBox.confirm('确认删除这条弹幕吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await proxy.$Request({
      url: proxy.$Api.delDanmuInfo,
      params: {
        danmuIds: row.danmuId,
      },
    })
    if (!res) {
      return
    }
    ElMessage.success('删除成功')
    loadDanmuList()
  })
}

const deleteSelectedRows = (): void => {
  if (rowSelectedList.value.length <= 0) {
    return
  }
  ElMessageBox.confirm(`确认删除选中的 ${rowSelectedList.value.length} 条弹幕吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const danmuIds = rowSelectedList.value.map((item) => item.danmuId).join(',')
    const res = await proxy.$Request({
      url: proxy.$Api.delDanmuInfo,
      params: {
        danmuIds,
      },
    })
    if (!res) {
      return
    }
    ElMessage.success('批量删除成功')
    rowSelectedList.value = []
    loadDanmuList()
  })
}
</script>

<style lang="scss" scoped>
.video-panel {
  width: 100%;
  margin-top: 5px;
  display: flex;
  overflow: hidden;
  flex-direction: column;
  height: calc(100vh - 165px);
  .user-search {
    display: flex;
    .user-shrink {
      height: 80px;
    }
    .user-shrink-show {
      height: 130px;
    }
    .user-search-card {
      flex: 1;
      .form-style {
        display: flex;
        flex-direction: column;
        margin-top: 5px;
        .show-form {
          display: flex;
          justify-content: space-around;
        }
        .hidden-from {
          margin-top: 5px;
          display: flex;
        }
        .input {
          width: 280px;
        }
        .search-btn {
          background-color: #fff;
          border: 1px solid rgb(216, 216, 216);
          box-shadow: 3px 3px 2px rgba(216, 216, 216, 0.6);
          .iconfont {
            color: var(--blue);
          }
        }
        .search-btn:hover {
          box-shadow: none;
          background-color: var(--blue);
          .iconfont {
            color: #fff;
          }
        }
      }
    }
  }
  .userCenter {
    display: flex;
    justify-content: space-between;
    .user-title {
      color: #0a54c9;
      margin: 25px 0px 10px 5px;
    }
    .user-controll {
      display: flex;
      .contrual-btn {
        cursor: pointer;
        margin: 20px 10px;
        padding: 8px 16px;
        background-color: #fff;
        border-radius: 5px;
        box-shadow: 3px 3px 2px rgba(216, 216, 216, 0.6);
        color: var(--blue);
      }
      .jia:hover {
        background-color: var(--blue);
        color: #fff;
        box-shadow: none;
      }
      .del:hover {
        background-color: rgb(206, 58, 13);
        color: rgb(255, 255, 255);
        box-shadow: none;
      }
    }
  }
  .user-table {
    flex: 1;
    .user-table-card {
      height: 100%;
      position: relative;
      .upstep {
        position: absolute;
        cursor: pointer;
        top: 2px;
        right: 4px;
        color: #bbb9b9;
        border: 1px solid rgba(219, 218, 218, 0.6);
      }
      .enabled {
        .comment {
          padding: 2px 8px;
          border-radius: 5px;
        }
        .useing {
          background-color: #7073f4;
          color: #fff;
        }
        .audit {
          background-color: #f39364ff;
          color: #fff;
        }
      }
      .row-op-panel {
        display: flex;
        justify-content: center;
        gap: 10px;
        .a-link {
          cursor: pointer;
          color: #7169c5;
        }
        .a-link:hover {
          color: #52b8e7;
        }
      }
      .video-info {
        display: flex;
        flex-direction: column;
        gap: 4px;
        .video-name {
          color: #2b2b2b;
          font-weight: 600;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        .video-id {
          color: #8a8a8a;
          font-size: 12px;
        }
      }
      .author-info {
        display: flex;
        flex-direction: column;
        .author-name {
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        .author-id {
          color: #8a8a8a;
          font-size: 12px;
        }
      }
      .danmu-content {
        display: flex;
        align-items: center;
        gap: 8px;
        line-height: 1.45;
        max-height: 44px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        .color-dot {
          width: 10px;
          height: 10px;
          border-radius: 50%;
          flex-shrink: 0;
          border: 1px solid rgba(0, 0, 0, 0.2);
        }
      }
    }
  }
}
</style>
