<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-brand">
        <div class="brand-icon">
          <img src="/xtulogo.png" alt="湘潭大学" class="brand-logo" />
        </div>
        <h2 class="login-title">湘大教务管理系统</h2>
        <p class="login-subtitle">XTU Educational Management System</p>
      </div>

      <!-- Role Selector -->
      <el-radio-group v-model="portal" class="role-selector">
        <el-radio-button value="admin">管理端</el-radio-button>
        <el-radio-button value="teacher">教师端</el-radio-button>
        <el-radio-button value="student">学生端</el-radio-button>
      </el-radio-group>

      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;background:#284974;border-color:#284974" :loading="loading" @click="handleLogin">
            {{ loading ? '登录中…' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-hint">
        <p>管理端：admin / 123456</p>
        <p>教师端：js001 / 123456</p>
        <p>学生端：S001 / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const portal = ref('admin')

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.login(form.username, form.password, portal.value)
    ElMessage.success('登录成功')
    router.push(authStore.getHomePath())
  } catch { /* handled by interceptor */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('/xtu.jpg') center/cover no-repeat;
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.55);
  pointer-events: none;
}

.login-card {
  width: 420px;
  padding: 36px 40px 30px;
  background: rgba(255, 255, 255, 0.88);
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;
  animation: fadeInUp 1.2s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-brand {
  text-align: center;
  margin-bottom: 24px;
}

.brand-icon {
  margin: 0 auto 14px;
}

.brand-logo {
  width: 72px;
  height: 72px;
  object-fit: contain;
}

.login-title {
  font-weight: 600;
  font-size: 22px;
  color: #303133;
  margin: 0 0 6px;
  letter-spacing: 1px;
}

.login-subtitle {
  font-weight: 400;
  font-size: 13px;
  color: #909399;
  margin: 0;
  letter-spacing: 0.5px;
}

.role-selector {
  display: flex;
  justify-content: center;
  margin-bottom: 22px;
  width: 100%;
}

.role-selector .el-radio-button__inner {
  font-size: 13px;
  padding: 8px 20px;
  border-color: #dcdfe6;
  color: #606266;
}

.role-selector .el-radio-button.is-active .el-radio-button__inner {
  background: #284974;
  color: #ffffff;
  border-color: #284974;
  box-shadow: none;
}

.login-hint {
  text-align: center;
  color: #c0c4cc;
  font-size: 12px;
  margin-top: 18px;
  line-height: 1.6;
}

.login-hint p { margin: 2px 0; }
</style>
