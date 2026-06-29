<template>
  <div>
    <div class="dashboard-welcome">
      <h2 class="welcome-title">欢迎回来，{{ authStore.user?.realName || authStore.user?.username }}</h2>
      <p class="welcome-subtitle">{{ roleLabel }} · 高校排课管理系统</p>
    </div>

    <el-row :gutter="24">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover" class="dash-card">
          <div class="dash-card-body">
            <div class="dash-card-icon">
              <el-icon :size="32"><component :is="card.icon" /></el-icon>
            </div>
            <div class="dash-card-text">
              <h3 class="dash-card-title">{{ card.title }}</h3>
              <p class="dash-card-desc">{{ card.desc }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="dash-user-card">
      <template #header><span>当前用户信息</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ authStore.user?.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ authStore.user?.realName }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ roleLabel }}</el-descriptions-item>
        <el-descriptions-item label="权限级别">{{ authStore.user?.roleLevel }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()

const roleLabel = computed(() => {
  const roles = authStore.user?.roles || []
  if (roles.includes('ADMIN_SCHOOL')) return '教务处管理员'
  if (roles.includes('ADMIN_COLLEGE')) return '学院管理员'
  if (roles.includes('DIRECTOR_DEPT')) return '教研室主任'
  if (roles.includes('TEACHER')) return '教师'
  return '未知'
})

const cards = computed(() => {
  const roles = authStore.user?.roles || []
  const allCards = [
    { icon: 'Document', title: '教学计划', desc: '管理学期教学计划', show: roles.includes('ADMIN_SCHOOL') },
    { icon: 'Setting', title: '规则配置', desc: '配置教学限制规则', show: roles.includes('ADMIN_COLLEGE') },
    { icon: 'EditPen', title: '课程申报', desc: '教师在线申报课程', show: roles.includes('TEACHER') },
    { icon: 'Timer', title: '智能排课', desc: '自动排课算法', show: roles.includes('ADMIN_SCHOOL') },
  ]
  return allCards.filter(c => c.show)
})
</script>

<style scoped>
.dashboard-welcome { margin-bottom: 24px; }
.welcome-title { font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: 500; font-size: 22px; color: var(--sb-ink); margin: 0 0 4px; letter-spacing: -0.3px; }
.welcome-subtitle { font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: 400; font-size: 13px; color: var(--sb-ink-mute); margin: 0; }
.dash-card { margin-bottom: 24px; cursor: default; }
.dash-card-body { display: flex; align-items: center; gap: 16px; padding: 8px 0; }
.dash-card-icon { width: 56px; height: 56px; display: flex; align-items: center; justify-content: center; background: var(--sb-canvas-soft); border-radius: var(--sb-radius-md); color: var(--sb-ink-mute); flex-shrink: 0; }
.dash-card-text { flex: 1; }
.dash-card-title { font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: 500; font-size: 16px; color: var(--sb-ink); margin: 0 0 2px; }
.dash-card-desc { font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: 400; font-size: 13px; color: var(--sb-ink-mute); margin: 0; }
.dash-user-card { margin-top: 0; }
</style>
