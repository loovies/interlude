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
        <div :class="['contrual-btn jia', tableData.length <= 0 ? 'disable' : '']">
          <i class="iconfont icon-xiazai"></i>
        </div>
        <div :class="['contrual-btn jia', tableData.length <= 0 ? 'disable' : '']">
          <i class="iconfont icon-paixu"></i>
        </div>
        <div :class="['contrual-btn', 'del', 'disable']">
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
        >
          <template #avatar="{ index, row }">
            <div class="cover">
              <Avatar :avatar="row.avatar" :lazy="false" :width="35"></Avatar>
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
              <a class="a-link">停用</a>
              <a class="a-link" @click="showEdit(row, 1)">修改</a>
              <a class="a-link">详情</a>
              <a class="a-link">重置密码</a>
              <a class="a-link">分配角色</a>
              <a class="a-link">删除</a>
            </div>
          </template>
        </Table>
      </el-card>
    </div>
  </div>
  <UserInfoEdit ref="userInfoRef" @reload="loadUserInfoList"></UserInfoEdit>
</template>

<script setup lang="ts">
import UserInfoEdit from './UserInfoEdit.vue'

import { ref, getCurrentInstance, Ref, watch, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

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

const tableData = ref([])

const loadUserInfoList = async (): Promise<void> => {
  let params: Record<string, any> = {}
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

const showEdit = (data: Object, type: number) => {
  userInfoRef.value.showEdit(data, type)
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
