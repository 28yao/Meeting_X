import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadCount } from '../api/notification'

/**
 * 站内通知未读数状态（顶栏角标等共用）。
 */
export const useNotificationStore = defineStore('notification', () => {
  const unreadCount = ref(0)
  const loading = ref(false)

  async function refreshUnreadCount() {
    loading.value = true
    try {
      const res = await getUnreadCount()
      if (res.code === 0 && res.data) {
        unreadCount.value = res.data.count
      }
    } catch {
      // 静默失败，避免影响主流程
    } finally {
      loading.value = false
    }
  }

  return {
    unreadCount,
    loading,
    refreshUnreadCount,
  }
})
