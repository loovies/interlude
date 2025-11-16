<template>
  <div class="video-panel" v-if="!isUploadvideo">
    <div class="user-search" v-if="userSearchDisplay">
      <el-card :class="['user-search-card', isShrink ? 'user-shrink-show' : 'user-shrink']">
        <el-form
          :model="formData"
          ref="formDataRef"
          class="form-style"
          label-width="100px"
          @submit.prevent
        >
          <div class="show-form">
            <!--input输入-->
            <el-form-item class="input" label="视频名称" prop="nickNameFuzzy">
              <el-input
                clearable
                placeholder="请输入视频名称"
                v-model.trim="formData.nickNameFuzzy"
              ></el-input>
            </el-form-item>
            <el-form-item label="分类" prop="enabled">
              <el-select style="width: 140px" placeholder="请选择" v-model="formData.enabled">
                <el-option :value="2" label="全部"></el-option>
                <el-option :value="0" label="未启用"></el-option>
                <el-option :value="1" label="已启用"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="视频类型:" prop="videoType">
              <el-radio-group v-model="formData.videoType">
                <el-radio :label="1">原视频</el-radio>
                <el-radio :label="2">转载视频</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="可见性:" prop="visibility">
              <el-checkbox-group v-model="formData.visibility" size="small">
                <el-checkbox label="公共" :value="0" border />
                <el-checkbox label="私人" :value="1" border />
                <el-checkbox label="仅限好友" :value="2" border />
              </el-checkbox-group>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="loadUserInfoList()"
                class="search-btn mainbtn"
                style="width: 100px"
              >
                <i class="iconfont icon-sousuo"></i>
              </el-button>
              <el-button type="primary" @click="cheanFrom()" class="search-btn">
                <i class="iconfont icon-rubber"></i>
              </el-button>
              <el-button
                type="primary"
                @click="changeShrink()"
                class="search-btn"
                style="width: 35px"
              >
                <i class="iconfont icon-kuoda"></i>
              </el-button>
            </el-form-item>
          </div>

          <div class="hidden-from">
            <el-form-item label="发布时间" prop="createTimeArray">
              <el-date-picker
                v-model="formData.createTimeArray"
                type="dates"
                placeholder="请输入发布时间"
              />
            </el-form-item>
            <el-form-item label="上次播放时间" prop="lastPlayTime" style="margin-left: 50px">
              <el-date-picker
                v-model="formData.lastPlayTime"
                type="dates"
                placeholder="请输入上次播放时间"
              />
            </el-form-item>
            <el-form-item label="状态" prop="enabled">
              <el-select style="width: 140px" placeholder="请选择" v-model="formData.enabled">
                <el-option :value="3" label="全部"></el-option>
                <el-option :value="0" label="已发布"></el-option>
                <el-option :value="1" label="已离线"></el-option>
                <el-option :value="2" label="已删除"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </el-form>
      </el-card>
      <el-card class="box-card" v-if="fileList.length <= 0">
        <el-upload
          class="uploader-start"
          multiple
          drag
          :show-file-list="false"
          :http-request="addFile"
          :before-upload="startUpload"
          :accept="proxy.videoAccept"
          :limit="3"
        >
        </el-upload>
        <div class="iconfont icon-jia"></div>
      </el-card>
      <el-card v-else class="box-card" @click="editVideo()">
        <div class="iconfont icon-jia"></div>
      </el-card>
    </div>
    <div class="userCenter" v-if="userSearchDisplay">
      <h2 class="user-title">视频列表</h2>
      <div class="user-controll">
        <div
          :class="['contrual-btn jia', tableData.length <= 0 ? 'disable' : '']"
          @click="exportClick()"
        >
          <i class="iconfont icon-xiazai"></i>
        </div>
        <div
          :class="['contrual-btn jia', tableData.length <= 0 ? 'disable' : '']"
          @click="changeSort()"
        >
          <i class="iconfont icon-paixu"></i>
        </div>
        <div
          :class="['contrual-btn', 'del', rowSelectedList.length <= 0 ? 'disable' : '']"
          @click="deleteSelectedRows"
        >
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
          :fetch="loadUserInfoList"
          :initFetch="true"
          :options="tableOptions"
          :MaxHeight="MaxHeight"
          @rowSelected="rowSelected"
        >
        </Table>
      </el-card>
    </div>
  </div>
  <videoEdit
    v-if="isUploadvideo"
    ref="videoEditRef"
    @closeVideoEdit="changeUploadvideo"
    :videoInfo="fileList"
  ></videoEdit>
</template>

<script setup lang="ts">
import videoEdit from './videoEdit.vue'

import { ElMessage, ElMessageBox } from 'element-plus'
import { ref, getCurrentInstance, nextTick, Ref, watch, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRoleByUserId } from '@/utils/Api.js'
import { storeToRefs } from 'pinia'

import { useLoginStore } from '../../../stores/loginStore.ts'
const loginStore = useLoginStore()

const { userInfo } = storeToRefs(loginStore)

const currentUserId = computed(() => {
  return userInfo.value.userId
})

