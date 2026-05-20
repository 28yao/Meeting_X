import axios from 'axios'

/**
 * Axios 实例：基址来自环境变量 VITE_API_BASE_URL。
 */
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

export default http
