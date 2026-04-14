<template>
  <Teleport to="body">
    <ElDialog
      :model-value="authStore.loginDialogVisible"
      width="560px"
      class="login-dialog"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @close="handleClose"
    >
      <template #header>
        <div class="dialog-header">
          <img class="dialog-logo" :src="logoImage" alt="Interlude logo" />
          <h2>{{ headerTitle }}</h2>
          <p>{{ headerSubtitle }}</p>
        </div>
      </template>
      <div class="dialog-body">
        <div class="dialog-intro">
          <span>同步观看记录与收藏内容</span>
          <span>登录后可进行点赞、评论、弹幕互动</span>
        </div>
        <ElTabs v-model="activeTab" class="login-tabs" @tab-change="handleTabSwitch">
          <ElTabPane label="账号登录" name="account">
            <ElForm
              ref="accountFormRef"
              :model="accountForm"
              :rules="accountRules"
              label-position="top"
              class="login-form"
              @keydown.enter.prevent="handleEnterSubmit"
            >
              <ElFormItem prop="account">
                <ElInput v-model.trim="accountForm.account" placeholder="手机号 / 用户名 / 邮箱" autocomplete="username" />
              </ElFormItem>
              <ElFormItem prop="password">
                <ElInput
                  v-model="accountForm.password"
                  type="password"
                  placeholder="请输入密码"
                  autocomplete="current-password"
                  show-password
                />
              </ElFormItem>
              <ElFormItem prop="checkCode" class="captcha-item">
                <ElInput v-model.trim="accountForm.checkCode" placeholder="请输入图形验证码" maxlength="5" />
                <button type="button" class="captcha-button" @click="refreshCaptcha('account')">
                  <img v-if="captchaMap.account.image" :src="captchaMap.account.image" alt="captcha" />
                  <span v-else>点击刷新</span>
                </button>
              </ElFormItem>
              <div class="remember-row">
                <ElCheckbox v-model="rememberPassword">记住密码</ElCheckbox>
                <button type="button" class="ghost-link" @click="activeTab = 'register'">立即注册</button>
              </div>
              <ElButton
                type="primary"
                class="submit-btn"
                round
                :loading="accountLoginLoading"
                @click="handleAccountLogin"
              >
                立即登录
              </ElButton>
            </ElForm>
          </ElTabPane>
          <ElTabPane label="邮箱登录" name="email">
            <ElForm
              ref="emailFormRef"
              :model="emailForm"
              :rules="emailRules"
              label-position="top"
              class="login-form"
              @keydown.enter.prevent="handleEnterSubmit"
            >
              <ElFormItem prop="email">
                <ElInput v-model.trim="emailForm.email" placeholder="请输入邮箱" autocomplete="email" />
              </ElFormItem>
              <ElFormItem prop="password">
                <ElInput
                  v-model="emailForm.password"
                  type="password"
                  placeholder="请输入密码"
                  autocomplete="current-password"
                  show-password
                />
              </ElFormItem>
              <ElFormItem prop="checkCode" class="captcha-item">
                <ElInput v-model.trim="emailForm.checkCode" placeholder="请输入图形验证码" maxlength="5" />
                <button type="button" class="captcha-button" @click="refreshCaptcha('email')">
                  <img v-if="captchaMap.email.image" :src="captchaMap.email.image" alt="captcha" />
                  <span v-else>点击刷新</span>
                </button>
              </ElFormItem>
              <div class="remember-row">
                <ElCheckbox v-model="rememberPassword">记住密码</ElCheckbox>
                <button type="button" class="ghost-link" @click="activeTab = 'account'">账号登录</button>
              </div>
              <ElButton
                type="primary"
                class="submit-btn"
                round
                :loading="emailLoginLoading"
                @click="handleEmailLogin"
              >
                邮箱登录
              </ElButton>
            </ElForm>
          </ElTabPane>
          <ElTabPane label="快速注册" name="register">
            <ElForm
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              label-position="top"
              class="login-form"
              @keydown.enter.prevent="handleEnterSubmit"
            >
              <ElFormItem prop="nickName">
                <ElInput v-model.trim="registerForm.nickName" placeholder="昵称" />
              </ElFormItem>
              <ElFormItem prop="email">
                <ElInput v-model.trim="registerForm.email" placeholder="请输入常用邮箱" autocomplete="email" />
              </ElFormItem>
              <ElFormItem prop="password">
                <ElInput v-model="registerForm.password" type="password" placeholder="设置登录密码" show-password />
              </ElFormItem>
              <ElFormItem prop="confirmPassword">
                <ElInput v-model="registerForm.confirmPassword" type="password" placeholder="再次输入密码" show-password />
              </ElFormItem>
              <ElFormItem prop="imageCode" class="captcha-item">
                <ElInput v-model.trim="registerForm.imageCode" placeholder="请输入图形验证码" maxlength="5" />
                <button type="button" class="captcha-button" @click="refreshCaptcha('register')">
                  <img v-if="captchaMap.register.image" :src="captchaMap.register.image" alt="captcha" />
                  <span v-else>点击刷新</span>
                </button>
              </ElFormItem>
              <ElFormItem prop="emailCode" class="email-code-item">
                <ElInput v-model.trim="registerForm.emailCode" placeholder="邮箱验证码" maxlength="6" />
                <ElButton
                  type="primary"
                  link
                  class="send-code-btn"
                  :disabled="emailCodeCountdown > 0"
                  @click="handleSendEmailCode"
                >
                  {{ emailCodeCountdown > 0 ? `${emailCodeCountdown}s` : '发送验证码' }}
                </ElButton>
              </ElFormItem>
              <ElButton
                type="primary"
                class="submit-btn"
                round
                :loading="registerLoading"
                @click="handleRegister"
              >
                完成注册并登录
              </ElButton>
            </ElForm>
          </ElTabPane>
        </ElTabs>
      </div>
    </ElDialog>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, onUnmounted, reactive, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { fetchCheckCode, loginByAccount, registerAccount, sendRegisterEmailCode } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import logoImage from '@/assets/images/logo-b.png'

