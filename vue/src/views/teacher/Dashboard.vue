<template>
  <div>
    <div class="dashboard-welcome">
      <h2 class="welcome-title">欢迎回来，{{ authStore.user?.realName || authStore.user?.username }}</h2>
      <p class="welcome-subtitle">{{ roleLabel }} · 教师端</p>
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
      <template #header><span>本周课表概览</span></template>
      <el-empty v-if="!schedules.length" description="暂无课表数据" />
      <el-table :data="schedules" border stripe v-else>
        <el-table-column label="星期" width="80">
          <template #default="{ row }">星期{{ row.weekday }}</template>
        </el-table-column>
        <el-table-column prop="sectionNo" label="节次" width="80" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import request from '../../utils/request'

const authStore = useAuthStore()
const schedules = ref([])

const roleLabel = computed(() => {
  const roles = authStore.user?.roles || []
  if (roles.includes('DIRECTOR_DEPT')) return '教研室主任'
  if (roles.includes('TEACHER')) return '教师'
  return '未知'
})

const cards = computed(() => [
  { icon: 'Notebook', title: '我的任务', desc: '查看教学任务与课表' },
  { icon: 'EditPen', title: '课程申报', desc: '在线申报授课课程' },
  { icon: 'Bell', title: '公告通知', desc: '查看系统公告' },
])

onMounted(async () => {
  try { schedules.value = await request.get('/teacher/schedules') } catch { schedules.value = [] }
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
