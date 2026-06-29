<template>
  <div>
    <el-card style="margin-bottom:20px">
      <template #header>
        <div class="header-row">
          <span>我的申报</span>
          <el-button type="primary" @click="showApply = true">新增申报</el-button>
        </div>
      </template>
      <el-table :data="myApps" border stripe>
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="planSemester" label="学期" width="120" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column prop="applyReason" label="申请理由" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="appStatusType(row.status)">{{ appStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showApply" title="新增申报" width="500px">
      <el-form :model="applyForm" label-width="100px">
        <el-form-item label="选择课程">
          <el-select v-model="applyForm.teachingPlanId" style="width:100%" filterable>
            <el-option v-for="p in availablePlans" :key="p.id"
              :label="`${p.courseName} - ${p.className} (${p.semester})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input v-model="applyForm.applyReason" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApply = false">取消</el-button>
        <el-button type="primary" @click="doApply">提交申报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const myApps = ref([])
const availablePlans = ref([])
const showApply = ref(false)

const applyForm = ref({ teachingPlanId: null, applyReason: '' })

function appStatusLabel(s) {
  return ['草稿','已提交','教研室通过','驳回','学院通过'][s] || '未知'
}
function appStatusType(s) {
  return ['info','warning','success','danger','success'][s] || 'info'
}

async function doApply() {
  if (!applyForm.value.teachingPlanId) {
    ElMessage.warning('请选择课程')
    return
  }
  await request.post('/teacher/applications', {
    teachingPlanId: applyForm.value.teachingPlanId,
    applyReason: applyForm.value.applyReason
  })
  ElMessage.success('申报成功')
  showApply.value = false
  load()
}

async function load() {
  try {
    [myApps.value, availablePlans.value] = await Promise.all([
      request.get('/teacher/applications'),
      request.get('/teacher/intents/available-plans')
    ])
  } catch { /* handled */ }
}

onMounted(load)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
</style>
