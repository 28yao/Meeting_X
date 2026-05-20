/** 与后端 ApiResponse 一致 */
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface UserInfo {
  id: number
  username: string
  displayName: string
  role: 'EMPLOYEE' | 'ADMIN'
}

export interface LoginResult {
  token: string
  user: UserInfo
}
