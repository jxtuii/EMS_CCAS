<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>班级课表 · {{ grid?.className || '' }}</span>
          <el-select v-model="semester" style="width:160px" @change="load">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
          </el-select>
        </div>
      </template>

      <div v-if="loading" style="text-align:center;padding:40px;color:#909399">加载中...</div>

      <div v-else-if="grid && grid.grid">
        <table class="grid-table">
          <thead>
            <tr>
              <th class="g-col-sec">节次</th>
              <th v-for="label in grid.weekLabels" :key="label">{{ label }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="sec in grid.sections" :key="sec">
              <td class="g-col-sec">第{{ sec }}节</td>
              <td
                v-for="(_, wi) in grid.weekLabels"
                :key="wi"
                v-show="!isSkipped(wi+1, sec)"
                :rowspan="getSpan(wi+1, sec)"
                :class="{ 'g-has-course': hasCourse(wi+1, sec) }"
              >
                <div v-if="hasCourse(wi+1, sec)" class="cell-block">
                  <div class="cell-course">{{ grid.grid[wi+1][sec].courseName }}</div>
                  <div class="cell-teacher">{{ grid.grid[wi+1][sec].teacherName }}</div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <el-empty v-else description="暂无课表数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../../utils/request'

const grid = ref(null)
const semesters = ref(['2025-2026-2'])
const semester = ref('2025-2026-2')
const loading = ref(false)

// 计算哪些单元格被上面的 rowspan 覆盖了
const skipSet = computed(() => {
  const skipped = new Set()
  if (!grid.value?.grid) return skipped
  for (let w = 1; w <= 5; w++) {
    const row = grid.value.grid[w]
    if (!row) continue
    for (let sec = 1; sec <= 11; sec++) {
      const cell = row[sec]
      if (cell?.courseName && cell.sectionSpan > 1) {
        for (let k = 1; k < cell.sectionSpan; k++) {
          skipped.add(`${w}-${sec + k}`)
        }
      }
    }
  }
  return skipped
})

function isSkipped(weekday, sec) {
  return skipSet.value.has(`${weekday}-${sec}`)
}

function hasCourse(weekday, sec) {
  const cell = grid.value?.grid?.[weekday]?.[sec]
  return cell?.courseName != null
}

function getSpan(weekday, sec) {
  const cell = grid.value?.grid?.[weekday]?.[sec]
  return cell?.sectionSpan || 1
}

async function load() {
  loading.value = true
  try {
    grid.value = await request.get(`/student/schedules/grid?semester=${semester.value}`)
  } catch {
    grid.value = null
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
.grid-table { width: 100%; border-collapse: collapse; font-size: 13px; border: 1px solid #ebeef5; }
.grid-table th, .grid-table td { border: 1px solid #ebeef5; text-align: center; vertical-align: middle; padding: 0; }
.grid-table th { background: #fafafa; color: #909399; font-size: 12px; height: 36px; }
.g-col-sec { width: 56px; background: #fafafa; color: #909399; font-size: 12px; padding: 4px; }
.g-has-course { background: #ecf5ff; }
.cell-block { padding: 8px 10px; display: flex; flex-direction: column; justify-content: center; align-items: center; min-height: 44px; }
.cell-course { color: #0000cd; font-weight: bold; font-size: 13px; line-height: 1.3; }
.cell-teacher { color: #606266; font-size: 12px; margin-top: 3px; }
</style>
