<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listNotifications,
  markAllNotificationsRead,
  markNotificationRead,
  type NotificationItem,
} from '../api/notification'
import { useNotificationStore } from '../stores/notification'
import { resolveAxiosError } from '../utils/errorMessages'
import { formatDateTime } from '../utils/bookingStatus'

const loading = ref(false)
const items = ref<NotificationItem[]>([])
const notificationStore = useNotificationStore()

async function loadList() {
  loading.value = true
  try {
    const res = await listNotifications()
    if (res.code !== 0) {
      ElMessage.error(res.message || '加载失败')
      return
    }
    items.value = res.data ?? []
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  } finally {
    loading.value = false
  }
}

async function onItemClick(item: NotificationItem) {
  if (item.read) {
    return
  }
  try {
    const res = await markNotificationRead(item.id)
    if (res.code !== 0) {
      ElMessage.error(res.message || '标记已读失败')
      return
    }
    item.read = true
    await notificationStore.refreshUnreadCount()
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  }
}

async function onReadAll() {
  try {
    const res = await markAllNotificationsRead()
    if (res.code !== 0) {
      ElMessage.error(res.message || '操作失败')
      return
    }
    ElMessage.success('已全部标为已读')
    await loadList()
    await notificationStore.refreshUnreadCount()
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  }
}

onMounted(async () => {
  await loadList()
  await notificationStore.refreshUnreadCount()
})
</script>

<template>
  <div class="notifications-page">
    <div class="page-header">
      <h2 class="page-title">通知中心</h2>
      <el-button
        v-if="items.some((i) => !i.read)"
        type="primary"
        link
        @click="onReadAll"
      >
        全部已读
      </el-button>
    </div>
    <el-card v-loading="loading" shadow="never">
      <el-empty v-if="!loading && items.length === 0" description="暂无通知" />
      <div v-else class="notify-list">
        <div
          v-for="item in items"
          :key="item.id"
          class="notify-item"
          :class="{ unread: !item.read }"
          @click="onItemClick(item)"
        >
          <div class="notify-item-header">
            <span class="notify-title">{{ item.title }}</span>
            <el-tag v-if="!item.read" type="danger" size="small">未读</el-tag>
            <el-tag v-else type="info" size="small">已读</el-tag>
          </div>
          <p class="notify-content">{{ item.content }}</p>
          <span class="notify-time">{{ formatDateTime(item.createdAt) }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.notifications-page {
  max-width: 720px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.notify-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notify-item {
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  cursor: pointer;
  transition: background 0.2s;
}

.notify-item.unread {
  background: #ecf5ff;
  border-color: #d9ecff;
}

.notify-item:hover {
  background: #f5f7fa;
}

.notify-item-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.notify-title {
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.notify-content {
  margin: 0 0 8px;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.notify-time {
  font-size: 12px;
  color: #909399;
}
</style>
