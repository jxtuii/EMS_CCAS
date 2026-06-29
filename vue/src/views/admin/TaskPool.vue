<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>任务池管理</span>
          <el-button type="primary" @click="showCreate = true">新增任务</el-button>
        </div>
      </template>
      <el-table :data="tasks" border stripe>
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column prop="weeklyHours" label="周学时" width="80" />
        <el-table-column prop="requiredTitleLevel" label="最低职称" width="100">
          <template #default="{ row }">{{ titleLabel(row.requiredTitleLevel) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="taskStatusType(row.status)">{{ taskStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignedTeacherName" label="已分配教师" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :disabled="row.status !== 0" @click="showAssign(row)">分配</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreate" title="新增任务" width="500px">
      <el-form :model="newTask" label-width="100px">
        <el-form-item label="学期"><el-input v-model="newTask.semester" placeholder="如 2025-2026-1" /></el-form-item>
        <el-form-item label="课程">
          <el-select v-model="newTask.courseId" style="width:100%" filterable>
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="newTask.classId" style="width:100%" filterable>
            <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="总学时"><el-input-number v-model="newTask.totalHours" :min="1" /></el-form-item>
        <el-form-item label="周学时"><el-input-number v-model="newTask.weeklyHours" :min="1" /></el-form-item>
        <el-form-item label="最低职称">
          <el-select v-model="newTask.requiredTitleLevel">
            <el-option label="助教" :value="1" />
            <el-option label="讲师" :value="2" />
            <el-option label="副教授" :value="3" />
            <el-option label="教授" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="createTask">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAssignDlg" title="分配教师" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="教师">
          <el-select v-model="assignForm.teacherId" style="width:100%" filterable>
            <el-option v-for="t in teachers" :key="t.id" :label="`${t.realName} (${t.teacherNo})`" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssignDlg = false">取消</el-button>
        <el-button type="primary" @click="doAssign">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tasks = ref([])
const courses = ref([])
const classList = ref([])
const teachers = ref([])
const showCreate = ref(false)
const showAssignDlg = ref(false)
const currentTask = ref(null)

const newTask = ref({
  semester: '2025-2026-1',
  courseId: null,
  classId: null,
  totalHours: 48,
  weeklyHours: 4,
  requiredTitleLevel: 2
})

const assignForm = ref({ teacherId: null })

function titleLabel(level) {
  return ['','助教','讲师','副教授','教授'][level] || '未知'
}
function taskStatusLabel(s) {
  return ['待分配','已分配','已完成'][s] || '未知'
}
function taskStatusType(s) {
  return ['info','success','primary'][s] || 'info'
}

async function load() {
  [tasks.value, courses.value, classList.value, teachers.value] = await Promise.all([
    request.get('/admin/task-pool'),
    request.get('/admin/courses'),
    request.get('/admin/plans/classes'),
    request.get('/admin/assignments/teachers')
  ])
}

async function createTask() {
  await request.post('/admin/task-pool', newTask.value)
  ElMessage.success('创建成功')
  showCreate.value = false
  load()
}

function showAssign(row) {
  currentTask.value = row
  assignForm.value = { teacherId: null }
  showAssignDlg.value = true
}

async function doAssign() {
  if (!assignForm.value.teacherId) {
    ElMessage.warning('请选择教师')
    return
  }
  await request.put(`/admin/task-pool/${currentTask.value.id}/assign`, { teacherId: assignForm.value.teacherId })
  ElMessage.success('分配成功')
  showAssignDlg.value = false
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除 ${row.courseName} 的任务？`, '提示')
  await request.delete(`/admin/task-pool/${row.id}`)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
</style>
