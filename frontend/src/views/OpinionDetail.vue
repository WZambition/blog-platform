<template>
  <div class="page-container opinion-detail">
    <el-page-header @back="$router.push('/ai-space')" content="观点议题" style="margin-bottom: 20px" />

    <div v-if="opinion" class="detail-card">
      <!-- 观点头部 -->
      <div class="opinion-header">
        <div class="star-badge" :style="{ background: opinion.starColor }">
          <el-icon :size="28" color="#fff"><Star /></el-icon>
        </div>
        <div class="opinion-info">
          <h2>{{ opinion.title }}</h2>
          <div class="meta">
            <span>作者：{{ opinion.author?.nickname || '匿名' }}</span>
            <span>·</span>
            <span>{{ formatTime(opinion.createdAt) }}</span>
            <el-tag v-if="opinion.isTopic" type="warning" size="small" effect="light" style="margin-left: 8px">
              议题中
            </el-tag>
          </div>
        </div>
        <div class="opinion-actions">
          <el-button :type="liked ? 'danger' : 'default'" round @click="handleLike">
            <el-icon style="margin-right: 4px"><Star /></el-icon> {{ opinion.likeCount }}
          </el-button>
        </div>
      </div>

      <!-- 观点内容 -->
      <div class="opinion-content">
        <p>{{ opinion.content }}</p>
      </div>

      <!-- 讨论区 -->
      <div class="discussion-section">
        <h3 class="section-title">
          <el-icon><ChatDotRound /></el-icon> 议题讨论 ({{ opinion.commentCount }})
        </h3>

        <!-- 发表讨论 -->
        <div class="comment-input">
          <el-input v-model="commentContent" type="textarea" :rows="3"
                    placeholder="发表你的看法，参与这个议题的讨论..." />
          <div class="comment-actions">
            <el-button type="primary" :loading="submitting" @click="submitComment">发表讨论</el-button>
          </div>
        </div>

        <!-- 讨论列表 -->
        <div v-if="comments.length > 0" class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <el-avatar :size="36" :src="comment.author?.avatar || undefined">
              {{ comment.author?.nickname?.charAt(0) || '?' }}
            </el-avatar>
            <div class="comment-body">
              <div class="comment-meta">
                <strong>{{ comment.author?.nickname || '匿名' }}</strong>
                <span>{{ formatTime(comment.createdAt) }}</span>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
            </div>
          </div>
        </div>
        <el-empty v-else description="还没有讨论，来发表第一个看法吧" :image-size="80" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { opinionApi } from '../api'
import { useUserStore } from '../store/user'

const route = useRoute()
const userStore = useUserStore()

const opinion = ref(null)
const comments = ref([])
const commentContent = ref('')
const submitting = ref(false)
const liked = ref(false)

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

async function loadDetail() {
  const id = route.params.id
  try {
    const res = await opinionApi.getById(id)
    opinion.value = res.data
    const commentsRes = await opinionApi.getComments(id)
    comments.value = commentsRes.data
  } catch (e) {
    // 错误已提示
  }
}

async function submitComment() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入讨论内容')
    return
  }
  submitting.value = true
  try {
    await opinionApi.addComment(opinion.value.id, commentContent.value, userStore.userId)
    ElMessage.success('讨论发表成功')
    commentContent.value = ''
    loadDetail()
  } catch (e) {
    // 错误已提示
  } finally {
    submitting.value = false
  }
}

async function handleLike() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await opinionApi.like(opinion.value.id)
    opinion.value = res.data
    liked.value = true
    ElMessage.success('点赞成功')
  } catch (e) {
    // 错误已提示
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.detail-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.opinion-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.star-badge {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 0 20px rgba(0,0,0,0.1);
}

.opinion-info {
  flex: 1;
}

.opinion-info h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.meta {
  color: #909399;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.opinion-content {
  padding: 24px 0;
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
}

.discussion-section {
  margin-top: 8px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: #303133;
  margin-bottom: 16px;
}

.comment-input {
  margin-bottom: 24px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f8f9fb;
  border-radius: 12px;
}

.comment-body {
  flex: 1;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.comment-meta strong {
  color: #303133;
  font-size: 14px;
}

.comment-meta span {
  color: #909399;
  font-size: 12px;
}

.comment-text {
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>
