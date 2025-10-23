// shims-vue.d.ts
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module 'vue' {
  import type { CompatVue } from 'vue'
  const Vue: CompatVue
  export default Vue
  export * from 'vue'
  // 根据需要添加其他导出
}

declare module 'pinia' {
  export * from 'pinia/dist/pinia'
}
