<template>
  <el-container class="container">
    <el-aside :class="['ElAsideStyle', isCollapse ? 'collapse-aside' : '']">
      <div class="title-district">
        <h5 class="title">
          <img
            src="../../assets/image/logo/perject-logo.png"
            :class="['logo-style', isCollapse ? 'collapse-logo' : '']"
            alt=""
          />
          <span
            :style="
              isCollapse
                ? 'opacity:0;transition: opacity 0.3s ease 0.1s;'
                : 'transition: opacity 1s ease 0.3s;'
            "
            >幕间管理系统</span
          >
        </h5>
      </div>
      <el-menu
        default-active="2"
        router
        class="menu-style"
        :defaultActive="route.path"
        background-color="#FFFFFF"
        text-color="#1e1e1e"
        active-text-color="#1e1e1e"
        :collapse="isCollapse"
        @open="handleOpen"
        @close="handleClose"
      >
        <template v-for="item in menuList">
          <el-sub-menu
            v-if="item.children"
            :index="item.path"
            :class="['menu-list', isCollapse ? 'collapse-list' : '']"
          >
            <template #title>
              <i :class="['iconfont', item.icon]"></i>
              <span class="menu-name">{{ item.menuName }}</span>
            </template>
            <el-menu-item class="menu-list" :index="record.path" v-for="record in item.children">
              <span class="menu-name">{{ record.menuName }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item
            v-else
            :index="item.path"
            :class="['menu-list', isCollapse ? 'collapse-list' : '']"
          >
            <!-- <template #title> -->
            <i :class="['iconfont', item.icon]"></i>
            <span class="menu-name">{{ item.menuName }}</span>
            <!-- </template> -->
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-main class="main-district">
      <el-header class="main-header">
        <span
          class="iconfont icon-shousuo shousuo-style"
          @click="changeCollapse(!isCollapse)"
        ></span>
        <div class="header-content">
          <el-popover class="box-item" placement="bottom">
            <template #reference>
              <Avatar :width="40" :lazy="false" :avatar="loginStore.userInfo.avatar"></Avatar
            ></template>
            <el-button class="logoutbtn" @click="logout()">退出登录</el-button>
          </el-popover>
        </div>
      </el-header>
      <div class="main-style">
        <Breadcrumb :menuList="menuList"></Breadcrumb>
        <div class="page-content">
          <router-view />
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance, Ref, watch, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'

const route = useRoute()
const router = useRouter()

const { proxy } = getCurrentInstance()

import { useLoginStore } from '../../stores/loginStore'
const loginStore = useLoginStore()
const { userInfo } = storeToRefs(loginStore)

const currentUserId = computed(() => {
  return userInfo.value.userId
})

const isCollapse: Ref<boolean> = ref(false)

const handleOpen = (key: string, keyPath: string[]) => {}
const handleClose = (key: string, keyPath: string[]) => {}

// 侧边栏菜单
const menuList: Array<Record<string, any>> = [
  {
    menuName: '首页',
    path: '/home',
    icon: 'icon-index',
  },
  {
    menuName: '内容管理',
    path: '/content',
    icon: 'icon-neirongguanli',
    children: [
      {
        menuName: '分类管理',
        path: '/content/category',
        icon: 'icon-fenleiguanli',
      },
      {
        menuName: '稿件管理',
        path: '/content/audit',
        icon: 'icon-gaojianguanli',
      },
    ],
  },
  {
    menuName: '互动管理',
    path: '/interact',
    icon: 'icon-hudongguanli',
    children: [
      {
        menuName: '评论管理',
        path: '/interact/comment',
        icon: 'icon-pinglunguanli',
      },
      {
        menuName: '弹幕管理',
        path: '/interact/danmu',
        icon: 'icon-danmuguanli',
      },
    ],
  },
  {
    menuName: '视频管理',
    path: '/video/videoList',
    icon: 'icon-shipinguanli',
  },
  {
    menuName: '用户管理',
    path: '/user/userList',
    icon: 'icon-yonghuguanli',
  },

  {
    menuName: '系统设置',
    path: '/setting',
    icon: 'icon-xitongshezhi',
  },
]

// 控制侧边栏收缩
const changeCollapse = (flag: boolean): void => {
  isCollapse.value = flag
}

const logout = async (): Promise<void> => {
  await proxy.$Request({ url: proxy.$Api.logout })
  proxy.$Message.success('退出成功')
  router.push('/')
}
</script>

<style lang="scss" scoped>
.container {
  .ElAsideStyle {
    width: 200px;
    transition: width 1s ease 0s;
    height: calc(100vh);
    z-index: 100;

    box-shadow: 5px 8px 12px rgba($color: #e1e2e3, $alpha: 0.4);
    .title-district {
      position: relative;
      height: 40px;
      line-height: 16px;
      //box-shadow: 5px 4px 4px rgba($color: #e1e2e3, $alpha: 0.4);
      z-index: 2;
      cursor: pointer;
      top: -5px;
      .logo-style {
        position: absolute;
        width: 32px;
        height: 28px;
        top: -5px;
        left: 12px;
        transition: top, left 0.5s ease 0.3s;
      }
      .collapse-logo {
        top: -10px;
        left: 19px;
        width: 40px;
        height: 35px;

        transition: top, left 0.5s ease 0.3s;
      }
      .title {
        text-align: center;
        font-size: 20px;
        font-family: '演示流云楷';
        padding-left: 13px;
        color: rgb(99, 98, 232);
      }
    }
    .menu-style {
      .iconfont {
        font-size: 18px;
        margin-right: 10px;
      }
      .menu-list {
        border-radius: 10px;
        margin: 5px 8px;
      }
      .collapse-list {
        width: 60px;
      }
    }
  }
  .collapse-aside {
    transition: width 1s ease 0s;
    width: 75px;
  }
  .main-district {
    padding: 0;
    background-color: #f5f7f9;
    .main-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 70px;
      background-color: #fff;
      box-shadow: 5px 4px 4px rgba($color: #e1e2e3, $alpha: 0.4);
      .shousuo-style {
        cursor: pointer;
        font-size: 28px;
      }
      .header-content {
        width: 40px;
        margin-right: 30px;
        .box-item {
        }
      }
    }
    .main-style {
      padding: 10px;

      .page-content {
        padding: 10px;
      }
    }
  }
}
.logoutbtn {
  width: 100%;
}
:deep(.el-menu) {
  border: 0;
}
:deep(.el-menu) {
  --el-menu-hover-bg-color: #efedff !important; /* 局部修改的颜色 */
}

.el-menu--popup {
  --el-menu-hover-bg-color: #ff0000 !important;
}

:deep(.el-sub-menu__title) {
  border-radius: 10px;
}

:deep(.el-menu-item.is-active) {
  background-color: #efedff !important; /* 你想要的激活背景色 */
}

.menu-style:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
</style>
