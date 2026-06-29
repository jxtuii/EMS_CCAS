import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const isLoggedIn = ref(!!localStorage.getItem('token'))

  function setUser(userData) {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
    localStorage.setItem('token', userData.token)
    isLoggedIn.value = true
  }

  function logout() {
    user.value = null
    isLoggedIn.value = false
    localStorage.clear()
  }

  async function login(username, password, portal = 'admin') {
    const baseURL = portal === 'admin' ? '/admin/auth/login'
      : portal === 'teacher' ? '/teacher/auth/login'
      : '/student/auth/login'
    const res = await request.post(baseURL, { username, password, portal })
    res.portal = portal
    setUser(res)
    return res
  }

  function hasRole(roleCode) {
    return user.value?.roles?.includes(roleCode)
  }

  function hasAnyRole(roles) {
    return roles.some(r => user.value?.roles?.includes(r))
  }

  function getPortal() {
    return user.value?.portal || 'admin'
  }

  function getHomePath() {
    const portal = getPortal()
    return `/${portal}/dashboard`
  }

  return { user, isLoggedIn, setUser, logout, login, hasRole, hasAnyRole, getPortal, getHomePath }
})
