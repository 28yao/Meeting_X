import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/HomePlaceholder.vue'),
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true },
  },
  {
    path: '/book',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'book',
        component: () => import('../views/BookView.vue'),
        meta: { title: '预约会议室' },
      },
    ],
  },
  {
    path: '/my-bookings',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'my-bookings',
        component: () => import('../views/MyBookingsView.vue'),
        meta: { title: '我的预约' },
      },
    ],
  },
  {
    path: '/notifications',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'notifications',
        component: () => import('../views/NotificationsView.vue'),
        meta: { title: '通知中心' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
