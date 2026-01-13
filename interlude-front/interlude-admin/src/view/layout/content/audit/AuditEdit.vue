<template>
  <Dialog
    :title="dialogConfig.title"
    :show="dialogConfig.show"
    :buttons="dialogConfig.buttons"
    width="600px"
    :showCancel="false"
    @closeDialog="dialogConfig.show = false"
  >
    <el-form :model="formData" ref="formDataRef" :rules="rules" label-width="100px" @submit.prevent>
      <el-form-item label="封面:" prop="videoCover">
        <Cover :source="formData.videoCover" :preview="true" width="200"></Cover>
        <span class="leftMargin30" @click="showplayer(formData.videoId)">预览</span>
      </el-form-item>
      <el-form-item label="视频名字:" prop="videoName">
        {{ formData.videoName }}
      </el-form-item>
      <el-form-item label="用户名:" prop="videoName">
        <span>{{ formData.nickName }}</span>
      </el-form-item>
      <el-form-item label="视频分类:" prop="categoryName">
        {{
          formData.pCategoryName == null
            ? formData.categoryName
            : formData.pCategoryName + '/' + formData.categoryName
        }}
      </el-form-item>
      <el-form-item label="标签:" prop="tags">
        <div class="tagString" v-if="tagsArray">
          <span class="tag-box" v-for="tag in tagsArray"
            ><el-tooltip :content="tag" placement="top">{{ tag }}</el-tooltip></span
          >
        </div>
      </el-form-item>
      <el-form-item label="描述:" prop="tags">
        {{ formData.description }}
      </el-form-item>
      <el-form-item label="审核:" prop="auditStatus">
        <el-radio-group v-model="formData.auditStatus">
          <el-radio :label="2">审核通过</el-radio>
          <el-radio :label="3">审核不通过</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核意见:" prop="auditComment" v-if="formData.auditStatus == 3">
        <el-input
          clearable
          placeholder="请输入审核意见"
          v-model.trim="formData.auditComment"
        ></el-input>
      </el-form-item>
      <el-form-item label="拒绝原因:" prop="rejectReason" v-if="formData.auditStatus == 3">
        <el-select style="width: 140px" v-model="formData.rejectReason">
          <el-option :value="1" label="版权"></el-option>
          <el-option :value="2" label="内容"></el-option>
          <el-option :value="3" label="质量"></el-option>
          <el-option :value="4" label="其他"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
  </Dialog>
</template>

<script setup lang="ts">
import Cover from '@/components/Cover.vue'
import { ref, reactive, getCurrentInstance, nextTick } from 'vue'
const { proxy } = getCurrentInstance()

import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const dialogConfig = ref({
  show: false,
  title: '新增分类',
  buttons: [
    {
      type: 'primary',
      click: (e) => {
        submitFrom()
      },
      text: '提交',
    },
  ],
})

const formData = ref({})
const formDataRef = ref()

const rules = ref<Record<string, any>>({
  auditStatus: [{ required: true, message: '请选择审核是否通过' }],
  rejectReason: [{ required: true, message: '请选择拒绝原因' }],
})

const tagsArray = ref([])
const showEdit = (data) => {
  dialogConfig.value.show = true
  nextTick(() => {
    formDataRef.value.resetFields() // 重置表单
    dialogConfig.value.title = '审核视频'
    formData.value = data
    console.log(data)
    tagsArray.value = formData.value.tags.split(',')
  })
}

const emit = defineEmits(['reload', 'show-videoPlayer'])

const showplayer = (videoId) => {
  emit('show-videoPlayer', videoId)
}

const submitFrom = () => {
  formDataRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    let params = {}
    Object.assign(params, formData.value)
    console.log(params)
    debugger
    let result = await proxy.$Request({
      url: proxy.$Api.updateVideoAudit,
      params,
    })
    if (!result) {
      return
    }
    dialogConfig.value.show = false
    proxy.$Message.success('提交成功')
    emit('reload')
  })
}
defineExpose({
  showEdit,
})
</script>

<style lang="scss" scoped>
.inputClass {
  height: 40px;
}
.leftMargin30 {
  margin-left: 20px;
  cursor: pointer;
  &:hover {
    color: var(--blue);
  }
}
.tagString {
  display: flex;
  .tag-box {
    width: 80px;
    height: 30px;
    background-color: var(--blue);
    padding: 5px;
    color: white;
    margin-right: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 5px;
  }
}
</style>
