import mitt from 'mitt'

export type MitterEvents = {
  windowResize: void
  themeChange: string
  [key: string]: unknown
}

export const mitter = mitt<MitterEvents>()

export default mitter
