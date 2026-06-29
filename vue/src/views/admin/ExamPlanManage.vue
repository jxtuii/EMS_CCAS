<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>考试安排管理</span>
          <el-button type="primary" @click="showCreate = true">新增安排</el-button>
        </div>
      </template>
      <el-table :data="examPlans" border stripe>
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="100" />
        <el-table-column prop="endTime" label="结束时间" width="100" />
        <el-table-column prop="room" label="教室" width="120" />
        <el-table-column prop="invigilatorName" label="监考教师" width="120" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发布' : '未发布' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" v-if="row.status === 0" @click="publishExam(row)">发布</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreate" title="新增考试安排" width="550px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="学期"><el-input v-model="form.semester" placeholder="如 2025-2026-1" /></el-form-item>
        <el-form-item label="课程">
          <el-select v-model="form.courseId" style="width:100%" filterable>
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="form.classId" style="width:100%" filterable>
            <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试日期"><el-input v-model="form.examDate" placeholder="2025-07-10" /></el-form-item>
        <el-form-item label="开始时间"><el-input v-model="form.startTime" placeholder="09:00" /></el-form-item>
        <el-form-item label="结束时间"><el-input v-model="form.endTime" placeholder="11:00" /></el-form-item>
        <el-form-item label="教室"><el-input v-model="form.room" placeholder="教室名称" /></el-form-item>
        <el-form-item label="监考教师">
          <el-select v-model="form.invigilatorId" style="width:100%" filterable clearable>
            <el-option v-for="t in teachers" :key="t.id" :label="t.realName" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="createExam">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const examPlans = ref([])
const courses = ref([])
const classList = ref([])
const teachers = ref([])
const showCreate = ref(false)

const form = ref({
  semester: '2025-2026-1',
  courseId: null,
  classId: null,
  examDate: '',
  startTime: '',
  endTime: '',
  room: '',
  invigilatorId: null
})

async function load() {
  [examPlans.value, courses.value, classList.value, teachers.value] = await Promise.all([
    request.get('/admin/exam-plans'),
    request.get('/admin/courses'),
    request.get('/admin/plans/classes'),
    request.get('/admin/assignments/teachers')
  ])
}

async function createExam() {
  await request.post('/admin/exam-plans', form.value)
  ElMessage.success('创建成功')
  showCreate.value = false
  form.value = { semester: '2025-2026-1', courseId: null, classId: null, examDate: '', startTime: '', endTime: '', room: '', invigilatorId: null }
  load()
}

async function publishExam(row) {
  await request.put(`/admin/exam-plans/${row.id}/publish`)
  ElMessage.success('已发布')
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该考试安排？', '提示')
  await request.delete(`/admin/exam-plans/${row.id}`)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
</style>
