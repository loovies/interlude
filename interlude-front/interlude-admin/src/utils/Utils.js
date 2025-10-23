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
}
