<template>
  <div class="user-panel">
    <div class="user-search">
      <el-card :class="['user-search-card', isShrink ? 'user-shrink-show' : 'user-shrink']">
        <el-form
          :model="formData"
          ref="formDataRef"
          class="form-style"
          label-width="100px"
          @submit.prevent
        >
          <div class="show-form">
            <!--input输入-->
            <el-form-item class="input" label="用户名" prop="nickNameFuzzy">
              <el-input
                clearable
                placeholder="请输入用户名"
                v-model.trim="formData.nickNameFuzzy"
              ></el-input>
            </el-form-item>
            <el-form-item class="input" label="邮箱/手机号" prop="account">
              <el-input
                clearable
                placeholder="请输入邮箱/手机号"
                v-model.trim="formData.account"
              ></el-input>
            </el-form-item>
            <el-form-item label="状态" prop="enabled">
              <el-select style="width: 140px" placeholder="请选择" v-model="formData.enabled">
                <el-option :value="2" label="全部"></el-option>
                <el-option :value="0" label="未启用"></el-option>
                <el-option :value="1" label="已启用"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="" prop="roleName">
              <el-checkbox-group v-model="formData.roleName" size="small">
                <template v-for="item in RoleNameList">
                  <el-checkbox :label="item.roleName" :value="item.roleId" border />
                </template>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="loadUserInfoList()"
                class="search-btn mainbtn"
                style="width: 100px"
              >
                <i class="iconfont icon-sousuo"></i>
              </el-button>
              <el-button type="primary" @click="cheanFrom()" class="search-btn">
                <i class="iconfont icon-rubber"></i>
              </el-button>
              <el-button
                type="primary"
                @click="changeShrink()"
                class="search-btn"
                style="width: 35px"
              >
                <i class="iconfont icon-kuoda"></i>
              </el-button>
            </el-form-item>
          </div>

          <div class="hidden-from">
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="formData.sex">
                <el-radio :label="1" @click.native.prevent="handleRadioClick(1)">男</el-radio>
                <el-radio :label="0" @click.native.prevent="handleRadioClick(0)">女</el-radio>
                <el-radio :label="2" @click.native.prevent="handleRadioClick(2)">其他</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item class="input" label="学校" prop="schoolFuzzy">
              <el-input
                clearable
                placeholder="请输入学校"
                v-model.trim="formData.schoolFuzzy"
              ></el-input>
            </el-form-item>
            <el-form-item class="input" label="地址" prop="addressFuzzy">
              <el-input
                clearable
                placeholder="请输入地址"
                v-model.trim="formData.addressFuzzy"
              ></el-input>
            </el-form-item>
            <el-form-item label="创建日期" prop="createTimeArray">
              <el-date-picker
                v-model="formData.createTimeArray"
                type="dates"
                placeholder="请输入创建日期"
              />
            </el-form-item>
          </div>
        </el-form>
      </el-card>
    </div>
    <div class="userCenter">
      <h2 class="user-title">用户列表</h2>
      <div class="user-controll">
        <div class="contrual-btn jia" @click="showEdit({}, 0)">
          <i class="iconfont icon-jia"></i>
        </div>
        <div
          :class="['contrual-btn jia', tableData.length <= 0 ? 'disable' : '']"
          @click="exportClick()"
        >
          <i class="iconfont icon-xiazai"></i>
        </div>
        <div
          :class="['contrual-btn jia', tableData.length <= 0 ? 'disable' : '']"
          @click="changeSort()"
        >
          <i class="iconfont icon-paixu"></i>
        </div>
        <div
          :class="['contrual-btn', 'del', rowSelectedList.length <= 0 ? 'disable' : '']"
          @click="deleteSelectedRows"
        >
          <i class="iconfont icon-lajitong"></i>
        </div>
      </div>
    </div>
    <div class="user-table">
      <el-card class="user-table-card">
        <Table
          ref="dataTableRef"
          :columns="columns"
          :dataSource="tableData"
          :fetch="loadUserInfoList"
          :initFetch="true"
          :options="tableOptions"
          :MaxHeight="MaxHeight"
          @rowSelected="rowSelected"
        >
          <template #avatar="{ index, row }">
            <div class="cover">
              <Avatar :avatar="row.avatar" :lazy="false" :width="35" :preview="true"></Avatar>
            </div>
          </template>
          <template #sex="{ index, row }">
            <span>{{ row.sex == 1 ? '男' : row.sex == 0 ? '女' : '其他' }}</span>
          </template>
          <template #phone="{ index, row }">
            <span>{{ row.phone }}</span>
          </template>
          <template #email="{ index, row }">
            <span>{{ row.email }}</span>
          </template>
          <template #enabled="{ index, row }">
            <div class="enabled">
              <span :class="['comment', row.enabled == 1 ? 'useing' : 'stoping']">{{
                row.enabled == 1 ? '已启用' : '已停用'
              }}</span>
            </div>
          </template>
          <template #slotOperation="{ index, row }">
            <div class="row-op-panel">
              <a class="a-link" v-if="row.roleId != 3" @click="stopEnable(row)">
                {{ row.enabled == 0 ? '启用' : '停用' }}
              </a>
              <a class="a-link" v-if="row.roleId != 3" @click="showEdit(row, 1)">修改</a>
              <a class="a-link" @click="showDetail(row)">详情</a>
              <a class="a-link" v-if="row.roleId != 3" @click="resetPassword(row)">重置密码</a>
              <a class="a-link" v-if="row.roleId != 3" @click="showRoleDialog(row)">分配角色</a>
              <a class="a-link" @click="deleteUser(row)" v-if="row.roleId != 3">删除</a>
            </div>
          </template>
        </Table>
      </el-card>
    </div>
  </div>
  <!-- 用户信息编辑弹窗 -->
  <UserInfoEdit ref="userInfoRef" @reload="loadUserInfoList"></UserInfoEdit>
  <UserInfoDetail ref="userInfoDetailRef"></UserInfoDetail>
  <Dialog
    :show="dialogConfig.show"
    :title="dialogConfig.title"
    :buttons="dialogConfig.buttons"
    width="400px"
    :showCancel="false"
    @closeDialog="dialogConfig.show = false"
  >
    <el-radio-group v-model="RoleSelected.roleId">
      <!-- <el-radio :label="3">超级管理员</el-radio> -->
      <el-radio :label="2">管理员</el-radio>
      <el-radio :label="1">用户</el-radio>
    </el-radio-group>
  </Dialog>
