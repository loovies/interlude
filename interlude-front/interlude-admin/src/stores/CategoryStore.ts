import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

const useCategoryStore = defineStore('categoryStore', {
  state: () => {
    return {
      categoryList: [],
      categoryMap: {},
      cureentPCategory: {},
    }
  },
  actions: {
    saveCategoryMap(data) {
      this.categoryMap = data
    },
    saveCategoryList(data) {
      this.categoryList = data
    },
    setCureentPCategory(data) {
      if (data) {
        this.cureentPCategory = this.categoryMap[data] || {}
      } else {
        this.cureentPCategory = {}
      }
    },
  },
})
export { useCategoryStore }
