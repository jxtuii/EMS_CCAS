<template>
  <div>
    <el-card style="margin-bottom:20px">
      <template #header><span>规则配置</span></template>
      <el-alert title="配置教学业务规则，修改后实时生效" type="info" show-icon :closable="false" style="margin-bottom:16px" />
      <el-form :model="config" label-width="160px">
        <el-form-item label="教师主讲最大课程数">
          <el-input-number v-model="config.maxMainCourses" :min="1" :max="5" />
          <span style="margin-left:12px; color:var(--sb-ink-mute); font-size:13px">每学期每位教师最多担任主讲课程数</span>
        </el-form-item>
        <el-form-item label="中层干部最大周学时">
          <el-input-number v-model="config.maxLeaderWeeklyHours" :min="1" :max="20" />
          <span style="margin-left:12px; color:var(--sb-ink-mute); font-size:13px">中层管理干部每周最多学时</span>
        </el-form-item>
        <el-form-item label="主干课程最低职称">
          <el-select v-model="config.minTitleForCore" style="width:200px">
            <el-option label="讲师" :value="2" />
            <el-option label="副教授" :value="3" />
            <el-option label="教授" :value="4" />
          </el-select>
          <span style="margin-left:12px; color:var(--sb-ink-mute); font-size:13px">承担主干课程的最低职称要求</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveConfig">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header><span>现有规则列表</span></template>
      <el-table :data="rules" border stripe>
        <el-table-column prop="ruleType" label="规则类型" width="200" />
        <el-table-column prop="ruleValue" label="规则值" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const config = ref({
  maxMainCourses: 2,
  maxLeaderWeeklyHours: 4,
  minTitleForCore: 2
})

const rules = ref([])

async function load() {
  try {
    rules.value = await request.get('/admin/plans/rules')
    const maxMain = rules.value.find(r => r.ruleType === 'MAX_MAIN_COURSES')
    const maxLeader = rules.value.find(r => r.ruleType === 'MAX_LEADER_HOURS')
    const minTitle = rules.value.find(r => r.ruleType === 'MIN_TITLE_CORE')
    if (maxMain) config.value.maxMainCourses = parseInt(maxMain.ruleValue) || 2
    if (maxLeader) config.value.maxLeaderWeeklyHours = parseInt(maxLeader.ruleValue) || 4
    if (minTitle) config.value.minTitleForCore = parseInt(minTitle.ruleValue) || 2
  } catch { /* handled */ }
}

async function saveConfig() {
  const payload = [
    { ruleType: 'MAX_MAIN_COURSES', ruleValue: String(config.value.maxMainCourses), description: '教师主讲最大课程数' },
    { ruleType: 'MAX_LEADER_HOURS', ruleValue: String(config.value.maxLeaderWeeklyHours), description: '中层干部最大周学时' },
    { ruleType: 'MIN_TITLE_CORE', ruleValue: String(config.value.minTitleForCore), description: '主干课程最低职称' }
  ]
  await request.post('/admin/plans/rules/save', payload)
  ElMessage.success('配置已保存')
  load()
}

onMounted(load)
</script>
