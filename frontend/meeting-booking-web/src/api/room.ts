import http from './http'
import type { ApiResponse } from '../types/api'

export interface MeetingRoom {
  id: number
  name: string
  capacity: number
  floor?: string
  roomType?: string
  equipment?: string
  status: string
}

export async function getAvailableRooms(
  date: string,
  startTime: string,
  endTime: string,
): Promise<ApiResponse<MeetingRoom[]>> {
  const res = await http.get<ApiResponse<MeetingRoom[]>>('/rooms/available', {
    params: { date, startTime, endTime },
  })
  return res.data
}
