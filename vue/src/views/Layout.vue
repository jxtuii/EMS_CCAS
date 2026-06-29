<template>
  <el-container style="min-height: 100vh">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo" :class="{ collapsed: isCollapse }">
        <span class="brand-dot"></span>
        <span v-show="!isCollapse" class="logo-text">排课管理系统</span>
        <span v-show="isCollapse" class="logo-text">排课</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        router
        class="layout-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 教务处菜单 -->
        <template v-if="hasRole('ADMIN_SCHOOL')">
          <el-menu-item index="/plans">
            <el-icon><Document /></el-icon>
            <span>教学计划管理</span>
          </el-menu-item>
          <el-menu-item index="/status-board">
            <el-icon><DataBoard /></el-icon>
            <span>状态看板</span>
          </el-menu-item>
          <el-menu-item index="/scheduling">
            <el-icon><Timer /></el-icon>
            <span>排课管理</span>
          </el-menu-item>
          <el-menu-item index="/reports">
            <el-icon><Histogram /></el-icon>
            <span>任务书与课表</span>
          </el-menu-item>
        </template>

        <!-- 学院管理员菜单 -->
        <template v-if="hasRole('ADMIN_COLLEGE')">
          <el-menu-item index="/config-rules">
            <el-icon><Setting /></el-icon>
            <span>教学限制配置</span>
          </el-menu-item>
          <el-menu-item index="/college-audit">
            <el-icon><Select /></el-icon>
            <span>学院审核</span>
          </el-menu-item>
        </template>

        <!-- 教研室主任菜单 -->
        <template v-if="hasRole('DIRECTOR_DEPT')">
          <el-menu-item index="/app-board">
            <el-icon><List /></el-icon>
            <span>申报看板</span>
          </el-menu-item>
          <el-menu-item index="/assign">
            <el-icon><UserFilled /></el-icon>
            <span>教师分配</span>
          </el-menu-item>
        </template>

        <!-- 教师菜单 -->
        <template v-if="hasRole('TEACHER')">
          <el-menu-item index="/my-apply">
            <el-icon><EditPen /></el-icon>
            <span>我的申报</span>
          </el-menu-item>
          <el-menu-item index="/my-tasks">
            <el-icon><Notebook /></el-icon>
            <span>我的任务</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon style="cursor:pointer; color: var(--sb-ink-mute);" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
          <span class="header-title">{{ route.meta.title }}</span>
        </div>
        <div class="header-right">
          <span class="header-role-tag">{{ roleLabel }}</span>
          <span class="header-user">{{ userInfo?.realName || userInfo?.username }}</span>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)
const userInfo = computed(() => authStore.user)

function hasRole(role) {
  return authStore.hasRole(role)
}

const roleLabel = computed(() => {
  const roles = userInfo.value?.roles || []
  if (roles.includes('ADMIN_SCHOOL')) return '教务处'
  if (roles.includes('ADMIN_COLLEGE')) return '学院管理员'
  if (roles.includes('DIRECTOR_DEPT')) return '教研室主任'
  if (roles.includes('TEACHER')) return '教师'
  return '未知'
})

function handleLogout() {
  ElMessageBox.confirm('确定要退出系统吗？', '提示').then(() => {
    authStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-aside {
  background: var(--sb-canvas);
  border-right: 1px solid var(--sb-hairline);
  transition: width 0.25s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px;
  border-bottom: 1px solid var(--sb-hairline-cool);
  transition: padding 0.25s ease;
}
.logo.collapsed {
  padding: 20px 10px;
  justify-content: center;
}
.brand-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: var(--sb-primary);
  border-radius: 50%;
  flex-shrink: 0;
}
.logo-text {
  color: var(--sb-ink);
  font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-weight: 500;
  font-size: 16px;
  white-space: nowrap;
  letter-spacing: -0.2px;
}

.layout-menu {
  flex: 1;
  border-right: none !important;
  padding: 8px 0;
}
.layout-menu .el-menu-item {
  color: var(--sb-ink-mute);
  font-weight: 400;
  margin: 2px 8px;
  border-radius: var(--sb-radius-sm);
  padding: 0 12px;
  height: 40px;
  line-height: 40px;
}
.layout-menu .el-menu-item:hover {
  background: var(--sb-canvas-soft);
  color: var(--sb-ink);
}
.layout-menu .el-menu-item.is-active {
  color: var(--sb-ink);
  background: #f0fdf6;
  font-weight: 500;
}
.layout-menu .el-menu-item.is-active .el-icon {
  color: var(--sb-primary);
}

.layout-header {
  background: var(--sb-canvas);
  border-bottom: 1px solid var(--sb-hairline);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-title {
  font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--sb-ink);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-role-tag {
  font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 12px;
  font-weight: 500;
  color: var(--sb-ink-mute);
  background: var(--sb-canvas-soft);
  padding: 2px 10px;
  border-radius: 4px;
  border: 1px solid var(--sb-hairline-cool);
}
.header-user {
  font-family: Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 14px;
  color: var(--sb-ink);
  font-weight: 500;
}

.layout-main {
  background: var(--sb-canvas-soft);
  padding: 24px;
  min-height: calc(100vh - 60px);
}
</style>
