<template>
  <div>
    <el-card>
      <template #header>
        <div class="header-row">
          <span>教室安排管理</span>
          <el-button type="primary" @click="showCreate = true">新增教室安排</el-button>
        </div>
      </template>
      <el-table :data="roomPlans" border stripe>
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="roomName" label="教室" width="120" />
        <el-table-column prop="capacity" label="容量" width="70" />
        <el-table-column label="星期" width="70">
          <template #default="{ row }">星期{{ row.weekday }}</template>
        </el-table-column>
        <el-table-column prop="sectionNo" label="节次" width="70" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'danger' : 'success'">{{ row.status === 1 ? '已占用' : '空闲' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" v-if="row.status === 0" @click="occupyRoom(row)">占用</el-button>
            <el-button size="small" type="success" v-else @click="releaseRoom(row)">释放</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreate" title="新增教室安排" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="学期"><el-input v-model="form.semester" placeholder="如 2025-2026-1" /></el-form-item>
        <el-form-item label="教室名称"><el-input v-model="form.roomName" /></el-form-item>
        <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="10" :max="200" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="createRoom">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const roomPlans = ref([])
const showCreate = ref(false)

const form = ref({
  semester: '2025-2026-1',
  roomName: '',
  capacity: 60
})

async function load() {
  roomPlans.value = await request.get('/admin/room-plans')
}

async function createRoom() {
  if (!form.value.roomName) { ElMessage.warning('请输入教室名称'); return }
  await request.post('/admin/room-plans', form.value)
  ElMessage.success('创建成功')
  showCreate.value = false
  form.value = { semester: '2025-2026-1', roomName: '', capacity: 60 }
  load()
}

async function occupyRoom(row) {
  await request.put(`/admin/room-plans/${row.id}/occupy`)
  ElMessage.success('已占用')
  load()
}

async function releaseRoom(row) {
  await request.put(`/admin/room-plans/${row.id}/release`)
  ElMessage.success('已释放')
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除 ${row.roomName} 的安排？`, '提示')
  await request.delete(`/admin/room-plans/${row.id}`)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
</style>