type LoginTab = 'account' | 'email' | 'register'
type CaptchaTarget = 'account' | 'email' | 'register'

interface CaptchaState {
  image: string
  key: string
  loading: boolean
}

const authStore = useAuthStore()
const activeTab = ref<LoginTab>('account')
const rememberPassword = ref(authStore.rememberMe)

const accountFormRef = ref<FormInstance>()
const emailFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()

const accountForm = reactive({ account: '', password: '', checkCode: '' })
const emailForm = reactive({ email: '', password: '', checkCode: '' })
const registerForm = reactive({
  email: '',
  nickName: '',
  password: '',
  confirmPassword: '',
  emailCode: '',
  imageCode: '',
})

const accountLoginLoading = ref(false)
const emailLoginLoading = ref(false)
const registerLoading = ref(false)
const emailCodeCountdown = ref(0)
let emailCodeTimer: number | null = null

const captchaMap = reactive<Record<CaptchaTarget, CaptchaState>>({
  account: { image: '', key: '', loading: false },
  email: { image: '', key: '', loading: false },
  register: { image: '', key: '', loading: false },
})

const headerTitle = computed(() => (activeTab.value === 'register' ? '注册 Interlude 账号' : '登录 Interlude'))
const headerSubtitle = computed(() => {
  if (activeTab.value === 'register') {
    return '注册后即可同步观看历史、互动记录和收藏内容'
  }
  return '登录后可进行点赞、评论、收藏、分享与弹幕等互动'
})

const accountRules: FormRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  checkCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

const emailRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] },
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  checkCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

const validateConfirmPassword = (_: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
    return
  }
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const registerRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] },
  ],
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度需在 6-32 位之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
  imageCode: [{ required: true, message: '请输入图形验证码', trigger: 'blur' }],
  emailCode: [{ required: true, message: '请输入邮箱验证码', trigger: 'blur' }],
}

const resolveCaptchaKey = (data?: { codeKey?: string; checkCodeKey?: string }): string => {
  return data?.checkCodeKey || data?.codeKey || ''
}

