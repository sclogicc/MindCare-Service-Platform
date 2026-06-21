import { createRouter, createWebHashHistory } from 'vue-router'

import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'appointments',
        name: 'Appointments',
        component: () => import('@/views/appointment/AppointmentListView.vue'),
        meta: { title: '预约管理' }
      },
      {
        path: 'counselors',
        name: 'Counselors',
        component: () => import('@/views/counselor/CounselorListView.vue'),
        meta: { title: '咨询师管理' }
      },
      {
        path: 'my-appointments',
        name: 'MyAppointments',
        component: () => import('@/views/appointment/MyAppointmentView.vue'),
        meta: { title: '我的预约' }
      },
      {
        path: 'appointments/create',
        name: 'AppointmentCreate',
        component: () => import('@/views/appointment/AppointmentCreateView.vue'),
        meta: { title: '新增预约' }
      },
      {
        path: 'schedules',
        name: 'Schedules',
        component: () => import('@/views/schedule/ScheduleListView.vue'),
        meta: { title: '时间段管理' }
      },
      {
        path: 'consultation-records',
        name: 'ConsultationRecords',
        component: () => import('@/views/consultation-record/ConsultationRecordListView.vue'),
        meta: { title: '咨询记录管理' }
      },
      {
        path: 'feedbacks',
        name: 'Feedbacks',
        component: () => import('@/views/feedback/FeedbackListView.vue'),
        meta: { title: '反馈评价管理' }
      },
      {
        path: 'reports',
        name: 'Reports',
        component: () => import('@/views/report/ReportView.vue'),
        meta: { title: '统计报表' }
      }
    ]
  }
]

const router = createRouter({
  // 前后端统一部署到 Spring Boot 后，前端页面路径和后端接口路径存在重名，
  // 这里改为 Hash 路由，避免浏览器直接访问页面地址时与后端 REST 接口冲突。
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getToken()
  document.title = to.meta?.title
    ? `${to.meta.title} - 心理咨询服务预约平台`
    : '心理咨询服务预约平台'

  // 未登录时仅允许访问登录页，其他页面统一跳转到登录页。
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }

  // 已登录后再次访问登录页时，直接回到首页。
  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }

  next()
})

export default router
