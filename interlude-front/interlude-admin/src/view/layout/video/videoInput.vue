<template>
  <div class="menu-container">
    <ul class="horizontal-menu">
      <li
        v-for="(item, index) in fileList"
        :key="index"
        class="menu-item"
        :class="{ active: activeIndex === index, disable: item.status === 'uploading' }"
        @click="setActive(index)"
      >
        <i class="iconfont icon-video1"></i>
        <span class="fileName"> {{ item.fileName }}</span>
      </li>
    </ul>
  </div>

  <div class="content-area">
    <div class="video-form">
      <el-form
        :model="formData"
        :rules="rules"
        ref="formDataRef"
        label-width="80px"
        @submit.prevent
      >
        <el-form-item label="封面:" prop="videoCoverFile">
          <ImageCoverSelect
            :coverWidth="200"
            :cutWidth="680"
            :scale="0.6"
            :coverImage="formData.videoCoverFile"
            @change="handleChange"
          ></ImageCoverSelect>
        </el-form-item>
        <el-form-item label="标题:" prop="videoName" class="input">
          <el-input
            v-model="formData.videoName"
            clearable
            placeholder="请输入标题"
            maxlength="100"
            show-word-limit
            @change="handleChange"
          ></el-input>
        </el-form-item>
        <el-form-item label="类型:" prop="postType" class="input">
          <el-radio-group v-model="formData.postType" @change="handleChange">
            <el-radio :value="0">自制</el-radio>
            <el-radio :value="1">转载</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label=""
          prop="originInfo"
          v-if="formData.postType == 1"
          style="margin-top: 10px"
        >
          <el-input
            v-model="formData.originInfo"
            clearable
            placeholder="转载视频请注明来源,时间,地点(例: 转自https://www.xxxx.com/yyyy). 注明来源更快的通过审核哦"
            maxlength="200"
            show-word-limit
            @change="handleChange"
          ></el-input>
        </el-form-item>
        <el-form-item label="标签:" prop="tags" class="input">
          <TagInput v-model="formData.tags" @change="handleChange"></TagInput>
        </el-form-item>
        <el-form-item label="分区:" prop="categoryArray" class="input">
          <el-cascader
            @change="handleChange"
            :options="categoryStore.categoryList"
            v-model="formData.categoryArray"
            :props="{ value: 'categoryId', label: 'categoryName' }"
          ></el-cascader>
        </el-form-item>
        <el-form-item label="简介:" prop="description" class="input">
          <!--input输入-->
          <el-input
            @change="handleChange"
            clearable
            placeholder="填写更全面的相关信息,让更多的人能找到你的视频(:"
            type="textarea"
            :rows="5"
            resize="none"
            :maxlength="2000"
            show-word-limit
            v-model="formData.description"
          ></el-input>
        </el-form-item>
        <el-form-item label="可见性:" prop="visibility" class="input">
          <el-radio-group v-model="formData.visibility" @change="handleChange">
            <el-radio :value="1">公共</el-radio>
            <el-radio :value="2">私人</el-radio>
            <el-radio :value="3">仅限好友</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="互动设置:" prop="interactionArray" class="input">
          <el-checkbox-group v-model="formData.interactionArray" @change="handleChange">
            <el-checkbox label="0">关闭弹幕</el-checkbox>
            <el-checkbox label="1">关闭评论</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="" class="inputbtn">
          <el-button type="primary" @click="submitForm" size="large">立即投稿</el-button>
          <el-button type="primary" size="large" @click="saveDraftInfo()">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import ImageCoverSelect from '../../../components/ImageCoverSelect.vue'
import TagInput from './TagInput.vue'

import { ref, watch, provide, reactive, getCurrentInstance, onMounted, nextTick } from 'vue'
const { proxy } = getCurrentInstance()
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()
import { ElMessage, ElMessageBox } from 'element-plus'

import { useCategoryStore } from '../../../stores/CategoryStore'
const categoryStore = useCategoryStore()

const props = defineProps({
  fileList: {
    type: Array,
    default: [],
  },
})

const activeIndex = ref(0)
const isSaveVideoInfo = ref(true)

const handleChange = () => {
  isSaveVideoInfo.value = false
}

const setActive = async (index: number): Promise<void> => {
  if (!isSaveVideoInfo.value) {
    ElMessageBox.confirm('有未保存的草稿,是否保存?', 'Warning', {
      confirmButtonText: '保存',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      await saveDraftInfo()
      formDataRef.value.resetFields()
      formData.value.interactionArray = []
      formData.value.visibility = []
      activeIndex.value = index
      await loadDraftInfo()
      showVideoInput(index)
    })
  } else {
    formDataRef.value.resetFields()
    formData.value.interactionArray = []
    formData.value.visibility = []
    activeIndex.value = index
    await loadDraftInfo()
    showVideoInput(index)
  }
}

