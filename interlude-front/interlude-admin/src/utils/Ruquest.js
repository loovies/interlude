import axios from 'axios'
import { ElLoading } from 'element-plus'
import Message from '../utils/Message'
import VueCookies from 'vue-cookies'
// import { useLoginStore } from '@/stores/loginStore'

const contentTypeForm = 'application/x-www-form-urlencoded;charset=UTF-8'
const contentTypeJson = 'application/json'
const responseTypeJson = 'json'
let loading = null
const instance = axios.create({
  withCredentials: true, // 携带跨域cookie
  baseURL: '/api', // 基础路径
  timeout: 100 * 1000, // 超时时间100秒
})
//请求前拦截器
instance.interceptors.request.use(
  (config) => {
    if (config.showLoading) {
      loading = ElLoading.service({
        lock: true,
        text: '加载中......',
        background: 'rgba(0, 0, 0, 0.7)',
      })
    }
    return config
  },
  (error) => {
    if (error.config.showLoading && loading) {
      loading.close()
    }
    Message.error('请求发送失败')
    return Promise.reject('请求发送失败') // 创建一个被拒绝（rejected）的Promise
  }
)

//请求后拦截器
instance.interceptors.response.use(
  (response) => {
    const { showLoading, errorCallback, showError = true, responseType } = response.config
    if (showLoading && loading) {
      loading.close()
    }
    const responseData = response.data
    // 如果响应类型是arraybuffer或blob（比如文件下载），则直接返回响应数据，不进行后续处理。
    if (responseType == 'arraybuffer' || responseType == 'blob') {
      return responseData
    }
    //正常请求
    if (responseData.code == 200) {
      return responseData
    } else if (responseData.code == 901) {
      // const loginStore = useLoginStore()
      // //登录超时
      // loginStore.setLogin(true)
      Message.warning('未登录或登录超时,请重新登录')
      setTimeout(() => {
        window.location.href = '/login'
      }, 500)
      return Promise.reject({ showError: false })
    } else {
      //其他错误
      if (errorCallback) {
        errorCallback(responseData)
      }
      return Promise.reject({ showError: showError, msg: responseData.info })
    }
  },
  (error) => {
    if (error.config.showLoading && loading) {
      loading.close()
    }
    return Promise.reject({ showError: true, msg: '网络异常' })
  }
)

const request = (config) => {
  const {
    url,
    params,
    dataType,
    showLoading = false,
    responseType = responseTypeJson,
    showError = true,
  } = config
  let contentType = contentTypeForm
  let formData = new FormData() // 创建form对象
  for (let key in params) {
    formData.append(key, params[key] == undefined ? '' : params[key])
  }
  if (dataType != null && dataType == 'json') {
    contentType = contentTypeJson
  }
  const token = VueCookies.get('adminToken')
  let headers = {
    'Content-Type': contentType,
    'X-Requested-With': 'XMLHttpRequest',
    adminToken: token,
  }
  return instance
    .post(url, formData, {
      onUploadProgress: (event) => {
        // 上传进度回调
        if (config.uploadProgressCallback) {
          config.uploadProgressCallback(event)
        }
      },
      responseType: responseType, // 响应类型
      headers: headers, // 请求头
      showLoading: showLoading, // 是否显示 loading
      errorCallback: config.errorCallback, // 错误回调
      showError: showError, // 是否显示错误
    })
    .catch((error) => {
      if (error.showError) {
        Message.error(error.msg)
      }
      return null
    })
}

export default request
