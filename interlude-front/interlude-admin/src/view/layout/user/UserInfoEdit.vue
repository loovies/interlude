<template>
  <Dialog
    :title="dialogConfig.title"
    :show="dialogConfig.show"
    :buttons="dialogConfig.buttons"
    width="1000px"
    @closeDialog="dialogConfig.show = false"
  >
    <el-form
      :model="formData"
      class="form-style"
      :rules="rules"
      ref="formDataEditRef"
      label-width="90px"
      @submit.prevent
    >
      <div class="title">
        <h3>基本信息</h3>
      </div>
      <div class="base-info">
        <div class="left-base-info">
          <!--input输入-->
          <el-form-item label="昵称:" prop="nickName">
            <el-input
              clearable
              placeholder="请输入昵称"
              v-model.trim="formData.nickName"
            ></el-input>
          </el-form-item>
          <!-- 下拉框 -->
          <el-form-item label="性别:" prop="sex" class="input">
            <el-select clearable placeholder="请选择性别" v-model="formData.sex">
              <el-option :value="0" label="女"></el-option>
              <el-option :value="1" label="男"></el-option>
              <el-option :value="2" label="其他"></el-option>
            </el-select>
          </el-form-item>
          <!-- 单选 -->

          <el-form-item label="手机号:" prop="phone">
            <el-input clearable placeholder="请输入手机号" v-model.trim="formData.phone"></el-input>
          </el-form-item>
          <el-form-item label="邮箱:" prop="email">
            <el-input clearable placeholder="请输入邮箱" v-model.trim="formData.email"></el-input>
          </el-form-item>
          <el-form-item label="登录密码:" prop="password">
            <el-input
              clearable
              type="password"
              show-password
              placeholder="请输入登录密码"
              v-model.trim="formData.password"
            ></el-input>
          </el-form-item>
          <el-form-item label="角色:" prop="roleNameIndex" v-if="showEnabled">
            <el-radio-group v-model="formData.roleNameIndex">
              <!-- <el-radio :label="3">超级管理员</el-radio> -->
              <el-radio :label="2">管理员</el-radio>
              <el-radio :label="1">用户</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <div class="right-base-info">
          <el-form-item label="头像:" prop="avatar">
            <ImageUpload v-model="formData.avatar" :width="150" :height="150"></ImageUpload>
          </el-form-item>

          <el-form-item v-if="showEnabled" label="用户状态:" prop="enable" class="status-switch">
            <el-switch v-model="formData.enable" />
          </el-form-item>
        </div>
      </div>
      <div class="title">
        <h3>其他信息</h3>
      </div>
      <div class="other-info">
        <div class="other-onerow">
          <el-form-item class="input" label="学校:" prop="school">
            <el-input clearable placeholder="请输入学校" v-model.trim="formData.school"></el-input>
          </el-form-item>
          <el-form-item label="生日:" prop="birthday">
            <el-date-picker v-model="formData.birthday" type="date" placeholder="请选择生日" />
          </el-form-item>
        </div>

        <!--input输入-->

        <el-form-item class="input" label="地址:" prop="address">
          <el-input clearable placeholder="请输入地址" v-model.trim="formData.address"></el-input>
        </el-form-item>
        <!--textarea输入-->
        <el-form-item label="简介:" prop="personIntroduction">
          <el-input
            clearable
            placeholder="请输入个人简介"
            type="textarea"
            :rows="5"
            :maxlength="150"
            resize="none"
            show-word-limit
            v-model.trim="formData.personIntroduction"
          ></el-input>
        </el-form-item>
      </div>
    </el-form>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance, nextTick } from 'vue'
import { uploadImage, getRoleByUserId } from '@/utils/Api.js'
const { proxy } = getCurrentInstance()

const formDataEditRef = ref()
const formData = ref<Record<string, any>>({
  enable: true,
})

const rules = ref<Record<string, any>>({
  nicName: [{ required: true, message: '请输入昵称' }],
  sex: [{ required: true, message: '请选择性别' }],
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: proxy.$Verify.phone, message: '请输入正确的手机号' },
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址' },
    { pattern: proxy.$Verify.email, message: '请输入正确的邮箱地址' },
  ],
  password: [
    { required: true, message: '请输入密码' },
    { pattern: proxy.$Verify.password, message: '请输入正确的密码' },
  ],
  roleNameIndex: [{ required: true, message: '请选择角色' }],
})

const dialogConfig = ref({
  show: false,
  title: '新增用户',
  buttons: [
    {
      type: 'primary',
      text: '确定',
      click: (e: any) => {
        submitForm()
      },
    },
  ],
})

const emit = defineEmits(['reload'])
// 提交表单
const submitForm = (): void => {
  formDataEditRef.value.validate(async (valid: boolean) => {
    if (!valid) {
      return
    }
    let params: Record<string, any> = {}
    Object.assign(params, formData.value)
    params.enabled = formData.value.enable ? 1 : 0
    params.birthday = proxy.$Utils.formatDateToYYYYMMDD(formData.value.birthday)
    if (params.avatar instanceof File) {
      params.avatar = await uploadImage(params.avatar)
    }
    const res = await proxy.$Request({
      url: proxy.$Api.getaddOrUpdateBatch,
      params,
    })
    if (!res) {
      return
    }
    dialogConfig.value.show = false
    proxy.$Message.success('保存成功')
    emit('reload')
  })
}

const showEnabled = ref(true)

// 显示编辑弹窗
const showEdit = (data: Array<Record<string, any>>, type: number) => {
  if (type === 1) {
    dialogConfig.value.title = '修改用户'
    Object.assign(formData.value, data)
    showEnabled.value = false
  } else {
    dialogConfig.value.title = '新增用户'
    // 确保在下一个渲染周期执行
    showEnabled.value = true
    formData.value.enable = true
    nextTick(() => {
      formDataEditRef.value?.resetFields()
      formData.value = {}
    })
  }
  dialogConfig.value.show = true
}
defineExpose({
  showEdit,
})
</script>

<style lang="scss" scoped>
.form-style {
  .title {
    margin-bottom: 20px;
    width: 100%;
    border-bottom: 1px solid #e8e8e8;
    h3 {
      font-weight: 600;
      color: var(--blue2);
      padding-bottom: 10px;
      margin-bottom: 0px;
      font-style: italic;
      font-family: '方正姚体';
    }
  }
  .base-info {
    display: flex;
    width: 100%;
    .left-base-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      width: 100%;
      .input {
        width: 300px;
      }
    }
    .right-base-info {
      flex: 1;
      margin-top: 30px;
      display: flex;
      flex-direction: column;
      align-items: center;
      .status-switch {
        margin-top: 20px;
      }
    }
  }
  .other-info {
    display: flex;
    flex-direction: column;
    width: 100%;
    .other-onerow {
      display: flex;
      width: 100%;
      .input {
        width: 500px;
        margin-right: 40px;
      }
    }
    .input {
      width: 600px;
    }
  }
}
</style>
