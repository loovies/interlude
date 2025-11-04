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
})

export { useLoginStore }
