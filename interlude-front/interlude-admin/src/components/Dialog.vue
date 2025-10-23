<template>
  <div>
    <el-dialog
      :title="title"
      :draggable="true"
      :model-value="show"
      :close-on-click-modal="false"
      :show-close="showClose"
      class="cust-dialog"
      :top="top + 'px'"
      :width="width"
      @close="closeDialog"
    >
      <div class="dialog-body" :style="{ 'max-height': MaxHeight + 'px', padding: padding + 'px' }">
        <slot></slot>
      </div>
      <template v-if="(buttons && buttons.length > 0) || showCancel">
        <div class="dialog-footer">
          <el-button link @click="closeDialog" v-if="showCancel">取消</el-button>
          <el-button
            size="large"
            v-for="btn in buttons"
            :type="btn.type || 'primary'"
            @click="btn.click"
            >{{ btn.text }}</el-button
          >
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  title: {
    type: String,
  },
  show: {
    type: Boolean,
    default: false,
  },
  showClose: {
    type: Boolean,
    default: true,
  },
  top: {
    type: Number,
    default: 50,
  },
  width: {
    type: String,
    default: '30%',
  },
  padding: {
    type: Number,
    default: 15,
  },
  buttons: {
    type: Array,
  },
  showCancel: {
    type: Boolean,
    default: true,
  },
})

const MaxHeight = window.innerWidth - props.top - 100

const emit = defineEmits()
const closeDialog = (): void => {
  emit('closeDialog')
}
</script>

<style lang="scss" scoped>
.cust-dialog {
  margin: 30px auto 10px !important;
  .el-dialog_body {
    padding: 0px;
  }
  .dialog-body {
    border-top: 1px solid #ddd;
    border-bottom: 1px solid #ddd;
    min-height: 80px;
    overflow: auto;
  }
  .dialog-footer {
    text-align: right;
    padding: 0px 20px;
    margin-top: 15px;
  }
}
</style>
