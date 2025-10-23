import { defineStore } from 'pinia'

const useBreadcrumbListStore = defineStore('breadcrumbstate', {
  state: () => ({
    breadcrumbList: [],
  }),
  actions: {
    saveBreadcrumbList(info) {
      this.breadcrumbList = info
    },
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'breadcrumbList',
        storage: localStorage,
      },
    ],
  },
})

export { useBreadcrumbListStore }
