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
  color: #fff;
  text-decoration: none;
  font-size: 14px;
}

.notify-link.router-link-active .notify-text {
  font-weight: 600;
  text-decoration: underline;
}

.notify-text {
  display: inline-block;
  padding: 0 4px;
}

:deep(.el-badge__content) {
  border: none;
}
</style>
