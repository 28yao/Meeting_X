import http from './http'
import type { ApiResponse, LoginResult, UserInfo } from '../types/api'

export interface LoginRequest {
  username: string
  password: string
}

export async function login(data: LoginRequest): Promise<ApiResponse<LoginResult>> {
  const res = await http.post<ApiResponse<LoginResult>>('/auth/login', data)
  return res.data
}

export async function getMe(): Promise<ApiResponse<UserInfo>> {
  const res = await http.get<ApiResponse<UserInfo>>('/auth/me')
  return res.data
}
