import { createRouter, createWebHistory } from 'vue-router'
import VueCookies from 'vue-cookies'

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
      meta: {
        guestOnly: true,
      },
      component: () => import('../view/account/Login.vue'),
    },
    {
      path: '/',
      name: 'layout',
      redirect: '/login',
      meta: {
        // 后台布局页下的页面都需要先完成管理员登录。
        requiresAuth: true,
      },
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

router.beforeEach((to) => {
  const adminToken = VueCookies.get('adminToken')
  const requiresAuth = to.matched.some((record) => record.meta?.requiresAuth)
  const guestOnly = to.matched.some((record) => record.meta?.guestOnly)

  // 未登录时先跳登录页，并带上原目标地址，登录成功后再跳回去。
  if (requiresAuth && !adminToken) {
    return {
      path: '/login',
      query: {
        redirect: to.fullPath,
      },
    }
  }

  // 已登录用户访问登录页时，直接回到原页面或后台首页。
  if (guestOnly && adminToken) {
    return typeof to.query.redirect === 'string' && to.query.redirect ? to.query.redirect : '/home'
  }

  return true
})

export default router
