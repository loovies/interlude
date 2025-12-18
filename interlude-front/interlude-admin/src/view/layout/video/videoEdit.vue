<template>
  <div class="videoEditContantor">
    <el-button type="primary" :class="[isVideoInput ? '' : 'retBtn']" @click="returnVideoEdit()">
      <i class="iconfont icon-fanhui"></i>
      返回视频管理</el-button
    >
    <div
      v-if="isUploadVideo"
      class="file-list"
      v-draggable="[fileList, { animation: 150, handle: '.video-p' }]"
    >
      <div class="file-item" v-for="(item, index) in fileList" :key="index">
        <el-card class="file-box-card">
          <div class="video-p">
            <div class="iconfont icon-video"></div>
          </div>
          <div class="video-info">
            <div class="video-title">
              <div class="upload-info">
                <div class="title">
                  <el-input
                    v-show="item.edit"
                    v-model="item.fileName"
                    :id="'file-input' + item.uid"
                    class="title-input"
                    size="small"
                    @blur="endEdit(index)"
                  ></el-input>
                  <div v-show="!item.edit" class="title-show" @click="editFileName(index)">
                    {{ item.fileName }}
                  </div>
                  <div class="upload-status">
                    <span v-if="item.status == 'uploading'">
                      已上传: {{ proxy.$Utils.size2Str(item.uploadSize) }}/{{
                        proxy.$Utils.size2Str(item.fileSize)
                      }}
                    </span>
                    <span
                      v-else
                      :class="['iconfont', 'icon-' + STATUS[item.status].icon]"
                      :style="{ color: STATUS[item.status].color }"
                      >{{ STATUS[item.status].desc }}</span
                    >
                  </div>
                </div>
                <div class="op">
                  <div class="item percent" v-if="item.status == 'uploading'">
                    {{ item.uploadPercent }}%
                  </div>
                  <template v-if="item.status == 'uploading'">
                    <div
                      v-if="item.pause"
                      class="item iconfont icon-play"
                      @click="restartUpload(item.uid)"
                    ></div>
                    <div
                      v-else
                      class="item iconfont icon-pause"
                      @click="pauseUpload(item.uid)"
                    ></div>
                  </template>
                  <div class="item iconfont icon-guanbi close" @click="delFile(index)"></div>
                </div>
                <div
                  class="video-propgress"
                  v-if="item.status == 'uploading' || item.status == 'success'"
                >
                  <el-progress
                    :percentage="item.uploadPercent"
                    :show-text="false"
                    :stroke-width="3"
                    :status="item.status == 'uploading' ? '' : 'success'"
                  ></el-progress>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
    <div class="add-video-btn" v-if="fileList.length < MAX_FILELIST && isVideoInput">
      <el-upload
        multiple
        :show-file-list="false"
        :http-request="addFile"
        :accept="proxy.videoAccept"
      >
        <el-button type="primary" class="addVideo">添加视频</el-button>
      </el-upload>
    </div>
  </div>
  <videoInput
    v-if="fileList.length > 0 && isVideoInput"
    :fileList="fileList"
    @update:fileList="fileList = $event"
    ref="videoInputRef"
  ></videoInput>
  <videoInput
    v-if="!isVideoInput"
    ref="videoInputRef"
    @addFile="changeVideoFile"
    :updateFileList="updateFileList"
  ></videoInput>
</template>

<script setup lang="ts">
import videoInput from './videoInput.vue'
import { ref, reactive, getCurrentInstance, nextTick } from 'vue'
const { proxy } = getCurrentInstance()
import { vDraggable } from 'vue-draggable-plus'
import { useSysSettingStore } from '../../../stores/sysSettingStore.ts'
const sysSettingStore: any = useSysSettingStore()

