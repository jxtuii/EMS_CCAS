<template>
  <div>
    <el-card>
      <template #header><span>考试安排</span></template>

      <el-form :inline="true" style="margin-bottom:16px">
        <el-form-item label="学期">
          <el-select v-model="semester">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="examPlans" border stripe>
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="100" />
        <el-table-column prop="endTime" label="结束时间" width="100" />
        <el-table-column prop="room" label="教室" width="120" />
        <el-table-column prop="invigilatorName" label="监考教师" width="120" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发布' : '未发布' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!examPlans.length && loaded" description="暂无考试安排" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import request from '../../utils/request'

const examPlans = ref([])
const semesters = ref(['2025-2026-1'])
const semester = ref('2025-2026-1')
const loaded = ref(false)

watch(semester, loadExams)

async function loadExams() {
  loaded.value = false
  try {
    examPlans.value = await request.get('/student/exam-plans') || []
  } catch {
    examPlans.value = []
  }
  loaded.value = true
}

onMounted(async () => {
  try { semesters.value = await request.get('/common/colleges/semesters') } catch { /* ignore */ }
  loadExams()
})
</script>
