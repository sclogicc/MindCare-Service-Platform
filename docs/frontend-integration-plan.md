# 前端联调方案

## 1. 方案目标

本方案基于当前“心理咨询服务预约平台”后端接口设计，给出一套适合：

- Vue 3
- Axios
- Pinia
- Vue Router
- Element Plus

的前端联调基线。

目标不是追求复杂前端架构，而是保证：

1. 登录状态稳定
2. token 管理清晰
3. 接口调用统一
4. 错误处理一致
5. 页面联调顺畅

页面整体风格以后台管理系统为主：

- 左侧菜单
- 顶部导航
- 查询区 + 表格区 + 分页区
- 表单页 / 详情页 / 图表页

---

## 2. 推荐目录结构

```text
src
├─ api
│  ├─ auth.js
│  ├─ appointment.js
│  ├─ counselor.js
│  ├─ feedback.js
│  ├─ record.js
│  ├─ report.js
│  ├─ schedule.js
│  └─ upload.js
├─ router
│  └─ index.js
├─ stores
│  ├─ auth.js
│  └─ app.js
├─ utils
│  ├─ request.js
│  ├─ auth.js
│  ├─ constant.js
│  ├─ format.js
│  └─ validate.js
├─ layout
│  └─ MainLayout.vue
└─ views
```

说明：

- `api`：按业务模块封装接口请求
- `router`：维护路由和路由守卫
- `stores`：维护登录状态和全局状态
- `utils/request.js`：统一 Axios 封装
- `utils/auth.js`：统一 token 持久化
- `layout`：后台管理系统主布局

---

## 3. axios 封装方案

建议文件：

- `src/utils/request.js`

职责：

1. 创建 Axios 实例
2. 配置基础 `baseURL`
3. 配置超时时间
4. 统一请求拦截器
5. 统一响应拦截器

推荐实现：

```js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { getToken, clearToken } from '@/utils/auth'
import { useAuthStore } from '@/stores/auth'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const token = getToken()

    // 后端当前统一从请求头 token 中取 JWT，
    // 因此前端所有需要登录态的请求都在这里自动补齐。
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

    // 后端统一返回 Result：
    // code = 1 表示成功
    // code = 0 表示业务失败
    if (res.code === 1) {
      return res.data
    }

    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    // HTTP 401 表示 token 失效或未登录，
    // 这里统一清空登录状态并跳回登录页。
    if (error.response && error.response.status === 401) {
      const authStore = useAuthStore()
      authStore.clearLoginState()
      clearToken()
      ElMessage.error('登录状态已失效，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error(error.message || '网络异常，请稍后重试')
    }

    return Promise.reject(error)
  }
)

export default request
```

---

## 4. 请求拦截器方案

请求拦截器重点做一件事：

- 自动携带 token

原因：

1. 当前后端登录校验通过 `TokenInterceptor` 完成
2. 后端统一从请求头 `token` 读取 JWT
3. 如果每个页面手动写请求头，容易漏掉，联调成本高

因此统一在请求拦截器里处理最稳。

关键点：

- 登录接口 `/login` 不要求必须特殊排除，因为没有 token 时也能正常请求
- 有 token 时统一放到 `config.headers.token`

---

## 5. 响应拦截器方案

响应拦截器建议统一处理两类问题：

### 5.1 业务失败

后端统一返回：

```json
{
  "code": 0,
  "msg": "错误信息"
}
```

前端处理原则：

- 统一弹出 `ElMessage.error`
- 同时 `Promise.reject(...)`
- 让页面可以按需捕获后续逻辑

### 5.2 未登录 / token 失效

当前后端若 token 无效会返回 HTTP `401`。

前端统一处理：

1. 清空 token
2. 清空 Pinia 登录信息
3. 跳转登录页
4. 弹出提示“登录状态已失效，请重新登录”

这样能保证：

- 登录态处理一致
- 所有页面联调行为统一

---

## 6. token 持久化方案

建议采用：

- `Pinia + localStorage`

设计原则：

1. `Pinia` 负责运行时响应式状态
2. `localStorage` 负责页面刷新后的持久化恢复

建议文件：

- `src/utils/auth.js`

推荐实现：

```js
const TOKEN_KEY = 'mindcare_token'
const USER_KEY = 'mindcare_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUserInfo() {
  const value = localStorage.getItem(USER_KEY)
  return value ? JSON.parse(value) : null
}

export function setUserInfo(userInfo) {
  localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
}

export function clearUserInfo() {
  localStorage.removeItem(USER_KEY)
}
```

建议 Pinia Store：

- `src/stores/auth.js`

```js
import { defineStore } from 'pinia'
import { getToken, setToken, clearToken, getUserInfo, setUserInfo, clearUserInfo } from '@/utils/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    userInfo: getUserInfo()
  }),
  actions: {
    setLoginState(data) {
      this.token = data.token
      this.userInfo = data
      setToken(data.token)
      setUserInfo(data)
    },
    clearLoginState() {
      this.token = ''
      this.userInfo = null
      clearToken()
      clearUserInfo()
    }
  }
})
```

说明：

- 登录成功后统一调用 `setLoginState`
- 退出登录或 token 失效时统一调用 `clearLoginState`

