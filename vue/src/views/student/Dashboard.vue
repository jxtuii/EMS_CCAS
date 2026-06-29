<template>
  <div>
    <div class="dashboard-welcome">
      <h2 class="welcome-title">欢迎回来，{{ authStore.user?.realName || authStore.user?.username }}</h2>
      <p class="welcome-subtitle">学生 · 高校排课管理系统</p>
    </div>

    <el-row :gutter="24">
      <el-col :span="8" v-for="card in cards" :key="card.title">
        <el-card shadow="hover" class="dash-card">
          <div class="dash-card-body">
            <div class="dash-card-icon">
              <el-icon :size="28"><component :is="card.icon" /></el-icon>
            </div>
            <div class="dash-card-text">
              <h3 class="dash-card-title">{{ card.title }}</h3>
              <p class="dash-card-desc">{{ card.desc }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header><span>个人信息</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ profile?.username || authStore.user?.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ profile?.realName || authStore.user?.realName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ profile?.studentNo || authStore.user?.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ profile?.className || '—' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import request from '../../utils/request'

const authStore = useAuthStore()
const profile = ref({})

const cards = [
  { icon: 'Timer', title: '班级课表', desc: '查看本周课表' },
  { icon: 'Calendar', title: '考试安排', desc: '查看学期考试计划' },
  { icon: 'Reading', title: '课程查询', desc: '浏览课程目录' },
]

onMounted(async () => {
  try { profile.value = await request.get('/common/profile') } catch { /* handled */ }
})
</script>

<style scoped>
.dashboard-welcome { margin-bottom: 24px; }
.welcome-title { font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: 500; font-size: 22px; color: var(--sb-ink); margin: 0 0 4px; letter-spacing: -0.3px; }
.welcome-subtitle { font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: 400; font-size: 13px; color: var(--sb-ink-mute); margin: 0; }
.dash-card { margin-bottom: 24px; cursor: default; }
.dash-card-body { display: flex; align-items: center; gap: 16px; padding: 8px 0; }
.dash-card-icon { width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; background: var(--sb-canvas-soft); border-radius: var(--sb-radius-md); color: var(--sb-ink-mute); flex-shrink: 0; }
.dash-card-text { flex: 1; }
.dash-card-title { font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: 500; font-size: 16px; color: var(--sb-ink); margin: 0 0 2px; }
.dash-card-desc { font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: 400; font-size: 13px; color: var(--sb-ink-mute); margin: 0; }
</style>
