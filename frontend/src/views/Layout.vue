<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="navbar-inner">
        <div class="logo" @click="$router.push('/')">
          <el-icon :size="26" color="#409EFF"><Star /></el-icon>
          <span class="logo-text">星语博客</span>
        </div>

        <nav class="nav-links">
          <router-link to="/" class="nav-link" active-class="active">
            <el-icon><HomeFilled /></el-icon> 首页
          </router-link>
          <router-link to="/ai-space" class="nav-link" active-class="active">
            <el-icon><Star /></el-icon> AI观点星空
          </router-link>
          <router-link to="/tech" class="nav-link" active-class="active">
            <el-icon><ChatDotRound /></el-icon> 技术讨论区
          </router-link>
        </nav>

        <div class="nav-right">
          <!-- 消息中心 -->
          <el-badge :value="userStore.unreadCount" :hidden="userStore.unreadCount === 0" :max="99">
            <el-icon class="bell-icon" :size="22" @click="$router.push('/notifications')">
              <Bell />
            </el-icon>
          </el-badge>

          <!-- 用户信息 -->
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.user?.avatar || undefined">
                  {{ userStore.nickname?.charAt(0) }}
                </el-avatar>
                <span class="nickname">{{ userStore.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <p>星语博客 · AI观点星空 & 技术讨论区 · Vue3 + Element Plus + Spring Boot</p>
    </footer>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  userStore.fetchUnreadCount()
})

function handleCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.logo-text {
  background: linear-gradient(90deg, #409EFF, #7c4dff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border-radius: 8px;
  color: #606266;
  font-size: 15px;
  transition: all 0.2s;
}

.nav-link:hover {
  background: #f0f2f5;
  color: #409EFF;
}

.nav-link.active {
  background: #ecf5ff;
  color: #409EFF;
  font-weight: 600;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.bell-icon {
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;
}

.bell-icon:hover {
  color: #409EFF;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}

.nickname {
  font-size: 14px;
  color: #303133;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.main-content {
  flex: 1;
}

.footer {
  text-align: center;
  padding: 20px;
  color: #909399;
  font-size: 13px;
  background: #fff;
  border-top: 1px solid #ebeef5;
}
</style>
