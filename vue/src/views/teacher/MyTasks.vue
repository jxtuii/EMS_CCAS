<template>
  <div>
    <el-card style="margin-bottom:20px">
      <template #header><span>我的教学任务</span></template>
      <el-table :data="tasks" border stripe>
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="roleType" label="角色" width="80" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column prop="weeklyHours" label="周学时" width="80" />
      </el-table>
    </el-card>

    <el-card>
      <template #header><span>我的课表</span></template>
      <el-table :data="schedules" border stripe v-if="schedules.length">
        <el-table-column label="星期" width="80">
          <template #default="{ row }">星期{{ row.weekday }}</template>
        </el-table-column>
        <el-table-column prop="sectionNo" label="节次" width="80" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
      </el-table>
      <el-empty v-else description="暂无课表" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const tasks = ref([])
const schedules = ref([])

onMounted(async () => {
  try {
    [tasks.value, schedules.value] = await Promise.all([
      request.get('/teacher/tasks?semester=2025-2026-1'),
      request.get('/teacher/schedules?semester=2025-2026-1')
    ])
  } catch { /* handled */ }
})
</script>
