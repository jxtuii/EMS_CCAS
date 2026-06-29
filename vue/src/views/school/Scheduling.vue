<template>
  <div>
    <!-- 排课操作区 -->
    <el-card style="margin-bottom:20px">
      <template #header><span>自动排课</span></template>
      <el-form :inline="true">
        <el-form-item label="学期">
          <el-select v-model="semester">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="running" @click="runScheduling">
            {{ running ? '排课中...' : '执行排课' }}
          </el-button>
          <el-button :disabled="!canPublish" :loading="publishing" @click="publishTasks">
            {{ publishing ? '发布中...' : '发布教学任务' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 排课结果 -->
    <el-card v-if="result" style="margin-bottom:20px">
      <template #header><span>排课结果</span></template>
      <el-alert
        :title="`排课完成：成功 ${result.successCount} 项，冲突 ${result.conflictCount} 项`"
        :type="result.conflictCount > 0 ? 'warning' : 'success'"
        show-icon
        style="margin-bottom:15px"
      />
      <el-table :data="result.conflicts || []" v-if="result.conflictCount > 0" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column label="冲突详情">
          <template #default="{ row }">{{ row }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发布结果 -->
    <el-card v-if="publishResult">
      <template #header><span>发布结果</span></template>
      <el-alert title="教学任务书已发布" type="success" show-icon style="margin-bottom:15px" />
      <el-descriptions :column="1" border style="margin-bottom:15px">
        <el-descriptions-item label="学期">{{ semester }}</el-descriptions-item>
        <el-descriptions-item label="新生成教学任务">{{ publishResult.taskCount }} 条</el-descriptions-item>
      </el-descriptions>
      <el-table :data="publishResult.details || []" border stripe v-if="(publishResult.details || []).length">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column label="任务详情">
          <template #default="{ row }">{{ row }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const semester = ref('2025-2026-1')
const semesters = ref([])
const running = ref(false)
const publishing = ref(false)
const result = ref(null)
const publishResult = ref(null)
const canPublish = ref(false)

async function runScheduling() {
  running.value = true
  publishResult.value = null
  try {
    result.value = await request.post('/scheduling/run', { semester: semester.value })
    ElMessage.success('排课完成')
    canPublish.value = true
  } catch { /* handled */ }
  finally { running.value = false }
}

async function publishTasks() {
  publishing.value = true
  try {
    publishResult.value = await request.post('/scheduling/publish', { semester: semester.value })
    ElMessage.success(`已发布 ${publishResult.value.taskCount} 条教学任务`)
    canPublish.value = false
  } catch { /* handled */ }
  finally { publishing.value = false }
}

onMounted(async () => {
  try { semesters.value = await request.get('/queries/semesters') } catch { semesters.value = ['2025-2026-1'] }
})
</script>
