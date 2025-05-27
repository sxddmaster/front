<template>
  <div class="home-wrapper">
    <div class="home-row-flex">
      <div class="tile" @click="goTo('chat')">
        <div class="tile-content">
          <el-icon size="72"><ChatDotRound /></el-icon>
          <h2>原始凭证上传</h2>
        </div>
      </div>
      <div class="tile" @click="goTo('table')">
        <div class="tile-content">
          <el-icon size="72"><List /></el-icon>
          <h2>记账凭证与财务报表</h2>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { ChatDotRound, List, ArrowRight } from '@element-plus/icons-vue'
import { useCompanyStore } from '../store/companyStore'

const router = useRouter()
const route = useRoute()
const companyStore = useCompanyStore()

// 页面加载时设置全局公司code
if (route.query.companyCode) {
  companyStore.setCompanyCode(route.query.companyCode as string)
} else if (!companyStore.companyCode) {
  // 没有公司参数也没全局公司，跳回公司列表
  router.replace({ name: 'company-list' })
}

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

.home-header, .tile-footer, .enter-hint { display: none; }

.home-row-flex {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  flex: 1;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 10px;
}

.tile {
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0,0,0,0.03);
  height: 340px;
  width: 320px;
  margin: 0 20px;
  flex: none;
}

.tile-content {
  height: 100%;
  padding: 32px 20px;
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
  margin: 18px 0 12px;
  font-size: 1.5rem;
  font-weight: 600;
  color: #303133;
}

.tile p {
  color: #606266;
  font-size: 1rem;
  margin: 0;
  line-height: 1.6;
}

.tile .el-icon {
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
  padding: 14px;
  border-radius: 16px;
  font-size: 32px;
}

@media (max-width: 768px) {
  .home-wrapper {
    position: relative;
    height: auto;
    min-height: 100vh;
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