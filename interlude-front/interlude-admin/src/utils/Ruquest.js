import axios from 'axios'
import { ElLoading } from 'element-plus'
import Message from '../utils/Message'
import VueCookies from 'vue-cookies'
// import { useLoginStore } from '@/stores/loginStore'

const contentTypeForm = 'application/x-www-form-urlencoded;charset=UTF-8'
const contentTypeJson = 'application/json'
const responseTypeJson = 'json'

let loading = null
let loadingCount = 0

const instance = axios.create({
  withCredentials: true,
  baseURL: '/api',
  timeout: 100 * 1000,
})

const openLoading = () => {
  loadingCount += 1
  if (!loading) {
    loading = ElLoading.service({
      lock: true,
      text: '加载中.....',
      background: 'rgba(0, 0, 0, 0.7)',
    })
  }
}

const closeLoading = () => {
  if (loadingCount > 0) {
    loadingCount -= 1
  }
  if (loadingCount === 0 && loading) {
    loading.close()
    loading = null
  }
}

const getErrorMessage = (responseData, defaultMessage = '请求处理失败') => {
  if (!responseData) {
    return defaultMessage
  }
  return responseData.info || responseData.msg || responseData.message || defaultMessage
}

const buildRequestData = (params = {}, dataType) => {
  if (dataType === 'json') {
    return {
      data: params,
      contentType: contentTypeJson,
    }
  }

  const hasFile =
    dataType === 'file' ||
    Object.values(params).some((value) => value instanceof File || value instanceof Blob)

  if (hasFile) {
    const formData = new FormData()
    Object.keys(params).forEach((key) => {
      formData.append(key, params[key] == undefined ? '' : params[key])
    })
    return {
      data: formData,
      contentType: null,
    }
  }

  const formData = new URLSearchParams()
  Object.keys(params).forEach((key) => {
    formData.append(key, params[key] == undefined ? '' : params[key])
  })
  return {
    data: formData,
    contentType: contentTypeForm,
  }
}

// 请求前拦截器
instance.interceptors.request.use(
  (config) => {
    if (config.showLoading) {
      openLoading()
    }
    return config
  },
  (error) => {
    if (error.config?.showLoading) {
      closeLoading()
    }
    Message.error('请求发送失败')
    return Promise.reject({ showError: false, msg: '请求发送失败' })
  }
)

// 请求后拦截器
instance.interceptors.response.use(
  (response) => {
    const { showLoading, errorCallback, showError = true, responseType } = response.config
    if (showLoading) {
      closeLoading()
    }

    const responseData = response.data
    if (responseType == 'arraybuffer' || responseType == 'blob') {
      return responseData
    }

    if (responseData?.code == 200) {
      return responseData
    }

    if (responseData?.code == 901) {
      // const loginStore = useLoginStore()
      // loginStore.setLogin(true)
      Message.warning('未登录或登录超时,请重新登录')
      setTimeout(() => {
        window.location.href = '/login'
      }, 500)
      return Promise.reject({ showError: false })
    }

    if (errorCallback) {
      errorCallback(responseData)
    }
    return Promise.reject({ showError: showError, msg: getErrorMessage(responseData) })
  },
  (error) => {
    if (error.config?.showLoading) {
      closeLoading()
    }
    const msg = error.code === 'ECONNABORTED' ? '请求超时,请稍后重试' : '网络异常'
    return Promise.reject({ showError: true, msg })
  }
)

const request = (config) => {
  const {
    url,
    params = {},
    dataType,
    showLoading = false,
    responseType = responseTypeJson,
    showError = true,
  } = config

  const { data, contentType } = buildRequestData(params, dataType)
  const token = VueCookies.get('adminToken')
  const headers = {
    'X-Requested-With': 'XMLHttpRequest',
  }

  if (contentType) {
    headers['Content-Type'] = contentType
  }
  if (token) {
    headers.adminToken = token
  }

  return instance
    .post(url, data, {
      onUploadProgress: (event) => {
        if (config.uploadProgressCallback) {
          config.uploadProgressCallback(event)
        }
      },
      responseType: responseType,
      headers: headers,
      showLoading: showLoading,
      errorCallback: config.errorCallback,
      showError: showError,
    })
    .catch((error) => {
      if (error.showError) {
        Message.error(error.msg || '请求处理失败')
      }
      return null
    })
}

export default request
