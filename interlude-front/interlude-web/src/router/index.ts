import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/recommend',
      component: () => import('@/view/Home/index.vue'),
      children: [
        {
          path: 'recommend',
          name: 'recommend',
          component: () => import('@/view/Home/recommend/index.vue'),
        },
        {
          path: 'recommend/videoId=:videoId',
          name: 'recommendWithVideo',
          component: () => import('@/view/Home/recommend/index.vue'),
        },
        {
          path: 'choiceness',
          name: 'choiceness',
          component: () => import('@/view/Home/choiceness/index.vue'),
        },
        {
          path: 'aiSearch',
          name: 'aiSearch',
          component: () => import('@/view/Home/aiSearch/index.vue'),
        },
        {
          path: 'mine',
          name: 'mine',
          component: () => import('@/view/Home/mine/index.vue'),
        },
        {
          path: 'follow',
          name: 'follow',
          component: () => import('@/view/Home/user/follow/index.vue'),
        },
        {
          path: 'friend',
          name: 'friend',
          component: () => import('@/view/Home/user/friend/index.vue'),
        },
        {
          path: 'test',
          name: 'test',
          component: () => import('@/view/Playground/test/index.vue'),
        },
        {
          path: 'simple-test',
          name: 'simple-test',
          component: () => import('@/view/Playground/simple-test/index.vue'),
        },
      ],
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('@/view/Search/index.vue'),
    },
    {
      path: '/creator',
      name: 'creator',
      redirect: '/creator/home',
      meta: {
        // 创作中心页面需要先登录，避免用户直接通过地址栏进入。
        requiresAuth: true,
      },
      component: () => import('@/view/Creator/index.vue'),
      children: [
        {
          path: 'home',
          name: 'creatorHome',
          component: () => import('@/view/Creator/home/index.vue'),
        },
        {
          path: 'publish',
          name: 'creatorPublish',
          component: () => import('@/view/Creator/publish/index.vue'),
        },
        {
          path: 'data',
          name: 'creatorData',
          component: () => import('@/view/Creator/data/index.vue'),
        },
        {
          path: 'works',
          name: 'creatorWorks',
          component: () => import('@/view/Creator/works/index.vue'),
        },
        {
          path: 'interaction',
          name: 'creatorInteraction',
          component: () => import('@/view/Creator/interaction/index.vue'),
        },
        {
          path: 'message',
          name: 'creatorMessage',
          component: () => import('@/view/Creator/message/index.vue'),
        },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const requiresAuth = to.matched.some((record) => record.meta?.requiresAuth)
  if (!requiresAuth) {
    return true
  }

  const authStore = useAuthStore()

  // 先尝试自动登录，避免已有 token 的用户被误判成未登录。
  if (!authStore.autoLoginFinished) {
    await authStore.runAutoLogin()
  }

  if (authStore.isLoggedIn) {
    return true
  }

  authStore.openLoginDialog('publish')
  return {
    path: '/recommend',
    query: {
      loginRedirect: to.fullPath,
    },
  }
})

export default router