</template>

<script setup lang="ts">
import UserInfoEdit from './UserInfoEdit.vue'
import UserInfoDetail from './UserInfoDatil.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ref, getCurrentInstance, Ref, watch, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRoleByUserId } from '@/utils/Api.js'

const { proxy } = getCurrentInstance()

const route = useRoute()
const router = useRouter()

import { useBreadcrumbListStore } from '../../../stores/breadcrumb'
const breadcrumbStore: any = useBreadcrumbListStore()

const formData = ref<Record<string, any>>({}) // 键值对象
const formDataRef = ref()

const tableOptions = {
  exHeight: 0,
  showIndex: true,
  selectType: 'checkbox',
  tableHeight: 550,
}

const columns = [
  {
    label: '头像',
    prop: 'avatar',
    scopedSlots: 'avatar',
    width: 80,
  },
  {
    label: '用户名',
    prop: 'nickName',
    width: 100,
  },
  {
    label: '性别',
    prop: 'sex',
    width: 85,
    scopedSlots: 'sex',
    align: 'center',
  },
  {
    label: '手机号',
    prop: 'phone',
    scopedSlots: 'phone',
    width: 150,
    align: 'center',
  },
  {
    label: '邮箱',
    prop: 'email',
    scopedSlots: 'email',
    width: 250,
    align: 'center',
  },
  {
    label: '状态',
    prop: 'enabled',
    scopedSlots: 'enabled',
    align: 'center',
    width: 100,
  },
  {
    label: '最后登录时间',
    prop: 'updateTime',
    align: 'center',
    width: 290,
  },
  {
    label: '操作',
    prop: 'type',
    scopedSlots: 'slotOperation',
    align: 'center',
    width: 450,
  },
]

onMounted(() => {
  getRoleName()
})

// 用户信息表格数据
const tableData = ref([])

const flag = ref('asc')

