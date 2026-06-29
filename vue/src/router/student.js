export default [
  { path: 'dashboard', name: 'StudentDashboard', component: () => import('../views/student/Dashboard.vue'), meta: { title: '首页' } },
  { path: 'schedule', name: 'ClassSchedule', component: () => import('../views/student/ClassSchedule.vue'), meta: { title: '班级课表' } },
  { path: 'exams', name: 'StudentExams', component: () => import('../views/student/ExamPlan.vue'), meta: { title: '考试安排' } },
  { path: 'courses', name: 'StudentCourses', component: () => import('../views/student/CourseCatalog.vue'), meta: { title: '课程查询' } },
  { path: 'notices', name: 'StudentNotices', component: () => import('../views/student/Notices.vue'), meta: { title: '公告' } },
  { path: 'profile', name: 'StudentProfile', component: () => import('../views/student/Profile.vue'), meta: { title: '个人设置' } },
]
