<template>
  <div class="page-container notifications-page">
    <div class="notif-header">
      <h2 class="page-title">
        <el-icon><Bell /></el-icon> 消息中心
      </h2>
      <el-button v-if="notifications.length > 0" size="small" @click="markAllRead">
        <el-icon style="margin-right: 4px"><Check /></el-icon> 全部已读
      </el-button>
    </div>

    <div v-loading="loading" class="notif-list">
      <div v-for="n in notifications" :key="n.id" class="notif-item"
           :class="{ unread: !n.read }" @click="handleClick(n)">
        <div class="notif-icon" :class="iconClass(n.type)">
          <el-icon :size="20"><component :is="iconName(n.type)" /></el-icon>
        </div>
        <div class="notif-body">
          <p class="notif-content">{{ n.content }}</p>
          <span class="notif-time">{{ formatTime(n.createdAt) }}</span>
        </div>
        <el-tag v-if="!n.read" type="danger" size="small" effect="light">未读</el-tag>
      </div>

      <el-empty v-if="!loading && notifications.length === 0" description="暂无消息" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { notificationApi } from '../api'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const notifications = ref([])
const loading = ref(true)

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function iconName(type) {
  const map = {
    NEW_SOLUTION: 'Promotion',
    SOLUTION_ACCEPTED: 'CircleCheck',
    NEW_COMMENT: 'ChatDotRound',
    SYSTEM: 'InfoFilled'
  }
  return map[type] || 'Bell'
}

function iconClass(type) {
  const map = {
    NEW_SOLUTION: 'icon-solution',
    SOLUTION_ACCEPTED: 'icon-accepted',
    NEW_COMMENT: 'icon-comment',
    SYSTEM: 'icon-system'
  }
  return map[type] || 'icon-system'
}

async function loadNotifications() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loading.value = true
  try {
    const res = await notificationApi.getByUser(userStore.userId)
    notifications.value = res.data
    userStore.fetchUnreadCount()
  } catch (e) {
    // 错误已提示
  } finally {
    loading.value = false
  }
}

async function handleClick(n) {
  // 标记已读
  if (!n.read) {
    try {
      await notificationApi.markRead(n.id, userStore.userId)
      n.read = true
      userStore.fetchUnreadCount()
    } catch (e) {
      // 忽略
    }
  }
  // 跳转到关联的问题
  if (n.question?.id) {
    router.push(`/question/${n.question.id}`)
  } else if (n.opinion?.id) {
    router.push(`/opinion/${n.opinion.id}`)
  }
}

async function markAllRead() {
  try {
    await notificationApi.markAllRead(userStore.userId)
    notifications.value.forEach(n => n.read = true)
    userStore.fetchUnreadCount()
    ElMessage.success('已全部标记为已读')
  } catch (e) {
    // 错误已提示
  }
}

onMounted(loadNotifications)
</script>

<style scoped>
.notif-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 24px;
  color: #303133;
}

.notif-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 200px;
}

.notif-item {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  cursor: pointer;
  border: 1px solid #ebeef5;
  transition: all 0.2s;
}

.notif-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.notif-item.unread {
  background: #f0f7ff;
  border-color: #a0cfff;
}

.notif-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.icon-solution {
  background: #409EFF;
}

.icon-accepted {
  background: #67c23a;
}

.icon-comment {
  background: #e6a23c;
}

.icon-system {
  background: #909399;
}

.notif-body {
  flex: 1;
}

.notif-content {
  color: #303133;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 4px;
}

.notif-time {
  color: #909399;
  font-size: 12px;
}
</style>
