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
    .main-content {
      flex: 1;
      background-color: var(--bg-color);
      color: var(--text-color);
      transition: background-color 0.3s, color 0.3s;
    }
    .search-container{
      padding-top: 10px;
    }
    .content-container{
      width: 100%;
      padding: 10px;
      height: calc(100vh - 56px);
      overflow-y: auto;
    }
  }

</style>