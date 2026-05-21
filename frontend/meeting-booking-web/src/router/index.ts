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
    path: '/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue'),
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
  {
    path: '/admin/users',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'admin-users',
        component: () => import('../views/admin/AdminUsersView.vue'),
        meta: { title: '用户管理', requiresAdmin: true },
      },
    ],
  },
  {
    path: '/admin/rooms',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'admin-rooms',
        component: () => import('../views/admin/AdminRoomsView.vue'),
        meta: { title: '会议室管理', requiresAdmin: true },
      },
    ],
  },
  {
    path: '/admin/bookings',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'admin-bookings',
        component: () => import('../views/admin/AdminBookingsView.vue'),
        meta: { title: '预约管理', requiresAdmin: true },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
