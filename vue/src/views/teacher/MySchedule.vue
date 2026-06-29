<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>我的教学任务书</span>
          <el-select v-model="semester" style="width:160px" @change="load">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
          </el-select>
        </div>
      </template>

      <div v-if="loading" style="text-align:center;padding:40px;color:#909399">加载中...</div>

      <div v-else-if="data && data.tasks && data.tasks.length">
        <div style="margin-bottom:16px;color:#606266">
          <strong>{{ data.teacherName }}</strong> · {{ data.semester }}
        </div>

        <div v-for="(item, idx) in data.tasks" :key="idx" class="task-card">
          <div class="task-header">
            <span class="task-course">{{ item.courseName }}</span>
            <el-tag size="small" type="info">{{ item.className }}</el-tag>
            <span class="task-hours">周{{ item.weeklyHours }}学时</span>
          </div>
          <div class="task-times">
            <div v-for="(t, i) in item.timeDescriptions" :key="i" class="time-line">
              📅 {{ t }}
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无排课数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const data = ref(null)
const semesters = ref(['2025-2026-2'])
const semester = ref('2025-2026-2')
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    // 先从 profile 取 teacherId，或直接用已知端点
    data.value = await request.get(`/teacher/schedules/formatted?semester=${semester.value}`)
  } catch {
    data.value = null
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try { semesters.value = await request.get('/common/colleges/semesters') } catch { /* ignore */ }
  load()
})
</script>

<style scoped>
.task-card {
  background: #f9fafb;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}
.task-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.task-course {
  font-size: 16px;
  font-weight: 600;
  color: #171717;
}
.task-hours {
  color: #909399;
  font-size: 13px;
  margin-left: auto;
}
.task-times {
  padding-left: 4px;
}
.time-line {
  color: #374151;
  font-size: 14px;
  line-height: 1.8;
}
</style>