// 加载用户信息列表
const loadUserInfoList = async (): Promise<void> => {
  let params: Record<string, any> = {
    pageNo: tableData.value.pageNo,
    pageSize: tableData.value.pageSize,
  }
  formData.value.isCreatTimeDesc = flag.value
  Object.assign(params, formData.value)
  if (params.enabled == 2) params.enabled = null
  let result = await proxy.$Request({
    url: proxy.$Api.getLoadDataList,
    params,
  })

  if (!result) {
    return
  }
  tableData.value = result.data
}

const MaxHeight: Ref<number> = ref(550)

const isShrink: Ref<boolean> = ref(false)

const changeShrink = (): void => {
  isShrink.value = !isShrink.value
  if (isShrink.value) {
    MaxHeight.value = 500
  } else {
    MaxHeight.value = 550
  }
}

const cheanFrom = () => {
  formData.value = ref<Record<string, any>>({})
  loadUserInfoList()
}

const RoleNameList = ref([])

// 获取角色列表
const getRoleName = async (): Promise<void> => {
  let result = await proxy.$Request({
    url: proxy.$Api.getRoleName,
  })
  if (!result) return
  RoleNameList.value = result.data
}

// 单选按钮点击处理函数
const handleRadioClick = (value) => {
  // 如果点击的是当前已选中的值，则清空，否则设置为新值
  formData.value.sex = value === formData.value.sex ? null : value
}

const userInfoRef = ref()
const userInfoDetailRef = ref()

// 显示编辑用户信息弹窗
const showEdit = (data: Object, type: number) => {
  userInfoRef.value.showEdit(data, type)
}

// 启用/停用用户
const stopEnable = (data: Object) => {
  const enableMsg = data.enabled === 0 ? '启用' : '停用'
  ElMessageBox.confirm(`是否${enableMsg}此账号`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    let params: Record<string, any> = {
      userId: data.userId,
      enabled: data.enabled === 0 ? 1 : 0,
    }
    const res = await proxy.$Request({
      url: proxy.$Api.getaddOrUpdateBatch,
      params,
    })
    if (!res) {
      return
    }
    loadUserInfoList()
    ElMessage({
      type: 'success',
      message: `${enableMsg}成功!`,
    })
  })
}

// 删除用户
const deleteUser = (data: Object) => {
  ElMessageBox.confirm(`是否删除此账号`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    let params: Record<string, any> = {
      userIds: data.userId,
    }
    const res = await proxy.$Request({
      url: proxy.$Api.getdeleteUserByUserId,
      params,
    })
    if (!res) {
      return
    }
    loadUserInfoList()
    ElMessage({
      type: 'success',
      message: `删除成功!`,
    })
  })
}

// 重置密码
const resetPassword = (data: Object) => {
  if (data.enabled == 0) {
    proxy.$Message.warning('该用户未启用，无法重置密码')
    return
  }
  ElMessageBox.confirm(`是否重置此账号密码`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    let params: Record<string, any> = {
      userId: data.userId,
    }
    const res = await proxy.$Request({
      url: proxy.$Api.getResetPassword,
      params,
    })
    if (!res) {
      return
    }
    ElMessageBox.alert(`密码重置成功: 密码为${res.data} , 请及时复制`, '提示', {
      confirmButtonText: 'OK',
      callback: (action: Action) => {},
    })
    loadUserInfoList()
  })
}
// 分配角色
const RoleSelected: Object = ref({
  userId: 0,
  roleId: 0,
})

const dialogConfig = ref({
  show: false,
  title: '分配角色',
  buttons: [
    {
      type: 'primary',
      text: '确定',
      click: async (e: any) => {
        let res = await proxy.$Request({
          url: proxy.$Api.updateUserRelation,
          params: {
            userId: RoleSelected.value.userId,
            roleId: RoleSelected.value.roleId,
          },
        })
        if (!res) {
          return
        }
        dialogConfig.value.show = false
        proxy.$Message.success('分配成功')
        loadUserInfoList()
      },
    },
  ],
})

// 显示分配角色弹窗
const showRoleDialog = (data: Object) => {
  if (data.enabled == 0) {
    proxy.$Message.warning('该用户未启用，无法分配角色')
    return
  }
  RoleSelected.value.userId = data.userId
  RoleSelected.value.roleId = data.roleId
  dialogConfig.value.show = true
}

