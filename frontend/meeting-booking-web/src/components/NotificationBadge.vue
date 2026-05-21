<script setup lang="ts">
import { onMounted } from 'vue'
import { useNotificationStore } from '../stores/notification'

const notificationStore = useNotificationStore()

onMounted(() => {
  notificationStore.refreshUnreadCount()
})
</script>

<template>
  <router-link class="notify-link" to="/notifications">
    <el-badge
      :value="notificationStore.unreadCount"
      :hidden="notificationStore.unreadCount === 0"
      :max="99"
    >
      <span class="notify-text">通知</span>
    </el-badge>
  </router-link>
</template>

<style scoped>
.notify-link {
  color: rgba(250, 249, 247, 0.88);
  text-decoration: none;
  font-size: 14px;
  padding: 6px 10px;
  border-radius: var(--app-radius-sm);
  transition: background-color 0.2s ease, color 0.2s ease;
  cursor: pointer;
}

.notify-link:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.notify-link.router-link-active {
  font-weight: 600;
  color: #fff;
  background: rgba(255, 255, 255, 0.14);
}

.notify-text {
  display: inline-block;
}

:deep(.el-badge__content) {
  border: none;
  box-shadow: none;
}
</style>
