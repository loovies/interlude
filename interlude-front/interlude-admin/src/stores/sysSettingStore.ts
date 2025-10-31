import { defineStore } from 'pinia'

const useSysSettingStore = defineStore('sysSetting', {
  state: () => ({
    sysSetting: {},
  }),
  actions: {
    saveSettingList(data) {
      this.sysSetting = data
    },
  },
  // persist: {
  //   enabled: true,
  //   strategies: [
  //     {
  //       key: 'breadcrumbList',
  //       storage: localStorage,
  //     },
  //   ],
  // },
})

export { useSysSettingStore }
