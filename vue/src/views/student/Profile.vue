<template>
  <div>
    <el-card style="margin-bottom:20px">
      <template #header><span>个人信息</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ profile?.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ profile?.realName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ profile?.studentNo || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ profile?.className || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ profile?.grade || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ profile?.email || '未设置' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card>
      <template #header><span>修改密码</span></template>
      <el-form :model="pwdForm" label-width="100px" style="max-width:400px">
        <el-form-item label="当前密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="changePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const profile = ref({})
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

onMounted(async () => {
  try { profile.value = await request.get('/common/profile') } catch { /* handled */ }
})

async function changePassword() {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  await request.put('/common/profile/password', {
    oldPassword: pwdForm.value.oldPassword,
    newPassword: pwdForm.value.newPassword
  })
  ElMessage.success('密码修改成功')
  pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
}
</script>
