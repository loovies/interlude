import type Verify from '@/utils/Verify'
import type Request from '@/utils/Ruquest.js'
import type Message from '@/utils/Message.js'
import type Api from '@/utils/Api.js'
import type Utils from '@/utils/Utils.js'
import type Confirm from '@/utils/Confirm.js'

declare module 'vue' {
  interface ComponentCustomProperties {
    $Verify: typeof Verify
    $Request: typeof Request
    $Message: typeof Message
    $Api: typeof Api
    $Utils: typeof Utils
    $Confirm: typeof Confirm
  }
}
