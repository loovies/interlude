<template>
  <div>faseufuseafhioashfiosehfioase</div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, nextTick } from 'vue'
const { proxy } = getCurrentInstance()

const uploadFile = (file: Object) => {
  console.log(file)
}

defineExpose({
  uploadFile,
})
</script>

<style lang="scss" scoped></style>
