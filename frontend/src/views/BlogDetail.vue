<template>
  <div class="page-container detail-page">
    <div v-loading="loading">
      <template v-if="post">
        <!-- 返回按钮 -->
        <div class="back-bar">
          <el-button text @click="$router.push('/blog')">
            <el-icon><ArrowLeft /></el-icon> 返回博客列表
          </el-button>
        </div>

        <!-- 文章头部 -->
        <div class="post-header">
          <h1 class="post-title">{{ post.title }}</h1>
          <div class="post-info">
            <span class="author">
              <el-avatar :size="28" :src="post.author?.avatar || undefined">
                {{ post.author?.nickname?.charAt(0) || '?' }}
              </el-avatar>
              {{ post.author?.nickname || '匿名' }}
            </span>
            <span class="date">{{ formatDate(post.createdAt) }}</span>
            <span class="stats">
              <el-icon><View /></el-icon> {{ post.viewCount }}
            </span>
          </div>
          <div class="post-tags" v-if="post.tags">
            <el-tag v-for="tag in post.tags.split(',')" :key="tag" size="small" type="info" effect="plain">
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <!-- 封面图 -->
        <div class="post-cover" v-if="post.coverImage">
          <img :src="post.coverImage" :alt="post.title" />
        </div>

        <!-- 文章内容 -->
        <div class="post-content">
          <p v-for="(para, i) in contentParagraphs" :key="i" class="paragraph">{{ para }}</p>
        </div>

        <!-- 操作栏 -->
        <div class="post-actions">
          <el-button :type="liked ? 'danger' : 'default'" round @click="handleLike">
            <el-icon style="margin-right: 4px"><Star /></el-icon>
            点赞 {{ post.likeCount }}
          </el-button>
          <template v-if="isAuthor">
            <el-button type="primary" round @click="$router.push(`/blog/edit/${post.id}`)">
              <el-icon style="margin-right: 4px"><EditPen /></el-icon> 编辑
            </el-button>
            <el-button type="danger" round @click="handleDelete">
              <el-icon style="margin-right: 4px"><Delete /></el-icon> 删除
            </el-button>
          </template>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { postApi } from '../api'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const post = ref(null)
const loading = ref(true)
const liked = ref(false)

const contentParagraphs = computed(() => {
  if (!post.value?.content) return []
  return post.value.content.split('\n').filter(p => p.trim())
})

const isAuthor = computed(() => {
  return userStore.isLoggedIn && post.value?.author?.id === userStore.userId
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

async function loadPost() {
  loading.value = true
  try {
    const res = await postApi.getById(route.params.id)
    post.value = res.data
  } catch (e) {
    // 错误已提示
  } finally {
    loading.value = false
  }
}

async function handleLike() {
  if (liked.value) return
  try {
    const res = await postApi.like(post.value.id)
    post.value = res.data
    liked.value = true
    ElMessage.success('点赞成功')
  } catch (e) {
    // 错误已提示
  }
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？删除后不可恢复。', '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await postApi.remove(post.value.id, userStore.userId)
    ElMessage.success('文章已删除')
    router.push('/blog')
  } catch (e) {
    // 用户取消或错误已提示
  }
}

onMounted(loadPost)
</script>

<style scoped>
.detail-page {
  max-width: 860px;
}

.back-bar {
  margin-bottom: 16px;
}

.post-header {
  margin-bottom: 24px;
}

.post-title {
  font-size: 32px;
  color: #303133;
  line-height: 1.4;
  margin-bottom: 16px;
}

.post-info {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 12px;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.stats {
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.post-cover {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
  max-height: 400px;
}

.post-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-content {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  border: 1px solid #ebeef5;
  margin-bottom: 24px;
  line-height: 1.9;
  font-size: 16px;
  color: #303133;
}

.paragraph {
  margin-bottom: 16px;
  white-space: pre-wrap;
}

.paragraph:last-child {
  margin-bottom: 0;
}

.post-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 8px 0 24px;
}
</style>
