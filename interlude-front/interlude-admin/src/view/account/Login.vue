<template>
  <div class="login-panel">
    <div class="login-box">
      <div class="login-content">
        <h1>登录</h1>
        <div class="login-input">
          <el-form
            :model="formData"
            :rules="rules"
            ref="formDataRef"
            @submit.prevent
            class="inputBox"
          >
            <!--input输入-->
            <el-form-item prop="account">
              <el-input
                clearable
                class="input"
                placeholder="请输入账号"
                size="large"
                v-model.trim="formData.account"
              >
                <template #prefix>
                  <span class="iconfont icon-yonghu"></span>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                clearable
                show-password
                class="input"
                size="large"
                placeholder="请输入密码"
                v-model.trim="formData.password"
              >
                <template #prefix>
                  <span class="iconfont icon-mima"></span>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="checkCode">
              <el-input
                clearable
                class="checkInput"
                size="large"
                placeholder="请输入验证码"
                v-model.trim="formData.checkCode"
              >
                <template #prefix>
                  <span class="iconfont icon-yanzhengyanzhengma"></span>
                </template>
              </el-input>
              <img :src="checkCodeInfo.code" class="checkImage" @click="changeCheckCode()" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="submitBtn" @click="submitFrom()">登录</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormRules } from 'element-plus'
import VueCookies from 'vue-cookies'
const { proxy } = getCurrentInstance()

import { useLoginStore } from '../../stores/loginStore'
const loginStore = useLoginStore()

import { useBreadcrumbListStore } from '../../stores/breadcrumbStore'
const breadcrumbStore: any = useBreadcrumbListStore()

import { useSysSettingStore } from '../../stores/sysSettingStore'
const sysSettingStore: any = useSysSettingStore()

const route = useRoute()
const router = useRouter()

const formData = ref<Record<string, any>>({}) // 键值对象
const checkCodeInfo = ref<Record<string, any>>({}) // 键值对象
const formDataRef = ref()

const rules: FormRules = {
  account: [{ required: true, message: '请输入账号' }],
  password: [
    { required: true, message: '请输入密码' },
    // { validator: proxy.$Verify.password, message: '密码只能是数字,字母,特殊字符8-18位' },
  ],
  checkCode: [{ required: true, message: '请输入图片验证码' }],
}

const submitFrom = (): void => {
  formDataRef.value.validate(async (vaild) => {
    if (!vaild) {
      return
    }
    let params: Record<string, string> = {}
    Object.assign(params, formData.value)
    params.checkCodeKey = checkCodeInfo.value.codeKey
    // md5
    let res: Record<string, any> = await proxy.$Request({
      url: proxy.$Api.login,
      params,
      errorCallback: (err: any) => {
        changeCheckCode()
      },
    })
    if (!res) {
      return
    }
    // 登录成功后优先回到登录前准备访问的后台页面。
    const redirectPath =
      typeof route.query.redirect === 'string' && route.query.redirect ? route.query.redirect : '/home'
    router.push(redirectPath)
    proxy.$Message.success('登录成功')
    VueCookies.set('account', res.data.phone)
    loginStore.saveUserInfo(res.data)
    breadcrumbStore.saveBreadcrumbList([])
    saveSysSettingInfo()
  })
}

const saveSysSettingInfo = async (): Promise<void> => {
  let result = await proxy.$Request({
    url: proxy.$Api.getSysSetting,
  })
  if (!result) {
    return
  }
  sysSettingStore.saveSettingList(result.data)
}

const changeCheckCode = async (): Promise<void> => {
  let result = await proxy.$Request({
    url: proxy.$Api.checkCode,
  })
  if (!result) {
    return
  }
  checkCodeInfo.value = result.data
}
changeCheckCode()
</script>

<style lang="scss" scoped>
.login-panel {
  width: 100vw;
  height: 100vh;
  background-image: url('@/assets/image/bg/login_bg.jpg');
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  display: flex;
  justify-content: center;
  .login-box {
    margin-top: 10%;
    width: 520px;
    height: 420px;
    border-radius: 8px;
    box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.2);
    background-color: rgba(255, 255, 255, 0.8);
    padding: 20px;
    text-align: center;
    .login-content {
      .login-input {
        margin-top: 0px;
        margin-left: 40px;
        width: 400px;
        .inputBox {
          .checkInput {
            width: 200px;
          }
          .submitBtn {
            width: 100%;
            height: 42px;
            margin-top: 10px;
          }
          .checkImage {
            margin-top: 5px;
            margin-left: 35px;
          }
        }
      }
    }
  }
}
.input,
.checkInput {
  margin-top: 10px;
  margin-bottom: 5px;
  height: 45px;
}
</style>
