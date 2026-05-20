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