const { proxy } = getCurrentInstance()

const route = useRoute()
const router = useRouter()

const formData = ref<Record<string, any>>({}) // 键值对象
const formDataRef = ref()

const tableOptions = {
  exHeight: 0,
  showIndex: true,
  selectType: 'checkbox',
  tableHeight: 720,
}

const columns = [
  {
    label: '文件名',
    prop: 'fileName',
    width: 100,
  },
  {
    label: '视频封面',
    prop: 'videoCover',
    scopedSlots: 'videoCover',
    width: 280,
  },
  {
    label: '用户名',
    prop: 'nickName',
    width: 100,
  },
  {
    label: '分类',
    prop: 'category',
    scopedSlots: 'category',
    align: 'center',
  },
  {
    label: '视频类型',
    prop: 'videoType',
    scopedSlots: 'videoType',
    align: 'center',
  },
  {
    label: '状态',
    prop: 'status',
    scopedSlots: 'status',
    align: 'center',
  },
  {
    label: '发布时间',
    prop: 'publishTime',
    align: 'center',
  },
  {
    label: '操作',
    prop: 'type',
    scopedSlots: 'slotOperation',
    align: 'center',
    width: 300,
  },
]

// 判断是否为视频编辑页面
let isUploadvideo: Ref<boolean> = ref(false)
let fileList = ref([])
let videoEditRef = ref()

const addFile = (file: Object) => {
  isUploadvideo.value = true
  nextTick(() => {
    videoEditRef.value.uploadFile(file)
  })
}

const editVideo = () => {
  isUploadvideo.value = true
}

const changeUploadvideo = () => {
  isUploadvideo.value = !isUploadvideo.value
  selectDarftInfo()
}

onMounted(() => {
  selectDarftInfo()
})

// 查询草稿信息
const selectDarftInfo = async (): Promise<void> => {
  let res = await proxy.$Request({
    url: proxy.$Api.getDraftInfoByUserId,
  })
  if (!res) return
  fileList.value = res.data ? res.data : []
}

const startUpload = (file: Object) => {}

// 用户信息表格数据
const tableData = ref([])
const flag = ref('asc')

// 加载用户信息列表
const loadUserInfoList = async (): Promise<void> => {
  let params: Record<string, any> = {
    pageNo: tableData.value.pageNo,
    pageSize: tableData.value.pageSize,
  }
}

const MaxHeight: Ref<number> = ref(565)
const isShrink: Ref<boolean> = ref(false)

// 收起/展开搜索框
const changeShrink = (): void => {
  isShrink.value = !isShrink.value
  if (isShrink.value) {
    MaxHeight.value = 510
  } else {
    MaxHeight.value = 565
  }
}

const cheanFrom = () => {
  formData.value = ref<Record<string, any>>({})
  loadUserInfoList()
}

// 单选按钮点击处理函数
const handleRadioClick = (value) => {
  // 如果点击的是当前已选中的值，则清空，否则设置为新值
  formData.value.sex = value === formData.value.sex ? null : value
}

const dataTableRef = ref()

// 导出用户信息
const exportClick = (): void => {
  dataTableRef.value.exportClick()
  proxy.$Message.success('导出成功')
}

// 排序切换
const changeSort = (): void => {
  if (flag.value === 'desc') {
    flag.value = 'asc'
  } else {
    flag.value = 'desc'
  }
  loadUserInfoList()
}

const rowSelectedList = ref([])

// 选中行数据
const rowSelected = async (selectedRows: Array<Object>): Promise<void> => {}

// 删除选中行
const deleteSelectedRows = (): void => {}

const userSearchDisplay = ref(true)
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
</script>

<style lang="scss" scoped>
.video-panel {
  width: 100%;
  margin-top: 5px;
  display: flex;
  overflow: hidden;
  flex-direction: column;
  height: calc(100vh - 165px); /* 或者使用 calc(100vh - 某个固定值) 来减去顶部导航等高度 */
  .user-search {
    display: flex;
    .box-card {
      position: relative;
      width: 80px;
      height: 80px;
      margin-left: 20px;
      cursor: pointer;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: var(--blue);
      color: #ffffff;
      .uploader-start {
        position: absolute;
        top: 0;
        left: 0;
        width: 80px;
        height: 80px;
        opacity: 0;
      }
    }
    .box-card:hover {
      background-color: #2b77f3ff;
    }
    .user-shrink {
      height: 80px;
    }
    .user-shrink-show {
      height: 130px;
    }
    .user-search-card {
      flex: 1;
      transition: width 1s ease 1s;
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
          padding: 2px 5px;
          cursor: pointer;
          border-radius: 5px;
        }
        .useing {
          background-color: #7073f4;
          color: #fff;
        }
        .stoping {
          background-color: rgb(219, 93, 8);
          color: #fff;
        }
      }
      .row-op-panel {
        display: flex;
        justify-content: space-around;
        .a-link {
          cursor: pointer;
          color: #7169c5;
        }
        .a-link:hover {
          color: #52b8e7;
        }
      }
    }
  }
}
</style>
