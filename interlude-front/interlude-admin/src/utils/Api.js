import Ruquest from './Ruquest.js'

const Api = {
  //login
  checkCode: '/account/checkCode',
  login: '/account/login',
  logout: '/account/logout',
  // user
  getLoadDataList: '/user/loadDataList',
  getaddOrUpdateBatch: '/user/addOrUpdateBatch',
  getRoleName: '/user/loadUserRole',
  getRoleByUserId: '/user/getRoleByUserId',
  getdeleteUserByUserId: '/user/deleteUserInfoByUserId',
  updateUserRelation: '/user/updateUserRelation',
  getResetPassword: '/user/getResetPassword',

  // category
  getLoadCategoryInfo: '/category/loadCategoryInfo',
  getSaveCategory: '/category/saveCategory',
  getDelCategory: '/category/delCategory',
  getChangeSort: '/category/changeSort',

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

// 根据userId获取用户角色
const getRoleByUserId = async (userId) => {
  let res = await Ruquest({
    url: Api.getRoleByUserId,
    params: { userId },
  })
  if (!res) return
  return res.data.roleId
}
export { Api, uploadImage, getRoleByUserId }
