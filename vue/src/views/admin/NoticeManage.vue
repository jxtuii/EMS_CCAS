<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>公告管理</span>
          <el-button type="primary" @click="showCreate = true">发布公告</el-button>
        </div>
      </template>
      <el-table :data="notices" border stripe>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type === 'SYSTEM' ? '系统' : row.type === 'COLLEGE' ? '学院' : '部门' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetRole" label="目标角色" width="120">
          <template #default="{ row }">{{ roleLabel(row.targetRole) }}</template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreate" title="发布公告" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="公告标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option label="系统公告" value="SYSTEM" />
            <el-option label="学院公告" value="COLLEGE" />
            <el-option label="部门公告" value="DEPT" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标角色">
          <el-select v-model="form.targetRole">
            <el-option label="全部" value="ALL" />
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="createNotice">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const notices = ref([])
const showCreate = ref(false)

const form = ref({
  title: '',
  type: 'SYSTEM',
  targetRole: 'ALL',
  content: ''
})

function roleLabel(r) {
  const map = { ALL: '全部', ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
  return map[r] || r
}

async function load() {
  notices.value = await request.get('/admin/notices')
}

async function createNotice() {
  if (!form.value.title) { ElMessage.warning('请输入标题'); return }
  if (!form.value.content) { ElMessage.warning('请输入内容'); return }
  await request.post('/admin/notices', form.value)
  ElMessage.success('发布成功')
  showCreate.value = false
  form.value = { title: '', type: 'SYSTEM', targetRole: 'ALL', content: '' }
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除公告「${row.title}」？`, '提示')
  await request.delete(`/admin/notices/${row.id}`)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
</style>