const refreshCaptcha = async (target: CaptchaTarget) => {
  const state = captchaMap[target]
  if (state.loading) {
    return
  }
  state.loading = true
  try {
    const data = await fetchCheckCode()
    state.image = data.code
    state.key = resolveCaptchaKey(data)
  } catch (error) {
    ElMessage.error('验证码加载失败，请稍后再试')
  } finally {
    state.loading = false
  }
}

const handleTabSwitch = (tab: LoginTab) => {
  if (tab === 'email' && !captchaMap.email.key) {
    refreshCaptcha('email')
  }
  if (tab === 'register' && !captchaMap.register.key) {
    refreshCaptcha('register')
  }
}

const fillRememberedCredentials = () => {
  const remembered = authStore.rememberedCredential
  rememberPassword.value = authStore.rememberMe
  if (remembered) {
    accountForm.account = remembered.account
    accountForm.password = remembered.password
    accountForm.checkCode = ''
    if (remembered.account.includes('@')) {
      emailForm.email = remembered.account
    }
    emailForm.password = remembered.password
  } else {
    accountForm.account = ''
    accountForm.password = ''
    accountForm.checkCode = ''
    emailForm.email = ''
    emailForm.password = ''
  }
  emailForm.checkCode = ''
}

const resetRegisterForm = () => {
  registerForm.nickName = ''
  registerForm.email = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
  registerForm.emailCode = ''
  registerForm.imageCode = ''
}

const stopEmailCountdown = () => {
  if (emailCodeTimer) {
    clearInterval(emailCodeTimer)
    emailCodeTimer = null
  }
  emailCodeCountdown.value = 0
}

const handleClose = () => {
  stopEmailCountdown()
  authStore.cancelLoginFlow()
}

const storeRememberedCredentials = (account: string, password: string) => {
  if (rememberPassword.value) {
    authStore.persistRemembered({ account, password })
  } else if (authStore.rememberMe) {
    authStore.persistRemembered(null)
  }
}

const getErrorMessage = (error: any, fallback: string) => {
  if (error?.message) {
    return error.message
  }
  if (error?.info) {
    return error.info
  }
  if (error?.response?.data?.info) {
    return error.response.data.info
  }
  return fallback
}

const validateForm = async (formRef: FormInstance | undefined): Promise<boolean> => {
  if (!formRef) {
    return false
  }
  try {
    await formRef.validate()
    return true
  } catch (error) {
    return false
  }
}

const validateFields = async (formRef: FormInstance | undefined, props: string | string[]): Promise<boolean> => {
  if (!formRef) {
    return false
  }
  try {
    await formRef.validateField(props as any)
    return true
  } catch (error) {
    return false
  }
}

const handleAccountLogin = async () => {
  if (!(await validateForm(accountFormRef.value))) {
    return
  }
  if (!captchaMap.account.key) {
    await refreshCaptcha('account')
    if (!captchaMap.account.key) {
      return
    }
  }
  accountLoginLoading.value = true
  try {
    const payload = {
      account: accountForm.account,
      password: accountForm.password,
      checkCode: accountForm.checkCode,
      checkCodeKey: captchaMap.account.key,
    }
    const user = await loginByAccount(payload)
    authStore.completeLogin(user)
    storeRememberedCredentials(accountForm.account, accountForm.password)
    ElMessage.success('登录成功')
  } catch (error) {
    ElMessage.error(getErrorMessage(error, '登录失败，请稍后再试'))
  } finally {
    accountLoginLoading.value = false
    accountForm.checkCode = ''
    refreshCaptcha('account')
  }
}

const handleEmailLogin = async () => {
  if (!(await validateForm(emailFormRef.value))) {
    return
  }
  if (!captchaMap.email.key) {
    await refreshCaptcha('email')
    if (!captchaMap.email.key) {
      return
    }
  }
  emailLoginLoading.value = true
  try {
    const payload = {
      account: emailForm.email,
      password: emailForm.password,
      checkCode: emailForm.checkCode,
      checkCodeKey: captchaMap.email.key,
    }
    const user = await loginByAccount(payload)
    authStore.completeLogin(user)
    storeRememberedCredentials(emailForm.email, emailForm.password)
    ElMessage.success('登录成功')
  } catch (error) {
    ElMessage.error(getErrorMessage(error, '邮箱登录失败，请稍后再试'))
  } finally {
    emailLoginLoading.value = false
    emailForm.checkCode = ''
    refreshCaptcha('email')
  }
}

