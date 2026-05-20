<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const displayName = computed(() => auth.user?.displayName || auth.user?.username || '')

function onLogout() {
  auth.logout()
  router.replace({ name: 'login' })
}
</script>

<template>
  <el-container class="layout-root">
    <el-header class="layout-header">
      <span class="layout-title">会议室预约系统</span>
      <span v-if="route.meta.title" class="layout-subtitle">{{ route.meta.title }}</span>
      <div class="layout-actions">
        <span class="layout-user">你好，{{ displayName }}</span>
        <el-button type="primary" link @click="onLogout">退出</el-button>
      </div>
    </el-header>
    <el-main class="layout-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<style scoped>
.layout-root {
  min-height: 100vh;
}

.layout-header {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #409eff;
  color: #fff;
}

.layout-title {
  font-size: 18px;
  font-weight: 600;
}

.layout-subtitle {
  font-size: 14px;
  opacity: 0.9;
}

.layout-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

.layout-user {
  font-size: 14px;
}

.layout-main {
  padding: 24px;
  background: #f5f7fa;
}
</style>
