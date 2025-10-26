import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

const useLoginStore = defineStore('loginstate', {
  state: () => {
    return {
      userInfo: {},
    }
  },
  actions: {
    saveUserInfo(info) {
      this.userInfo = info
    },
  },
  persist: {
    storage: localStorage, // 或 sessionStorage
    paths: ['userInfo'], // 只持久化 userInfo
  },
})

export { useLoginStore }
