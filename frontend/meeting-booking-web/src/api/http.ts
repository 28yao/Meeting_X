import axios, { type AxiosError } from 'axios'
import { useAuthStore } from '../stores/auth'

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

http.interceptors.request.use((config) => {
  const url = config.url || ''
  const isPublicAuth = url.includes('/auth/login')
  if (!isPublicAuth) {
    const auth = useAuthStore()
    if (auth.token) {
      config.headers.Authorization = `Bearer ${auth.token}`
    }
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error: AxiosError<{ code?: number; message?: string }>) => {
    const url = error.config?.url || ''
    const isLoginRequest = url.includes('/auth/login')
    if (error.response?.status === 401 && !isLoginRequest) {
      const auth = useAuthStore()
      auth.logout()
      import('../router').then(({ default: router }) => {
        if (router.currentRoute.value.name !== 'login') {
          router.replace({
            name: 'login',
            query: { redirect: router.currentRoute.value.fullPath },
          })
        }
      })
    }
    return Promise.reject(error)
  },
)

export default http
