<template>
  <div class="category-content">
    <div class="tree-content">
      <el-input
        v-model="filterText"
        class="tree-search"
        placeholder="请输入分类名"
        :suffix-icon="Search"
      />

      <el-tree
        ref="treeRef"
        style="max-width: 600px"
        class="filter-tree"
        :data="data"
        :props="defaultProps"
        default-expand-all
        :filter-node-method="filterNode"
        @node-click="nodeClick"
      />
    </div>
    <div class="category-list">
      <el-card class="box-card">
        <template #header>
          <div class="header">
            <div class="header-title">一级分类</div>
            <div class="header-action" @click="showEdit({}, 0)">新增分类</div>
          </div>
        </template>
        <Table
          ref="tableRef"
          :dataSource="tableData"
          :columns="columns"
          :fetch="loadCategoryInfo"
          :initFetch="true"
          :options="tableOptions"
          :MaxHeight="700"
          :showPagination="false"
          @rowClick="handleRowClick"
        >
          <template #slotOperation="{ index, row }">
            <div class="row-op-panel">
              <a class="a-link" href="javascript:void(0)" @click="showEdit(row, 0)">修改</a>
              <el-divider direction="vertical"></el-divider>
              <a class="a-link" href="javascript:void(0)" @click="delCategory(row)">删除</a>
              <el-divider direction="vertical"></el-divider>
              <a
                :class="[index == 0 ? 'disable' : 'a-link']"
                href="javascript:void(0)"
                @click="changeSort(0, index, 'up')"
                >上移</a
              >
              <el-divider direction="vertical"></el-divider>
              <a
                :class="[index == tableData.list.length - 1 ? 'disable' : 'a-link']"
                href="javascript:void(0)"
                @click="changeSort(0, index, 'down')"
                >下移</a
              >
            </div>
          </template>
        </Table>
      </el-card>
    </div>
    <div class="children-list">
      <el-card class="box-card">
        <template #header>
          <div class="header">
            <div class="header-title">二级分类</div>
            <div class="header-action" @click="showEdit({}, 1)">新增二级分类</div>
          </div>
        </template>
        <Table
          ref="tableRef"
          :dataSource="tableChildrenData"
          :columns="columnsChildren"
          :options="tableOptionsChildren"
          :MaxHeight="700"
          :showPagination="false"
        >
          <template #slotOperation="{ index, row }">
            <div class="row-op-panel">
              <a class="a-link" href="javascript:void(0)" @click="showEdit(row, 1)">修改</a>
              <el-divider direction="vertical"></el-divider>
              <a class="a-link" href="javascript:void(0)" @click="delCategory(row)">删除</a>
              <el-divider direction="vertical"></el-divider>
              <a
                :class="[index == 0 ? 'disable' : 'a-link']"
                href="javascript:void(0)"
                @click="changeSort(row.pCategoryId, index, 'up')"
                >上移</a
              >
              <el-divider direction="vertical"></el-divider>
              <a
                :class="[index == currentRowCategory.children.length - 1 ? 'disable' : 'a-link']"
                href="javascript:void(0)"
                @click="changeSort(row.pCategoryId, index, 'down')"
                >下移</a
              >
            </div>
          </template>
        </Table>
      </el-card>
    </div>
  </div>
  <CategoryEdit ref="categoryEditRef" @reload="loadCategoryInfo"></CategoryEdit>
</template>

<script setup lang="ts">
import CategoryEdit from './CategoryEdit.vue'
import { ref, watch, onMounted, getCurrentInstance } from 'vue'
const { proxy } = getCurrentInstance()
import { Search } from '@element-plus/icons-vue'
import type { FilterNodeMethodFunction, TreeInstance } from 'element-plus'

interface Tree {
  [key: string]: any
}

const filterText = ref('')
const treeRef = ref<TreeInstance>()

const defaultProps = {
  children: 'children',
  label: 'categoryName',
}

watch(filterText, (val) => {
  treeRef.value!.filter(val)
})

// 过滤节点方法
const filterNode: FilterNodeMethodFunction = (value: string, data: Tree) => {
  if (!value) return true
  return data.categoryName.includes(value)
}

let data: Tree[] = ref([
  {
    id: 1,
    categoryName: '分类',
    children: [],
  },
])

const columns = [
  {
    label: '分类编号',
    prop: 'categoryCode',
    align: 'center',
    width: '200px',
  },
  {
    label: '分类名称',
    prop: 'categoryName',
    width: '180px',
    align: 'center',
  },
  {
    label: '操作',
    prop: 'action',
    width: '300px',
    align: 'center',
    scopedSlots: 'slotOperation',
  },
]

