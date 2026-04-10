import type { AxiosResponse } from 'axios'

export interface ResponseVO<T> {
  status?: string
  code: number
  info?: string
  data: T
}

export class ApiError extends Error {
  code?: number

  constructor(message: string, code?: number) {
    super(message)
    this.name = 'ApiError'
    this.code = code
  }
}

export const unwrapResponse = <T>(response: AxiosResponse<ResponseVO<T>>): T => {
  const payload = response.data
  if (!payload) {
    throw new ApiError('Server response is empty')
  }
  if (typeof payload.code !== 'undefined' && payload.code !== 200) {
    throw new ApiError(payload.info || 'Request failed', payload.code)
  }
  return payload.data
}
