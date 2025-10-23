import '@/assets/scss/base.scss'
import '@/assets/icon/iconfont.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-plugin-persistedstate'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import VueCookies from 'vue-cookies'
import App from './App.vue'
import router from './router'

// 自定义函数
import Verify from '@/utils/Verify.js'
import Request from '@/utils/Ruquest.js'
import Message from '@/utils/Message.js'
import Api from '@/utils/Api.js'
import utils from '@/utils/Utils.js'

// 自定义组件
import Cover from '@/components/Cover.vue'
import Avatar from '@/components/Avatar.vue'
import Breadcrumb from '@/components/Breadcrumb.vue'
import Table from '@/components/Table.vue'
import Dialog from '@/components/Dialog.vue'
import ImageUpload from '@/components/ImageUpload.vue'

const app = createApp(App)

app.component('Cover', Cover)
app.component('Avatar', Avatar)
app.component('Breadcrumb', Breadcrumb)
app.component('Table', Table)
app.component('Dialog', Dialog)
app.component('ImageUpload', ImageUpload)

app.config.globalProperties.$VueCookies = VueCookies
app.config.globalProperties.$Verify = Verify
app.config.globalProperties.$Message = Message
app.config.globalProperties.$Request = Request
app.config.globalProperties.$Api = Api
app.config.globalProperties.$Utils = utils

//缩略图后缀
app.config.globalProperties.imageThumbnailSuffix = '_thumbnail.jpg'

//图片后缀
app.config.globalProperties.imageAccept = '.jpg,.png,.gif,.bmp,.webp'
//视频后缀
app.config.globalProperties.videoAccept = '.mp4,.avi,.rmvb,.mkv,.mov'

app.use(ElementPlus)

const pinia = createPinia()
pinia.use(createPersistedState())

app.use(pinia)
app.use(router)

app.mount('#app')
