<template>
  <div class="ai-space">
    <!-- 星空背景 -->
    <div class="starry-sky" ref="skyRef">
      <!-- 背景小星星 -->
      <div v-for="i in 80" :key="'bg' + i" class="bg-star"
           :style="bgStarStyle(i)"></div>

      <!-- 观点星星 -->
      <div v-for="opinion in opinions" :key="opinion.id"
           class="opinion-star"
           :style="starStyle(opinion)"
           @click="handleStarClick(opinion)">
        <div class="star-glow" :style="{ background: opinion.starColor }"></div>
        <div class="star-core" :style="{ background: opinion.starColor }">
          <span class="star-title">{{ opinion.title }}</span>
        </div>
        <div class="star-tooltip">
          <strong>{{ opinion.title }}</strong>
          <p>{{ opinion.content }}</p>
          <small>by {{ opinion.author?.nickname || '匿名' }} · {{ opinion.commentCount }} 讨论</small>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && opinions.length === 0" class="empty-sky">
        <el-empty description="夜空还很安静，来点亮第一颗星星吧">
          <el-button type="primary" @click="openPublishDialog">发布观点</el-button>
        </el-empty>
      </div>

      <!-- 顶部操作栏 -->
      <div class="sky-toolbar">
        <h2 class="sky-title">
          <el-icon><Star /></el-icon> AI 观点星空
        </h2>
        <p class="sky-subtitle">每一个观点都是一颗星星，点击星星进入议题讨论</p>
        <el-button type="primary" size="large" @click="openPublishDialog">
          <el-icon style="margin-right: 6px"><Plus /></el-icon> 发布观点
        </el-button>
      </div>
    </div>

    <!-- 发布观点对话框 -->
    <el-dialog v-model="publishVisible" title="发布你的观点" width="520px">
      <el-form :model="publishForm" label-width="70px">
        <el-form-item label="观点标题" required>
          <el-input v-model="publishForm.title" placeholder="用一句话概括你的观点" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="观点内容" required>
          <el-input v-model="publishForm.content" type="textarea" :rows="4"
                    placeholder="详细描述你的观点..." />
        </el-form-item>
        <el-form-item label="星星颜色">
          <div class="color-picker">
            <div v-for="c in colors" :key="c" class="color-dot"
                 :class="{ active: publishForm.starColor === c }"
                 :style="{ background: c }"
                 @click="publishForm.starColor = c"></div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishVisible = false">取消</el-button>
        <el-button type="primary" :loading="publishing" @click="publishOpinion">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { opinionApi } from '../api'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const opinions = ref([])
const loading = ref(true)
const skyRef = ref(null)
const publishVisible = ref(false)
const publishing = ref(false)

const colors = ['#FFD700', '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#DDA0DD', '#F8C471', '#85C1E9']

const publishForm = ref({
  title: '',
  content: '',
  starColor: '#FFD700'
})

// 生成背景星星样式
function bgStarStyle(i) {
  const size = Math.random() * 2 + 1
  return {
    left: Math.random() * 100 + '%',
    top: Math.random() * 100 + '%',
    width: size + 'px',
    height: size + 'px',
    animationDelay: Math.random() * 3 + 's',
    animationDuration: (Math.random() * 2 + 2) + 's'
  }
}

// 观点星星样式
function starStyle(opinion) {
  const size = (opinion.starSize || 3) * 14 + 30
  return {
    left: (opinion.posX || Math.random() * 80) + '%',
    top: (opinion.posY || Math.random() * 70 + 10) + '%',
    width: size + 'px',
    height: size + 'px',
    '--star-color': opinion.starColor,
    animationDelay: (opinion.id % 5) * 0.5 + 's'
  }
}

// 加载观点
async function loadOpinions() {
  loading.value = true
  try {
    const res = await opinionApi.findAll()
    opinions.value = res.data
  } catch (e) {
    // 错误已由拦截器提示
  } finally {
    loading.value = false
  }
}

