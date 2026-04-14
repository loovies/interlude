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
            <el-form-item class="input" label="评论用户" prop="authorKeyword">
              <el-input
                clearable
                placeholder="请输入用户ID或昵称"
                v-model.trim="formData.authorKeyword"
              ></el-input>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select style="width: 130px" v-model="formData.status">
                <el-option label="全部" value="all"></el-option>
                <el-option label="显示中" value="visible"></el-option>
                <el-option label="已屏蔽" value="hidden"></el-option>
                <el-option label="已删除" value="deleted"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadCommentList()" class="search-btn mainbtn" style="width: 100px">
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
            <el-form-item class="input" label="评论内容" prop="contentFuzzy">
              <el-input
                clearable
                placeholder="请输入评论内容关键字"
                v-model.trim="formData.contentFuzzy"
              ></el-input>
            </el-form-item>
            <el-form-item label="评论类型" prop="commentType">
              <el-select style="width: 130px" v-model="formData.commentType">
                <el-option label="全部" :value="-1"></el-option>
                <el-option label="主评论" :value="0"></el-option>
                <el-option label="回复" :value="1"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="评论时间" prop="createTimeRange">
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
      <h2 class="user-title">评论管理</h2>
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
          :fetch="loadCommentList"
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

          <template #author="{ row }">
            <div class="author-info">
              <Avatar :avatar="row.authorAvatar" :lazy="false" :width="32"></Avatar>
              <div class="author-text">
                <span class="author-name">{{ row.authorName || row.authorId }}</span>
                <span class="author-id">@{{ row.authorId }}</span>
              </div>
            </div>
          </template>

          <template #commentType="{ row }">
            <span>{{ row.parentCommentId ? '回复' : '主评论' }}</span>
          </template>

          <template #content="{ row }">
            <div class="comment-content">
              <span v-if="row.replyToUserName" class="reply-user">回复 {{ row.replyToUserName }}：</span>
              <span>{{ row.content }}</span>
            </div>
          </template>

          <template #status="{ row }">
            <div class="enabled">
              <span :class="['comment', getStatusClass(row.status)]">{{ getStatusText(row.status) }}</span>
            </div>
          </template>

          <template #metrics="{ row }">
            <span>赞 {{ row.likeCount || 0 }} / 回复 {{ row.replyCount || 0 }}</span>
          </template>

          <template #slotOperation="{ row }">
            <div class="row-op-panel">
              <a class="a-link" @click="toggleCommentStatus(row)">
                {{ row.status === 'visible' ? '屏蔽' : '恢复' }}
              </a>
              <a class="a-link" @click="deleteComment(row)">删除</a>
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
  authorKeyword: '',
  contentFuzzy: '',
  commentType: -1,
  status: 'all',
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
    label: '评论ID',
    prop: 'commentId',
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
    label: '评论用户',
    prop: 'author',
    scopedSlots: 'author',
    width: 200,
    align: 'left',
  },
  {
    label: '评论类型',
    prop: 'commentType',
    scopedSlots: 'commentType',
    width: 100,
    align: 'center',
  },
  {
    label: '评论内容',
    prop: 'content',
    scopedSlots: 'content',
    align: 'left',
  },
  {
    label: '互动',
    prop: 'metrics',
    scopedSlots: 'metrics',
    width: 140,
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
    label: '评论时间',
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
    authorKeyword: formData.value.authorKeyword,
    contentFuzzy: formData.value.contentFuzzy,
    commentType: formData.value.commentType,
    status: formData.value.status,
    isDescOrAscCreateTime: flag.value,
  }
  if (params.commentType === -1) {
    params.commentType = null
  }
  if (params.status === 'all') {
    params.status = null
  }

  const range = formData.value.createTimeRange
  if (Array.isArray(range) && range.length === 2) {
    params.createTimeStart = range[0]
    params.createTimeEnd = range[1]
  }
  return params
}

const loadCommentList = async (): Promise<void> => {
  const result = await proxy.$Request({
    url: proxy.$Api.getLoadCommentList,
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
  loadCommentList()
}

const cheanFrom = () => {
  formData.value = {
    videoNameFuzzy: '',
    authorKeyword: '',
    contentFuzzy: '',
    commentType: -1,
    status: 'all',
    createTimeRange: [],
  }
  loadCommentList()
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

const getStatusText = (status: string): string => {
  if (status === 'visible') {
    return '显示中'
  }
  if (status === 'hidden') {
    return '已屏蔽'
  }
  if (status === 'deleted') {
    return '已删除'
  }
  return status || '-'
}

const getStatusClass = (status: string): string => {
  if (status === 'visible') {
    return 'useing'
  }
  if (status === 'hidden') {
    return 'audit'
  }
  return 'stoping'
}

const toggleCommentStatus = async (row: Record<string, any>) => {
  const nextStatus = row.status === 'visible' ? 'hidden' : 'visible'
  const res = await proxy.$Request({
    url: proxy.$Api.updateCommentStatus,
    params: {
      commentId: row.commentId,
      status: nextStatus,
    },
  })
  if (!res) {
    return
  }
  ElMessage.success(nextStatus === 'visible' ? '评论已恢复' : '评论已屏蔽')
  loadCommentList()
}

const deleteComment = (row: Record<string, any>) => {
  ElMessageBox.confirm('确认删除这条评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await proxy.$Request({
      url: proxy.$Api.delCommentInfo,
      params: {
        commentIds: row.commentId,
      },
    })
    if (!res) {
      return
    }
    ElMessage.success('删除成功')
    loadCommentList()
  })
}

const deleteSelectedRows = (): void => {
  if (rowSelectedList.value.length <= 0) {
    return
  }
  ElMessageBox.confirm(`确认删除选中的 ${rowSelectedList.value.length} 条评论吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const commentIds = rowSelectedList.value.map((item) => item.commentId).join(',')
    const res = await proxy.$Request({
      url: proxy.$Api.delCommentInfo,
      params: {
        commentIds,
      },
    })
    if (!res) {
      return
    }
    ElMessage.success('批量删除成功')
    rowSelectedList.value = []
    loadCommentList()
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
        .stoping {
          background-color: rgba(122, 120, 119, 1);
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
        align-items: center;
        gap: 8px;
        .author-text {
          min-width: 0;
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
      }
      .comment-content {
        line-height: 1.45;
        max-height: 44px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        .reply-user {
          color: #007fff;
        }
      }
    }
  }
}
</style>
