/**
 * 预约展示状态标签与样式映射。
 */
export type BookingDisplayStatus =
  | 'UPCOMING'
  | 'IN_PROGRESS'
  | 'ENDED'
  | 'CANCELLED'

const STATUS_LABELS: Record<BookingDisplayStatus, string> = {
  UPCOMING: '未开始',
  IN_PROGRESS: '进行中',
  ENDED: '已结束',
  CANCELLED: '已取消',
}

const STATUS_TAG_TYPES: Record<
  BookingDisplayStatus,
  'success' | 'warning' | 'info' | 'danger'
> = {
  UPCOMING: 'success',
  IN_PROGRESS: 'warning',
  ENDED: 'info',
  CANCELLED: 'danger',
}

export function getStatusLabel(status: BookingDisplayStatus): string {
  return STATUS_LABELS[status] ?? status
}

export function getStatusTagType(
  status: BookingDisplayStatus,
): 'success' | 'warning' | 'info' | 'danger' {
  return STATUS_TAG_TYPES[status] ?? 'info'
}

export function formatDateTime(iso: string): string {
  if (!iso) {
    return ''
  }
  const normalized = iso.replace('T', ' ')
  return normalized.length >= 16 ? normalized.substring(0, 16) : normalized
}