// 显示用户详情弹窗
const showDetail = (data: Object) => {
  userInfoDetailRef.value.showEdit(data)
}

const dataTableRef = ref()

// 导出用户信息
const exportClick = (): void => {
  dataTableRef.value.exportClick()
  proxy.$Message.success('导出成功')
}

// 排序切换
const changeSort = (): void => {
  if (flag.value === 'desc') {
    flag.value = 'asc'
  } else {
    flag.value = 'desc'
  }
  loadUserInfoList()
}

const rowSelectedList = ref([])

// 选中行数据
const rowSelected = async (selectedRows: Array<Object>): Promise<void> => {
  for (let item of selectedRows) {
    let roleId = await getRoleByUserId(item.userId)
    if (roleId == 3) {
      proxy.$Message.warning('超级管理员不能被批量操作')
      loadUserInfoList()
      return
    }
  }
  rowSelectedList.value = selectedRows
}

// 删除选中行
const deleteSelectedRows = (): void => {
  if (rowSelectedList.value.length <= 0) {
    return
  }
  ElMessageBox.confirm(`是否删除选中账号`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    let userIds = rowSelectedList.value.map((item: any) => item.userId).join(',')
    let params: Record<string, any> = {
      userIds: userIds,
    }
    const res = await proxy.$Request({
      url: proxy.$Api.getdeleteUserByUserId,
      params,
    })
    if (!res) {
      return
    }
    loadUserInfoList()
    proxy.$Message.success('删除成功')
  })
}
</script>

<style lang="scss" scoped>
.user-panel {
  width: 100%;
  margin-top: 5px;
  display: flex;
  overflow: hidden;
  flex-direction: column;
  height: calc(100vh - 165px); /* 或者使用 calc(100vh - 某个固定值) 来减去顶部导航等高度 */
  .user-search {
    .user-shrink {
      height: 80px;
    }
    .user-shrink-show {
      height: 130px;
    }
    .user-search-card {
      width: 100%;
      transition: width 1s ease 1s;
      .form-style {
        display: flex;
        flex-direction: column;
        margin-top: 5px;
        .show-form {
          display: flex;
          justify-content: space-around;
        }
        .hidden-from {
          margin-top: 5px;
          display: flex;
        }
        .input {
          width: 280px;
        }
        .search-btn {
          background-color: #fff;
          border: 1px solid rgb(216, 216, 216);
          box-shadow: 3px 3px 2px rgba(216, 216, 216, 0.6);
          .iconfont {
            color: var(--blue);
          }
        }
        .search-btn:hover {
          box-shadow: none;
          background-color: var(--blue);
          .iconfont {
            color: #fff;
          }
        }
      }
    }
  }
  .userCenter {
    display: flex;
    justify-content: space-between;
    .user-title {
      color: #0a54c9;
      margin: 25px 0px 10px 5px;
    }
    .user-controll {
      display: flex;
      .contrual-btn {
        cursor: pointer;
        margin: 20px 10px;
        padding: 8px 16px;
        background-color: #fff;
        border-radius: 5px;
        box-shadow: 3px 3px 2px rgba(216, 216, 216, 0.6);
        color: var(--blue);
      }
      .jia:hover {
        background-color: var(--blue);
        color: #fff;
        box-shadow: none;
      }
      .del:hover {
        background-color: rgb(206, 58, 13);
        color: rgb(255, 255, 255);
        box-shadow: none;
      }
    }
  }
  .user-table {
    flex: 1;
    .user-table-card {
      height: 100%;
      .enabled {
        .comment {
          padding: 2px 5px;
          cursor: pointer;
          border-radius: 5px;
        }
        .useing {
          background-color: #7073f4;
          color: #fff;
        }
        .stoping {
          background-color: rgb(219, 93, 8);
          color: #fff;
        }
      }
      .row-op-panel {
        display: flex;
        justify-content: space-around;
        .a-link {
          cursor: pointer;
          color: #7169c5;
        }
        .a-link:hover {
          color: #52b8e7;
        }
      }
    }
  }
}
</style>
