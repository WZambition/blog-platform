<template>
  <div class="page-container question-detail">
    <el-page-header @back="$router.push('/tech')" content="问题详情" style="margin-bottom: 20px" />

    <div v-if="question" class="detail-layout">
      <!-- 左侧：问题主体 -->
      <div class="main-col">
        <div class="detail-card">
          <!-- 问题头部 -->
          <div class="q-header">
            <div class="q-title-row">
              <h2>{{ question.title }}</h2>
              <el-tag :type="statusTagType(question.status)" size="large" effect="light">
                {{ statusText(question.status) }}
              </el-tag>
            </div>
            <div class="q-meta">
              <span class="author">
                <el-avatar :size="24" :src="question.author?.avatar || undefined">
                  {{ question.author?.nickname?.charAt(0) || '?' }}
                </el-avatar>
                {{ question.author?.nickname || '匿名' }}
              </span>
              <span>发布于 {{ formatTime(question.createdAt) }}</span>
              <span><el-icon><View /></el-icon> {{ question.viewCount }} 浏览</span>
            </div>
          </div>

          <!-- 问题配图 -->
          <div v-if="question.imageUrl" class="q-image">
            <img :src="question.imageUrl" :alt="question.title" />
          </div>

          <!-- 问题描述 -->
          <div class="q-description">
            <p>{{ question.description }}</p>
          </div>

          <!-- 标签 -->
          <div v-if="question.tags" class="q-tags">
            <el-tag v-for="tag in question.tags.split(',')" :key="tag" type="info" effect="plain">
              {{ tag }}
            </el-tag>
          </div>

          <!-- 问题发起者操作区 -->
          <div v-if="isAuthor" class="author-actions">
            <template v-if="question.status === 'VERIFYING'">
              <el-button type="success" @click="handleMarkSolved">
                <el-icon style="margin-right: 4px"><CircleCheck /></el-icon> 标记为已解决
              </el-button>
              <el-button type="warning" @click="handleMarkUnsolved">
                <el-icon style="margin-right: 4px"><RefreshLeft /></el-icon> 标记为未解决
              </el-button>
            </template>
            <template v-else-if="question.status === 'UNSOLVED'">
              <el-button type="success" plain @click="handleMarkSolved">直接标记为已解决</el-button>
            </template>
          </div>
        </div>

        <!-- 方案列表 -->
        <div class="detail-card solutions-card">
          <h3 class="section-title">
            <el-icon><Document /></el-icon> 方案列表 ({{ question.solutionCount }})
          </h3>

          <!-- 提交方案 -->
          <div v-if="!isAuthor" class="solution-input">
            <el-input v-model="solutionContent" type="textarea" :rows="4"
                      placeholder="如果你有解决方案，请在这里提交..." />
            <div class="solution-actions">
              <el-button type="primary" :loading="submitting" @click="submitSolution">
                <el-icon style="margin-right: 4px"><Promotion /></el-icon> 提交方案
              </el-button>
            </div>
          </div>
          <el-alert v-else type="info" :closable="false" show-icon
                    title="这是你发起的问题，等待他人提交方案。你可以采纳某个方案或直接标记问题状态。" />

          <!-- 方案列表 -->
          <div v-if="solutions.length > 0" class="solution-list">
            <div v-for="sol in solutions" :key="sol.id" class="solution-item"
                 :class="{ accepted: sol.accepted }">
              <div class="sol-header">
                <div class="sol-author">
                  <el-avatar :size="32" :src="sol.author?.avatar || undefined">
                    {{ sol.author?.nickname?.charAt(0) || '?' }}
                  </el-avatar>
                  <div>
                    <strong>{{ sol.author?.nickname || '匿名' }}</strong>
                    <span class="sol-time">{{ formatTime(sol.createdAt) }}</span>
                  </div>
                </div>
                <div class="sol-badges">
                  <el-tag v-if="sol.accepted" type="success" effect="dark">
                    <el-icon style="margin-right: 2px"><CircleCheck /></el-icon> 已采纳
                  </el-tag>
                  <el-tag v-if="sol.author?.id === userStore.userId" type="info" effect="plain">我的方案</el-tag>
                </div>
              </div>

              <p class="sol-content">{{ sol.content }}</p>

              <div class="sol-actions">
                <!-- 方案作者可修改 -->
                <template v-if="sol.author?.id === userStore.userId">
                  <el-button size="small" type="primary" plain @click="openEditSolution(sol)">
                    <el-icon style="margin-right: 2px"><Edit /></el-icon> 修改方案
                  </el-button>
                </template>
                <!-- 问题发起者可采纳 -->
                <template v-if="isAuthor && !sol.accepted && question.status !== 'SOLVED'">
                  <el-button size="small" type="success" @click="handleAccept(sol)">
                    <el-icon style="margin-right: 2px"><CircleCheck /></el-icon> 采纳此方案
                  </el-button>
                </template>
              </div>
            </div>
          </div>
          <el-empty v-else description="还没有人提交方案" :image-size="80" />
        </div>
      </div>

      <!-- 右侧：问题状态说明 -->
      <div class="side-col">
        <div class="detail-card status-guide">
          <h3 class="section-title"><el-icon><InfoFilled /></el-icon> 问题状态说明</h3>
          <div class="status-item">
            <el-tag type="danger" effect="light">未解决</el-tag>
            <p>问题刚发布，等待他人提供方案</p>
          </div>
          <div class="status-item">
            <el-tag type="warning" effect="light">验证中</el-tag>
            <p>已收到方案，发起者正在验证方案是否有效</p>
          </div>
          <div class="status-item">
            <el-tag type="success" effect="light">已解决</el-tag>
            <p>方案已被采纳或问题已解决</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改方案对话框 -->
    <el-dialog v-model="editVisible" title="修改方案" width="520px">
      <el-input v-model="editContent" type="textarea" :rows="5" placeholder="修改你的方案内容..." />
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editing" @click="confirmEditSolution">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { questionApi } from '../api'
import { useUserStore } from '../store/user'

