// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },  // 开启调试工具
  modules: [],                  // 后续可添加 Element Plus 等模块
  ssr: true,                    // 开启 SSR（满足 SEO 需求）
  vite: {
    // 兼容你的 Node 版本，避免 vite 报错
    server: {
      fs: {
        strict: false
      }
    }
  }
})