// 点击星星
async function handleStarClick(opinion) {
  // 如果还不是议题，先转化为议题
  if (!opinion.isTopic) {
    try {
      await ElMessageBox.confirm(
        `点击后观点「${opinion.title}」将转化为一个议题，大家可以围绕它展开讨论。是否继续？`,
        '转化为议题',
        { confirmButtonText: '进入议题', cancelButtonText: '取消', type: 'info' }
      )
      await opinionApi.convertToTopic(opinion.id)
      opinion.isTopic = true
    } catch (e) {
      return // 用户取消
    }
  }
  router.push(`/opinion/${opinion.id}`)
}

// 打开发布对话框
function openPublishDialog() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再发布观点')
    router.push('/login')
    return
  }
  publishForm.value = { title: '', content: '', starColor: '#FFD700' }
  publishVisible.value = true
}

// 发布观点
async function publishOpinion() {
  if (!publishForm.value.title.trim() || !publishForm.value.content.trim()) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  publishing.value = true
  try {
    await opinionApi.create(publishForm.value, userStore.userId)
    ElMessage.success('观点发布成功，它已成为夜空中一颗新的星星！')
    publishVisible.value = false
    loadOpinions()
  } catch (e) {
    // 错误已提示
  } finally {
    publishing.value = false
  }
}

onMounted(() => {
  loadOpinions()
})
</script>

<style scoped>
.ai-space {
  height: calc(100vh - 60px);
  position: relative;
  overflow: hidden;
}

.starry-sky {
  position: relative;
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at bottom, #1b2735 0%, #090a0f 100%);
  overflow: hidden;
}

/* 背景小星星 */
.bg-star {
  position: absolute;
  background: #fff;
  border-radius: 50%;
  opacity: 0.6;
  animation: twinkle 3s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.2; }
  50% { opacity: 0.9; }
}

/* 顶部工具栏 */
.sky-toolbar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 20;
  padding: 24px 32px;
  display: flex;
  align-items: center;
  gap: 16px;
  background: linear-gradient(to bottom, rgba(0,0,0,0.6), transparent);
}

.sky-title {
  color: #fff;
  font-size: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.sky-subtitle {
  color: rgba(255,255,255,0.7);
  font-size: 14px;
  flex: 1;
  margin: 0;
}

/* 观点星星 */
.opinion-star {
  position: absolute;
  transform: translate(-50%, -50%);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: starPulse 3s ease-in-out infinite;
  z-index: 10;
}

@keyframes starPulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.15); }
}

.star-glow {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  filter: blur(12px);
  opacity: 0.5;
  animation: glowPulse 3s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.7; }
}

.star-core {
  position: relative;
  width: 60%;
  height: 60%;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 20px var(--star-color);
  transition: transform 0.2s;
}

.opinion-star:hover .star-core {
  transform: scale(1.3);
}

.star-title {
  color: #fff;
  font-size: 10px;
  font-weight: 600;
  text-align: center;
  padding: 2px;
  text-shadow: 0 1px 3px rgba(0,0,0,0.8);
  max-width: 90%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 悬浮提示 */
.star-tooltip {
  position: absolute;
  bottom: 110%;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0,0,0,0.85);
  color: #fff;
  padding: 12px 16px;
  border-radius: 8px;
  width: 220px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s;
  z-index: 30;
  pointer-events: none;
  backdrop-filter: blur(4px);
}

.opinion-star:hover .star-tooltip {
  opacity: 1;
  visibility: visible;
  bottom: 120%;
}

.star-tooltip strong {
  display: block;
  margin-bottom: 6px;
  color: var(--star-color);
}

.star-tooltip p {
  font-size: 12px;
  line-height: 1.5;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.star-tooltip small {
  color: rgba(255,255,255,0.6);
  font-size: 11px;
}

/* 空状态 */
.empty-sky {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 15;
}

.empty-sky :deep(.el-empty__description p) {
  color: rgba(255,255,255,0.7);
}

/* 颜色选择器 */
.color-picker {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.color-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.color-dot.active {
  border-color: #409EFF;
  transform: scale(1.2);
}
</style>
