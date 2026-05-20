import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as authApi from '../api/auth'
import type { UserInfo } from '../types/api'

const TOKEN_KEY = 'meeting_token'
const USER_KEY = 'meeting_user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const user = ref<UserInfo | null>(readStoredUser())

  const isAuthenticated = computed(() => !!token.value)

  function readStoredUser(): UserInfo | null {
    const raw = localStorage.getItem(USER_KEY)
    if (!raw) {
      return null
    }
    try {
      return JSON.parse(raw) as UserInfo
    } catch {
      return null
    }
  }

  function persistSession(newToken: string, newUser: UserInfo) {
    token.value = newToken
    user.value = newUser
    localStorage.setItem(TOKEN_KEY, newToken)
    localStorage.setItem(USER_KEY, JSON.stringify(newUser))
  }

  function clearSession() {
    token.value = null
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  async function login(username: string, password: string) {
    const res = await authApi.login({
      username: username.trim(),
      password: password.trim(),
    })
    if (res.code !== 0 || !res.data) {
      throw new Error(res.message || '登录失败')
    }
    persistSession(res.data.token, res.data.user)
    return res.data.user
  }

  async function fetchMe() {
    const res = await authApi.getMe()
    if (res.code !== 0 || !res.data) {
      clearSession()
      throw new Error(res.message || '获取用户信息失败')
    }
    user.value = res.data
    localStorage.setItem(USER_KEY, JSON.stringify(res.data))
    return res.data
  }

  async function restoreSession() {
    if (!token.value) {
      return false
    }
    try {
      await fetchMe()
      return true
    } catch {
      clearSession()
      return false
    }
  }

  function logout() {
    clearSession()
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    fetchMe,
    restoreSession,
    logout,
  }
})
