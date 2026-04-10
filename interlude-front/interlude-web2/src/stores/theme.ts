import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  // 状态：当前主题，默认从 localStorage 读取
  const currentTheme = ref(localStorage.getItem('theme') || 'light')

  // 切换主题的方法
  function toggleTheme() {
    const newTheme = currentTheme.value === 'light' ? 'dark' : 'light'
    currentTheme.value = newTheme
    localStorage.setItem('theme', newTheme)

    // 更新 <link> 标签的 href
    const themeLink = document.getElementById('theme-link') as HTMLLinkElement
    if (themeLink) {
      themeLink.href = `../src/assets/css/${newTheme}-theme.css`
    }
  }

  // 监听主题变化（可选，用于其他响应式逻辑）
  watch(currentTheme, (newTheme) => {
    // 可以在这里执行额外的操作，比如更新 meta 主题颜色等
  })

  return { currentTheme, toggleTheme }
})