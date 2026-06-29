import { createRouter, createWebHashHistory } from 'vue-router'
import adminRoutes from './admin'
import teacherRoutes from './teacher'
import studentRoutes from './student'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: adminRoutes
  },
  {
    path: '/teacher',
    component: () => import('../layouts/TeacherLayout.vue'),
    redirect: '/teacher/dashboard',
    children: teacherRoutes
  },
  {
    path: '/student',
    component: () => import('../layouts/StudentLayout.vue'),
    redirect: '/student/dashboard',
    children: studentRoutes
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')

  if (to.path === '/login') {
    next()
    return
  }

  if (!token) {
    next('/login')
    return
  }

  // Validate portal access
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      const roles = user.roles || []

      if (to.path.startsWith('/admin')) {
        const hasAccess = roles.some(r => r.startsWith('ADMIN_') || r === 'DIRECTOR_DEPT' || r === 'TEACHER')
        if (!hasAccess) { next('/login'); return }
      } else if (to.path.startsWith('/teacher')) {
        if (!roles.includes('TEACHER') && !roles.includes('DIRECTOR_DEPT')) { next('/login'); return }
      } else if (to.path.startsWith('/student')) {
        if (!roles.includes('STUDENT')) { next('/login'); return }
      }
    } catch { /* ignore parse errors */ }
  }

  next()
})

export default router
