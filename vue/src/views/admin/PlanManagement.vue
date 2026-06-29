<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>教学计划管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="showCreate = true">新增计划</el-button>
            <el-button @click="batchPublish" :disabled="!selectedIds.length">批量发布</el-button>
            <el-button type="danger" plain @click="resetAll">🔄 一键重置</el-button>
          </div>
        </div>
      </template>

      <el-table :data="plans" @selection-change="ids => selectedIds = ids.map(i => i.id)" border stripe>
        <el-table-column type="selection" width="50" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="collegeName" label="学院" width="120" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column prop="weeklyHours" label="周学时" width="80" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" v-if="row.status === 0" @click="publishPlan(row.id)">发布</el-button>
            <el-button size="small" type="primary" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreate" title="新增教学计划" width="500px">
      <el-form :model="newPlan" label-width="100px">
        <el-form-item label="学期"><el-input v-model="newPlan.semester" placeholder="如 2025-2026-1" /></el-form-item>
        <el-form-item label="学院">
          <el-select v-model="newPlan.collegeId" style="width:100%">
            <el-option v-for="c in colleges" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="newPlan.courseId" style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="newPlan.classId" style="width:100%">
            <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="总学时"><el-input-number v-model="newPlan.totalHours" :min="1" /></el-form-item>
        <el-form-item label="周学时"><el-input-number v-model="newPlan.weeklyHours" :min="1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="createPlan">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDetail" title="计划详情" width="600px">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="学期">{{ detail.semester }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{ detail.collegeName }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ detail.courseName }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ detail.className }}</el-descriptions-item>
        <el-descriptions-item label="总学时">{{ detail.totalHours }}</el-descriptions-item>
        <el-descriptions-item label="周学时">{{ detail.weeklyHours }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detail.realName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const plans = ref([])
const colleges = ref([])
const courses = ref([])
const classList = ref([])
const selectedIds = ref([])
const showCreate = ref(false)
const showDetail = ref(false)
const detail = ref(null)

const newPlan = ref({
  semester: '2025-2026-1',
  collegeId: null,
  courseId: null,
  classId: null,
  totalHours: 48,
  weeklyHours: 4,
  status: 0
})

watch(() => newPlan.value.collegeId, async (id) => {
  if (!id) return
  courses.value = await request.get(`/admin/plans/courses?collegeId=${id}`)
  classList.value = await request.get(`/admin/plans/classes?collegeId=${id}`)
})

async function load() {
  plans.value = await request.get('/admin/plans')
  colleges.value = await request.get('/admin/plans/colleges')
}

async function createPlan() {
  await request.post('/admin/plans', newPlan.value)
  ElMessage.success('创建成功')
  showCreate.value = false
  load()
}

async function publishPlan(id) {
  await request.put(`/admin/plans/${id}/publish`)
  ElMessage.success('发布成功')
  load()
}

async function batchPublish() {
  if (!selectedIds.value.length) return ElMessage.warning('请选择计划')
  await request.put('/admin/plans/batch-publish', selectedIds.value)
  ElMessage.success('批量发布成功')
  load()
}

async function viewDetail(row) {
  detail.value = await request.get(`/admin/plans/${row.id}`)
  showDetail.value = true
}

function statusLabel(s) {
  const map = ['草稿','待学院配置','待教研室协调','待学院审核','待教务处排课','已发布']
  return map[s] || '未知'
}
function statusType(s) {
  const map = ['info','warning','','primary','success','success']
  return map[s] || 'info'
}

async function resetAll() {
  try {
    await ElMessageBox.confirm(
      '此操作将清空所有申报、审批、教学任务、课表数据，并将所有教学计划恢复为草稿状态。确定继续？',
      '⚠️ 一键重置',
      { confirmButtonText: '确定重置', cancelButtonText: '取消', type: 'warning' }
    )
    await request.post('/admin/plans/reset-all')
    ElMessage.success('已重置：所有计划回到草稿，下游数据已清空')
    load()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('重置失败：' + (e.message || '未知错误'))
  }
}

onMounted(load)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
.header-actions { display: flex; gap: 8px; }
</style>
