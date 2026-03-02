<template>
  <button class="theme-toggle-btn" @click="toggleTheme">
    {{ currentTheme === 'dark' ? '☀️' : '🌙' }}
  </button>
</template>

<script setup lang="ts">
import { useThemeStore } from '../stores/theme'
import { storeToRefs } from 'pinia'   // 导入

const themeStore = useThemeStore()
// 使用 storeToRefs 保持响应性
const { currentTheme } = storeToRefs(themeStore)
// 方法可以直接解构
const { toggleTheme } = themeStore
</script>
<style scoped>
.theme-toggle-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--sidebar-bg);
  color: var(--text-color);
  border: 1px solid var(--border-color);
  transition: background-color 0.3s, color 0.3s, border-color 0.3s;
}

.theme-toggle-btn:hover {
  background-color: var(--hover-bg);
}
</style>