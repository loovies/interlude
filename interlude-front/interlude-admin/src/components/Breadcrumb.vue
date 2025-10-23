<template>
  <div class="breadcrumbList">
    <span @click="goBack" class="iconfont icon-a-lianhe6 goBack"></span>
    <template v-for="(item, index) in breadcrumbList" :key="index">
      <router-link
        :to="item.path"
        :class="['bread-menuName', { 'bread-active': $route.path === item.path }]"
      >
        <i v-if="item.icon" :class="['iconfont', item.icon]"></i>
        {{ item.menuName }}
        <span
          class="iconfont icon-guanbi closeBreadMenu"
          v-if="index !== 0 && item.path !== currentRoute"
          @click="removeBreadMenu(index)"
        ></span>
      </router-link>
    </template>
    <div class="clearBreadrumb">
      <span class="iconfont icon-shanchu" @click="clearBreadcrumb"></span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance, Ref, watch, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const { proxy } = getCurrentInstance()

import { useBreadcrumbListStore } from '../stores/breadcrumb'
const breadcrumbStore: any = useBreadcrumbListStore()

const props: any = defineProps({
  menuList: {
    type: Array,
    default: [],
  },
})

const initBreadcurmbList = () => {
  const saved = breadcrumbStore.breadcrumbList

  if (saved.length <= 0) {
    const homeItem = props.menuList.find((item) => item.path === '/home')
    if (homeItem) {
      breadcrumbList.value = [
        {
          menuName: homeItem.menuName,
          path: homeItem.path,
        },
      ]
    }
  } else {
    breadcrumbList.value = saved
  }
}

onMounted(() => {
  initBreadcurmbList()
})

// 面包屑的数据
const breadcrumbList = ref([])

// 保存面包屑到本地存储
function saveBreadcrumb() {
  breadcrumbStore.saveBreadcrumbList(breadcrumbList.value)
}

const addBreadcrumbList = (path: string) => {
  const falg = breadcrumbList.value.filter((item) => item.path == path)
  console.log(falg)

  if (falg.length > 0) {
    return
  }
  props.menuList.forEach((item) => {
    if (item.children) {
      item.children.forEach((record) => {
        if (record.path == path) {
          breadcrumbList.value.push(record)
        }
      })
    } else {
      if (item.path == path) {
        breadcrumbList.value.push(item)
      }
    }
  })
  saveBreadcrumb()
}

// 清空面包屑（保留首页）
function clearBreadcrumb() {
  if (breadcrumbList.value.length > 1) {
    // const homeItem = breadcrumbList.value[0]
    breadcrumbList.value = [
      {
        menuName: '首页',
        path: '/home',
        icon: 'icon-index',
      },
    ]
    saveBreadcrumb()

    // 跳转到首页
    router.push('/home')
  }
}

function removeBreadMenu(index) {
  if (index <= 0 || index >= breadcrumbList.value.length) return

  const removedItem = breadcrumbList.value[index]
  breadcrumbList.value.splice(index, 1)
  saveBreadcrumb()
}

function goBack(index: number) {
  router.go(-1)
}

const currentRoute = computed(() => route.path)

// 监听路由变化，确保当前路由在面包屑中
watch(
  () => route.path,
  (newPath, oldPath) => {
    // 如果当前路由不在面包屑中，添加到面包屑

    if (newPath == '/login') return
    addBreadcrumbList(newPath)
  }
)
</script>

<style lang="scss" scoped>
.breadcrumbList {
  margin: 15px;
  .goBack {
    cursor: pointer;
    margin-right: 20px;
  }
  .goBack:hover {
    cursor: pointer;
    margin-right: 20px;
    color: var(--blue);
  }
  .bread-menuName {
    margin-right: 20px;
    color: #1e1e1e;
    text-decoration: none;
    background-color: #fff;
    padding: 5px 8px;
    border-radius: 5px;
    box-shadow: 1px 2px 2px #e7e6e6;
  }
  .bread-menuName:hover {
    color: var(--blue);
  }
  .bread-active {
    color: var(--blue);
    box-shadow: none;
  }
  .clearBreadrumb {
    float: right;
    cursor: pointer;
    .iconfont {
      font-size: 22px;
      color: rgb(121, 119, 119);
    }
  }
  .closeBreadMenu {
    position: relative;
    top: 1px;
    left: 2px;
    font-size: 14px;
  }
}
</style>
