<template>
  <router-view />
  <LoginModal />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import LoginModal from '@/components/auth/LoginModal.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  authStore.runAutoLogin()
})
</script>

<style>
</style>
