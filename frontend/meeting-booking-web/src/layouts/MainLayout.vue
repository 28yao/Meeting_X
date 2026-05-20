<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <el-container class="layout-root">
    <el-header class="layout-header">
      <span class="layout-title">会议室预约系统</span>
      <span v-if="route.meta.title" class="layout-subtitle">{{ route.meta.title }}</span>
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

.layout-main {
  padding: 24px;
  background: #f5f7fa;
}
</style>
