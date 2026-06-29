<template>
  <div>
    <el-card>
      <template #header><span>教师分配</span></template>
      <el-table :data="pendingApps" border stripe>
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="totalHours" label="学时" width="80" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showAssign(row)">分配教师</el-button>
            <el-button size="small" @click="showValidation(row)">校验规则</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" title="分配教师" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="课程">{{ currentApp?.courseName }}</el-form-item>
        <el-form-item label="申报教师">{{ currentApp?.teacherName }}</el-form-item>
        <el-form-item label="选择教师">
          <el-select v-model="assignForm.teacherId" style="width:100%" filterable>
            <el-option v-for="t in teachers" :key="t.id" :label="`${t.realName} (${t.teacherNo})`" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色类型">
          <el-radio-group v-model="assignForm.roleType">
            <el-radio value="主讲">主讲</el-radio>
            <el-radio value="辅讲">辅讲</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="validationErrors.length">
          <el-alert :title="validationErrors.join('; ')" type="error" show-icon />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button @click="doValidate">校验规则</el-button>
        <el-button type="primary" :disabled="validationErrors.length > 0" @click="doAssign">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const applications = ref([])
const teachers = ref([])
const showDialog = ref(false)
const currentApp = ref(null)
const validationErrors = ref([])

const assignForm = ref({ teacherId: null, roleType: '主讲' })

const pendingApps = computed(() =>
  applications.value.filter(a => a.status === 1 || a.status === 2)
)

async function showAssign(row) {
  currentApp.value = row
  assignForm.value = { teacherId: row.teacherId || null, roleType: '主讲' }
  validationErrors.value = []
  showDialog.value = true
}

async function doValidate() {
  if (!assignForm.value.teacherId) {
    ElMessage.warning('请选择教师')
    return
  }
  try {
    const errors = await request.post('/admin/assignments/validate', {
      applicationId: currentApp.value.id,
      teacherId: assignForm.value.teacherId,
      roleType: assignForm.value.roleType
    })
    validationErrors.value = errors || []
    if (!errors?.length) ElMessage.success('规则校验通过')
  } catch { /* handled */ }
}

async function doAssign() {
  if (!assignForm.value.teacherId) {
    ElMessage.warning('请选择教师')
    return
  }
  if (validationErrors.value.length) {
    ElMessage.warning('请先解决规则校验问题')
    return
  }
  await request.post('/admin/assignments', {
    applicationId: currentApp.value.id,
    teacherId: assignForm.value.teacherId,
    roleType: assignForm.value.roleType
  })
  ElMessage.success('分配成功')
  showDialog.value = false
  load()
}

async function load() {
  [applications.value, teachers.value] = await Promise.all([
    request.get('/admin/applications'),
    request.get('/admin/assignments/teachers')
  ])
}

onMounted(load)
</script>
