import http from './http'
import type { ApiResponse } from '../types/api'
import type { PageResult } from '../types/paging'

export interface AdminUser {
  id: number
  username: string
  displayName: string
  role: 'EMPLOYEE' | 'ADMIN'
  enabled: boolean
}

export interface CreateAdminUserRequest {
  username: string
  role: 'EMPLOYEE' | 'ADMIN'
  enabled?: boolean
}

export interface UpdateAdminUserRequest {
  displayName?: string
  role?: 'EMPLOYEE' | 'ADMIN'
  enabled?: boolean
}

export async function listAdminUsers(page = 1): Promise<ApiResponse<PageResult<AdminUser>>> {
  const res = await http.get<ApiResponse<PageResult<AdminUser>>>('/admin/users', {
    params: { page },
  })
  return res.data
}

export async function createAdminUser(
  data: CreateAdminUserRequest,
): Promise<ApiResponse<AdminUser>> {
  const res = await http.post<ApiResponse<AdminUser>>('/admin/users', data)
  return res.data
}

export async function updateAdminUser(
  id: number,
  data: UpdateAdminUserRequest,
): Promise<ApiResponse<AdminUser>> {
  const res = await http.put<ApiResponse<AdminUser>>(`/admin/users/${id}`, data)
  return res.data
}

export async function resetAdminUserPassword(id: number): Promise<ApiResponse<null>> {
  const res = await http.post<ApiResponse<null>>(`/admin/users/${id}/reset-password`)
  return res.data
}

export async function deleteAdminUser(id: number): Promise<ApiResponse<null>> {
  const res = await http.delete<ApiResponse<null>>(`/admin/users/${id}`)
  return res.data
}
