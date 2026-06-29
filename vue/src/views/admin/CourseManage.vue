<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>课程管理</span>
          <el-button type="primary" @click="showCreate = true">新增课程</el-button>
        </div>
      </template>
      <el-table :data="courses" border stripe>
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="180" />
        <el-table-column prop="credit" label="学分" width="60" />
        <el-table-column prop="totalHours" label="总学时" width="70" />
        <el-table-column label="主干课程" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isCore ? 'success' : 'info'" size="small">{{ row.isCore ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requiredTitleLevel" label="最低职称" width="100">
          <template #default="{ row }">{{ titleLabel(row.requiredTitleLevel) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" :title="isEdit ? '编辑课程' : '新增课程'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="课程编号"><el-input v-model="form.courseCode" /></el-form-item>
        <el-form-item label="课程名称"><el-input v-model="form.courseName" /></el-form-item>
        <el-form-item label="学分"><el-input-number v-model="form.credit" :min="0.5" :step="0.5" /></el-form-item>
        <el-form-item label="总学时"><el-input-number v-model="form.totalHours" :min="1" /></el-form-item>
        <el-form-item label="主干课程">
          <el-switch v-model="form.isCore" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="最低职称">
          <el-select v-model="form.requiredTitleLevel">
            <el-option label="无限制" :value="0" />
            <el-option label="助教" :value="1" />
            <el-option label="讲师" :value="2" />
            <el-option label="副教授" :value="3" />
            <el-option label="教授" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCourse">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const courses = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const editingId = ref(null)

const form = ref({
  courseCode: '',
  courseName: '',
  credit: 3,
  totalHours: 48,
  isCore: 0,
  requiredTitleLevel: 0
})

function titleLabel(level) {
  return ['无限制','助教','讲师','副教授','教授'][level] || '未知'
}

async function load() {
  courses.value = await request.get('/admin/courses')
}

function showEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  form.value = {
    courseCode: row.courseCode,
    courseName: row.courseName,
    credit: row.credit,
    totalHours: row.totalHours,
    isCore: row.isCore ?? 0,
    requiredTitleLevel: row.requiredTitleLevel ?? 0
  }
  showDialog.value = true
}

function resetForm() {
  form.value = { courseCode: '', courseName: '', credit: 3, totalHours: 48, isCore: 0, requiredTitleLevel: 0 }
  isEdit.value = false
  editingId.value = null
}

async function saveCourse() {
  if (!form.value.courseCode || !form.value.courseName) {
    ElMessage.warning('请填写课程编号和名称')
    return
  }
  if (isEdit.value) {
    await request.put('/admin/courses', { ...form.value, id: editingId.value })
    ElMessage.success('更新成功')
  } else {
    await request.post('/admin/courses', form.value)
    ElMessage.success('创建成功')
  }
  showDialog.value = false
  resetForm()
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除课程 ${row.courseName}？`, '提示')
  await request.delete(`/admin/courses/${row.id}`)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
</style>