const handleSendEmailCode = async () => {
  if (emailCodeCountdown.value > 0) {
    return
  }
  if (!(await validateFields(registerFormRef.value, ['email', 'imageCode']))) {
    return
  }
  if (!captchaMap.register.key) {
    await refreshCaptcha('register')
    if (!captchaMap.register.key) {
      return
    }
  }
  try {
    await sendRegisterEmailCode({
      email: registerForm.email,
      checkCode: registerForm.imageCode,
      checkCodeKey: captchaMap.register.key,
    })
    ElMessage.success('验证码已发送，请稍候查收邮件')
    registerForm.imageCode = ''
    startEmailCountdown()
    refreshCaptcha('register')
  } catch (error) {
    ElMessage.error(getErrorMessage(error, '验证码发送失败，请稍后再试'))
    registerForm.imageCode = ''
    refreshCaptcha('register')
  }
}

const startEmailCountdown = () => {
  stopEmailCountdown()
  emailCodeCountdown.value = 60
  emailCodeTimer = window.setInterval(() => {
    emailCodeCountdown.value -= 1
    if (emailCodeCountdown.value <= 0) {
      stopEmailCountdown()
    }
  }, 1000)
}

const handleRegister = async () => {
  if (!(await validateForm(registerFormRef.value))) {
    return
  }
  registerLoading.value = true
  try {
    const payload = {
      email: registerForm.email,
      nickName: registerForm.nickName,
      password: registerForm.password,
      emailCode: registerForm.emailCode,
    }
    const user = await registerAccount(payload)
    authStore.completeLogin(user)
    storeRememberedCredentials(registerForm.email, registerForm.password)
    ElMessage.success('注册并登录成功')
    resetRegisterForm()
    stopEmailCountdown()
    refreshCaptcha('register')
  } catch (error) {
    ElMessage.error(getErrorMessage(error, '注册失败，请稍后再试'))
  } finally {
    registerLoading.value = false
  }
}

const handleEnterSubmit = (event: KeyboardEvent) => {
  const nativeEvent = event as KeyboardEvent & { isComposing?: boolean; keyCode?: number }
  if (nativeEvent.isComposing || nativeEvent.keyCode === 229) {
    return
  }
  if (activeTab.value === 'account') {
    if (!accountLoginLoading.value) {
      handleAccountLogin().catch(() => undefined)
    }
    return
  }
  if (activeTab.value === 'email') {
    if (!emailLoginLoading.value) {
      handleEmailLogin().catch(() => undefined)
    }
    return
  }
  if (!registerLoading.value) {
    handleRegister().catch(() => undefined)
  }
}

watch(activeTab, (tab) => {
  handleTabSwitch(tab)
})

watch(
  () => authStore.loginDialogVisible,
  (visible) => {
    if (visible) {
      activeTab.value = 'account'
      fillRememberedCredentials()
      refreshCaptcha('account')
      refreshCaptcha('register')
    } else {
      stopEmailCountdown()
      accountForm.checkCode = ''
      emailForm.checkCode = ''
      resetRegisterForm()
    }
  }
)

onUnmounted(() => {
  stopEmailCountdown()
})
</script>

<style scoped lang="scss">
.login-dialog {
  :deep(.el-dialog) {
    border-radius: 20px;
    overflow: hidden;
    background: #ffffff;
    border: 1px solid #e8ecf3;
    box-shadow: 0 26px 56px rgba(0, 0, 0, 0.24);
  }

  :deep(.el-dialog__header) {
    margin: 0;
    padding: 22px 24px 12px;
    background:
      radial-gradient(circle at right top, rgba(255, 115, 90, 0.16), transparent 48%),
      linear-gradient(150deg, #fff7f6, #ffffff 68%);
  }

  :deep(.el-dialog__body) {
    padding: 14px 24px 24px;
    background: #ffffff;
  }

  :deep(.el-dialog__headerbtn .el-dialog__close) {
    color: #7b8798;
  }

  :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
    color: #414e63;
  }
}

