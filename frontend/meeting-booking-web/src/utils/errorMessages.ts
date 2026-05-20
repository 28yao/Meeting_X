/**
 * 后端业务错误码到用户可读文案的映射。
 */
const ERROR_MESSAGE_MAP: Record<number, string> = {
  40001: '结束时间必须晚于开始时间',
  40002: '不允许跨天预约，请选择同一天内的时段',
  40003: '时间须按 15 分钟对齐（如 10:00、10:15）',
  40004: '不能预约过去的时间',
  40005: '最多只能提前 30 天预约',
  40901: '该时段已被预约，请更换时间或会议室',
  40902: '该会议室维护中，暂不可预约',
  40401: '会议室不存在',
  40101: '账号或密码错误',
}

export function resolveErrorMessage(
  code?: number,
  fallback?: string,
): string {
  if (code != null && ERROR_MESSAGE_MAP[code]) {
    return ERROR_MESSAGE_MAP[code]
  }
  return fallback || '操作失败，请稍后重试'
}

export function resolveAxiosError(err: unknown): string {
  const axiosErr = err as {
    response?: { data?: { code?: number; message?: string } }
    message?: string
  }
  const data = axiosErr.response?.data
  return resolveErrorMessage(data?.code, data?.message || axiosErr.message)
}
