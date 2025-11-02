<template>
  <div class="videoEditContantor">
    <el-button type="primary">
      <i class="iconfont icon-fanhui"></i>
      返回视频管理</el-button
    >
    <div class="file-list" v-draggable="[fileList, { animation: 150, handle: '.video-p' }]">
      <div class="file-item" v-for="(item, index) in fileList" :key="index"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
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
    icon: 'wating',
  },
  uploading: {
    value: 'uploading',
    desc: '上传中',
    color: '#409eff',
    icon: 'upload',
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

// 文件列表
let fileList: Ref<Array<any>> = ref([])

// 分片大小
const CHUNK_SIZE = proxy.chunkSize

// 同时最大上传数量
const MAX_UPLOADING = proxy.maxUploading

const emit = defineEmits(['closeVideoEdit'])

const uploadFile = (file: Object) => {
  file = file.file

  // 文件名
  let fileName = proxy.$Utils.getFileName(file.name)
  // 文件的基本信息
  const fileItem: Object = {
    file: file,
    uid: file.uid,
    fileName: fileName,
    status: STATUS.wating.value,
    uploadSize: 0, // 上传大小
    totalSize: file.size,
    uploadPercent: 0, // 上传百分比
    pause: false,
    chunkIndex: 0, // 分片索引
    errorMsg: null,
  }
  fileList.value.push(fileItem)
  // 判断文件大小是否为0
  if (fileItem.totalSize == 0) {
    proxy.$Message.error(STATUS.emptyfile.desc)
    emit('closeVideoEdit')
    return
  }
  //判断文件大小是否超过设定最大值
  if (fileItem.totalSize > sysSettingStore.sysSetting.videoSize * 1024 * 1024) {
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
  uploadVideo4Draft(fileItem.uid)
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
  const fileSize = currentFile.totalSize
  // 用 文件大小 除 每个分片的规定大小 向上取整得总分片得数量
  const chunks = Math.ceil(fileSize / CHUNK_SIZE) // 当前视频的分片数量

  // 如果文件没上传id 就上传视频, 并保存草稿表
  if (!currentFile.uploadId) {
    let result = await proxy.$Request({
      url: proxy.$Api.preUploadVideo,
      params: {
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
    currentFile.uploadId = result.data // 上传成功获得 上传id
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

defineExpose({
  uploadFile,
})
</script>

<style lang="scss" scoped></style>
