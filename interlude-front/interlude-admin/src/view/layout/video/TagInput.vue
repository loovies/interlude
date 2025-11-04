<template>
  <div class="tag-input-panel">
    <div class="tag-list">
      <el-tag
        type="primary"
        class="tag-item"
        v-for="tag in modelValue"
        :key="tag"
        closable
        :disable-transitions="false"
        @close="handleDeltag(tag)"
        >{{ tag }}</el-tag
      >
    </div>
    <div class="tag-input">
      <!--input输入-->
      <el-input
        placeholder="按下回车键Enter来创建标签"
        v-model="inputValue"
        show-word-limit
        resize="none"
        :maxlength="15"
        @keyup.enter="changeInput"
      ></el-input>
    </div>
    <div class="info">最多还可以输入 {{ 10 - modelValue.length }}个标签</div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, nextTick, provide } from 'vue'
const { proxy } = getCurrentInstance()
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const props = defineProps({
  modelValue: {
    type: Array,
    default: [],
  },
})

const handleDeltag = (tag) => {
  props.modelValue.splice(props.modelValue.indexOf(tag), 1)
}

const inputValue = ref()
const changeInput = () => {
  if (!inputValue.value) {
    return
  }
  if (props.modelValue.length >= 10) {
    proxy.Message.warning('最多设置十个标签')
    inputValue.value = ''
    return
  }
  let flag = true
  if (props.modelValue.length != 0) {
    for (let item in props.modelValue) {
      if (props.modelValue[item] == inputValue.value) {
        flag = false
        break
      } else {
        flag = true
      }
    }
  }
  if (flag) {
    props.modelValue.push(inputValue.value)
  } else {
    proxy.Message.warning('设置的标签重复了')
  }
  inputValue.value = ''
}
</script>

<style lang="scss" scoped>
.tag-input-panel {
  width: 100%;
  border: 1px solid #ddd;
  display: flex;
  flex-wrap: wrap;
  border-radius: 5px;
  padding: 0px 10px;
  .tag-list {
    .tag-item {
      margin-right: 10px;
    }
  }
  .tag-input {
    flex: 1;
    min-width: 150px;
    margin-right: 10px;
    :deep(.el-input__wrapper) {
      box-shadow: none;
      padding: 0px;
    }
  }
  .info {
    color: #939393;
    font-size: 12px;
  }
}
</style>
