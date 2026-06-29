<template>
  <div>
    <el-card>
      <template #header><span>公告</span></template>
      <el-table :data="notices" border stripe>
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type === 'SYSTEM' ? '系统' : row.type === 'COLLEGE' ? '学院' : '部门' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" :title="currentNotice?.title" width="600px">
      <div style="color: var(--sb-ink-mute); font-size: 12px; margin-bottom: 16px">
        发布人：{{ currentNotice?.publisherName }} &nbsp;|&nbsp; 时间：{{ currentNotice?.publishTime }}
      </div>
      <div style="white-space: pre-wrap; line-height: 1.6; color: var(--sb-ink); font-size: 14px">
        {{ currentNotice?.content }}
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const notices = ref([])
const showDialog = ref(false)
const currentNotice = ref(null)

function showDetail(row) {
  currentNotice.value = row
  showDialog.value = true
}

onMounted(async () => {
  try { notices.value = await request.get('/teacher/notices') } catch { notices.value = [] }
})
</script>
