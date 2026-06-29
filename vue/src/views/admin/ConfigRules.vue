<template>
  <div>
    <el-card>
      <template #header><span>教学限制配置</span></template>
      <el-table :data="plans" border stripe>
        <el-table-column prop="semester" label="学期" width="100" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="totalHours" label="总学时" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag>{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="primary" size="small" @click="showConfig(row)">配置限制</el-button>
            <el-button v-else size="small" disabled>已配置</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" title="配置教学限制" width="500px">
      <el-form :model="configForm" label-width="120px">
        <el-form-item label="中层最大周学时">
          <el-input-number v-model="configForm.middleLeaderHours" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="普通教师最大学时">
          <el-input-number v-model="configForm.normalTeacherHours" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="主讲最低职称">
          <el-select v-model="configForm.minTitleLevel">
            <el-option label="助教(1)" :value="1" />
            <el-option label="讲师(2)" :value="2" />
            <el-option label="副教授(3)" :value="3" />
            <el-option label="教授(4)" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveConfig">确定配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const plans = ref([])
const showDialog = ref(false)
const currentPlanId = ref(null)

const configForm = ref({
  middleLeaderHours: 14,
  normalTeacherHours: 12,
  minTitleLevel: 2
})

function statusLabel(s) {
  return ['草稿','待学院配置','待教研室协调','待学院审核','待教务处排课','已发布'][s] || '未知'
}

function showConfig(row) {
  currentPlanId.value = row.id
  showDialog.value = true
}

async function saveConfig() {
  const rules = [
    { ruleType: 'MAX_HOUR_MIDDLE', ruleValue: String(configForm.value.middleLeaderHours) },
    { ruleType: 'MAX_HOUR_NORMAL', ruleValue: String(configForm.value.normalTeacherHours) },
    { ruleType: 'MIN_TITLE_LEVEL', ruleValue: String(configForm.value.minTitleLevel) }
  ]
  await request.put(`/admin/plans/${currentPlanId.value}/rules`, {
    ruleTypes: rules.map(r => r.ruleType),
    ruleValues: rules.map(r => r.ruleValue)
  })
  ElMessage.success('配置成功')
  showDialog.value = false
  load()
}

async function load() {
  plans.value = await request.get('/admin/plans')
}

onMounted(load)
</script>