---

## 7. 路由守卫方案

建议文件：

- `src/router/index.js`

职责：

1. 维护登录页和后台主布局路由
2. 配置全局前置守卫
3. 未登录时跳转登录页
4. 已登录访问登录页时重定向到首页

推荐实现：

```js
import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue')
      },
      {
        path: 'appointments',
        name: 'AppointmentList',
        component: () => import('@/views/appointment/AppointmentListView.vue')
      },
      {
        path: 'my-appointments',
        name: 'MyAppointment',
        component: () => import('@/views/appointment/MyAppointmentView.vue')
      },
      {
        path: 'reports',
        name: 'Report',
        component: () => import('@/views/report/ReportView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getToken()

  // 未登录只能访问登录页
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }

  // 已登录再次访问登录页，直接跳到首页
  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }

  next()
})

export default router
```

说明：

- 当前项目先用简单守卫即可
- 不急着做复杂动态权限菜单
- 后续如果需要按角色限制页面访问，可以在 `meta.roles` 上扩展

---

## 8. API 模块划分建议

前端接口建议按后端模块一一对应拆分，保持命名清晰：

### 8.1 `src/api/auth.js`

负责：

- 登录
- 获取当前登录用户信息

建议方法：

```js
import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

export function getLoginUserInfo() {
  return request({
    url: '/login/info',
    method: 'get'
  })
}
```

### 8.2 `src/api/appointment.js`

负责：

- 新增预约
- 分页查询预约
- 查询预约详情
- 取消预约
- 修改预约状态

建议方法：

```js
import request from '@/utils/request'

export function getAppointmentPage(params) {
  return request({
    url: '/appointments',
    method: 'get',
    params
  })
}

export function addAppointment(data) {
  return request({
    url: '/appointments',
    method: 'post',
    data
  })
}

export function getAppointmentDetail(id) {
  return request({
    url: `/appointments/${id}`,
    method: 'get'
  })
}

export function cancelAppointment(id, data) {
  return request({
    url: `/appointments/cancel/${id}`,
    method: 'put',
    data
  })
}

export function updateAppointmentStatus(data) {
  return request({
    url: '/appointments/status',
    method: 'put',
    data
  })
}
```

### 8.3 `src/api/counselor.js`

负责：

- 咨询师列表
- 咨询师详情
- 咨询师详情及时间段列表

### 8.4 `src/api/schedule.js`

负责：

- 时间段列表
- 可预约时间段查询

### 8.5 `src/api/record.js`

负责：

- 咨询记录
- 完成预约并写入咨询记录事务接口

### 8.6 `src/api/feedback.js`

负责：

- 新增评价
- 评价列表

### 8.7 `src/api/upload.js`

负责：

- 文件上传

建议方法：

```js
import request from '@/utils/request'

export function uploadFile(file, businessType = 'common') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('businessType', businessType)

  return request({
    url: '/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
```

### 8.8 `src/api/report.js`

负责：

- 预约状态分布统计
- 咨询师工作量统计
- 每月预约数量统计
- 反馈评分统计

建议方法：

```js
import request from '@/utils/request'

export function getAppointmentStatusReport(params) {
  return request({
    url: '/reports/appointmentStatus',
    method: 'get',
    params
  })
}

export function getCounselorWorkloadReport(params) {
  return request({
    url: '/reports/counselorWorkload',
    method: 'get',
    params
  })
}

export function getMonthlyAppointmentReport(params) {
  return request({
    url: '/reports/monthlyAppointments',
    method: 'get',
    params
  })
}

export function getFeedbackScoreReport(params) {
  return request({
    url: '/reports/feedbackScore',
    method: 'get',
    params
  })
}
```

---

## 9. 页面联调建议

页面风格统一采用后台管理系统布局：

- 左侧菜单
- 顶部信息栏
- 内容区

每个业务列表页尽量统一结构：

1. 查询区
2. 表格区
3. 分页区
4. 操作按钮区

例如预约管理页：

- 查询条件：预约单号、咨询师、状态、日期范围
- 表格字段：预约单号、用户、咨询师、时间段、状态、创建时间
- 操作按钮：详情、确认、完成、取消

这样的好处：

- 各业务模块代码结构一致
- 前后端联调更顺手
- 后续封装公共组件也更方便

---

## 10. 命名规范建议

建议统一遵循以下命名：

- 页面组件：`AppointmentListView.vue`
- Store：`useAuthStore`
- 工具文件：`request.js`
- API 文件：`appointment.js`
- API 方法：`getAppointmentPage`、`addAppointment`

统一原则：

- 文件名用模块名
- 方法名用“动作 + 模块”
- 页面名用“模块 + 页面类型”

---

## 11. 联调顺序建议

建议前端按下面顺序联调：

1. 登录接口 `/login`
2. 当前登录用户 `/login/info`
3. 预约列表 `/appointments`
4. 预约新增 `/appointments`
5. 上传接口 `/upload`
6. 报表接口 `/reports/*`

原因：

- 先跑通登录和 token，后续所有接口都会顺很多
- 再跑列表页和表单页，能快速看到业务结果
- 上传和报表作为增强模块，放在后面更稳
