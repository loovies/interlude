<template>
  <div class="home-page">
    <h1>短视频用户端首页（Nuxt 3）</h1>
    <p>已兼容 Node 20.18.0，支持 SSR 优化 SEO</p>
  </div>
</template>

<script setup>
// 后续可迁移你原有 Vue3 首页的代码到这里
// 注意：如果用到 window/localStorage，需用 onMounted 包裹
import { onMounted } from 'vue'

onMounted(() => {
  // 仅在客户端执行的代码（比如初始化视频播放器）
  console.log('Nuxt 3 首页加载完成')
})
</script>

<style scoped>
.home-page {
  padding: 20px;
  text-align: center;
}
</style>