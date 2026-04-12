<template>
  <aside class="sidebar">
    <div class="logo">
      <img :src="logoSrc" class="logo-img" />
      <span class="logo-text">幕间</span>
    </div>

    <nav class="nav-menu">
      <ul>
        <li class="nav-item" :class="{ active: route.path === '/choiceness' }">
          <router-link class="nav-link" to="/choiceness">
            <span class="icon">🔥</span>
            <span class="label">精选</span>
          </router-link>
        </li>
        <li class="nav-item" :class="{ active: route.path === '/recommend' }">
          <router-link class="nav-link" to="/recommend">
            <span class="icon">✅</span>
            <span class="label">推荐</span>
          </router-link>
        </li>
        <li class="nav-item" :class="{ active: route.path === '/aiSearch' }">
          <router-link class="nav-link" to="/aiSearch">
            <span class="icon">🤖</span>
            <span class="label">AI搜索</span>
          </router-link>
        </li>
      </ul>

      <div class="divider"></div>

      <ul>
        <li class="nav-item" :class="{ active: route.path === '/follow' }">
          <router-link class="nav-link" to="/follow">
            <span class="icon">👣</span>
            <span class="label">关注</span>
          </router-link>
        </li>
        <li class="nav-item">
          <span class="icon">👥</span>
          <span class="label">朋友</span>
        </li>
        <li class="nav-item" :class="{ active: route.path === '/mine' }">
          <router-link class="nav-link" to="/mine">
            <span class="icon">🙂</span>
            <span class="label">我的</span>
          </router-link>
        </li>
      </ul>
    </nav>

    <div class="sidebar-footer">
      <ThemeToggle />
    </div>
  </aside>
</template>

<script setup lang="ts">
import ThemeToggle from './ThemeToggle.vue'
import { computed } from 'vue'
import { useThemeStore } from '../stores/theme'
import { useRoute } from 'vue-router'

const themeStore = useThemeStore()
const route = useRoute()

const logoSrc = computed(() => {
  const imageName = themeStore.currentTheme === 'light' ? 'logo-b.png' : 'logo-l.png'
  return new URL(`../assets/images/${imageName}`, import.meta.url).href
})
</script>

<style scoped>
.sidebar {
  width: 200px;
  background-color: var(--sidebar-bg);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  padding: 20px 0;
  transition: background-color 0.3s, border-color 0.3s;
  height: 100vh;
  position: sticky;
  top: 0;
}

.logo {
  display: flex;
  align-items: center;
  padding: 0 16px;
  margin-bottom: 10px;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-left: 20px;
}

.logo-icon {
  font-size: 28px;
  margin-right: 8px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  margin-left: 8px;
  color: var(--text-color);
  cursor: pointer;
}

.nav-menu {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-menu ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  padding: 12px 16px;
  margin: 4px 8px;
  border-radius: 8px;
  color: var(--text-color);
  transition: background-color 0.2s;
}

.nav-item:hover {
  background-color: var(--hover-bg);
}

.nav-item.active {
  background-color: var(--primary-color);
  color: white;
}

.nav-item .icon {
  font-size: 20px;
  margin-right: 12px;
  width: 24px;
  text-align: center;
}

.nav-item .label {
  font-size: 16px;
  font-weight: 500;
}

.nav-link {
  display: flex;
  align-items: center;
  color: inherit;
  text-decoration: none;
  width: 100%;
}

.divider {
  height: 1px;
  background-color: var(--border-color);
  margin: 12px 16px;
}

.sidebar-footer {
  padding: 20px 16px 0;
  border-top: 1px solid var(--border-color);
  margin-top: auto;
}
</style>
