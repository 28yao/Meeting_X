<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '../api/http'

const healthStatus = ref<string>('检查中…')
const apiBase = import.meta.env.VITE_API_BASE_URL

onMounted(async () => {
  try {
    const res = await http.get('/health')
    if (res.data?.code === 0 && res.data?.data?.status === 'UP') {
      healthStatus.value = '后端服务正常'
    } else {
      healthStatus.value = '后端响应异常'
    }
  } catch {
    healthStatus.value = '后端未启动或无法连接（页面仍可正常打开）'
  }
})
</script>

<template>
  <div class="login-placeholder">
    <el-card class="login-card" shadow="hover">
      <template #header>
        <span>会议室预约系统</span>
      </template>
      <p class="hint">登录功能将在模块 2 实现，当前为脚手架占位页。</p>
      <el-descriptions :column="1" border size="small">
        <el-descriptions-item label="API 基址">{{ apiBase }}</el-descriptions-item>
        <el-descriptions-item label="健康检查">{{ healthStatus }}</el-descriptions-item>
      </el-descriptions>
      <p class="footer-hint">模块 1 验收：能打开本页即表示前端脚手架正常。</p>
    </el-card>
  </div>
</template>

<style scoped>
.login-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  background: var(--app-bg);
}

.login-card {
  width: 420px;
  max-width: 92vw;
  border: 1px solid var(--app-border-light);
}

.hint {
  margin: 0 0 16px;
  color: var(--app-text-secondary);
  line-height: 1.6;
}

.footer-hint {
  margin: 16px 0 0;
  font-size: 12px;
  color: var(--app-text-muted);
}
</style>
