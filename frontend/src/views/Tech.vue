<template>
  <div class="page-container tech-page">
    <!-- 页面头部 -->
    <div class="tech-header">
      <div>
        <h2 class="page-title">
          <el-icon><ChatDotRound /></el-icon> 技术讨论区
        </h2>
        <p class="page-subtitle">以卡片形式展示技术问题，遇到难题就发出来，大家一起解决</p>
      </div>
      <el-button type="primary" size="large" @click="openPublishDialog">
        <el-icon style="margin-right: 6px"><Plus /></el-icon> 发布问题
      </el-button>
    </div>

    <!-- 状态筛选 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" @change="loadQuestions">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="UNSOLVED">未解决</el-radio-button>
        <el-radio-button value="VERIFYING">验证中</el-radio-button>
        <el-radio-button value="SOLVED">已解决</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 问题卡片列表 -->
    <div v-loading="loading" class="question-grid">
      <div v-for="q in filteredQuestions" :key="q.id" class="question-card hover-card"
           @click="$router.push(`/question/${q.id}`)">
        <!-- 上方图片 -->
        <div class="card-image">
          <img v-if="q.imageUrl" :src="q.imageUrl" :alt="q.title" />
          <div v-else class="image-placeholder">
            <el-icon :size="40"><Picture /></el-icon>
          </div>
          <div class="status-badge" :class="statusClass(q.status)">
            {{ statusText(q.status) }}
          </div>
        </div>

        <!-- 下方描述 -->
        <div class="card-body">
          <h3 class="card-title">{{ q.title }}</h3>
          <p class="card-desc">{{ q.description }}</p>
          <div class="card-tags" v-if="q.tags">
            <el-tag v-for="tag in q.tags.split(',')" :key="tag" size="small" type="info" effect="plain">
              {{ tag }}
            </el-tag>
          </div>
          <div class="card-footer">
            <span class="author">
              <el-avatar :size="22" :src="q.author?.avatar || undefined">
                {{ q.author?.nickname?.charAt(0) || '?' }}
              </el-avatar>
              {{ q.author?.nickname || '匿名' }}
            </span>
            <span class="stats">
              <el-icon><View /></el-icon> {{ q.viewCount }}
              <el-icon style="margin-left: 10px"><ChatLineSquare /></el-icon> {{ q.solutionCount }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && filteredQuestions.length === 0" description="暂无相关问题" />

    <!-- 发布问题对话框 -->
    <el-dialog v-model="publishVisible" title="发布技术问题" width="560px">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="问题标题" required>
          <el-input v-model="publishForm.title" placeholder="简要描述你的问题" maxlength="80" show-word-limit />
        </el-form-item>
        <el-form-item label="问题描述" required>
          <el-input v-model="publishForm.description" type="textarea" :rows="5"
                    placeholder="详细描述你遇到的问题、期望结果、已尝试的方法..." />
        </el-form-item>
        <el-form-item label="配图URL">
          <el-input v-model="publishForm.imageUrl" placeholder="可选，粘贴问题相关图片的URL" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="publishForm.tags" placeholder="用逗号分隔，如：Java,Spring Boot" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishVisible = false">取消</el-button>
        <el-button type="primary" :loading="publishing" @click="publishQuestion">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { questionApi } from '../api'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const questions = ref([])
const loading = ref(true)
const filterStatus = ref('')
const publishVisible = ref(false)
const publishing = ref(false)

const publishForm = ref({
  title: '',
  description: '',
  imageUrl: '',
  tags: ''
})

const filteredQuestions = computed(() => {
  if (!filterStatus.value) return questions.value
  return questions.value.filter(q => q.status === filterStatus.value)
})

function statusText(status) {
  const map = { UNSOLVED: '未解决', VERIFYING: '验证中', SOLVED: '已解决' }
  return map[status] || status
}

function statusClass(status) {
  const map = { UNSOLVED: 'status-unsolved', VERIFYING: 'status-verifying', SOLVED: 'status-solved' }
  return map[status] || ''
}

async function loadQuestions() {
  loading.value = true
  try {
    const res = await questionApi.findAll()
    questions.value = res.data
  } catch (e) {
    // 错误已提示
  } finally {
    loading.value = false
  }
}

function openPublishDialog() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再发布问题')
    router.push('/login')
    return
  }
  publishForm.value = { title: '', description: '', imageUrl: '', tags: '' }
  publishVisible.value = true
}

async function publishQuestion() {
  if (!publishForm.value.title.trim() || !publishForm.value.description.trim()) {
    ElMessage.warning('请填写标题和描述')
    return
  }
  publishing.value = true
  try {
    await questionApi.create(publishForm.value, userStore.userId)
    ElMessage.success('问题发布成功')
    publishVisible.value = false
    loadQuestions()
  } catch (e) {
    // 错误已提示
  } finally {
    publishing.value = false
  }
}

onMounted(loadQuestions)
</script>

<style scoped>
.tech-header {
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
  margin-bottom: 6px;
}

.page-subtitle {
  color: #909399;
  font-size: 14px;
}

.filter-bar {
  margin-bottom: 20px;
}

.question-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.question-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}

.card-image {
  position: relative;
  height: 160px;
  background: #f0f2f5;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  background: linear-gradient(135deg, #f5f7fa, #e4e7ed);
}

.status-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  color: #fff;
  font-weight: 600;
}

.status-unsolved {
  background: #f56c6c;
}

.status-verifying {
  background: #e6a23c;
}

.status-solved {
  background: #67c23a;
}

.card-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid #f0f2f5;
  padding-top: 12px;
}

.author {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
}

.stats {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 13px;
}

@media (max-width: 768px) {
  .question-grid {
    grid-template-columns: 1fr;
  }
}
</style>
