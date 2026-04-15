import { createRouter, createWebHistory } from 'vue-router'

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
        }
      ]
    }
],
})

export default router

