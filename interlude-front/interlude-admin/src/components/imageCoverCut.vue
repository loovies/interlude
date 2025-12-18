<template>
  <Dialog
    :show="dialogConfig.show"
    :title="dialogConfig.title"
    :buttons="dialogConfig.buttons"
    width="1000px"
    :showCancel="false"
    @closeDialog="handleCloseDialog"
  >
    <div class="cut-image-panel">
      <VueCropper
        ref="cropperRef"
        class="cropper"
        :img="sourceImage"
        outputType="png"
        :autoCrop="true"
        :autoCropWidth="cutWidth"
        :autoCropHeight="Math.round(cutWidth * scale)"
        :fixed="true"
        :fixedNumber="[1, scale]"
        :centerBox="true"
        :full="false"
        @realTime="prview"
        mode="100%"
      ></VueCropper>
      <div class="preview-panel">
        <div class="preview-image">
          <img :src="previewsImage" alt="" />
        </div>
        <el-upload
          :multiple="false"
          :show-file-list="false"
          :http-request="selectFile"
          :accept="proxy.imageAccept"
        >
          <el-button type="primary" class="select-btn"> 选择图片 </el-button>
        </el-upload>
      </div>
    </div>
    <div class="info">建议上传至少 {{ cutWidth }} * {{ Math.round(cutWidth * scale) }}的图片</div>
  </Dialog>
</template>

<script setup>
import 'vue-cropper/dist/index.css'
import { VueCropper } from 'vue-cropper'

import { ref, reactive, getCurrentInstance, nextTick, inject, watch } from 'vue'
const { proxy } = getCurrentInstance()
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const props = defineProps({
  cutWidth: {
    type: Number,
    default: 400,
  },
  // 高宽比例
  scale: {
    type: Number,
    default: 0.5,
  },
  // 添加 coverImage 属性，用于接收当前封面图片
  coverImage: {
    type: [String, File],
    default: null,
  },
})

// 对话框配置（放在最前面，避免访问顺序问题）
const dialogConfig = reactive({
  show: false,
  title: '上传图片',
  buttons: [
    {
      type: 'primary',
      text: '确定',
      click: (e) => {
        cutImage()
      },
    },
  ],
})

const cropperRef = ref()
const previewsImage = ref()
const sourceImage = ref()

// 监听对话框显示/隐藏状态
watch(
  () => dialogConfig.show,
  (newVal) => {
    if (newVal) {
      // 对话框打开时，重置裁剪器
      resetCropper()
      // 如果有封面图片，设置为裁剪器的图片源
      if (props.coverImage) {
        initSourceImage()
      }
    }
  }
)

const prview = (data) => {
  cropperRef.value.getCropData((data) => {
    previewsImage.value = data
  })
}

const show = () => {
  dialogConfig.show = true
}

// 初始化裁剪器图片源
const initSourceImage = async () => {
  if (!props.coverImage) return

  if (typeof props.coverImage === 'string') {
    // 如果是字符串路径，直接使用
    sourceImage.value = proxy.$Api.sourcePath + props.coverImage
  } else if (props.coverImage instanceof File) {
    // 如果是File对象，转换为base64
    const base64 = await coverFile2Base64(props.coverImage)
    sourceImage.value = base64
  }
}

// 将File转换为base64
const coverFile2Base64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = ({ target }) => {
      resolve(target.result)
    }
    reader.onerror = reject
  })
}

const selectFile = (file) => {
  file = file.file
  let img = new FileReader()
  img.readAsDataURL(file)
  img.onload = ({ target }) => {
    sourceImage.value = target.result
  }
}

// 重置裁剪器状态
const resetCropper = () => {
  // 重置图片源为空，等待初始化
  sourceImage.value = ''
  // 重置预览图
  previewsImage.value = ''

  // 如果有裁剪器实例，清空裁剪区域
  nextTick(() => {
    if (cropperRef.value) {
      cropperRef.value.clearCrop()
    }
  })
}

// 处理对话框关闭
const handleCloseDialog = () => {
  dialogConfig.show = false
}

const cutImageCallback = inject('cutImageCallback', null)
const emit = defineEmits(['change'])

const cutImage = () => {
  if (!cropperRef.value) {
    proxy.$Message.warning('裁剪器未初始化')
    return
  }

  const cropW = Math.round(cropperRef.value.cropW)
  const cropH = Math.round(cropperRef.value.cropH)
  if (cropW == 0 || cropH == 0) {
    proxy.$Message.warning('请选择图片')
    return
  }
  if (cropW < props.cutWidth || cropH < Math.round(props.cutWidth * props.scale)) {
    proxy.$Message.warning(
      `图片尺寸至少满足(${props.cutWidth}*${Math.round(props.cutWidth * props.scale)})`
    )
    return
  }
  cropperRef.value.getCropBlob((blob) => {
    const file = new File([blob], 'temp.' + blob.type.substring(blob.type.indexOf('/') + 1), {
      type: blob.type,
    })
    dialogConfig.show = false
    emit('change', file)
    if (cutImageCallback) {
      cutImageCallback({
        coverImage: file,
      })
    }
  })
}

defineExpose({
  show,
})
</script>

<style lang="scss" scoped>
.cut-image-panel {
  display: flex;
  .cropper {
    flex: 1;
    height: 500px;
  }
  .preview-panel {
    width: 200px;
    margin-left: 20px;
    text-align: center;
    .preview-image {
      width: 100%;
      height: 200px;
      background: #f6f6f6;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    img {
      max-width: 100%;
      max-height: 200px;
    }
  }
  .select-btn {
    margin-top: 20px;
  }
}
.info {
  color: #6b6b6b;
  margin-top: 10px;
}
</style>