const route = useRoute()
const userStore = useUserStore()

const question = ref(null)
const solutions = ref([])
const solutionContent = ref('')
const submitting = ref(false)
const editVisible = ref(false)
const editContent = ref('')
const editing = ref(false)
const editingSolution = ref(null)

const isAuthor = computed(() => {
  return question.value && userStore.userId === question.value.author?.id
})

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function statusText(status) {
  const map = { UNSOLVED: '未解决', VERIFYING: '验证中', SOLVED: '已解决' }
  return map[status] || status
}

function statusTagType(status) {
  const map = { UNSOLVED: 'danger', VERIFYING: 'warning', SOLVED: 'success' }
  return map[status] || 'info'
}

async function loadDetail() {
  const id = route.params.id
  try {
    const res = await questionApi.getById(id)
    question.value = res.data
    const solRes = await questionApi.getSolutions(id)
    solutions.value = solRes.data
  } catch (e) {
    // 错误已提示
  }
}

// 提交方案
async function submitSolution() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (!solutionContent.value.trim()) {
    ElMessage.warning('请输入方案内容')
    return
  }
  submitting.value = true
  try {
    await questionApi.submitSolution(question.value.id, solutionContent.value, userStore.userId)
    ElMessage.success('方案提交成功，问题已标记为验证中，并已通知问题发起者')
    solutionContent.value = ''
    loadDetail()
  } catch (e) {
    // 错误已提示
  } finally {
    submitting.value = false
  }
}

// 采纳方案
async function handleAccept(sol) {
  try {
    await ElMessageBox.confirm(
      `确定采纳「${sol.author?.nickname}」的方案吗？采纳后问题将标记为已解决。`,
      '采纳方案',
      { confirmButtonText: '确认采纳', cancelButtonText: '取消', type: 'success' }
    )
    await questionApi.acceptSolution(sol.id, userStore.userId)
    ElMessage.success('方案已采纳，问题已解决')
    loadDetail()
  } catch (e) {
    // 用户取消或错误
  }
}

// 标记为已解决
async function handleMarkSolved() {
  try {
    await ElMessageBox.confirm('确定将问题标记为已解决吗？', '标记已解决',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'success' })
    await questionApi.markSolved(question.value.id, userStore.userId)
    ElMessage.success('问题已标记为已解决')
    loadDetail()
  } catch (e) {
    // 取消
  }
}

// 标记为未解决
async function handleMarkUnsolved() {
  try {
    await ElMessageBox.confirm('确定将问题重新标记为未解决吗？', '标记未解决',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' })
    await questionApi.markUnsolved(question.value.id, userStore.userId)
    ElMessage.success('问题已标记为未解决')
    loadDetail()
  } catch (e) {
    // 取消
  }
}

// 打开修改方案对话框
function openEditSolution(sol) {
  editingSolution.value = sol
  editContent.value = sol.content
  editVisible.value = true
}

// 确认修改方案
async function confirmEditSolution() {
  if (!editContent.value.trim()) {
    ElMessage.warning('请输入方案内容')
    return
  }
  editing.value = true
  try {
    await questionApi.updateSolution(editingSolution.value.id, editContent.value, userStore.userId)
    ElMessage.success('方案修改成功')
    editVisible.value = false
    loadDetail()
  } catch (e) {
    // 错误已提示
  } finally {
    editing.value = false
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.detail-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
  align-items: start;
}

.main-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.q-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.q-title-row h2 {
  font-size: 22px;
  color: #303133;
  flex: 1;
}

.q-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #909399;
  font-size: 13px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.author {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.q-image {
  margin: 20px 0;
  border-radius: 8px;
  overflow: hidden;
  max-height: 400px;
}

.q-image img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
}

.q-description {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
  margin-bottom: 16px;
}

.q-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.author-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: #303133;
  margin-bottom: 16px;
}

.solution-input {
  margin-bottom: 20px;
}

.solution-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.solution-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.solution-item {
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.2s;
}

.solution-item.accepted {
  border-color: #67c23a;
  background: #f0f9eb;
}

.sol-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.sol-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sol-author strong {
  display: block;
  color: #303133;
  font-size: 14px;
}

.sol-time {
  color: #909399;
  font-size: 12px;
}

.sol-badges {
  display: flex;
  gap: 8px;
}

.sol-content {
  color: #606266;
  line-height: 1.7;
  white-space: pre-wrap;
  margin-bottom: 12px;
}

.sol-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.side-col {
  position: sticky;
  top: 80px;
}

.status-guide {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.status-item p {
  color: #909399;
  font-size: 13px;
  line-height: 1.5;
}

@media (max-width: 900px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
  .side-col {
    position: static;
  }
}
</style>