.dialog-header {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
  text-align: center;

  .dialog-logo {
    position: absolute;
    top: -2px;
    left: 0;
    width: 22px;
    height: 22px;
    object-fit: contain;
    opacity: 0.95;
    user-select: none;
    pointer-events: none;
  }

  h2 {
    margin: 0;
    font-size: 28px;
    font-weight: 800;
    line-height: 1.1;
    letter-spacing: 0.02em;
    color: #1f2937;
  }

  p {
    margin: 0;
    color: #64748b;
    font-size: 13px;
    max-width: 430px;
    line-height: 1.5;
  }
}

.dialog-body {
  border-radius: 14px;
  border: 1px solid #e7ebf1;
  background: #ffffff;
  padding: 14px;
}

.dialog-intro {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  font-size: 12px;
  color: #5b6678;
  padding: 8px 10px;
  border-radius: 10px;
  background: #f5f7fb;
  margin-bottom: 10px;
}

.login-tabs {
  :deep(.el-tabs__header) {
    display: flex;
    justify-content: center;
    margin-bottom: 10px;
  }

  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }

  :deep(.el-tabs__nav-wrap) {
    display: flex;
    justify-content: center;
    width: fit-content;
    margin: 0 auto;
  }

  :deep(.el-tabs__nav-scroll) {
    display: flex;
    justify-content: center;
    width: auto;
  }

  :deep(.el-tabs__nav) {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    width: fit-content;
    margin: 0 auto;
    padding: 4px;
    border-radius: 999px;
    background: #f3f5f9;
  }

  :deep(.el-tabs__item) {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 104px;
    height: 36px;
    line-height: 36px;
    border-radius: 999px;
    padding: 0 18px;
    margin: 0;
    font-size: 14px;
    text-align: center;
  }

  :deep(.el-tabs__item.is-active) {
    background: #fff2f4;
    color: #ff2d55;
    font-weight: 600;
    box-shadow: 0 2px 10px rgba(255, 45, 85, 0.2);
  }

  :deep(.el-tabs__active-bar) {
    display: none;
  }
}

.login-form {
  margin-top: 12px;

  :deep(.el-form-item) {
    margin-bottom: 16px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    min-height: 42px;
    background: #ffffff;
    box-shadow: 0 0 0 1px #dbe2ed inset;
  }

  :deep(.el-input__wrapper.is-focus) {
    box-shadow:
      0 0 0 1px rgba(255, 45, 85, 0.72) inset,
      0 0 0 4px rgba(255, 45, 85, 0.12);
  }

  :deep(.el-input__inner) {
    color: #1f2937;
  }

  :deep(.el-input__inner::placeholder) {
    color: #98a3b4;
  }

  :deep(.el-checkbox__label) {
    color: #4b5563;
  }
}

.captcha-item {
  :deep(.el-form-item__content) {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: nowrap;
  }

  :deep(.el-input) {
    flex: 1;
    min-width: 0;
  }

  .captcha-button {
    width: 146px;
    flex: 0 0 146px;
    height: 42px;
    border: 1px solid #dbe2ed;
    border-radius: 10px;
    background: #f7f9fc;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0;
    color: #64748b;
    font-size: 12px;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 8px;
    }
  }
}

.email-code-item {
  display: flex;
  align-items: center;
  gap: 8px;

  .send-code-btn {
    font-size: 13px;
    font-weight: 600;
  }
}

.remember-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.ghost-link {
  background: none;
  border: none;
  color: #ff2d55;
  cursor: pointer;
  padding: 0;
  font-size: 13px;
  font-weight: 600;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border: none;
  background: linear-gradient(135deg, var(--primary-color, #ff2d55), #ff9b6b);
  box-shadow: 0 8px 20px color-mix(in srgb, var(--primary-color, #ff2d55) 35%, transparent);
}

@media (max-width: 768px) {
  .dialog-intro {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>





