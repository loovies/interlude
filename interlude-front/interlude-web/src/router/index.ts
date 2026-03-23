import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/recommend',
      component: () => import('@/view/home/index.vue'),
      children: [
        {
          path: 'recommend',
          name: 'recommend',
          component: () => import('@/view/home/recommend/index.vue'),
        },
        {
          path: 'choiceness',
          name: 'choiceness',
          component: () => import('@/view/home/choiceness/index.vue'),
        },
        {
          path: 'aiSearch',
          name: 'aiSearch',
          component: () => import('@/view/home/aiSearch/index.vue'),
        },
        {
          path: 'test',
          name: 'test',
          component: () => import('@/view/home/test/index.vue'),
        },
        {
          path: 'simple-test',
          name: 'simple-test',
          component: () => import('@/view/home/simple-test/index.vue'),
        }
      ]
    }
],
})

export default router
