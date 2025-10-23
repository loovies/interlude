// src/types/vue-cookies.d.ts
declare module 'vue-cookies' {
  interface VueCookies {
    set(
      key: string,
      value: any,
      expire?: string | number | Date,
      path?: string,
      domain?: string,
      secure?: boolean
    ): boolean
    get(key: string): any
    remove(key: string, path?: string, domain?: string): boolean
    isKey(key: string): boolean
    keys(): string[]
  }

  const vueCookies: VueCookies
  export default vueCookies
}
