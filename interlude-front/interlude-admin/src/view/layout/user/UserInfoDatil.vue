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
      ref="formDataEditRef"
      label-width="100px"
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
              disabled
              placeholder="请输入昵称"
              v-model.trim="formData.nickName"
            ></el-input>
          </el-form-item>
          <!-- 下拉框 -->
          <el-form-item label="性别:" prop="sex" class="input">
            <el-select disabled clearable placeholder="请选择性别" v-model="formData.sex">
              <el-option :value="0" label="女"></el-option>
              <el-option :value="1" label="男"></el-option>
              <el-option :value="2" label="其他"></el-option>
            </el-select>
          </el-form-item>
          <!-- 单选 -->

          <el-form-item label="手机号:" prop="phone">
            <el-input disabled clearable placeholder="无" v-model.trim="formData.phone"></el-input>
          </el-form-item>
          <el-form-item label="邮箱:" prop="email">
            <el-input clearable disabled placeholder="无" v-model.trim="formData.email"></el-input>
          </el-form-item>
          <el-form-item label="登录密码:" prop="password">
            <el-input
              clearable
              disabled
              type="password"
              show-password
              placeholder="无"
              v-model.trim="formData.password"
            ></el-input>
          </el-form-item>
          <el-form-item label="角色:" prop="roleNameIndex" v-if="showEnabled">
            <el-radio-group disabled v-model="formData.roleNameIndex">
              <!-- <el-radio :label="3">超级管理员</el-radio> -->
              <el-radio :label="2">管理员</el-radio>
              <el-radio :label="1">用户</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <div class="right-base-info">
          <el-form-item label="头像:" prop="avatar">
            <ImageUpload
              class="disable"
              v-model="formData.avatar"
              :width="150"
              :height="150"
              :isBtn="false"
            ></ImageUpload>
          </el-form-item>

          <el-form-item v-if="showEnabled" label="用户状态:" prop="enable" class="status-switch">
            <el-switch disabled v-model="formData.enable" />
          </el-form-item>
        </div>
      </div>
      <div class="title">
        <h3>其他信息</h3>
      </div>
      <div class="other-info">
        <div class="other-onerow">
          <el-form-item class="input" label="学校:" prop="school">
            <el-input disabled clearable placeholder="无" v-model.trim="formData.school"></el-input>
          </el-form-item>
          <el-form-item label="生日:" prop="birthday">
            <el-date-picker disabled v-model="formData.birthday" type="date" placeholder="无" />
          </el-form-item>
        </div>

        <!--input输入-->
        <div class="other-tworow">
          <el-form-item class="input" label="地址:" prop="address">
            <el-input
              clearable
              disabled
              placeholder="无"
              v-model.trim="formData.address"
            ></el-input>
          </el-form-item>
          <el-form-item class="input" label="创建者:" prop="createBy">
            <el-input clearable disabled v-model.trim="formData.createBy"></el-input>
          </el-form-item>
          <el-form-item class="input" label="更新者:" prop="updateBy">
            <el-input clearable disabled v-model.trim="formData.updateBy"></el-input>
          </el-form-item>
        </div>
        <div class="other-threerow">
          <el-form-item class="input" label="创建时间:" prop="createTime">
            <el-input clearable disabled v-model.trim="formData.createTime"></el-input>
          </el-form-item>
          <el-form-item class="input" label="更新时间:" prop="updateTime">
            <el-input
              clearable
              disabled
              placeholder="无"
              v-model.trim="formData.updateTime"
            ></el-input>
          </el-form-item>
          <el-form-item class="input" placeholder="无" label="密码更新时间:" prop="pwdResetTime">
            <el-input
              clearable
              disabled
              placeholder="无"
              v-model.trim="formData.pwdResetTime"
            ></el-input>
          </el-form-item>
        </div>

        <!--textarea输入-->
        <el-form-item label="简介:" prop="personIntroduction">
          <el-input
            clearable
            disabled
            placeholder="无"
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

const dialogConfig = ref({
  show: false,
  title: '详情信息',
  buttons: [
    {
      type: 'primary',
      text: '确定',
      click: (e: any) => {},
    },
  ],
})

const emit = defineEmits(['reload'])

const showEnabled = ref(true)

const showEdit = (data: Array<Record<string, any>>, type: number) => {
  dialogConfig.value.show = true
  Object.assign(formData.value, data)
  formData.value.roleNameIndex = data.roleId
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
        width: 400px;
        margin-right: 40px;
      }
    }
    .other-tworow {
      display: flex;
      width: 100%;
      .input {
        width: 400px;
        margin-right: 40px;
      }
    }
    .other-threerow {
      display: flex;
      width: 100%;
      .input {
        width: 300px;
        margin-right: 40px;
      }
    }
    .input {
      width: 400px;
    }
  }
}
</style>