const columnsChildren = [
  {
    label: '分类编号',
    prop: 'categoryCode',
    align: 'center',
    width: '140px',
  },
  {
    label: '分类名称',
    prop: 'categoryName',
    width: '130px',
    align: 'center',
  },
  {
    label: '操作',
    prop: 'action',
    width: '260px',
    align: 'center',
    scopedSlots: 'slotOperation',
  },
]

let tableData = ref([])
let tableChildrenData = ref([])

let tableOptions = {
  exHeight: 0,
  showIndex: true,
  tableHeight: 720,
}
let tableOptionsChildren = {
  exHeight: 0,
  showIndex: false,
  tableHeight: 720,
}

let currentRowCategory = ref(null)

// 获取分类信息
const loadCategoryInfo = async (): Promise<void> => {
  let res = await proxy.$Request({
    url: proxy.$Api.getLoadCategoryInfo,
  })
  if (!res) return
  data.value[0].children = res.data
  tableData.value.list = res.data
  if (currentRowCategory.value == null && res.data.length > 0) {
    currentRowCategory.value = res.data[0]
    tableChildrenData.value.list = res.data[0].children || []
  } else {
    currentRowCategory.value = res.data.find((item) => {
      return item.categoryId == currentRowCategory.value?.categoryId
    })
    tableChildrenData.value.list = currentRowCategory.value?.children || []
  }
}

const handleRowClick = (row: any) => {
  currentRowCategory.value = row
  tableChildrenData.value.list = row.children || []
}

const categoryEditRef = ref()

const showEdit = (data: any, type: number) => {
  if (type === 1 && currentRowCategory.value == null) {
    proxy.$Message.warning('请先选择一级分类')
    return
  }
  // type 0 一级分类 1 二级分类
  if (type === 0) {
    data.pCategoryId = 0
    // 二级分类
    // 判断data的长度,表示是给二级分类的创建功能添加父级的分类id
    // 如果data有值表示是修改功能,不需要设置pCategoryId
  } else if (type === 1 && Object.keys(data).length === 0) {
    data.pCategoryId = currentRowCategory.value.categoryId
  }
  categoryEditRef.value.showEdit(Object.assign({}, data))
}

// 改变排序
const changeSort = async (pCategoryId: number, index: number, type: string): Promise<void> => {
  // type 0 一级分类 1 二级分类
  let dataList = pCategoryId == 0 ? tableData.value.list : currentRowCategory.value.children
  if ((type == 'down' && index == dataList.length - 1) || (type == 'up' && index == 0)) {
    return
  }

  let temp = dataList[index]
  let number = type == 'down' ? 1 : -1
  dataList.splice(index, 1)
  dataList.splice(index + number, 0, temp)
  let categoryIds = []
  dataList.forEach((item: any) => {
    categoryIds.push(item.categoryId)
  })
  let result = await proxy.$Request({
    url: proxy.$Api.getChangeSort,
    params: {
      pCategoryId,
      categoryIds: categoryIds.join(','),
    },
  })
  if (!result) {
    return
  }
  proxy.$Message.success('操作成功')
  loadCategoryInfo()
}

const delCategory = (data) => {
  proxy.$Confirm({
    message: `确定要删除[${data.categoryName}] 吗?`,
    okfun: async () => {
      let result = await proxy.$Request({
        url: proxy.$Api.getDelCategory,
        params: {
          categoryId: data.categoryId,
        },
      })
      if (!result) {
        return
      }
      proxy.$Message.success('操作成功')
      // 清空选中
      if (currentRowCategory.value.categoryId == data.categoryId) {
        currentRowCategory.value = null
      }
      loadCategoryInfo()
    },
  })
}

// 点击树状分类,切换表格内容
const nodeClick = (data, node) => {
  if (node.data.pCategoryId == 0) {
    currentRowCategory.value = node.data
  } else {
    tableData.value.list.forEach((item) => {
      if (item.categoryId == node.data.pCategoryId) {
        currentRowCategory.value = item
      }
    })
  }
  loadCategoryInfo()
}
</script>

<style lang="scss" scoped>
.category-content {
  display: flex;
  justify-content: space-around;
  margin-top: 15px;
  .tree-content {
    width: 220px;
    .tree-search {
      margin-bottom: 10px;
    }
    .filter-tree {
      height: calc(100vh - 230px);
      overflow: auto;
      background-color: #f5f7f9;
    }
  }
  .category-list {
    width: 800px;
    height: calc(100vh - 180px);
  }
  .children-list {
    width: 550px;
    height: calc(100vh - 180px);
  }
}
.box-card {
  height: 100%;
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .header-title {
      font-size: 18px;
      font-weight: bold;
    }
    .header-action {
      font-size: 14px;
      color: #409eff;
      cursor: pointer;
    }
  }
}
a {
  text-decoration: none;
}
</style>
