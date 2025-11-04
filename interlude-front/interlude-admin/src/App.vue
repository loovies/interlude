<template>
  <el-config-provider :locale="zhCn" :message="config">
    <router-view></router-view>
  </el-config-provider>
</template>

<script setup lang="ts">
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import { ref, reactive, onMounted, getCurrentInstance, nextTick, onBeforeMount } from 'vue'
const { proxy } = getCurrentInstance()
import VueCookies from 'vue-cookies'

import { useLoginStore } from './stores/loginStore'
const loginStore = useLoginStore()

const autoLogin = async () => {
  const token = VueCookies.get('adminToken')
  if (!token) {
    return
  }
  let result = await proxy.$Request({
    url: proxy.$Api.autoLogin,
  })
  if (!result) {
    return
  }
  saveLoginInfo(result.data)
}

const saveLoginInfo = (loginInfo) => {
  if (!loginInfo) {
    loginStore.saveUserInfo({})
  } else {
    loginStore.saveUserInfo(loginInfo)
  }
}

let categoryList = []
let categoryMap = {}

const loadCategory = async () => {
  const result = await proxy.Request({
    url: proxy.Api.loadDataList,
  })
  if (!result) {
    return
  }
  categoryList = result.data
  result.data.forEach((element) => {
    categoryMap[element.categoryCode] = element
    element.children.forEach((sub) => {
      categoryMap[sub.categoryCode] = sub
    })
  })
  categoryStore.saveCategoryMap(categoryMap)
  categoryStore.saveCategoryList(categoryList)
}

onMounted(() => {
  autoLogin()
  loadCategory()
})

const config = ref({
  max: 1,
})
</script>

<style scoped></style>
