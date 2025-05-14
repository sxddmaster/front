<template>
  <div class="home-wrapper">
    <div class="home-header">
      <h1>欢迎使用 AI 助手</h1>
      <p class="subtitle">智能对话与数据管理，让工作更高效</p>
    </div>
    <div class="home-row-flex">
      <div class="tile" @click="goTo('chat')">
        <div class="tile-content">
          <el-icon size="72"><ChatDotRound /></el-icon>
          <h2>Chat with AI</h2>
          <p>与 AI 助手对话，获取智能回复和建议</p>
          <div class="tile-footer">
            <span class="enter-hint">点击进入 <el-icon><ArrowRight /></el-icon></span>
          </div>
        </div>
      </div>
      <div class="tile" @click="goTo('table')">
        <div class="tile-content">
          <el-icon size="72"><List /></el-icon>
          <h2>Data Table</h2>
          <p>查看和管理数据，支持筛选和导出</p>
          <div class="tile-footer">
            <span class="enter-hint">点击进入 <el-icon><ArrowRight /></el-icon></span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ChatDotRound, List, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const goTo = (page: string) => {
  router.push({ name: page })
}
</script>

<style scoped>
.home-wrapper {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
}

.home-header {
  text-align: center;
  padding: 60px 0 40px;
  flex-shrink: 0;
}

.home-header h1 {
  font-size: 3rem;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.subtitle {
  font-size: 1.3rem;
  color: #606266;
  margin: 0;
}

.home-row-flex {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
  flex: 1;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

.tile {
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0,0,0,0.03);
  height: 500px;
  width: 400px;
  margin: 0 20px;
  flex: none;
}

.tile-content {
  height: 100%;
  padding: 60px 40px;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.tile:hover {
  box-shadow: 0 8px 40px rgba(0,0,0,0.1);
  transform: translateY(-6px);
  border-color: rgba(64, 158, 255, 0.1);
}

.tile h2 {
  margin: 32px 0 20px;
  font-size: 2.4rem;
  font-weight: 600;
  color: #303133;
}

.tile p {
  color: #606266;
  font-size: 1.2rem;
  margin: 0;
  line-height: 1.6;
}

.tile .el-icon {
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
  padding: 24px;
  border-radius: 24px;
  font-size: 48px;
}

.tile-footer {
  margin-top: auto;
  padding-top: 32px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.enter-hint {
  color: #409EFF;
  font-size: 1.1rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.enter-hint .el-icon {
  font-size: 1.1rem;
  padding: 0;
  background: none;
}

@media (max-width: 768px) {
  .home-wrapper {
    position: relative;
    height: auto;
    min-height: 100vh;
  }
  .home-header {
    padding: 40px 0 24px;
  }
  .home-header h1 {
    font-size: 2.2rem;
  }
  .subtitle {
    font-size: 1.1rem;
  }
  .home-row-flex {
    flex-direction: column;
    padding: 0 24px 40px;
  }
  .tile {
    height: 400px;
    width: 100%;
    max-width: 400px;
  }
  .tile-content {
    padding: 40px 24px;
  }
  .tile h2 {
    font-size: 1.8rem;
    margin: 24px 0 16px;
  }
  .tile p {
    font-size: 1.1rem;
  }
  .tile .el-icon {
    padding: 20px;
    font-size: 40px;
  }
}
</style> 