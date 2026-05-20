import http from './http'
import type { ApiResponse } from '../types/api'

export interface CreateBookingRequest {
  roomId: number
  title: string
  startTime: string
  endTime: string
}

export interface CreateBookingResult {
  bookingId: number
  roomId: number
  title: string
  startTime: string
  endTime: string
}

export async function createBooking(
  data: CreateBookingRequest,
): Promise<ApiResponse<CreateBookingResult>> {
  const res = await http.post<ApiResponse<CreateBookingResult>>('/bookings', data)
  return res.data
}

export interface MyBookingItem {
  bookingId: number
  title: string
  roomId: number
  roomName: string
  startTime: string
  endTime: string
  status: string
  displayStatus: 'UPCOMING' | 'IN_PROGRESS' | 'ENDED' | 'CANCELLED'
  cancellable: boolean
}

export interface PageResult<T> {
  items: T[]
  page: number
  pageSize: number
  total: number
}

export async function listMyBookings(
  page = 1,
): Promise<ApiResponse<PageResult<MyBookingItem>>> {
  const res = await http.get<ApiResponse<PageResult<MyBookingItem>>>('/bookings/mine', {
    params: { page },
  })
  return res.data
}

export async function cancelBooking(bookingId: number): Promise<ApiResponse<null>> {
  const res = await http.post<ApiResponse<null>>(`/bookings/${bookingId}/cancel`)
  return res.data
}
