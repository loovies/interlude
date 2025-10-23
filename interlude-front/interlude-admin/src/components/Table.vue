<template>
  <div>
    <el-table
      :data="dataSource.list || []"
      ref="dataTable"
      :height="tableHeight"
      :stripe="options.stripe"
      :border="options.border"
      header-row-class-name="table-header-row"
      highlight-current-row
      :max-height="MaxHeight"
      @row-click="handleRowClick"
      class="table-style"
      @selection-change="headleSelectionChange"
    >
      <!-- selection 选择器 -->
      <el-table-column
        v-if="options.selectType && options.selectType == 'checkbox'"
        type="selection"
        width="50"
        align="center"
      ></el-table-column>
      <!-- 序号 -->
      <el-table-column
        v-if="options.showIndex"
        label="ID"
        type="index"
        width="80"
        align="center"
      ></el-table-column>
      <!-- 数据列 -->
      <template v-for="(column, index) in columns">
        <template v-if="column.scopedSlots">
          <el-table-column
            :key="index"
            :prop="column.prop"
            :label="column.label"
            :align="column.align || 'left'"
            :width="column.width"
          >
            <template #default="scope">
              <slot :name="column.scopedSlots" :index="scope.$index" :row="scope.row"></slot>
            </template>
          </el-table-column>
        </template>
        <template v-else>
          <el-table-column
            :label="column.label"
            :align="column.align || 'left'"
            :prop="column.prop"
            :width="column.width"
            :fixed="column.fixed"
          ></el-table-column>
        </template>
      </template>
    </el-table>
    <!-- 分页 -->
    <div class="pageination" v-if="showPagination">
      <el-pagination
        v-if="dataSource.totalCount"
        background
        :total="dataSource.totalCount"
        :page-sizes="[15, 30, 50, 100]"
        :page-size="dataSource.pageSize"
        :current-page.sync="dataSource.pageNo"
        :layout="layout"
        @size-change="handlePageSizeChange"
        @current-change="handlePageNoChange"
        style="text-align: center"
      ></el-pagination>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  dataSource: Object,
  options: {
    type: Object,
    default: {
      exHeight: 0,
      showIndex: false,
      stripe: false,
      border: false,
    },
  },
  MaxHeight: {
    type: Number,
    default: 700,
  },
  columns: {
    type: Array,
  },
  showPagination: {
    type: Boolean,
    default: true,
  },
  showPageSize: {
    type: Boolean,
    default: true,
  },
  fetch: Function, // 获取数据的函数
  initFetch: {
    // 是否获取函数
    type: Boolean,
    default: true,
  },
})

// 顶部距离
const topHeight = 300

const tableHeight = ref(
  props.options.tableHeight
    ? props.options.tableHeight
    : window.innerHeight - topHeight - props.options.exHeight
)

const layout = computed(() => {
  return `total, ${props.showPageSize ? 'sizes' : ''}, prev, pager, next, jumper`
})

// 初始化
const init = () => {
  if (props.initFetch && props.fetch) {
    props.fetch()
  }
}
init()

const dataTable = ref()
//清除选中
const clearSelection = () => {
  dataTable.value.clearSelection()
}

//设置行选中
const setCurrentRow = (rowKey, rowValue) => {
  let row = props.dataSource.list.find((item) => {
    return item[rowKey] === rowValue
  })
  dataTable.value.setCurrentRow(row)
}

// 将子组件暴露出去, 否则父组件无法调用
defineExpose({ setCurrentRow, clearSelection })

// 行点击
const handleRowClick = (row) => {
  emit('rowClick', row)
}

// 多选
const headleSelectionChange = (row) => {
  emit('rowSelected', row)
}

// 切换每页大小
const handlePageSizeChange = (size) => {
  props.dataSource.pageSize = size
  props.dataSource.pageNo = 1
  // 获取数据
  props.fetch()
}

// 切换页码
const handlePageNoChange = (pageNo) => {
  props.dataSource.pageNo = pageNo
  // 获取数据
  props.fetch()
}
</script>

<style lang="scss" scoped>
.pageination {
  padding-top: 10px;
  padding-right: 10px;
  margin-left: 35%;
}
:deep(.el-table__cell) {
  padding: 4px 0px;
}

:deep(.el-table__row) {
  height: 50px;
}
</style>
