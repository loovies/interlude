<template>
  <div class="container">
    <Sidebar />
    <main class="main-content">
      <div class="search-container">
        <SearchBar />
      </div>
      <router-view />
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
  }

</style>