// 文件状态
const STATUS = {
  emptyfile: {
    value: 'emptyfile',
    desc: '文件为空',
    color: '#F75000',
    icon: 'error',
  },
  largefile: {
    value: 'largefile',
    desc: '文件超过大小' + sysSettingStore.sysSetting.videoSize + 'MB',
    color: '#F75000',
    icon: 'error',
  },
  wating: {
    value: 'wating',
    desc: '等待上传',
    color: '#e6a23c',
    icon: 'icon_wating',
  },
  uploading: {
    value: 'uploading',
    desc: '上传中',
    color: '#409eff',
    icon: 'uploadingshangchuan',
  },
  fail: {
    value: 'fail',
    desc: '上传失败',
    color: '#F75000',
    icon: 'error',
  },
  success: {
    value: 'success',
    desc: '上传完成',
    color: '#67c23a',
    icon: 'success',
  },
}

const props = defineProps({
  videoInfo: {
    type: Array,
    default: [],
  },
})

// 文件列表
let fileList: Ref<Array<any>> = ref(props.videoInfo?.length == 0 ? [] : props.videoInfo)
// let fileList: Ref<Array<any>> = ref([])
let updateFileList: Ref<Array<any>> = ref([])

const videoInputRef = ref()

// const isUpdateEdit = ref(true)
const isVideoInput = ref(true)
const isUploadVideo = ref(true)

// 分片大小
const CHUNK_SIZE = proxy.chunkSize

// 同时最大上传数量
const MAX_UPLOADING = proxy.maxUploading
const MAX_FILELIST = proxy.maxFileListNumber

const emit = defineEmits(['closeVideoEdit'])

const returnVideoEdit = () => {
  emit('closeVideoEdit')
}

const uploadFile = (file: Object, uploadId: string) => {
  file = file.file
  // 文件名
  console.log(file.name)
  let fileName = proxy.$Utils.getFileName(file.name)
  // 文件的基本信息
  const fileItem: Object = {
    file: file,
    uid: file.uid,
    fileName: fileName,
    status: STATUS.wating.value,
    uploadSize: 0, // 上传大小
    fileSize: file.size,
    uploadPercent: 0, // 上传百分比
    pause: false,
    chunkIndex: 0, // 分片索引
    errorMsg: null,
    fileIdentifier: generateFileIdentifier(file),
    uploadId,
  }
  if (fileList.value.length == MAX_FILELIST) {
    proxy.$Message.warning('超出最大上传文件数量' + MAX_FILELIST)
    return
  }
  fileList.value.push(fileItem)
  // 判断文件大小是否为0
  if (fileItem.fileSize == 0) {
    proxy.$Message.error(STATUS.emptyfile.desc)
    emit('closeVideoEdit')
    return
  }
  //判断文件大小是否超过设定最大值
  if (fileItem.fileSize > sysSettingStore.sysSetting.videoSize * 1024 * 1024) {
    proxy.$Message.error(STATUS.largefile.desc)
    emit('closeVideoEdit')
    return
  }
  // 筛选文件的状态为上传中的文件
  let uploadFiles = fileList.value.filter((item) => {
    return item.status == STATUS.uploading.value
  })

  // 判断同时最大上传数量
  if (uploadFiles.length > MAX_UPLOADING) {
    emit('closeVideoEdit')
    return
  }

  // 上传uid
  uploadVideo4Draft(fileItem.uid, 0)
}

const getFileByuid = (uid: number): Object => {
  const currentFile = fileList.value.find((item) => item.uid == uid)
  return currentFile
}

