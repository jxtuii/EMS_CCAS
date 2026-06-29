<template>
  <div>
    <el-card>
      <template #header><span>课程查询</span></template>

      <el-form :inline="true" style="margin-bottom:16px">
        <el-form-item label="关键词">
          <el-input v-model="keyword" placeholder="课程名称 / 编号" clearable @keyup.enter="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="courses" border stripe>
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="credit" label="学分" width="60" />
        <el-table-column prop="totalHours" label="总学时" width="70" />
        <el-table-column label="主干课程" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isCore ? 'success' : 'info'" size="small">{{ row.isCore ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!courses.length && loaded" description="暂无数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const courses = ref([])
const keyword = ref('')
const loaded = ref(false)

async function search() {
  loaded.value = false
  try {
    const params = keyword.value ? { keyword: keyword.value } : {}
    courses.value = await request.get('/student/courses', { params }) || []
  } catch {
    courses.value = []
  }
  loaded.value = true
}

onMounted(search)
</script>
