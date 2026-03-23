# Interlude Web - Agent 开发指南

本文档为 Interlude Web 代码库中的 Agent 提供开发规范和约定。

## 项目概述

基于 Vue 3、Vite、Pinia 和 Vue Router 构建的视频流媒体平台前端。

## 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Vue 3（组合式 API + `<script setup>`）|
| 构建工具 | Vite 7.x |
| 状态管理 | Pinia 3.x |
| 路由 | Vue Router 5.x |
| 视频播放器 | XGPlayer 3.x |
| 样式 | SCSS + CSS 变量 |
| 语言 | TypeScript |

## 可用命令

```bash
# 开发
npm run dev              # 启动开发服务器（端口 3001）

# 构建
npm run build            # 生产构建（包含类型检查）
npm run build-only       # 仅 Vite 构建（无类型检查）
npm run preview          # 本地预览生产构建

# 类型检查
npm run type-check       # 运行 vue-tsc --build 进行 TypeScript 验证
```

**注意**：目前未配置测试框架。

## 项目结构

```
src/
├── App.vue                    # 根组件
├── main.ts                    # 入口文件
├── assets/
│   ├── css/
│   │   ├── base.css           # 全局基础样式
│   │   ├── light-theme.css    # 浅色主题 CSS 变量
│   │   └── dark-theme.css     # 深色主题 CSS 变量
│   ├── images/                # 静态图片
│   └── js/                    # JavaScript 工具函数
├── components/
│   ├── SearchBar.vue
│   ├── Sidebar.vue
│   ├── ThemeToggle.vue
│   └── video/
│       ├── VideoFeed.vue      # 视频列表（滚动吸附）
│       └── VideoItem.vue      # 单个视频播放器
├── router/
│   └── index.ts               # Vue Router 配置
├── stores/
│   ├── counter.ts             # Pinia store 示例
│   └── theme.ts               # 主题状态管理
├── utils/
│   ├── demoBasePlugin.js      # XGPlayer 插件
│   └── demoPlugin.js          # 弹幕插件
└── view/
    └── home/
        ├── index.vue          # 首页布局
        ├── recommend/         # 推荐页
        ├── choiceness/        # 精选页
        ├── aiSearch/          # AI 搜索页
        └── user/follow/       # 用户关注页
```

## 代码风格指南

### Vue 组件

- **始终使用组合式 API**，配合 `<script setup lang="ts">`
- 组件文件名使用 **PascalCase**（如 `VideoItem.vue`）
- **定义 props** 使用 `defineProps<{ ... }>()` 泛型语法
- **定义 emits** 使用 `defineEmits<{ ... }>()` 泛型语法
- **暴露方法** 使用 `defineExpose({ ... })`
- 使用 `ref()` 和 `reactive()` 管理响应式状态
- 使用生命周期钩子：`onMounted`、`onUnmounted` 等

### Props 和 Emits

```typescript
// 推荐写法
const props = defineProps<{
  src: string
  videoId: string | number
  autoplay?: boolean
}>()

const emit = defineEmits<{
  fullscreenchange: [isFull: boolean]
}>()

// 调用
emit('fullscreenchange', true)
```

### Store（Pinia）

- 使用 **setup store 语法**（函数式）
- Store 文件名使用 **camelCase**
- 导出 hook 命名为 `useXxxStore`

```typescript
// stores/theme.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const currentTheme = ref('light')
  
  function toggleTheme() {
    // ...
  }
  
  return { currentTheme, toggleTheme }
})
```

### 导入和路径别名

- 使用 `@/` 别名表示 `src/`
- 导入分组顺序：Vue → 外部库 → 内部模块
- 使用 **绝对路径** 导入，以 `@/` 开头

```typescript
import { ref, onMounted } from 'vue'
import Player from 'xgplayer'
import '@/utils/demoPlugin.js'
import Sidebar from '@/components/Sidebar.vue'
```

### TypeScript 规范

- 使用 **interface** 定义对象结构
- 使用 **type** 定义联合类型、交叉类型和别名
- 避免使用 `any`——类型不明确时使用 `unknown`
- 使用 **可选链**（`?.`）和 **空值合并**（`??`）
- 优先使用类型守卫而非类型断言

```typescript
// 推荐写法
interface VideoProps {
  src: string
  videoId: string | number
  autoplay?: boolean
}

// 避免写法
const player: any = new Player(...)
```

### CSS / 样式

- 使用 **CSS 变量** 管理主题（在 `*-theme.css` 中定义）
- 使用 **SCSS** 嵌套编写组件样式
- 组件样式使用 `scoped` 属性隔离
- 可用的主题变量：
  - `--bg-color`、`--sidebar-bg`、`--text-color`
  - `--text-secondary`、`--primary-color`
  - `--border-color`、`--hover-bg`

```scss
<style scoped>
.container {
  display: flex;
  .main-content {
    flex: 1;
    background-color: var(--bg-color);
  }
}
</style>
```

### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `VideoItem.vue`、`SearchBar.vue` |
| Vue 文件 | PascalCase | `VideoItem.vue` |
| TypeScript 文件 | camelCase | `theme.ts`、`videoUtils.ts` |
| Composables | camelCase 或 `useXxx` | `useTheme`、`useVideoPlayer` |
| Stores | camelCase | `theme.ts`、`counter.ts` |
| CSS 类名 | kebab-case | `video-item`、`main-content` |
| 常量 | SCREAMING_SNAKE | `MAX_VIDEO_COUNT` |

### 错误处理

- 访问可能为 null 的值前使用 **可选链**
- 操作 DOM 元素前先检查元素是否存在
- 异步操作使用 try-catch
- 适当提供备用值

```typescript
// 推荐写法
if (containerRef.value) {
  player = new Player({ el: containerRef.value, ... })
}

// 避免写法
player = new Player({ el: containerRef.value, ... })
```

### Vue Router

- 路由组件使用 **懒加载**
- 定义路由时使用规范的命名

```typescript
{
  path: '/recommend',
  name: 'recommend',
  component: () => import('@/view/home/recommend/index.vue')
}
```

## API 配置

- 开发服务器代理：`/api` → `http://localhost:7071/`
- 代理规则：转发时移除 `/api` 前缀

## 已知问题

- `choiceness/index.vue` 中引用的 `@/eventbus/eventBus.js` 尚不存在——如有需要请创建

## IDE 配置

- **VS Code** 推荐安装 **Vue - Official** 扩展（Volar）
- 推荐安装 **Vue.js devtools** 浏览器扩展用于调试
- 推荐使用 **Chromium 内核浏览器** 进行开发
