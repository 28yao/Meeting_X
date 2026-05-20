<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNotificationStore } from '../stores/notification'
import NotificationBadge from '../components/NotificationBadge.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const notificationStore = useNotificationStore()

const displayName = computed(() => auth.user?.displayName || auth.user?.username || '')

onMounted(() => {
  notificationStore.refreshUnreadCount()
})

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
      <nav class="layout-nav">
        <router-link class="nav-link" to="/book">预约会议室</router-link>
        <router-link class="nav-link" to="/my-bookings">我的预约</router-link>
        <NotificationBadge />
      </nav>
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

.layout-nav {
  display: flex;
  gap: 16px;
}

.nav-link {
  color: #fff;
  text-decoration: none;
  font-size: 14px;
}

.nav-link.router-link-active {
  font-weight: 600;
  text-decoration: underline;
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
