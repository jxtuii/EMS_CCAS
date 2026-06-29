export default [
  { path: 'dashboard', name: 'TeacherDashboard', component: () => import('../views/teacher/Dashboard.vue'), meta: { title: '首页' } },
  { path: 'tasks', name: 'MyTasks', component: () => import('../views/teacher/MyTasks.vue'), meta: { title: '我的任务' } },
  { path: 'applications', name: 'MyApply', component: () => import('../views/teacher/MyApply.vue'), meta: { title: '我的申报' } },
  { path: 'apply-intent', name: 'ApplyIntent', component: () => import('../views/teacher/ApplyIntent.vue'), meta: { title: '授课意向' } },
  { path: 'schedule', name: 'TeacherSchedule', component: () => import('../views/teacher/MySchedule.vue'), meta: { title: '我的课表' } },
  { path: 'notices', name: 'TeacherNotices', component: () => import('../views/teacher/Notices.vue'), meta: { title: '公告' } },
  { path: 'profile', name: 'TeacherProfile', component: () => import('../views/teacher/Profile.vue'), meta: { title: '个人设置' } },
]
