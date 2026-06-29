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
        <el-menu-item index="/admin/dashboard"><el-icon><HomeFilled /></el-icon><span slot="title">首页</span></el-menu-item>
        <template v-if="hasRole('ADMIN_SCHOOL')">
          <el-menu-item index="/admin/plans"><el-icon><Document /></el-icon><span slot="title">教学计划管理</span></el-menu-item>
          <el-menu-item index="/admin/status-board"><el-icon><DataBoard /></el-icon><span slot="title">状态看板</span></el-menu-item>
          <el-menu-item index="/admin/scheduling"><el-icon><Timer /></el-icon><span slot="title">排课管理</span></el-menu-item>
          <el-menu-item index="/admin/reports"><el-icon><Histogram /></el-icon><span slot="title">任务书与课表</span></el-menu-item>
        </template>
        <template v-if="hasRole('ADMIN_COLLEGE')">
          <el-menu-item index="/admin/config-rules"><el-icon><Setting /></el-icon><span slot="title">教学限制配置</span></el-menu-item>
          <el-menu-item index="/admin/college-audit"><el-icon><Select /></el-icon><span slot="title">学院审核</span></el-menu-item>
        </template>
        <template v-if="hasRole('DIRECTOR_DEPT')">
          <el-menu-item index="/admin/app-board"><el-icon><List /></el-icon><span slot="title">申报看板</span></el-menu-item>
          <el-menu-item index="/admin/assign"><el-icon><UserFilled /></el-icon><span slot="title">教师分配</span></el-menu-item>
        </template>
        <div class="menu-divider"></div>
        <el-menu-item index="/admin/task-pool"><el-icon><FolderOpened /></el-icon><span slot="title">任务池</span></el-menu-item>
        <el-menu-item index="/admin/notices"><el-icon><Bell /></el-icon><span slot="title">公告管理</span></el-menu-item>
        <el-menu-item index="/admin/exam-plans"><el-icon><Calendar /></el-icon><span slot="title">考试安排</span></el-menu-item>
        <el-menu-item index="/admin/room-plans"><el-icon><HomeFilled /></el-icon><span slot="title">教室安排</span></el-menu-item>
        <el-menu-item index="/admin/courses"><el-icon><Reading /></el-icon><span slot="title">课程管理</span></el-menu-item>
        <el-menu-item index="/admin/rule-config"><el-icon><Tools /></el-icon><span slot="title">规则配置</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="toggle-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
          <span class="header-title">{{ route.meta?.title || '管理端' }}</span>
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
                <el-dropdown-item command="logout">退出系统</el-dropdown-item>
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

function hasRole(role) { return authStore.hasRole(role) }

const roleLabel = computed(() => {
  const roles = userInfo.value?.roles || []
  if (roles.includes('ADMIN_SCHOOL')) return '教务处'
  if (roles.includes('ADMIN_COLLEGE')) return '学院管理员'
  if (roles.includes('DIRECTOR_DEPT')) return '教研室主任'
  if (roles.includes('TEACHER')) return '教师'
  return '管理员'
})

function handleCommand(cmd) {
  if (cmd === 'logout') handleLogout()
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
