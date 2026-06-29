<template>
  <div>
    <!-- ===== 教学任务书 ===== -->
    <el-card style="margin-bottom:20px">
      <template #header>
        <span>
          教师教学任务书
          <el-tag type="success" size="small" style="margin-left:8px" v-if="tasks.length">已发布</el-tag>
        </span>
      </template>
      <el-table :data="tasks" border stripe v-if="tasks.length">
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column prop="teacherNo" label="工号" width="100" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="roleType" label="角色" width="80" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column prop="weeklyHours" label="周学时" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : 'info'" size="small">
              {{ row.status === 2 ? '已发布' : '待发布' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无教学任务书数据，请先完成排课并点击「发布教学任务」" />
    </el-card>

    <!-- ===== 班级课表（5×5 网格视图） ===== -->
    <el-card>
      <template #header><span>班级课表</span></template>
      <el-form :inline="true">
        <el-form-item label="学期">
          <el-select v-model="semester">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="classId" style="width:200px" @change="loadSchedule">
            <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 5天×5节 网格课表 -->
      <div v-if="scheduleGrid.length" style="overflow-x:auto">
        <table class="schedule-grid">
          <thead>
            <tr>
              <th style="width:60px">节次</th>
              <th v-for="d in weekdays" :key="d.value" style="min-width:140px">
                {{ d.label }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="section in 5" :key="section">
              <td class="section-label">{{ section }}</td>
              <td v-for="d in weekdays" :key="d.value" class="schedule-cell">
                <div v-if="getCellData(d.value, section)" class="cell-content">
                  <div class="cell-course">{{ getCellData(d.value, section).courseName }}</div>
                  <div class="cell-teacher">{{ getCellData(d.value, section).teacherName }}</div>
                </div>
                <div v-else class="cell-empty">—</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 旧列表视图（作为备用） -->
      <el-table :data="schedules" border stripe v-if="schedules.length && !scheduleGrid.length" style="margin-top:15px">
        <el-table-column prop="weekday" label="星期" width="80">
          <template #default="{ row }">星期{{ row.weekday }}</template>
        </el-table-column>
        <el-table-column prop="sectionNo" label="节次" width="80" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
      </el-table>
      <el-empty v-else-if="classId && !scheduleGrid.length" description="暂无课表数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import request from '../../utils/request'

const tasks = ref([])
const schedules = ref([])
const classList = ref([])
const semesters = ref(['2025-2026-1'])
const semester = ref('2025-2026-1')
const classId = ref(null)

const weekdays = [
  { value: 1, label: '星期一' },
  { value: 2, label: '星期二' },
  { value: 3, label: '星期三' },
  { value: 4, label: '星期四' },
  { value: 5, label: '星期五' }
]

// 构建网格数据
const scheduleGrid = ref([])

function getCellData(weekday, sectionNo) {
  return schedules.value.find(s => s.weekday === weekday && s.sectionNo === sectionNo)
}

async function loadSchedule() {
  if (!classId.value) return
  try {
    const data = await request.get(`/queries/schedules/class/${classId.value}?semester=${semester.value}`)
    schedules.value = data || []
  } catch {
    schedules.value = []
  }
}

watch(semester, async () => {
  loadTasks()
  if (classId.value) loadSchedule()
})

async function loadTasks() {
  try { tasks.value = await request.get('/queries/tasks') } catch { tasks.value = [] }
}

onMounted(async () => {
  try {
    [tasks.value, classList.value, semesters.value] = await Promise.all([
      request.get('/queries/tasks'),
      request.get('/classes'),
      request.get('/queries/semesters')
    ])
  } catch { /* handled */ }
})
</script>

<style scoped>
.schedule-grid {
  width: 100%;
  border-collapse: collapse;
  font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 13px;
}
.schedule-grid th,
.schedule-grid td {
  border: 1px solid var(--sb-hairline);
  text-align: center;
  padding: 8px 4px;
}
.schedule-grid th {
  background: var(--sb-canvas-soft);
  color: var(--sb-ink-mute);
  font-weight: 500;
  font-size: 12px;
}
.section-label {
  background: var(--sb-canvas-soft);
  font-weight: 500;
  color: var(--sb-ink-mute-2);
  font-size: 12px;
}
.schedule-cell {
  height: 65px;
  vertical-align: top;
}
.cell-content {
  background: #e7faf1;
  border-radius: 4px;
  padding: 4px 6px;
  height: 100%;
}
.cell-course {
  color: #1a7a4e;
  font-weight: 500;
  font-size: 12px;
  line-height: 1.4;
}
.cell-teacher {
  color: var(--sb-ink-mute);
  font-size: 11px;
  line-height: 1.4;
  margin-top: 2px;
}
.cell-empty {
  color: var(--sb-ink-faint);
  font-size: 13px;
}
</style>
