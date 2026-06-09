import axios from 'axios'
import { ElMessage } from 'element-plus'

import router from '@/router'
import { getToken } from '@/utils/auth'
import { useAuthStore } from '@/stores/auth'

const request = axios.create({
  // 开发环境通过 Vite 代理转发到 8080，打包后前后端同源部署在 Spring Boot 中，
  // 因此默认使用相对路径即可，避免把接口地址写死为 localhost。
  baseURL: import.meta.env.VITE_BASE_API || '',
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const token = getToken()

    // 后端统一从请求头 token 读取 JWT，这里统一补齐，避免页面重复写请求头。
    if (token) {
      config.headers.token = token
    }

    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => {
    const res = response.data

    // 后端统一返回 Result。
    // code=1 说明业务成功，直接把 data 返回给页面层使用。
    if (res.code === 1) {
      return res.data
    }

    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    // token 过期或未登录时，后端会返回 401。
    // 前端统一清空登录状态并跳回登录页。
    if (error.response && error.response.status === 401) {
      const authStore = useAuthStore()
      authStore.clearLoginState()
      ElMessage.error('登录状态已失效，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error(error.message || '网络异常，请稍后重试')
    }

    return Promise.reject(error)
  }
)

export default request
