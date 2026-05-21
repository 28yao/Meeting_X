import http from './http'
import type { ApiResponse } from '../types/api'
import type { PageResult } from '../types/paging'

export interface NotificationItem {
  id: number
  type: string
  title: string
  content: string
  relatedBookingId: number | null
  read: boolean
  createdAt: string
}

export interface UnreadCountResult {
  count: number
}

export async function listNotifications(
  page = 1,
): Promise<ApiResponse<PageResult<NotificationItem>>> {
  const res = await http.get<ApiResponse<PageResult<NotificationItem>>>('/notifications', {
    params: { page },
  })
  return res.data
}

export async function getUnreadCount(): Promise<ApiResponse<UnreadCountResult>> {
  const res = await http.get<ApiResponse<UnreadCountResult>>('/notifications/unread-count')
  return res.data
}

export async function markNotificationRead(id: number): Promise<ApiResponse<null>> {
  const res = await http.post<ApiResponse<null>>(`/notifications/${id}/read`)
  return res.data
}

export async function markAllNotificationsRead(): Promise<ApiResponse<null>> {
  const res = await http.post<ApiResponse<null>>('/notifications/read-all')
  return res.data
}
