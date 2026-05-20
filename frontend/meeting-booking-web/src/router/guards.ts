import type { Router } from 'vue-router'
import { useAuthStore } from '../stores/auth'

/**
 * 注册全局路由守卫：未登录访问受保护页时跳转登录。
 */
export function setupRouterGuards(router: Router) {
  router.beforeEach(async (to, _from, next) => {
    const auth = useAuthStore()

    if (to.meta.public) {
      if (to.name === 'login') {
        if (auth.token) {
          const restored = await auth.restoreSession()
          if (restored) {
            next({ name: 'book' })
            return
          }
        }
        next()
        return
      }
      next()
      return
    }

    if (!auth.isAuthenticated) {
      const restored = await auth.restoreSession()
      if (!restored) {
        next({ name: 'login', query: { redirect: to.fullPath } })
        return
      }
    }

    next()
  })
}
