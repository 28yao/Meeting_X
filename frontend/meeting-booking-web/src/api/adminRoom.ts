import http from './http'
import type { ApiResponse } from '../types/api'

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

export async function listAdminRooms(): Promise<ApiResponse<AdminRoom[]>> {
  const res = await http.get<ApiResponse<AdminRoom[]>>('/admin/rooms')
  return res.data
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
