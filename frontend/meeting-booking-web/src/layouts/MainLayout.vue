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
const isAdmin = computed(() => auth.user?.role === 'ADMIN')

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
        <router-link v-if="isAdmin" class="nav-link" to="/admin/users">用户管理</router-link>
        <router-link v-if="isAdmin" class="nav-link" to="/admin/rooms">会议室管理</router-link>
        <router-link v-if="isAdmin" class="nav-link" to="/admin/bookings">预约管理</router-link>
      </nav>
      <div class="layout-actions">
        <span class="layout-user">你好，{{ displayName }}</span>
        <el-button class="logout-btn" link @click="onLogout">退出登录</el-button>
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
  background: var(--app-bg);
}

.layout-header {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px 20px;
  padding: 0 24px;
  height: auto;
  min-height: 56px;
  background: var(--app-primary-dark);
  color: #faf9f7;
  box-shadow: var(--app-shadow-sm);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.layout-title {
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.layout-subtitle {
  font-size: 13px;
  color: rgba(250, 249, 247, 0.75);
  padding-left: 12px;
  border-left: 1px solid rgba(250, 249, 247, 0.25);
}

.layout-nav {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px 8px;
}

.nav-link {
  color: rgba(250, 249, 247, 0.88);
  text-decoration: none;
  font-size: 14px;
  padding: 6px 10px;
  border-radius: var(--app-radius-sm);
  transition: background-color 0.2s ease, color 0.2s ease;
  cursor: pointer;
}

.nav-link:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.nav-link.router-link-active {
  font-weight: 600;
  color: #fff;
  background: rgba(255, 255, 255, 0.14);
  text-decoration: none;
}

.layout-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.layout-user {
  font-size: 14px;
  color: rgba(250, 249, 247, 0.9);
}

.logout-btn {
  color: rgba(250, 249, 247, 0.95) !important;
  font-weight: 500;
}

.logout-btn:hover {
  color: var(--app-accent) !important;
}

.layout-main {
  padding: 28px 24px 40px;
  background: var(--app-bg);
}
</style>
