type RequestConfig = {
  url: string
  params?: Record<string, any>
  dataType?: string
  showLoading?: boolean
  responseType?: string
  showError?: boolean
  errorCallback?: (error: any) => void
  uploadProgressCallback?: (event: ProgressEvent) => void
}

type RequestResult<T = any> = Promise<T | null>

type RequestMethod = (config: RequestConfig) => RequestResult

type VerifyMethod = (rule: any, value: any, callback: (error?: Error) => void) => void

type ConfirmConfig = {
  message: string
  okfun?: () => void | Promise<void>
  showCancelBtn?: boolean
  okText?: string
}

declare module 'vue' {
  interface ComponentCustomProperties {
    $Verify: Record<string, VerifyMethod>
    $Request: RequestMethod
    $Message: {
      error: (msg: string, callback?: () => void) => void
      success: (msg: string, callback?: () => void) => void
      warning: (msg: string, callback?: () => void) => void
    }
    $Api: Record<string, string>
    $Utils: Record<string, (...args: any[]) => any>
    $Confirm: (config: ConfirmConfig) => void
  }
}

declare module '@/utils/Verify.js' {
  const verify: Record<string, VerifyMethod>
  export default verify
}

declare module '@/utils/Ruquest.js' {
  const request: RequestMethod
  export default request
}

declare module '@/utils/Message.js' {
  const message: {
    error: (msg: string, callback?: () => void) => void
    success: (msg: string, callback?: () => void) => void
    warning: (msg: string, callback?: () => void) => void
  }
  export default message
}

declare module '@/utils/Api.js' {
  export const Api: Record<string, string>
  export const uploadImage: (file: File, createThumbnail?: boolean) => Promise<any>
  export const getRoleByUserId: (userId: string | number) => Promise<any>
  export const getCategoryInfoById: (
    pCategoryId: string | number,
    categoryId: string | number
  ) => Promise<any>
}

declare module '@/utils/Utils.js' {
  const utils: Record<string, (...args: any[]) => any>
  export default utils
}

declare module '@/utils/Confirm.js' {
  const confirm: (config: ConfirmConfig) => void
  export default confirm
}

declare module '../../../../utils/Utils.js' {
  const utils: Record<string, (...args: any[]) => any>
  export default utils
}