const uploadVideo4Draft = async (uid: number, chunkIndex: number): Promise<void> => {
  // 根据 uid获取文件
  const currentFile = getFileByuid(uid)

  // 文件状态设置为上传中
  currentFile.status = STATUS.uploading.value
  // 设置分片索引
  chunkIndex = chunkIndex ? chunkIndex : 0
  const file = currentFile.file
  const fileSize = currentFile.fileSize
  // 用 文件大小 除 每个分片的规定大小 向上取整得总分片得数量
  const chunks = Math.ceil(fileSize / CHUNK_SIZE) // 当前视频的分片数量

  // 如果文件没上传id 就上传视频, 并保存草稿表
  debugger
  if (!currentFile.uploadId) {
    let result = await proxy.$Request({
      url: proxy.$Api.preUploadVideo,
      params: {
        fileIdentifier: currentFile.fileIdentifier,
        fileName: currentFile.fileName,
        chunks,
      },
      errorCallback: (errorMsg) => {
        currentFile.status = STATUS.fail.value
        currentFile.errorMsg = errorMsg
      },
    })
    if (!result) {
      return
    }
    // 如果时上传中的视频
    if (result.data.status == STATUS.uploading.value) {
      Object.assign(currentFile, result.data)
      chunkIndex = result.data.chunkIndex
    } else {
      // 如果时上传成功的视频
      currentFile.uploadId = result.data.uploadId
    }
    console.log(currentFile)
  }

  // 循环分片数量
  for (let i = chunkIndex; i < chunks; i++) {
    if (currentFile.pause || currentFile.del) {
      // 如果传输暂停或者文件被删除就跳出循环
      break
    }
    // 获得开始的文件大小, 如果刚开始上传就是从0开始
    // 暂停后,在开始就是根据每次chunkIndex * 每个分片大小 来判断 解除暂停后开始的文件大小
    // 每个分片的上传完成后,给chunkIndex动态赋值i,记录当前分片上传文件的进度索引
    let start = i * CHUNK_SIZE
    // 结尾大小 = 开始的文件大小 + 每个分片设定的大小
    // 结尾大小是指每个分片结尾的大小
    // 如果结尾文件大小大于文件总大小就设置 结尾文件大小为总大小
    let end = start + CHUNK_SIZE >= fileSize ? fileSize : start + CHUNK_SIZE
    // file.slice(start,end) 运行将大文件切割成较小的片段
    // start: 切割的起始站字节索引  end: 结束字节索引
    let chunkFile = file.slice(start, end)
    let uploadData = await proxy.$Request({
      url: proxy.$Api.uploadVideo,
      dataType: 'file',
      params: {
        file: chunkFile,
        chunksIndex: i,
        uploadId: currentFile.uploadId,
        uid: file.uid,
        status: currentFile.status,
        uploadSize: currentFile.uploadSize,
        uploadPercent: currentFile.uploadPercent,
        fileSize: currentFile.fileSize,
        pause: currentFile.pause,
      },
      showError: false,
      errorCallback: (errorMsg) => {
        currentFile.status = STATUS.fail.value
        currentFile.errorMsg = errorMsg
      },
      uploadProgressCallback: (even) => {
        // loaded 属性表示已上传的字节数
        // 防止已上传的字节数超过文件总大小 确保进度不会超过 100%
        let loaded = even.loaded
        if (loaded > fileSize) {
          loaded = fileSize
        }

        // 总上传大小 = 已完成的分块总大小 加 当前分块已上传的大小
        currentFile.uploadSize = i * CHUNK_SIZE + loaded
        // 计算已上传部分占总文件大小的百分比 Math.floor() 向下取整，显示整数百分比
        //  (25/100) × 100 = 25%
        currentFile.uploadPercent = Math.floor((currentFile.uploadSize / fileSize) * 100)
      },
    })
    if (uploadData == null) {
      break
    }

    // 记录当前的分片索引
    currentFile.chunkIndex = i
    // 如果当前分片 < 总分片-1
    if (i < chunks - 1) {
      continue
    }
    currentFile.status = STATUS.success.value
    currentFile.uploadPercent = 100

    // 如果当前文件的状态为等待中
    const nextItem = fileList.value.find((item) => {
      return item.status == STATUS.wating.value
    })
    if (nextItem) {
      uploadVideo4Draft(nextItem.uid)
    }
  }
}

// 添加视频
const addFile = (file: Object) => {
  uploadFile(file)
}

// 更换视频
const changeVideoFile = (file: Object, uploadId: string) => {
  fileList.value = []
  isUploadVideo.value = true
  nextTick(() => {
    debugger
    uploadFile(file, uploadId.uploadId)
  })
}

// 定义视频唯一标识符
const generateFileIdentifier = (file) => {
  return `${file.name}-${file.size}-${file.lastModified}`
}

