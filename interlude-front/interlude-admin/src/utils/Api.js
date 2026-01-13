import Ruquest from './Ruquest.js'

const Api = {
  //login
  checkCode: '/account/checkCode',
  login: '/account/login',
  logout: '/account/logout',
  autoLogin: '/account/autoLogin',
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
  getCategoryInfoById: '/category/loadCategoryInfoById',
  getSaveCategory: '/category/saveCategory',
  getDelCategory: '/category/delCategory',
  getChangeSort: '/category/changeSort',

  //file
  uploadImage: '/file/uploadImage',
  sourcePath: '/api/file/getResource?sourceName=',
  preUploadVideo: '/file/preUploadVideo',
  uploadVideo: '/file/uploadVideo',
  delUploadVideo: '/file/delUploadVideo',

  //系统
  getSysSetting: '/sysSetting/loadSysSetting',

  //草稿
  getDraftInfoByUserId: '/draft/getDraftInfoByUserId',
  updateDraftInfo: '/draft/updateDraftInfo',
  updateVideoName: '/draft/updateVideoName',

  // 投稿
  postVideo: '/vp/postVideo',

  // video
  getLoadVideoInfo: '/videoInfo/loadVideoInfo',
  getDelVideoInfo: '/videoInfo/delVideoInfo',
  getVideoAuditInfo: '/audit/loadVideoAuditInfo',
  updateVideoAudit: '/audit/updateVideoAudit',
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

// 上传封面
// const uploadImage = async (file, createThumbnail = true) => {
//   let result = await Ruquest({
//     url: Api.uploadImage,
//     params: { file, createThumbnail },
//   })
//   if (!result) {
//     return
//   }
//   return result.data
// }

// 根据userId获取用户角色
const getRoleByUserId = async (userId) => {
  let res = await Ruquest({
    url: Api.getRoleByUserId,
    params: { userId },
  })
  if (!res) return
  return res.data.roleId
}

// 提供 pCategoryId 和 categoryId
const getCategoryInfoById = async (pCategoryId, categoryId) => {
  let res = await Ruquest({
    url: Api.getCategoryInfoById,
    params: {
      pCategoryId,
      categoryId,
    },
  })
  if (!res) return
  return res.data
}

export { Api, uploadImage, getRoleByUserId, getCategoryInfoById }
