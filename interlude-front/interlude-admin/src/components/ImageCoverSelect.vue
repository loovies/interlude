<template>
  <div
    class="cover"
    :style="{
      width: coverWidth + 'px',
      height: coverWidth * scale + 'px',
    }"
  >
    <el-image :src="coverFile" fit="scale-down" :width="cutWidth" v-if="coverFile">
      <template #error>
        <div class="iconfont icon-image-error"></div>
      </template>
    </el-image>
    <div class="mask" @click="selectImage">
      {{ coverImage ? '重新上传' : '上传' }}
    </div>
  </div>
  <imageCoverCut ref="imageCoverCurRef"></imageCoverCut>
</template>

<script setup>
import imageCoverCut from '@/components/imageCoverCut.vue'

import { ref, reactive, getCurrentInstance, nextTick } from 'vue'
import { asyncComputed } from '@vueuse/core'
const { proxy } = getCurrentInstance()
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const props = defineProps({
  coverImage: {
    type: [String, File],
  },
  coverWidth: {
    type: Number,
    default: 100,
  },
  cutWidth: {
    type: Number,
    default: 150,
  },
  scale: {
    type: Number,
    default: 1,
  },
})

const imageCoverCurRef = ref()

const coverFile = asyncComputed(async () => {
  if (!props.coverImage) {
    return null
  }
  if (typeof props.coverImage == 'string') {
    return proxy.Api.sourcePath + props.coverImage
  } else if (props.coverImage instanceof File) {
    const base64 = await coverFile2Base64(props.coverImage)
    return base64
  }
})

const coverFile2Base64 = (file) => {
  return new Promise((resolve, reject) => {
    let img = new FileReader()
    img.readAsDataURL(file)
    img.onload = ({ target }) => {
      resolve(target.result)
    }
  })
}

const selectImage = () => {
  imageCoverCurRef.value.show()
}
</script>

<style lang="scss" scoped>
.cover {
  background: #f0f0f0;
  position: relative;
  .mask {
    width: 100%;
    position: absolute;
    left: 0px;
    bottom: 0px;
    height: 30px;
    background: rgba(0, 0, 0, 0.7);
    opacity: 0.8;
    z-index: 1;
    color: #fff;
    text-align: center;
    cursor: pointer;
    align-content: center;
    align-items: center;
  }
}
</style>
