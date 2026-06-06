<template>
  <div class="setting-page">
    <div class="page-head">
      <div>
        <h2>系统设置</h2>
        <p>统一维护视频上传限制、互动阈值和注册邮件模板。</p>
      </div>
      <div class="page-actions">
        <el-button plain @click="resetToDefault">恢复默认</el-button>
        <el-button plain @click="loadSysSetting">重新加载</el-button>
        <el-button type="primary" :loading="saving" @click="saveSetting">保存设置</el-button>
      </div>
    </div>

    <el-row :gutter="16" class="overview-row">
      <el-col :xs="12" :lg="6">
        <el-card shadow="never" class="overview-card">
          <div class="overview-label">视频大小上限</div>
          <div class="overview-value">{{ formData.videoSize }} MB</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :lg="6">
        <el-card shadow="never" class="overview-card">
          <div class="overview-label">单视频分片数</div>
          <div class="overview-value">{{ formData.videoPCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :lg="6">
        <el-card shadow="never" class="overview-card">
          <div class="overview-label">用户视频上限</div>
          <div class="overview-value">{{ formData.videoCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :lg="6">
        <el-card shadow="never" class="overview-card">
          <div class="overview-label">互动总额度</div>
          <div class="overview-value">{{ formData.commentCount + formData.danmuCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :xl="14">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>视频发布限制</span>
              <span class="panel-sub">控制单个视频上传与账号内容量级</span>
            </div>
          </template>
          <el-form label-width="130px" class="setting-form">
            <el-form-item label="视频大小上限">
              <el-input-number
                v-model="formData.videoSize"
                :min="100"
                :max="10240"
                :step="100"
                controls-position="right"
              />
              <span class="form-tip">单位 MB，用于限制单个视频可上传大小。</span>
            </el-form-item>
            <el-form-item label="单视频分片数">
              <el-input-number
                v-model="formData.videoPCount"
                :min="1"
                :max="1000"
                :step="10"
                controls-position="right"
              />
              <span class="form-tip">超过这个分片数的视频会被拒绝上传。</span>
            </el-form-item>
            <el-form-item label="用户视频上限">
              <el-input-number
                v-model="formData.videoCount"
                :min="1"
                :max="10000"
                :step="10"
                controls-position="right"
              />
              <span class="form-tip">限制单个用户可管理的视频数量。</span>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="10">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>互动内容限制</span>
              <span class="panel-sub">控制每个视频下的评论和弹幕容量</span>
            </div>
          </template>
          <el-form label-width="120px" class="setting-form">
            <el-form-item label="评论数量上限">
              <el-input-number
                v-model="formData.commentCount"
                :min="1"
                :max="5000"
                :step="10"
                controls-position="right"
              />
            </el-form-item>
            <el-form-item label="弹幕数量上限">
              <el-input-number
                v-model="formData.danmuCount"
                :min="1"
                :max="5000"
                :step="10"
                controls-position="right"
              />
            </el-form-item>
            <div class="limit-summary">
              <div class="limit-title">当前策略</div>
              <p>每个视频最多保留 {{ formData.commentCount }} 条评论。</p>
              <p>每个视频最多保留 {{ formData.danmuCount }} 条弹幕。</p>
            </div>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>注册邮件模板</span>
              <span class="panel-sub">发送邮箱验证码时使用的标题与正文</span>
            </div>
          </template>
          <el-form label-width="120px" class="setting-form">
            <el-form-item label="邮件标题">
              <el-input v-model.trim="formData.registerEMailTitle" maxlength="80" show-word-limit />
            </el-form-item>
            <el-form-item label="邮件正文">
              <el-input
                v-model.trim="formData.registerEmailContent"
                type="textarea"
                :rows="5"
                maxlength="300"
                show-word-limit
              />
              <span class="form-tip">正文里请保留 `%s`，发送时系统会替换成验证码。</span>
            </el-form-item>
            <div class="mail-preview">
              <div class="preview-head">邮件预览</div>
              <div class="preview-card">
                <div class="preview-title">{{ formData.registerEMailTitle }}</div>
                <div class="preview-content">{{ previewContent }}</div>
              </div>
            </div>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useSysSettingStore } from '@/stores/sysSettingStore'

const { proxy } = getCurrentInstance() as any
const sysSettingStore = useSysSettingStore()

const createDefaultSetting = () => ({
  videoSize: 2000,
  videoPCount: 100,
  videoCount: 100,
  commentCount: 50,
  danmuCount: 50,
  registerEMailTitle: '邮箱验证码',
  registerEmailContent: '您好, 您的邮箱验证码是, %s, 15分钟有效',
})

const formData = ref<Record<string, any>>(createDefaultSetting())
const saving = ref(false)

const previewContent = computed(() => {
  const template = formData.value.registerEmailContent || ''
  return template.replace('%s', '123456')
})

const loadSysSetting = async (): Promise<void> => {
  const result = await proxy.$Request({
    url: proxy.$Api.getSysSetting,
  })
  if (!result) {
    return
  }
  formData.value = {
    ...createDefaultSetting(),
    ...(result.data || {}),
  }
  sysSettingStore.saveSettingList(formData.value)
}

const resetToDefault = (): void => {
  formData.value = createDefaultSetting()
}

const validateSetting = (): boolean => {
  if (!formData.value.registerEmailContent.includes('%s')) {
    ElMessage.warning('邮件正文必须保留 %s 作为验证码占位符')
    return false
  }
  return true
}

const saveSetting = async (): Promise<void> => {
  if (!validateSetting()) {
    return
  }
  saving.value = true
  try {
    const result = await proxy.$Request({
      url: proxy.$Api.saveSysSetting,
      params: formData.value,
    })
    if (!result) {
      return
    }
    formData.value = {
      ...createDefaultSetting(),
      ...(result.data || {}),
    }
    sysSettingStore.saveSettingList(formData.value)
    ElMessage.success('系统设置已保存')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadSysSetting()
})
</script>

<style lang="scss" scoped>
.setting-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 4px 2px 12px;
}

.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;

  h2 {
    margin: 0;
    font-size: 22px;
    font-weight: 700;
    color: #1f2937;
  }

  p {
    margin: 6px 0 0;
    color: #6b7280;
    font-size: 13px;
  }
}

.page-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.overview-row {
  width: 100%;
}

.overview-card,
.panel-card {
  border: 1px solid #edf0f5;
  border-radius: 12px;
  background: #fff;
}

.overview-label {
  font-size: 13px;
  color: #6b7280;
}

.overview-value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  color: #111827;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.panel-sub {
  font-size: 12px;
  font-weight: 400;
  color: #8a94a6;
}

.setting-form {
  .form-tip {
    margin-left: 10px;
    font-size: 12px;
    color: #8a94a6;
  }
}

.limit-summary {
  margin-top: 10px;
  padding: 14px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #edf0f5;

  .limit-title {
    margin-bottom: 8px;
    font-size: 13px;
    font-weight: 600;
    color: #334155;
  }

  p {
    margin: 0 0 6px;
    font-size: 13px;
    color: #64748b;
  }
}

.mail-preview {
  margin-top: 8px;

  .preview-head {
    margin-bottom: 10px;
    font-size: 13px;
    font-weight: 600;
    color: #334155;
  }

  .preview-card {
    padding: 16px;
    border-radius: 10px;
    border: 1px solid #edf0f5;
    background: #f8fafc;
  }

  .preview-title {
    font-size: 14px;
    font-weight: 700;
    color: #111827;
  }

  .preview-content {
    margin-top: 10px;
    font-size: 13px;
    color: #475569;
    line-height: 1.8;
    white-space: pre-wrap;
  }
}

:deep(.el-card__header) {
  padding: 14px 16px;
}

:deep(.el-card__body) {
  padding: 16px;
}

@media (max-width: 768px) {
  .page-head {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
