<template>
  <div class="container">
    <Sidebar />
    <main class="main-content">
      <div class="search-container">
        <SearchBar />
      </div>
      <div class="content-container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import Sidebar from '@/components/Sidebar.vue'
import SearchBar from '@/components/SearchBar.vue'
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
  }

</style>
