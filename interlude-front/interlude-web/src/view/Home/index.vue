<template>
  <div class="container">
    <Sidebar />
    <main class="main-content">
      <div v-if="route.name !== 'aiSearch'" class="search-container">
        <SearchBar />
      </div>
      <div class="content-container" :class="{ 'content-container--ai': route.name === 'aiSearch' }">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import Sidebar from '@/components/Sidebar.vue'
import SearchBar from '@/components/SearchBar.vue'

const route = useRoute()
</script>

<style lang="scss">
  .container{
    display: flex;
    height: 100vh;
    overflow: hidden;

    .main-content {
      flex: 1;
      min-width: 0;
      height: 100vh;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      background-color: var(--bg-color);
      color: var(--text-color);
      transition: background-color 0.3s, color 0.3s;
    }

    .search-container{
      padding-top: 10px;
      flex: 0 0 auto;
    }

    .content-container{
      flex: 1;
      min-height: 0;
      width: 100%;
      padding: 10px;
      overflow-y: auto;
      overflow-x: hidden;
    }

    .content-container--ai {
      overflow: hidden;
    }
  }

</style>
