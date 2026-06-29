<template>
  <el-container style="min-height: 100vh">
    <el-aside :width="isCollapse ? '64px' : '230px'" class="layout-aside">
      <div class="logo" :class="{ collapsed: isCollapse }">
        <img src="/xtulogo.png" class="logo-img" alt="logo" />
        <span v-show="!isCollapse" class="logo-text">湘大教务管理系统</span>
        <span v-show="isCollapse" class="logo-text">教务</span>
      </div>
      <el-menu :default-active="route.path" :collapse="isCollapse" router
               background-color="#001529" text-color="#a6adb4" active-text-color="#ffffff">
        <el-menu-item index="/teacher/dashboard"><el-icon><HomeFilled /></el-icon><span slot="title">首页</span></el-menu-item>
        <el-menu-item index="/teacher/tasks"><el-icon><Notebook /></el-icon><span slot="title">我的任务</span></el-menu-item>
        <el-menu-item index="/teacher/applications"><el-icon><EditPen /></el-icon><span slot="title">我的申报</span></el-menu-item>
        <el-menu-item index="/teacher/apply-intent"><el-icon><TrendCharts /></el-icon><span slot="title">授课意向</span></el-menu-item>
        <el-menu-item index="/teacher/schedule"><el-icon><Timer /></el-icon><span slot="title">我的课表</span></el-menu-item>
        <el-menu-item index="/teacher/notices"><el-icon><Bell /></el-icon><span slot="title">公告</span></el-menu-item>
        <div class="menu-divider"></div>
        <el-menu-item index="/teacher/profile"><el-icon><User /></el-icon><span slot="title">个人设置</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="toggle-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
          <span class="header-title">{{ route.meta?.title || '教师端' }}</span>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-dropdown">
              <span class="header-user">{{ userInfo?.realName || userInfo?.username }}</span>
              <span class="header-role-tag">{{ roleLabel }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出系统</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="layout-main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)
const userInfo = computed(() => authStore.user)

const roleLabel = computed(() => {
  const roles = userInfo.value?.roles || []
  if (roles.includes('DIRECTOR_DEPT')) return '教研室主任'
  if (roles.includes('TEACHER')) return '教师'
  return ''
})

function handleCommand(cmd) {
  if (cmd === 'logout') handleLogout()
  else if (cmd === 'profile') router.push('/teacher/profile')
}

function handleLogout() {
  ElMessageBox.confirm('确定要退出系统吗？', '提示').then(() => {
    authStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-aside {
  background: #001529;
  transition: width 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  z-index: 10;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
  height: 60px;
  background: #002140;
  transition: padding 0.3s;
  overflow: hidden;
}

.logo.collapsed {
  padding: 16px 10px;
  justify-content: center;
}

.logo-img {
  width: 36px;
  height: 36px;
  object-fit: contain;
  flex-shrink: 0;
}

.logo-text {
  color: #ffffff;
  font-weight: 500;
  font-size: 17px;
  white-space: nowrap;
  letter-spacing: 0.5px;
}

.layout-menu {
  flex: 1;
  border-right: none !important;
  overflow-y: auto;
  padding-top: 4px;
}

.layout-menu .el-menu-item {
  height: 44px;
  line-height: 44px;
  margin: 2px 0;
}

.layout-menu .el-menu-item:hover {
  background: #1890ff !important;
  color: #ffffff !important;
}

.layout-menu .el-menu-item.is-active {
  background: #1890ff !important;
  color: #ffffff !important;
}

.layout-menu .el-menu-item .el-icon {
  color: #a4b2c2;
  font-size: 16px;
  margin-right: 8px;
}

.layout-menu .el-menu-item:hover .el-icon,
.layout-menu .el-menu-item.is-active .el-icon {
  color: #ffffff;
}

.menu-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.08);
  margin: 6px 16px;
}

.layout-header {
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: relative;
  z-index: 9;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.toggle-btn {
  font-size: 20px;
  color: #666;
  cursor: pointer;
}

.toggle-btn:hover {
  color: #1890ff;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 4px;
}

.user-dropdown:hover {
  background: #f5f7fa;
}

.header-user {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.header-role-tag {
  font-size: 12px;
  color: #1890ff;
  background: #e6f7ff;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 400;
}

.layout-main {
  background: #f4f6f9;
  padding: 20px;
  min-height: calc(100vh - 60px);
}
</style>
