<template>
  <div
    class="image-panel"
    ref="coverRef"
    :style="{
      'border-radius': borderRadius,
      width: width ? width + 'px' : '100%',
      height: width ? width * scale + 'px' : '100%',
      marginLeft: marginLeft ? marginLeft + 'px' : 0 + 'px',
    }"
  >
    <el-image
      :lazy="lazy"
      :fit="fit"
      :src="fileSrouce || fileImage"
      v-if="fileSrouce || fileImage"
      @click="showViewerHander"
    >
      <template #placeholder>
        <div class="loading" :style="{ height: loadingHeight + 'px' }">
          <img :src="proxy.$Utils.getLocalImage('playing.gif')" alt="" />
        </div>
      </template>
      <template #error>
        <img
          :src="proxy.$Utils.getLocalImage(img404)"
          class="el_image_inner"
          :style="{ 'object-fit': fit }"
        />
      </template>
    </el-image>
    <div v-else class="no-image">请选择图片</div>
    <el-image-viewer
      v-if="showViewer"
      :hide-on-click-modal="true"
      :url-list="imageList"
      :teleported="true"
      @close="
        () => {
          showViewer = false
        }
      "
    >
    </el-image-viewer>
  </div>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance, computed, onMounted, Ref } from 'vue'
const { proxy } = getCurrentInstance()
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const props = defineProps({
  width: {
    type: Number,
  },
  scale: {
    type: Number,
    default: 0.6,
  },
  borderRadius: {
    type: String,
    default: '5px',
  },
  lazy: {
    //顶部头像使用lazy页面 不跳转路由不加载图片
    type: Boolean,
    default: true,
  },
  fit: { type: String, default: 'scale-down' },
  source: {
    type: [String, File],
  },
  defaultImg: {
    type: String,
  },
  img404: {
    type: String,
    default: 'user.png',
  },
  preview: {
    type: Boolean,
    default: false,
  },
  marginLeft: {
    type: Number,
  },
})

const fileImage: any = ref()
const fileSrouce = computed(() => {
  if (!props.source && !props.defaultImg) {
    fileImage.value = null
    return null
  }
  if (!props.source && props.defaultImg) {
    return proxy.$Utils.getLocalImage(props.defaultImg)
  }
  /**
    1.创建一个FileReader实例。
    2.调用readAsDataURL方法，传入一个文件（Blob或File对象），开始读取文件。
    3.设置onload事件处理函数，当文件读取完成时，会触发onload事件。
    4.在onload事件处理函数中，通过target.result获取到读取的结果（即Data URL字符串），然后将其赋值给fileImage.value。
   */
  if (props.source instanceof File) {
    let img = new FileReader()
    img.readAsDataURL(props.source)
    img.onload = ({ target }) => {
      fileImage.value = target.result
    }
  } else if (typeof props.source === 'string') {
    let imagePath = `${proxy.$Api.sourcePath}${props.source}`
    return imagePath
  } else {
    return
  }
})

const showViewer: Ref<boolean> = ref(false)
const showViewerHander = () => {
  if (!props.preview) {
    return
  }
  showViewer.value = true
}

const imageList = computed(() => {
  if (!props.preview) {
    return []
  }
  const sourceImage = proxy.$Api.sourcePath + props.source.replace(proxy.imageThumbnailSuffix, '')
  return [sourceImage]
})

const coverRef = ref()
const loadingHeight = ref()

onMounted(() => {
  loadingHeight.value = coverRef.value.clientWidth * props.scale
})
</script>

<style lang="scss" scoped>
.image-panel {
  position: relative;
  overflow: hidden;
  cursor: pointer;
  background-color: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  :deep(.el-image) {
    width: 100%;
    height: 100%;
  }
  :deep(.is-loading) {
    display: none;
  }
  :deep(.el-image__wrapper) {
    position: relative;
    vertical-align: top;
    width: 100%;
    height: 100%;
    display: flex;
  }
  .icon-image-error {
    margin: 0px auto;
    font-size: 20px;
    color: #838383;
    height: 100%;
  }
  .loading {
    width: 100%;
    display: flex;
    align-content: center;
    justify-content: center;
    img {
      width: 20px;
    }
  }
  .no-image {
    text-align: center;
    color: #9f9f9f;
  }
}
</style>
