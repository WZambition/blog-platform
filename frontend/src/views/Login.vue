<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <el-icon :size="32" color="#409EFF"><Star /></el-icon>
        </div>
        <h2>星语博客</h2>
        <p>登录后即可发布观点、参与技术讨论</p>
      </div>

      <!-- 登录/注册切换 -->
      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" label-width="0" @submit.prevent>
            <el-form-item>
              <el-input v-model="loginForm.username" placeholder="邮箱 / 手机号" size="large">
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-input v-model="loginForm.password" type="password" placeholder="密码" size="large" show-password @keyup.enter="handleLogin">
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
            <div class="login-options">
              <span class="forgot-link" @click="openForgotDialog">忘记密码？</span>
            </div>
            <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
              登录
            </el-button>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" label-width="0" @submit.prevent>
            <el-form-item>
              <el-input v-model="registerForm.username" placeholder="邮箱或手机号" size="large">
                <template #prefix><el-icon><Message /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.nickname" placeholder="昵称（可选）" size="large">
                <template #prefix><el-icon><Avatar /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.password" type="password" placeholder="密码" size="large" show-password>
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-select v-model="registerForm.securityQuestion" placeholder="选择安全问题" size="large" style="width: 100%">
                <el-option label="你的出生城市是？" value="你的出生城市是？" />
                <el-option label="你的小学名称是？" value="你的小学名称是？" />
                <el-option label="你最喜欢的宠物名字是？" value="你最喜欢的宠物名字是？" />
                <el-option label="你母亲的姓氏是？" value="你母亲的姓氏是？" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.securityAnswer" placeholder="安全问题答案（用于找回密码）" size="large">
                <template #prefix><el-icon><Key /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleRegister">
              注册
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <!-- 游客登录 -->
      <el-divider>或</el-divider>
      <div class="guest-login">
        <el-input v-model="guestName" placeholder="输入昵称（可选）" size="large" style="margin-bottom: 12px">
          <template #prefix><el-icon><UserFilled /></el-icon></template>
        </el-input>
        <el-button size="large" style="width: 100%" :loading="loading" @click="handleGuest">
          以游客身份进入
        </el-button>
      </div>

      <div class="back-home" @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon> 返回首页
      </div>
    </div>

    <!-- 忘记密码弹窗 -->
    <el-dialog v-model="forgotVisible" title="找回密码" width="420px" :close-on-click-modal="false">
      <el-form :model="forgotForm" label-width="0" @submit.prevent>
        <el-form-item>
          <el-input v-model="forgotForm.username" placeholder="注册时使用的邮箱或手机号" size="large">
            <template #prefix><el-icon><Message /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="forgotForm.securityAnswer" placeholder="安全问题答案" size="large">
            <template #prefix><el-icon><Key /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="forgotForm.newPassword" type="password" placeholder="设置新密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="forgotVisible = false">取消</el-button>
        <el-button type="primary" :loading="resetting" @click="handleResetPassword">重置密码</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import { userApi } from '../api'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)
const guestName = ref('')

const loginForm = ref({ username: '', password: '' })
const registerForm = ref({
  username: '',
  nickname: '',
  password: '',
  securityQuestion: '',
  securityAnswer: ''
})

// 忘记密码
const forgotVisible = ref(false)
const resetting = ref(false)
const forgotForm = ref({ username: '', securityAnswer: '', newPassword: '' })

function openForgotDialog() {
  forgotForm.value = { username: '', securityAnswer: '', newPassword: '' }
  forgotVisible.value = true
}

async function handleLogin() {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入邮箱/手机号和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(loginForm.value.username, loginForm.value.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // 错误已提示
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  const f = registerForm.value
  if (!f.username) {
    ElMessage.warning('请输入邮箱或手机号')
    return
  }
  if (!f.password) {
    ElMessage.warning('请输入密码')
    return
  }
  if (!f.securityQuestion) {
    ElMessage.warning('请选择安全问题')
    return
  }
  if (!f.securityAnswer) {
    ElMessage.warning('请输入安全问题答案')
    return
  }
  loading.value = true
  try {
    await userStore.register(f)
    ElMessage.success('注册成功，已自动登录')
    router.push('/')
  } catch (e) {
    // 错误已提示
  } finally {
    loading.value = false
  }
}

async function handleGuest() {
  loading.value = true
  try {
    await userStore.guestLogin(guestName.value)
    ElMessage.success('已以游客身份进入')
    router.push('/')
  } catch (e) {
    // 错误已提示
  } finally {
    loading.value = false
  }
}

async function handleResetPassword() {
  const f = forgotForm.value
  if (!f.username) {
    ElMessage.warning('请输入注册的邮箱或手机号')
    return
  }
  if (!f.securityAnswer) {
    ElMessage.warning('请输入安全问题答案')
    return
  }
  if (!f.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  resetting.value = true
  try {
    await userApi.resetPassword({
      username: f.username,
      securityAnswer: f.securityAnswer,
      password: f.newPassword
    })
    ElMessage.success('密码重置成功，请用新密码登录')
    forgotVisible.value = false
    activeTab.value = 'login'
    loginForm.value.username = f.username
    loginForm.value.password = ''
  } catch (e) {
    // 错误已提示
  } finally {
    resetting.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  padding: 20px;
}

.login-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
}

.logo {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
}

.login-header h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 6px;
}

.login-header p {
  color: #909399;
  font-size: 13px;
}

.login-options {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.forgot-link {
  color: #409EFF;
  font-size: 13px;
  cursor: pointer;
}

.forgot-link:hover {
  text-decoration: underline;
}

.guest-login {
  margin-top: 4px;
}

.back-home {
  text-align: center;
  margin-top: 20px;
  color: #909399;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.back-home:hover {
  color: #409EFF;
}
</style>
