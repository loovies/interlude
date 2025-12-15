import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 4000,
    hmr: true,
    proxy: {
      '/api': {
        target: 'http://localhost:9000/',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/admin'),
      },
      '/admin/videos': {
        target: 'http://localhost:9000/',
        changeOrigin: true,
      },
      '/videos': {
        target: 'http://localhost:9000/',
        changeOrigin: true,
        rewrite: (path) => '/admin' + path, // 将 /videos 重写为 /admin/videos
      },
    },
  },
})