// 重命名
const endEdit = async (index): Promise<void> => {
  const currentFile = fileList.value[index]
  console.log(currentFile)
  const res = await proxy.$Request({
    url: proxy.$Api.updateVideoName,
    params: {
      fileName: currentFile.fileName,
      uploadId: currentFile.uploadId,
    },
  })
  currentFile.edit = false
}

const editFileName = (index) => {
  const currentFile = fileList.value[index]
  if (currentFile.status == STATUS.uploading.value) {
    return
  }
  currentFile.edit = true

  nextTick(() => {
    const input = document.getElementById('file-input' + currentFile.uid)
    input.focus()
  })
}

// 暂停上传
const pauseUpload = (uid: number) => {
  const currentFile = getFileByuid(uid)
  currentFile.pause = true
}

// 开始上传
const restartUpload = (uid: number) => {
  const currentFile = getFileByuid(uid)
  currentFile.pause = false
  uploadVideo4Draft(uid, currentFile.chunkIndex)
}

// 删除文件
const delFile = async (index): Promise<void> => {
  const currentFile = fileList.value[index]
  currentFile.del = true
  fileList.value.splice(index, 1)
  // if (currentFile.fileId) {
  //   return
  // }
  await proxy.$Request({
    url: proxy.$Api.delUploadVideo,
    params: {
      uploadId: currentFile.uploadId,
    },
    showError: false,
  })
}

const showUpdateEdit = (data) => {
  isVideoInput.value = false
  isUploadVideo.value = false
  nextTick(() => {
    videoInputRef.value.showEdit(data)
  })
}

defineExpose({
  uploadFile,
  showUpdateEdit,
})
</script>

<style lang="scss" scoped>
.videoEditContantor {
  .retBtn {
    margin-bottom: 20px;
    position: relative;
    top: 20px;
    left: 45px;
  }
  .file-list {
    width: 100%;
    //background-color: #f6f7f8;
    border-radius: 5px;
    .file-item {
      margin: 20px 40px 20px 40px;
      .file-box-card {
        .video-p {
          flex-shrink: 0;
          position: relative;
          width: 44px;
          height: 40px;
          cursor: pointer;
          margin-top: 12px;
          .icon-video {
            font-size: 40px;
            color: #a6def1;
            padding: 0px;
          }
          .video-p-info {
            width: 35px;
            line-height: 40px;
            text-align: center;
            color: #fff;
            top: 0px;
            left: 0px;
            z-index: 1;
            position: absolute;
          }
        }

        .video-info {
          flex: 1;
          min-width: 0;
          padding-left: 10px;
          width: 400px;
          .video-title {
            display: flex;
            align-items: center;
            width: 100%;
            position: relative;

            .upload-info {
              width: 100%;
              min-width: 0;
              flex: 1;

              .title {
                width: 100%;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                .title-show {
                  line-height: 35px;
                  padding-left: 7px;
                  font-size: 12px;
                }
                .title-input {
                  width: 1400px;
                  margin: 10px 0px;
                }
              }
              .upload-status {
                margin-top: 2px;
                margin-bottom: 6px;
                color: #999;
                font-size: 12px;
                .iconfont {
                  font-size: 12px;
                  &::before {
                    font-size: 16px;
                    margin-left: 2px;
                  }
                }
              }
            }
            .op {
              margin-left: 10px;
              display: flex;
              align-items: center;
              color: #909090;
              position: absolute;
              right: 0;
              top: 0;
              .item {
                margin-right: 10px;
                font-size: 13px;
                margin-top: 10px;
              }
              .percent {
                width: 30px;
              }
              .iconfont {
                cursor: pointer;
                font-size: 20px;
                color: #909090;
              }
            }
          }
          .video-progress {
            margin-top: 5px;
          }
        }
      }
    }
  }
  .add-video-btn {
    margin-left: 40px;
    .addVideo {
      margin-top: 10px;
      color: #666;
      background-color: #f5f7f9;
    }
  }
}

:deep(.el-card__body) {
  width: 100%;
  display: flex;
  padding-top: 10px;
}
</style>
