<template>
  <div class="sg-wrapper">
    <div v-if="loading" style="text-align:center;padding:40px;color:#909399">加载中...</div>
    <div v-else-if="courseBlocks.length" style="overflow-x:auto">
      <table class="sg-table">
        <thead>
          <tr>
            <th style="width:56px">时间段</th>
            <th style="width:44px">节次</th>
            <th v-for="d in weekdays" :key="d.value" style="min-width:150px">{{ d.label }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in 11" :key="row">
            <td v-if="row === 1" :rowspan="4" class="sg-period am">上午</td>
            <td v-if="row === 5" :rowspan="4" class="sg-period pm">下午</td>
            <td v-if="row === 9" :rowspan="3" class="sg-period evening">晚上</td>
            <td class="sg-section">
              <div>{{ row }}</div>
              <div class="sg-section-time">{{ sectionTimes[row] || '' }}</div>
            </td>
            <template v-for="col in 5" :key="col">
              <td v-if="!skipCell(row, col)" :rowspan="getRowspan(row, col)" class="sg-cell" :class="cellColor(row, col)">
                <div v-if="getBlock(row, col)" class="sg-content">
                  <div class="sg-name">{{ getBlock(row, col).courseName }}</div>
                  <div class="sg-meta">
                    <span v-if="showTeacher && getBlock(row, col).teacherName">{{ getBlock(row, col).teacherName }}</span>
                    <span v-if="!showTeacher && getBlock(row, col).className">{{ getBlock(row, col).className }}</span>
                    <span v-if="showTeacher && getBlock(row, col).className" class="sg-divider">|</span>
                    <span v-if="showTeacher && getBlock(row, col).className">{{ getBlock(row, col).className }}</span>
                  </div>
                  <div class="sg-time">
                    第{{ getBlock(row, col).sectionStart }}-{{ getBlock(row, col).sectionEnd }}节
                    <template v-if="getBlock(row, col).startTime">
                      {{ getBlock(row, col).startTime }}-{{ getBlock(row, col).endTime }}
                    </template>
                  </div>
                </div>
              </td>
            </template>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else class="sg-empty">暂无课表数据</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  schedules: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  showTeacher: { type: Boolean, default: true }
})

const weekdays = [
  { value: 1, label: '星期一' },
  { value: 2, label: '星期二' },
  { value: 3, label: '星期三' },
  { value: 4, label: '星期四' },
  { value: 5, label: '星期五' }
]

const sectionTimes = {
  1: '08:00-08:45',
  2: '08:50-09:35',
  3: '09:50-10:35',
  4: '10:45-11:30',
  5: '14:00-14:45',
  6: '14:50-15:35',
  7: '15:45-16:30',
  8: '16:35-17:20',
  9: '19:00-19:45',
  10: '19:50-20:35',
  11: '20:40-21:25'
}

// Group consecutive sections for the same course into blocks
const courseBlocks = computed(() => {
  const scheds = props.schedules
  if (!scheds || !scheds.length) return []
  const groups = {}
  scheds.forEach(s => {
    const key = `${s.weekday}-${s.courseName}-${s.className}`
    if (!groups[key]) groups[key] = []
    groups[key].push(s)
  })
  const blocks = []
  Object.values(groups).forEach(group => {
    group.sort((a, b) => a.sectionNo - b.sectionNo)
    let start = group[0].sectionNo
    let prev = start
    for (let i = 1; i < group.length; i++) {
      if (group[i].sectionNo === prev + 1) {
        prev = group[i].sectionNo
      } else {
        blocks.push({ ...group[0], sectionStart: start, sectionEnd: prev })
        start = group[i].sectionNo
        prev = start
      }
    }
    blocks.push({ ...group[0], sectionStart: start, sectionEnd: prev })
  })
  return blocks
})

// skipMatrix: 11 rows × 5 cols — marks cells consumed by rowspan
const skipMatrix = computed(() => {
  const TOTAL_ROWS = 11
  const matrix = Array.from({ length: TOTAL_ROWS }, () => Array(5).fill(false))
  courseBlocks.value.forEach(block => {
    const start = block.sectionStart - 1
    const end = block.sectionEnd - 1
    const col = block.weekday - 1
    if (start >= 0 && end > start && col >= 0 && start < TOTAL_ROWS && col < 5) {
      for (let i = start + 1; i <= end; i++) {
        if (i < TOTAL_ROWS) matrix[i][col] = true
      }
    }
  })
  return matrix
})

function skipCell(row, col) {
  return skipMatrix.value[row - 1][col - 1]
}

function getBlock(row, col) {
  return courseBlocks.value.find(b => b.sectionStart === row && b.weekday === col)
}

function getRowspan(row, col) {
  const block = getBlock(row, col)
  if (block && block.sectionEnd >= block.sectionStart) {
    return block.sectionEnd - block.sectionStart + 1
  }
  return 1
}

function cellColor(row, col) {
  const block = getBlock(row, col)
  if (!block) return ''
  const hash = block.courseName.split('').reduce((a, c) => a + c.charCodeAt(0), 0)
  const idx = hash % 3
  return ['sg-blue', 'sg-blue-2', 'sg-blue-3'][idx]
}
</script>

<style scoped>
.sg-wrapper {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}

.sg-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  border: 1px solid #ebeef5;
}

.sg-table th,
.sg-table td {
  border: 1px solid #ebeef5;
  text-align: center;
  padding: 6px 4px;
}

.sg-table th {
  background: #fafafa;
  color: #909399;
  font-weight: 500;
  font-size: 12px;
}

.sg-period {
  background: #fafafa;
  color: #606266;
  font-weight: 500;
  font-size: 12px;
  writing-mode: vertical-lr;
  letter-spacing: 4px;
  width: 36px;
}

.sg-period.am,
.sg-period.pm,
.sg-period.evening {
  vertical-align: middle;
}

.sg-period.evening {
  background: #f5f0eb;
  color: #8b7355;
}

.sg-section {
  background: #fafafa;
  color: #606266;
  font-size: 12px;
  font-weight: 500;
  font-style: italic;
  padding: 4px 2px !important;
  line-height: 1.2;
}

.sg-section-time {
  color: #bbb;
  font-size: 10px;
  font-style: normal;
  font-weight: 400;
  margin-top: 1px;
}

.sg-cell {
  height: 52px;
  vertical-align: top;
  padding: 2px !important;
}

.sg-content {
  border-radius: 6px;
  padding: 2px 6px;
  height: 100%;
  min-height: 44px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: left;
}

.sg-blue .sg-content {
  background: #ecf5ff;
}

.sg-blue-2 .sg-content {
  background: #e6f0ff;
}

.sg-blue-3 .sg-content {
  background: #f0f6ff;
}

.sg-name {
  color: #0000cd;
  font-weight: bold;
  font-size: 12px;
  line-height: 1.4;
  margin-bottom: 2px;
}

.sg-meta {
  color: #606266;
  font-size: 11px;
  line-height: 1.4;
  margin-bottom: 1px;
}

.sg-divider {
  color: #dcdfe6;
  margin: 0 4px;
}

.sg-time {
  color: #909399;
  font-size: 11px;
  line-height: 1.4;
}

.sg-empty {
  text-align: center;
  padding: 40px;
  color: #909399;
  font-size: 14px;
}
</style>
