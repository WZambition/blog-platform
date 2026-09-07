<template>
  <div class="home">
    <!-- 顶部横幅 -->
    <section class="hero">
      <div class="hero-content">
        <h1>欢迎来到 <span class="gradient">星语博客</span></h1>
        <p>在这里，每一个观点都是一颗星星，每一个技术问题都值得被认真回答。</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="$router.push('/ai-space')">
            <el-icon style="margin-right: 6px"><Star /></el-icon> 探索 AI 观点星空
          </el-button>
          <el-button size="large" @click="$router.push('/tech')">
            <el-icon style="margin-right: 6px"><ChatDotRound /></el-icon> 进入技术讨论区
          </el-button>
        </div>
      </div>
    </section>

    <!-- 两大模块介绍 -->
    <div class="page-container">
      <div class="modules">
        <!-- AI观点星空 -->
        <div class="module-card hover-card" @click="$router.push('/ai-space')">
          <div class="module-icon ai-icon">
            <el-icon :size="40"><Star /></el-icon>
          </div>
          <h3>AI 观点星空</h3>
          <p>每个人都可以在这里发表自己的观点，它们像星星一样在夜空中闪烁。点击任意一颗星星，观点就会变成一个议题，大家可以围绕它展开讨论。</p>
          <div class="module-tags">
            <el-tag type="warning" effect="light">观点即星星</el-tag>
            <el-tag type="danger" effect="light">点击成议题</el-tag>
            <el-tag type="success" effect="light">自由讨论</el-tag>
          </div>
        </div>

        <!-- 技术讨论区 -->
        <div class="module-card hover-card" @click="$router.push('/tech')">
          <div class="module-icon tech-icon">
            <el-icon :size="40"><ChatDotRound /></el-icon>
          </div>
          <h3>技术讨论区</h3>
          <p>以卡片形式展示技术问题，上方配图、下方描述。发起问题后可收到他人方案，支持验证中、已解决、未解决三种状态流转。</p>
          <div class="module-tags">
            <el-tag type="primary" effect="light">卡片展示</el-tag>
            <el-tag type="success" effect="light">方案提交</el-tag>
            <el-tag type="warning" effect="light">状态流转</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  min-height: 100%;
}

.hero {
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  color: #fff;
  padding: 80px 20px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero h1 {
  font-size: 42px;
  margin-bottom: 16px;
}

.gradient {
  background: linear-gradient(90deg, #409EFF, #7c4dff, #ff6b6b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero p {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 32px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.modules {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-top: 40px;
}

.module-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  cursor: pointer;
  border: 1px solid #ebeef5;
}

.module-icon {
  width: 72px;
  height: 72px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.ai-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.tech-icon {
  background: linear-gradient(135deg, #409EFF, #36cfc9);
  color: #fff;
}

.module-card h3 {
  font-size: 22px;
  margin-bottom: 12px;
  color: #303133;
}

.module-card p {
  color: #606266;
  line-height: 1.7;
  margin-bottom: 16px;
}

.module-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .modules {
    grid-template-columns: 1fr;
  }
  .hero h1 {
    font-size: 30px;
  }
}
</style>
