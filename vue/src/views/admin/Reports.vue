<template>
  <div>
    <!-- ── 教师教学任务书（合并连续节次）── -->
    <el-card style="margin-bottom:20px">
      <template #header><span>教师教学任务书</span></template>

      <el-form :inline="true" style="margin-bottom:12px">
        <el-form-item label="学期">
          <el-select v-model="taskSemester" style="width:160px" @change="loadTeacherTasks">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师">
          <el-select v-model="taskTeacherId" style="width:200px" @change="loadTeacherSchedule" clearable placeholder="全部教师">
            <el-option v-for="t in teacherList" :key="t.id" :label="t.realName || t.teacherNo" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 单教师任务书详情 -->
      <div v-if="teacherSchedule && teacherSchedule.tasks">
        <div style="margin-bottom:12px;color:#606266">
          <strong>{{ teacherSchedule.teacherName }}</strong> · {{ teacherSchedule.semester }}
        </div>
        <div v-for="(item, idx) in teacherSchedule.tasks" :key="idx" class="task-card">
          <div class="task-card-header">
            <span class="tc-course">{{ item.courseName }}</span>
            <el-tag size="small">{{ item.className }}</el-tag>
            <span class="tc-hours">周{{ item.weeklyHours }}学时</span>
          </div>
          <div v-for="(t, i) in item.timeDescriptions" :key="i" class="tc-time">📅 {{ t }}</div>
        </div>
      </div>

      <!-- 全部教师教学任务一览表 -->
      <el-table v-else-if="allTasks.length" :data="allTasks" border stripe>
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column prop="teacherNo" label="工号" width="100" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column prop="weeklyHours" label="周学时" width="80" />
      </el-table>
      <el-empty v-else description="暂无教学任务，请先完成排课并发布" />
    </el-card>

    <!-- ── 班级课表（网格） ── -->
    <el-card>
      <template #header><span>班级课表</span></template>

      <el-form :inline="true" style="margin-bottom:12px">
        <el-form-item label="学期">
          <el-select v-model="gridSemester" style="width:160px" @change="loadClassGrid">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="gridClassId" style="width:200px" @change="loadClassGrid" placeholder="选择班级">
            <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <div v-if="classGrid && classGrid.grid">
        <div style="margin-bottom:8px;color:#606266">
          <strong>{{ classGrid.className }}</strong> · {{ classGrid.semester }}
        </div>
        <table class="grid-table">
          <thead>
            <tr>
              <th class="g-col-sec">节次</th>
              <th v-for="label in classGrid.weekLabels" :key="label">{{ label }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="sec in classGrid.sections" :key="sec">
              <td class="g-col-sec">第{{ sec }}节</td>
              <td
                v-for="(_, wi) in classGrid.weekLabels"
                :key="wi"
                v-show="!gridSkipped(wi+1, sec)"
                :rowspan="gridSpan(wi+1, sec)"
                :class="{ 'g-has-course': gridHasCourse(wi+1, sec) }"
              >
                <div v-if="gridHasCourse(wi+1, sec)" class="g-cell-block">
                  <div class="g-cell-course">{{ classGrid.grid[wi+1][sec].courseName }}</div>
                  <div class="g-cell-teacher">{{ classGrid.grid[wi+1][sec].teacherName }}</div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <el-empty v-else description="请选择班级查看课表" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../../utils/request'

// 教学任务
const allTasks = ref([])
const teacherList = ref([])
const taskSemester = ref('2025-2026-2')
const taskTeacherId = ref(null)
const teacherSchedule = ref(null)

// 班级课表
const classList = ref([])
const gridSemester = ref('2025-2026-2')
const gridClassId = ref(null)
const classGrid = ref(null)
const semesters = ref(['2025-2026-2'])

async function loadTeacherTasks() {
  try { allTasks.value = await request.get('/admin/reports/tasks') } catch { allTasks.value = [] }
}

async function loadTeacherSchedule() {
  if (!taskTeacherId.value) { teacherSchedule.value = null; return }
  try {
    teacherSchedule.value = await request.get(`/admin/reports/teacher-schedule/${taskTeacherId.value}?semester=${taskSemester.value}`)
  } catch { teacherSchedule.value = null }
}

async function loadClassGrid() {
  if (!gridClassId.value) { classGrid.value = null; return }
  try {
    classGrid.value = await request.get(`/admin/reports/class-grid/${gridClassId.value}?semester=${gridSemester.value}`)
  } catch { classGrid.value = null }
}

// 班级网格 rowspan 辅助
const gridSkipSet = computed(() => {
  const s = new Set()
  if (!classGrid.value?.grid) return s
  for (let w = 1; w <= 5; w++) {
    const row = classGrid.value.grid[w]
    if (!row) continue
    for (let sec = 1; sec <= 11; sec++) {
      const c = row[sec]
      if (c?.courseName && c.sectionSpan > 1) {
        for (let k = 1; k < c.sectionSpan; k++) s.add(`${w}-${sec + k}`)
      }
    }
  }
  return s
})
function gridSkipped(w, sec) { return gridSkipSet.value.has(`${w}-${sec}`) }
function gridHasCourse(w, sec) { return classGrid.value?.grid?.[w]?.[sec]?.courseName != null }
function gridSpan(w, sec) { return classGrid.value?.grid?.[w]?.[sec]?.sectionSpan || 1 }

onMounted(async () => {
  try {
    const [tasks, teachers, classes, sems] = await Promise.all([
      request.get('/admin/reports/tasks'),
      request.get('/admin/assignments/teachers'),
      request.get('/admin/plans/classes'),
      request.get('/admin/reports/semesters')
    ])
    allTasks.value = tasks || []
    teacherList.value = teachers || []
    classList.value = classes || []
    if (sems && sems.length) semesters.value = sems
  } catch { /* handled */ }
})
</script>

<style scoped>
/* 教师任务书 */
.task-card { background: #f9fafb; border: 1px solid #ebeef5; border-radius: 8px; padding: 14px; margin-bottom: 10px; }
.task-card-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.tc-course { font-size: 15px; font-weight: 600; color: #171717; }
.tc-hours { color: #909399; font-size: 13px; margin-left: auto; }
.tc-time { color: #374151; font-size: 14px; line-height: 1.8; padding-left: 4px; }

/* 班级网格 */
.grid-table { width: 100%; border-collapse: collapse; font-size: 13px; border: 1px solid #ebeef5; }
.grid-table th, .grid-table td { border: 1px solid #ebeef5; text-align: center; vertical-align: top; padding: 0; }
.grid-table th { background: #fafafa; color: #909399; font-size: 12px; height: 36px; }
.g-col-sec { width: 56px; background: #fafafa; color: #909399; font-size: 12px; padding: 4px; }
.g-has-course { background: #ecf5ff; }
.g-cell-block { padding: 8px 10px; display: flex; flex-direction: column; justify-content: center; align-items: center; min-height: 44px; }
.g-cell-course { color: #0000cd; font-weight: bold; font-size: 13px; line-height: 1.3; }
.g-cell-teacher { color: #606266; font-size: 12px; margin-top: 3px; }
</style>
