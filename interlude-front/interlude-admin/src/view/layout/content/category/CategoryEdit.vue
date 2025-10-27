<template>
  <Dialog
    :title="dialogConfig.title"
    :show="dialogConfig.show"
    :buttons="dialogConfig.buttons"
    width="600px"
    :showCancel="false"
    @closeDialog="dialogConfig.show = false"
  >
    <el-form :model="formData" :rules="rules" ref="formDataRef" label-width="80px" @submit.prevent>
      <el-form-item label="分类编号" prop="categoryCode">
        <el-input
          size="large"
          v-model.trim="formData.categoryCode"
          maxlength="100"
          :show-word-limit="true"
          class="inputClass"
        >
        </el-input>
      </el-form-item>
      <el-form-item label="分类名称" prop="categoryName">
        <el-input
          size="large"
          v-model.trim="formData.categoryName"
          maxlength="100"
          :show-word-limit="true"
          class="inputClass"
        >
        </el-input>
      </el-form-item>
    </el-form>
  </Dialog>
</template>
<script setup>
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
      text: '确定',
      click: (e) => {
        submitFrom()
      },
      text: '确定',
    },
  ],
})

const formData = ref({})
const formDataRef = ref()

const rules = {
  categoryCode: [{ required: true, message: '请输入分类编号' }],
  categoryName: [{ required: true, message: '请输入分类名称' }],
}

const showEdit = (data) => {
  dialogConfig.value.show = true
  nextTick(() => {
    formDataRef.value.resetFields() // 重置表单
    if (data.categoryId == null) {
      dialogConfig.value.title = '新增分类'
    } else {
      dialogConfig.value.title = '修改分类'
    }
    formData.value = data
  })
}

const emit = defineEmits(['reload'])
const submitFrom = () => {
  formDataRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    let params = {}
    Object.assign(params, formData.value)
    let result = await proxy.$Request({
      url: proxy.$Api.getSaveCategory,
      params,
    })
    if (!result) {
      return
    }
    dialogConfig.value.show = false
    proxy.$Message.success('保存成功')
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
</style>
