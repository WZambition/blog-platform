<template>
  <div class="page-container editor-page">
    <div class="editor-header">
      <h2 class="page-title">
        <el-icon><EditPen /></el-icon> {{ isEdit ? '编辑文章' : '写文章' }}
      </h2>
      <el-button text @click="$router.push('/blog')">
        <el-icon><ArrowLeft /></el-icon> 返回博客列表
      </el-button>
    </div>

    <div class="editor-card">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2"
                    placeholder="可选，一句话概括文章内容（不填则自动截取正文开头）" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="可选，粘贴封面图片的URL" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="用逗号分隔，如：生活,技术,随笔" />
        </el-form-item>
        <el-form-item label="正文" required>
          <el-input v-model="form.content" type="textarea" :rows="16"
                    placeholder="开始书写你的文章...（支持多段落，空行分段）" />
        </el-form-item>
      </el-form>
      <div class="editor-actions">
        <el-button @click="$router.push('/blog')">取消</el-button>
        <el-button type="primary" :loading="saving" @click="savePost">发布文章</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postApi } from '../api'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const form = ref({
  title: '',
  summary: '',
  coverImage: '',
  tags: '',
  content: ''
})
const saving = ref(false)

const isEdit = computed(() => !!route.params.id)

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (isEdit.value) {
    try {
      const res = await postApi.getById(route.params.id)
      const post = res.data
      if (post.author?.id !== userStore.userId) {
        ElMessage.warning('只能编辑自己的文章')
        router.push('/blog')
        return
      }
      form.value = {
        title: post.title,
        summary: post.summary || '',
        coverImage: post.coverImage || '',
        tags: post.tags || '',
        content: post.content
      }
    } catch (e) {
      // 错误已提示
    }
  }
})

async function savePost() {
  if (!form.value.title.trim()) {
    ElMessage.warning('请填写文章标题')
    return
  }
  if (!form.value.content.trim()) {
    ElMessage.warning('请填写文章正文')
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await postApi.update(route.params.id, form.value, userStore.userId)
      ElMessage.success('文章更新成功')
      router.push(`/blog/${route.params.id}`)
    } else {
      const res = await postApi.create(form.value, userStore.userId)
      ElMessage.success('文章发布成功')
      router.push(`/blog/${res.data.id}`)
    }
  } catch (e) {
    // 错误已提示
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.editor-page {
  max-width: 860px;
}

.editor-header {
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

.editor-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  border: 1px solid #ebeef5;
}

.editor-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}
</style>