const showVideoInput = (index: number) => {
  // formData.value = props.fileList[index]
  if (!props.fileList[index]) return
  if (props.fileList[index].videoCover) {
    formData.value.videoCoverFile = props.fileList[index].videoCover
  }

  formData.value.videoName = props.fileList[index].videoName
  formData.value.postType = props.fileList[index].postType
  if (props.fileList[index].tags) {
    formData.value.tags = props.fileList[index].tags.split(',')
  }

  formData.value.categoryArray = []
  if (props.fileList[index].pCategoryId) {
    formData.value.categoryArray = [
      props.fileList[index].pCategoryId,
      props.fileList[index].categoryId,
    ]
  } else {
    formData.value.categoryArray = [props.fileList[index].categoryId]
  }
  formData.value.description = props.fileList[index].description
  if (props.fileList[index].tags) {
    formData.value.tags = props.fileList[index].tags.split(',')
  }
  if (props.fileList[index].interactionSettings) {
    formData.value.interactionArray = props.fileList[index].interactionSettings.split(',')
  }
  formData.value.visibility = props.fileList[index].visibility
}

const formData = ref({})
const formDataRef = ref()

const rules = {
  videoCoverFile: [{ required: true, message: '封面不能为空' }],
  videoName: [{ required: true, message: '标题不能为空' }],
  postType: [{ required: true, message: '类型不能为空' }],
  originInfo: [{ required: true, message: '转载说明不能为空' }],
  categoryArray: [{ required: true, message: '分区不能为空' }],
  tags: [{ required: true, message: '标签不能为空' }],
  visibility: [{ required: true, message: '可见性不能为空' }],
}

const emit = defineEmits(['update:fileList'])
const loadDraftInfo = async (): Promise<void> => {
  let res = await proxy.$Request({
    url: proxy.$Api.getDraftInfoByUserId,
  })
  if (!res) return
  emit('update:fileList', res.data ? res.data : [])
  await nextTick()
}

const submitForm = () => {
  formDataRef.value.validate(async (vaild) => {
    if (!vaild) {
      return
    }
    if (!isSaveVideoInfo.value) {
      saveDraftInfo(true)
    }
    const currentIndex = activeIndex.value
    const uploadId = props.fileList[activeIndex.value].uploadId
    let res = await proxy.$Request({
      url: proxy.$Api.postVideo,
      params: {
        uploadId,
      },
    })
    if (!res) {
      return
    }
    proxy.$Message.success('投稿成功')

    // 立即删除当前项
    const newFileList = [...props.fileList]
    newFileList.splice(currentIndex, 1)
    emit('update:fileList', newFileList)

    // 处理 activeIndex
    if (newFileList.length === 0) {
      activeIndex.value = 0
      // 清空表单
      formDataRef.value.resetFields()
      formData.value = {}
    } else if (currentIndex >= newFileList.length) {
      activeIndex.value = newFileList.length - 1
      await nextTick()
      formData.value.videoCoverFile = ''
      formData.value.tags = ''
      showVideoInput(activeIndex.value)
    } else {
      // 如果删除的不是当前显示项，则不需要改变activeIndex，但需要更新表单显示
      await nextTick()
      formData.value.videoCoverFile = ''
      formData.value.tags = ''
      showVideoInput(activeIndex.value)
    }
  })
}

const saveDraftInfo = async (showMsg: boolean = false): Promise<void> => {
  let params = {}
  Object.assign(params, formData.value)
  console.log(params)
  // 判断是否有文件
  if (props.fileList[activeIndex.value]) {
    params.uploadId = props.fileList[activeIndex.value].uploadId
  }
  if (params.categoryArray.length > 1) {
    params.pCategoryId = params.categoryArray[0]
    params.categoryId = params.categoryArray[1]
  } else {
    params.categoryId = params.categoryArray[0]
  }
  if (params.interactionArray) {
    params.interactionSettings = params.interactionArray.join(',')
  }
  if (params.tags) {
    params.tags = params.tags.join(',')
  }
  params.createThumbnail = true
  let res = await proxy.$Request({
    url: proxy.$Api.updateDraftInfo,
    params,
  })

  if (!res) {
    return
  }
  if (!showMsg) {
    proxy.$Message.success('保存成功')
  }
  isSaveVideoInfo.value = true
}

provide('cutImageCallback', ({ coverImage }) => {
  formData.value.videoCoverFile = coverImage
})

onMounted(() => {
  showVideoInput(activeIndex.value)
})
</script>

<style lang="scss" scoped>
.app-container {
  max-width: 1000px;
  margin: 0 auto;
}

header {
  text-align: center;
  margin-bottom: 40px;
}

h1 {
  font-size: 2.2rem;
  margin-bottom: 10px;
  color: #2c3e50;
}

.subtitle {
  font-size: 1rem;
  color: #7f8c8d;
}

.menu-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 0;
  margin-bottom: 30px;
  overflow: hidden;
  margin: 60px 40px 20px 40px;
}

.horizontal-menu {
  display: flex;
  list-style: none;
  padding-right: 40px;
}

.menu-item {
  flex: 1;
  text-align: center;
  padding: 15px 15px;
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 3px solid transparent;
  position: relative;
  font-weight: 500;
}

.menu-item:hover {
  background: #f8f9fa;
  color: #3498db;
}

.menu-item.active {
  color: #3498db;
  border-bottom-color: #3498db;
  background: #f8f9fa;
}
.iconfont {
  font-size: 25px;
  margin-right: 10px;
  position: relative;
  top: 5px;
}

.content-area {
  margin: 20px 40px 20px 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  min-height: 400px;
  display: flex;

  .video-form {
    width: 100%;
    .input {
      margin: 30px 50px 0px 0px;
    }
    .inputbtn {
      margin: 30px 50px 0px 40%;
    }
  }
}
</style>
