<template>
  <div>
    <el-card style="margin-bottom:20px">
      <template #header><span>学院审核 - 待审核列表</span></template>
      <el-table :data="pendingApps" border stripe>
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column prop="teacherNo" label="工号" width="100" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">已分配</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleApprove(row)">通过</el-button>
            <el-button type="danger" size="small" @click="handleReject(row)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card>
      <template #header><span>审核记录</span></template>
      <el-table :data="allApps" border stripe>
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">{{ appStatusLabel(row.status) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const allApps = ref([])
const pendingApps = computed(() => allApps.value.filter(a => a.status === 2))

function appStatusLabel(s) {
  return ['草稿','已提交','教研室通过','驳回','学院通过'][s] || '未知'
}

async function handleApprove(row) {
  await ElMessageBox.confirm(`确定通过 ${row.teacherName} 的申报？`, '提示')
  await request.post('/approvals', { applicationId: row.id, approved: true, comment: '学院审核通过' })
  ElMessage.success('已通过')
  load()
}

async function handleReject(row) {
  const { value } = await ElMessageBox.prompt('请输入驳回意见', '驳回', { inputType: 'textarea' })
  await request.post('/approvals', { applicationId: row.id, approved: false, comment: value || '驳回' })
  ElMessage.success('已驳回')
  load()
}

async function load() {
  allApps.value = await request.get('/applications')
}

onMounted(load)
</script>
