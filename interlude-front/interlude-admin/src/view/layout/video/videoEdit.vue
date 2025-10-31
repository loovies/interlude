<template>
  <div>faseufuseafhioashfiosehfioase</div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, nextTick } from 'vue'
const { proxy } = getCurrentInstance()

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
    uploadSize: 0,
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

const uploadVideo4Draft = (uid: number, chunkIndex: number): Promise<void> => {
  // 根据 uid获取文件
  const currentFile = getFileByuid(uid)

  // 文件状态设置为上传中
  currentFile.status = STATUS.uploading.value
  // 设置分片索引
  chunkIndex = chunkIndex ? chunkIndex : 0
  const file = currentFile.file
  console.log(file)
}

defineExpose({
  uploadFile,
})
</script>

<style lang="scss" scoped></style>
