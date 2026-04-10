import { createApp } from 'vue'
import { createPinia } from 'pinia'
import type { Emitter } from 'mitt'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import { mitter } from './eventbus/eventBus'

const app = createApp(App)

app.config.globalProperties.emitter = mitter

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    emitter: typeof mitter
  }
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')

