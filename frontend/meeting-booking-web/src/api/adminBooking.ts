import http from './http'
import type { ApiResponse } from '../types/api'
import type { PageResult } from './booking'

export interface AdminBookingItem {
  bookingId: number
  title: string
  roomId: number
  roomName: string
  organizerId: number
  organizerDisplayName: string
  startTime: string
  endTime: string
  status: string
  displayStatus: 'UPCOMING' | 'IN_PROGRESS' | 'ENDED' | 'CANCELLED'
  editable: boolean
  cancellable: boolean
}

export interface UpdateAdminBookingRequest {
  roomId: number
  startTime: string
  endTime: string
}

export async function listAdminBookings(
  page = 1,
): Promise<ApiResponse<PageResult<AdminBookingItem>>> {
  const res = await http.get<ApiResponse<PageResult<AdminBookingItem>>>('/admin/bookings', {
    params: { page },
  })
  return res.data
}

export async function updateAdminBooking(
  id: number,
  data: UpdateAdminBookingRequest,
): Promise<ApiResponse<AdminBookingItem>> {
  const res = await http.put<ApiResponse<AdminBookingItem>>(`/admin/bookings/${id}`, data)
  return res.data
}

export async function cancelAdminBooking(id: number): Promise<ApiResponse<null>> {
  const res = await http.post<ApiResponse<null>>(`/admin/bookings/${id}/cancel`)
  return res.data
}
