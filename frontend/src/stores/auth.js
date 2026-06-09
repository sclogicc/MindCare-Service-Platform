import { defineStore } from 'pinia'
import { getToken, setToken, clearToken, getUserInfo, setUserInfo, clearUserInfo } from '@/utils/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    userInfo: getUserInfo()
  }),
  actions: {
    setLoginState(userInfo) {
      this.token = userInfo.token
      this.userInfo = userInfo
      setToken(userInfo.token)
      setUserInfo(userInfo)
    },
    clearLoginState() {
      this.token = ''
      this.userInfo = null
      clearToken()
      clearUserInfo()
    }
  }
})
