export default {
  size2Str: (limit) => {
    var size = ''
    if (limit < 1 * 1024) {
      // 小于 1 KB，转换成 B
      size = limit.toFixed(2) + 'B'
    } else if (limit < 1 * 1024 * 1024) {
      // 小于 1 MB，转换成 KB
      size = (limit / 1024).toFixed(2) + 'KB'
    } else if (limit < 1 * 1024 * 1024 * 1024) {
      // 小于 1 GB，转换成 MB
      size = (limit / (1024 * 1024)).toFixed(2) + 'MB'
    } else {
      // 大于等于 1 GB，转换成 GB
      size = (limit / (1024 * 1024 * 1024)).toFixed(2) + 'GB'
    }
    var sizeStr = size + '' // 转成字符串
    var index = sizeStr.indexOf('.') // 获取小数点出的索引
    var dou = sizeStr.substring(index + 1, 2) //获取小数点后两位的值
    if (dou == '00') {
      //判断后两位是否为 00 , 如果是则删除 00
      return sizeStr.substring(0, index) + sizeStr.substring(index + 3, 2)
    }
    return size
  },
  getLocalImage: (image) => {
    return new URL(`../assets/${image}`, import.meta.url).href
  },
  getFileName: (fileName) => {
    if (!fileName) {
      return fileName
    }
    return fileName.lastIndexOf('.') == -1
      ? fileName
      : fileName.substring(0, fileName.lastIndexOf('.'))
  },
  convertSecondsToHMS: (seconds) => {
    // 处理非数字和负数
    if (isNaN(seconds) || seconds < 0) {
      return '00:00:00'
    }

    // 确保是整数
    seconds = Math.floor(seconds)

    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    const secs = seconds % 60

    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs
      .toString()
      .padStart(2, '0')}`
  },
  // 将日期格式化为 yy-MM-dd 格式
  formatDateToYYYYMMDD: (date) => {
    if (!date) return ''

    const d = new Date(date)
    const year = d.getFullYear().toString() // 获取后两位年份
    const month = (d.getMonth() + 1).toString().padStart(2, '0')
    const day = d.getDate().toString().padStart(2, '0')

    return `${year}-${month}-${day}`
  },
  // 生成含数字字母的随机数
  generateSecureRandomString: (length, options = {}) => {
    const {
      includeUppercase = true,
      includeLowercase = true,
      includeNumbers = true,
      customCharset = '',
    } = options

    let charset = customCharset

    if (!customCharset) {
      charset = ''
      if (includeLowercase) charset += 'abcdefghijklmnopqrstuvwxyz'
      if (includeUppercase) charset += 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
      if (includeNumbers) charset += '0123456789'

      if (charset === '') charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
    }

    // 使用更安全的随机数生成器（如果可用）
    const crypto = window.crypto || window.msCrypto
    const charsetLength = charset.length
    let result = ''

    if (crypto && crypto.getRandomValues) {
      const randomValues = new Uint32Array(length)
      crypto.getRandomValues(randomValues)

      for (let i = 0; i < length; i++) {
        result += charset[randomValues[i] % charsetLength]
      }
    } else {
      // 回退到 Math.random()
      for (let i = 0; i < length; i++) {
        const randomIndex = Math.floor(Math.random() * charsetLength)
        result += charset[randomIndex]
      }
    }
    return result
  },
}
