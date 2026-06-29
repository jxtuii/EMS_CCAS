<template>
  <div>
    <el-card>
      <template #header><span>教师申报看板</span></template>
      <el-table :data="applications" border stripe>
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column prop="teacherNo" label="工号" width="100" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column prop="planSemester" label="学期" width="120" />
        <el-table-column prop="applyReason" label="申请理由" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="appStatusType(row.status)">{{ appStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const applications = ref([])

function appStatusLabel(s) {
  return ['草稿','已提交','教研室通过','驳回','学院通过'][s] || '未知'
}
function appStatusType(s) {
  return ['info','warning','success','danger','success'][s] || 'info'
}

onMounted(async () => {
  applications.value = await request.get('/applications')
})
</script>
