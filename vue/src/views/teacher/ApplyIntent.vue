<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>授课意向</span>
          <el-button type="primary" :loading="submitting" @click="submitIntent">提交意向</el-button>
        </div>
      </template>

      <el-alert title="选择您有意向授课的课程，系统将自动校验业务规则" type="info" show-icon :closable="false" style="margin-bottom:16px" />

      <el-form :inline="true">
        <el-form-item label="学期">
          <el-select v-model="semester">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="availablePlans" @selection-change="onSelectionChange" border stripe>
        <el-table-column type="selection" width="50" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column prop="weeklyHours" label="周学时" width="80" />
        <el-table-column prop="requiredTitleLevel" label="最低职称" width="100">
          <template #default="{ row }">{{ titleLabel(row.requiredTitleLevel) }}</template>
        </el-table-column>
        <el-table-column label="可申报" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.canApply !== false" type="success" size="small">是</el-tag>
            <el-tag v-else type="danger" size="small">否</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showResult" title="校验结果" width="500px">
      <el-alert v-if="validationErrors.length === 0" title="所有规则校验通过" type="success" show-icon />
      <template v-else>
        <el-alert title="存在以下问题" type="error" show-icon style="margin-bottom:12px" />
        <el-tag v-for="(err, i) in validationErrors" :key="i" type="danger" style="display:block; margin-bottom:6px">
          {{ err }}
        </el-tag>
      </template>
      <template #footer>
        <el-button @click="showResult = false">关闭</el-button>
        <el-button v-if="validationErrors.length === 0" type="primary" @click="confirmSubmit">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const availablePlans = ref([])
const semesters = ref(['2025-2026-1'])
const semester = ref('2025-2026-1')
const selectedPlans = ref([])
const validationErrors = ref([])
const showResult = ref(false)
const submitting = ref(false)

function titleLabel(level) {
  return ['','助教','讲师','副教授','教授'][level] || '未知'
}

watch(semester, loadPlans)

async function loadPlans() {
  try {
    availablePlans.value = await request.get(`/teacher/intents/available-plans?semester=${semester.value}`)
  } catch {
    availablePlans.value = []
  }
}

function onSelectionChange(selection) {
  selectedPlans.value = selection
}

async function submitIntent() {
  if (!selectedPlans.value.length) {
    ElMessage.warning('请至少选择一个课程')
    return
  }
  submitting.value = true
  try {
    const planIds = selectedPlans.value.map(p => p.id)
    validationErrors.value = await request.post('/teacher/intents/validate', {
      planIds,
      semester: semester.value
    }) || []
    showResult.value = true
  } catch { /* handled */ }
  finally { submitting.value = false }
}

async function confirmSubmit() {
  const planIds = selectedPlans.value.map(p => p.id)
  await request.post('/teacher/intents/submit', {
    planIds,
    semester: semester.value
  })
  ElMessage.success('授课意向已提交')
  showResult.value = false
  loadPlans()
}

onMounted(loadPlans)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
</style>
