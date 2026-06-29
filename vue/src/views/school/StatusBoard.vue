<template>
  <div>
    <el-card>
      <template #header><span>教学计划状态看板</span></template>

      <el-table :data="plans" border stripe>
        <el-table-column prop="semester" label="学期" width="100" />
        <el-table-column prop="collegeName" label="学院" width="120" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" min-width="200">
          <template #default="{ row }">
            <el-steps :active="row.status || 0" simple size="small" style="margin-top: 4px">
              <el-step title="草稿" />
              <el-step title="配置" />
              <el-step title="协调" />
              <el-step title="审核" />
              <el-step title="排课" />
              <el-step title="发布" />
            </el-steps>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const plans = ref([])

function statusLabel(s) {
  const map = ['草稿','待学院配置','待教研室协调','待学院审核','待教务处排课','已发布']
  return map[s] || '未知'
}
function statusType(s) {
  const map = ['info','warning','','primary','success','success']
  return map[s] || 'info'
}

onMounted(async () => {
  plans.value = await request.get('/plans')
})
</script>
