<template>
  <div class="page-container blog-page">
    <!-- 页面头部 -->
    <div class="blog-header">
      <div>
        <h2 class="page-title">
          <el-icon><Notebook /></el-icon> 博客文章
        </h2>
        <p class="page-subtitle">记录你的思考与分享，让文字沉淀成记忆</p>
      </div>
      <el-button type="primary" size="large" @click="goWrite">
        <el-icon style="margin-right: 6px"><EditPen /></el-icon> 写文章
      </el-button>
    </div>

    <!-- 文章列表 -->
    <div v-loading="loading" class="post-list">
      <div v-for="post in posts" :key="post.id" class="post-card hover-card"
           @click="$router.push(`/blog/${post.id}`)">
        <!-- 封面图 -->
        <div class="post-cover" v-if="post.coverImage">
          <img :src="post.coverImage" :alt="post.title" />
        </div>

        <!-- 文章主体 -->
        <div class="post-body">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-summary">{{ post.summary || post.content }}</p>
          <div class="post-tags" v-if="post.tags">
            <el-tag v-for="tag in post.tags.split(',')" :key="tag" size="small" type="info" effect="plain">
              {{ tag }}
            </el-tag>
          </div>
          <div class="post-footer">
            <span class="author">
              <el-avatar :size="24" :src="post.author?.avatar || undefined">
                {{ post.author?.nickname?.charAt(0) || '?' }}
              </el-avatar>
              {{ post.author?.nickname || '匿名' }}
            </span>
            <span class="post-meta">
              <span class="date">{{ formatDate(post.createdAt) }}</span>
              <span class="stats">
                <el-icon><View /></el-icon> {{ post.viewCount }}
                <el-icon style="margin-left: 10px"><Star /></el-icon> {{ post.likeCount }}
              </span>
            </span>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && posts.length === 0" description="还没有文章，来写下第一篇吧">
      <el-button type="primary" @click="goWrite">写文章</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { postApi } from '../api'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const posts = ref([])
const loading = ref(true)

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

function goWrite() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  router.push('/blog/write')
}

async function loadPosts() {
  loading.value = true
  try {
    const res = await postApi.findAll()
    posts.value = res.data
  } catch (e) {
    // 错误已提示
  } finally {
    loading.value = false
  }
}

onMounted(loadPosts)
</script>

<style scoped>
.blog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 24px;
  color: #303133;
  margin-bottom: 6px;
}

.page-subtitle {
  color: #909399;
  font-size: 14px;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 200px;
}

.post-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #ebeef5;
  display: flex;
}

.post-cover {
  width: 260px;
  min-height: 180px;
  flex-shrink: 0;
  background: #f0f2f5;
}

.post-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-body {
  padding: 20px 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.post-title {
  font-size: 20px;
  color: #303133;
  margin-bottom: 10px;
  transition: color 0.2s;
}

.post-card:hover .post-title {
  color: #409EFF;
}

.post-summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.7;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 14px;
}

.post-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.post-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid #f0f2f5;
  padding-top: 14px;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.date {
  color: #909399;
  font-size: 13px;
}

.stats {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 13px;
}

@media (max-width: 768px) {
  .post-card {
    flex-direction: column;
  }
  .post-cover {
    width: 100%;
    height: 160px;
  }
}
</style>
