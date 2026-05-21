import http from './http'
import type { ApiResponse } from '../types/api'
import type { PageResult } from '../types/paging'

export interface AdminRoom {
  id: number
  name: string
  capacity: number
  floor?: string | null
  roomType?: string | null
  equipment?: string | null
  status: 'NORMAL' | 'MAINTENANCE'
}

export interface CreateAdminRoomRequest {
  name: string
  capacity: number
  floor?: string
  roomType?: string
  equipment?: string
}

export interface UpdateAdminRoomRequest {
  name?: string
  capacity?: number
  floor?: string
  roomType?: string
  equipment?: string
}

export async function listAdminRooms(page = 1): Promise<ApiResponse<PageResult<AdminRoom>>> {
  const res = await http.get<ApiResponse<PageResult<AdminRoom>>>('/admin/rooms', {
    params: { page },
  })
  return res.data
}

/** 合并各页结果，供改约下拉等需要全量会议室的场景 */
export async function listAllAdminRooms(): Promise<AdminRoom[]> {
  const first = await listAdminRooms(1)
  if (first.code !== 0 || !first.data) {
    return []
  }
  const all = [...first.data.items]
  const totalPages = Math.ceil(first.data.total / first.data.pageSize)
  let p = 2
  while (p <= totalPages) {
    const res = await listAdminRooms(p)
    if (res.code === 0 && res.data) {
      all.push(...res.data.items)
    }
    p++
  }
  return all
}

export async function createAdminRoom(
  data: CreateAdminRoomRequest,
): Promise<ApiResponse<AdminRoom>> {
  const res = await http.post<ApiResponse<AdminRoom>>('/admin/rooms', data)
  return res.data
}

export async function updateAdminRoom(
  id: number,
  data: UpdateAdminRoomRequest,
): Promise<ApiResponse<AdminRoom>> {
  const res = await http.put<ApiResponse<AdminRoom>>(`/admin/rooms/${id}`, data)
  return res.data
}

export async function updateAdminRoomStatus(
  id: number,
  status: 'NORMAL' | 'MAINTENANCE',
): Promise<ApiResponse<AdminRoom>> {
  const res = await http.patch<ApiResponse<AdminRoom>>(`/admin/rooms/${id}/status`, { status })
  return res.data
}

export async function deleteAdminRoom(id: number): Promise<ApiResponse<null>> {
  const res = await http.delete<ApiResponse<null>>(`/admin/rooms/${id}`)
  return res.data
}
