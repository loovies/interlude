<template>
    <div class="top-box">
        <div class="search-bar">

            <!-- 搜索输入框 -->
            <input type="text" class="search-input" placeholder="搜索你感兴趣的内容" />

            <!-- 右侧按钮组 -->
            <div class="button-group">
                <button class="search-btn">🔍&nbsp;搜索</button>
            </div>
        </div>
        <div class="button-bar">
            <div class="button-list">
                <span>壁纸</span>
                <span>通知</span>
                <span>私信</span>
                <span>投稿</span>
            </div>
            <button class="login-btn">登录</button>
        </div>
    </div>

</template>

<script setup lang="ts">
// 此组件为纯展示，无需逻辑
// 后续可根据需要添加事件处理（如 @click、v-model 等）
</script>

<style scoped>

.top-box{
    display: flex;
    padding-left: 180px;
}

.search-bar {
    display: flex;
    align-items: center;
    background-color: var(--sidebar-bg);
    /* 使用侧边栏背景色 */
    border: 1px solid var(--border-color);
    border-radius: 15px;
    /* 圆角风格 */
    padding: 4px 4px 4px 16px;
    width: 100%;
    max-width: 600px;
    /* 可根据需要调整宽度 */
    margin: 0 auto;
    /* 居中显示 */
    transition: background-color 0.3s, border-color 0.3s;
}

.button-bar{
    width: 300px;
    display: flex;
    justify-content: flex-end;
    margin-right: 10px;
    .button-list{
        display: flex;
        margin: 5px 5px;
        span{
            margin: 0 10px;
            margin-top: 5px;
            color: var(--text-secondary);
            cursor: pointer;
            transition: color 0.2s;
        }
    }
}

.search-icon {
    color: var(--text-secondary);
    font-size: 20px;
    margin-right: 8px;
}

.search-input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    color: var(--text-color);
    font-size: 16px;
    padding: 8px 0;
}

.search-input::placeholder {
    color: var(--text-secondary);
    font-size: 15px;
}

.button-group {
    display: flex;
    gap: 8px;
    margin-left: 8px;
}

.search-btn {
    background-color: var(--primary-color);
    color: white;
    border: none;
    border-radius: 15px;
    padding: 8px 18px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.2s;
}

.search-btn:hover {
    background-color: #ff8a8a;
    /* 稍浅的红色，可自定义或添加变量 */
}

.login-btn {
    width: 80px;
    background-color: transparent;
    color: var(--text-color);
    border: 1px solid var(--border-color);
    border-radius: 30px;
    padding: 8px 18px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.2s, border-color 0.2s;
}

.login-btn:hover {
    background-color: var(--hover-bg);
    border-color: var(--text-secondary);
}
</style>