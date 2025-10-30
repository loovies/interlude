import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  // createWebHistory()  美观，无 #，如 https://example.com/user/id
  // createWebHashHistory()	有 #，如 https://example.com/#/user/id
  // 当 BASE_URL 为默认的 '/'，你的应用部署在网站根目录，路由直接追加到域名后
  // 当 BASE_URL 为 '/my-app/'，你的应用部署在名为 "my-app" 的子目录下 https://example.com/my-app/about。
  history: createWebHistory(import.meta.env.BASE_URL), // 默认为 "/"
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../view/account/Login.vue'),
    },
    {
      path: '/',
      name: 'layout',
      redirect: '/login',
      component: () => import('@/view/layout/Layout.vue'),
      children: [
        {
          path: '/home',
          name: 'home',
          component: () => import('@/view/home/home.vue'),
        },
        {
          path: '/setting',
          name: 'setting',
          component: () => import('@/view/layout/setting/index.vue'),
        },
        {
          path: '/user/userList',
          name: 'user',
          component: () => import('@/view/layout/user/index.vue'),
        },
        {
          path: '/video/videoList',
          name: 'video',
          component: () => import('@/view/layout/video/index.vue'),
        },
        {
          path: '/interact/comment',
          name: 'comment',
          component: () => import('@/view/layout/interact/comment/index.vue'),
        },
        {
          path: '/interact/danmu',
          name: 'danmu',
          component: () => import('@/view/layout/interact/danmu/index.vue'),
        },
        {
          path: '/content/category',
          name: 'category',
          component: () => import('@/view/layout/content/category/index.vue'),
        },
        {
          path: '/content/audit',
          name: 'audit',
          component: () => import('@/view/layout/content/audit/index.vue'),
        },
      ],
    },
  ],
})

export default router
