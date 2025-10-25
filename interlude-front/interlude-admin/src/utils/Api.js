import Ruquest from './Ruquest.js'

const Api = {
  //login
  checkCode: '/account/checkCode',
  login: '/account/login',

  // user
  getLoadDataList: '/user/loadDataList',
  getRoleName: '/user/loadUserRole',
  getaddOrUpdateBatch: '/user/addOrUpdateBatch',

  //file
  uploadImage: '/file/uploadImage',
  sourcePath: '/api/file/getResource?sourceName=',
}

//上传图片
const uploadImage = async (file, createThumbnail = true) => {
  let result = await Ruquest({
    url: Api.uploadImage,
    params: { file, createThumbnail },
  })
  if (!result) {
    return
  }
  return result.data
}
export { Api, uploadImage }
