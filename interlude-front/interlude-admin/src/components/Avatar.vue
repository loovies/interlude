<template>
  <div>
    <router-link v-if="userId" :to="`/user/${userId}`" target="_blank">
      <Cover
        :lazy="lazy"
        :width="width"
        :source="avatar"
        :defaultImg="defaultAvatar"
        borderRadius="50%"
        :scale="1"
      ></Cover>
    </router-link>
    <Cover
      v-else
      :lazy="lazy"
      :source="avatar"
      :defaultImg="defaultAvatar"
      borderRadius="50%"
      :scale="1"
      :width="width"
    ></Cover>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick } from 'vue'

const { proxy } = getCurrentInstance()
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const defaultAvatar = ref('user.png')

const props = defineProps({
  userId: {
    type: String,
  },
  avatar: {
    type: String,
  },
  timestamp: {
    type: Number,
    default: 0,
  },
  width: {
    type: Number,
    default: 40,
  },
  lazy: {
    //顶部头像使用lazy页面 不跳转路由不加载图片
    type: Boolean,
    default: true,
  },
})
</script>

<style lang="scss" scoped>
.avatar {
  display: flex;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  overflow: hidden;
  img {
    widows: 100%;
    object-fit: cover;
  }
}
</style>
