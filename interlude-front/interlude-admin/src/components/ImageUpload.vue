<template>
  <div class="cover-upload">
    <div class="cover" :style="{ width: props.width + 'px', height: props.height + 'px' }">
      <Cover :source="modelValue"></Cover>
      <div class="iconfont icon-close" @click="cleanImg" v-if="modelValue"></div>
    </div>
    <el-upload
      :multiple="false"
      :show-file-list="false"
      :http-request="selectFile"
      :accept="proxy.imageAccept"
    >
      <el-button type="primary" class="select-btn" v-if="isBtn">选择</el-button>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick } from 'vue'
const { proxy } = getCurrentInstance()

import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const props = defineProps({
  modelValue: {
    type: [String, File],
  },
  width: {
    type: Number,
    default: 100,
  },
  height: {
    type: Number,
    default: 100,
  },
  isBtn: {
    type: Boolean,
    default: true,
  },
})

const emit = defineEmits(['update:modelValue'])
const selectFile = (file) => {
  emit('update:modelValue', file.file)
}
const cleanImg = () => {
  emit('update:modelValue', '')
}
</script>

<style lang="scss" scoped>
.cover-upload {
  display: flex;
  align-items: flex-end;
  .cover {
    position: relative;
    margin-right: 10px;
    .icon-close {
      position: absolute;
      top: -8px;
      right: 0px;
      cursor: pointer;
    }
  }
}
</style>
