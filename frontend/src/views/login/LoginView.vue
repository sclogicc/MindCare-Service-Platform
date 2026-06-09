<template>
  <div class="login-page">
    <section class="login-stage">
      <div class="login-copy">
        <div class="eyebrow">心理咨询服务预约平台</div>
        <h1>心理咨询服务预约平台</h1>
        <p>
          围绕登录认证、咨询预约、咨询记录、反馈评价和数据统计构建完整业务闭环，支持平台日常管理与业务协同。
        </p>

        <div class="feature-grid">
          <article class="feature-card">
            <span>预约闭环</span>
            <strong>预约到记录</strong>
            <em>覆盖待确认、已确认、已完成、已取消四种流转状态。</em>
          </article>
          <article class="feature-card">
            <span>系统架构</span>
            <strong>结构清晰</strong>
            <em>采用 Spring Boot、MyBatis 与 Vue 3，便于维护和持续扩展。</em>
          </article>
          <article class="feature-card">
            <span>业务联动</span>
            <strong>真实联调</strong>
            <em>首页、预约、记录、反馈、报表等页面均已接入真实接口。</em>
          </article>
        </div>
      </div>

      <section class="login-panel page-card">
        <div class="panel-top">
          <div>
            <div class="panel-kicker">后台登录</div>
            <h2>登录系统</h2>
          </div>
          <div class="panel-status">安全访问</div>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleLogin">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>

          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            登录系统
          </el-button>

          <div class="login-tip">
            登录后可直接查看首页仪表盘、预约管理、咨询记录和统计报表。
          </div>
        </el-form>
      </section>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { getLoginUserInfo, login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import { setToken } from '@/utils/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true

  try {
    const loginData = await login(form)
    const userInfo = await getLoginUserInfoByTokenFallback(loginData)
    authStore.setLoginState(userInfo)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}

// 登录接口已经返回 token 和基础信息，这里继续查询一次登录用户信息，
// 便于统一走“当前登录用户”接口，减少前端状态来源分散的问题。
async function getLoginUserInfoByTokenFallback(loginData) {
  try {
    // 登录接口已返回 JWT，这里先临时写入本地，
    // 让后续 /login/info 请求能够通过请求拦截器自动携带 token。
    setToken(loginData.token)

    const userInfo = await getLoginUserInfo()
    return {
      ...userInfo,
      token: loginData.token
    }
  } catch (error) {
    return loginData
  }
}
</script>

<style scoped>
.login-page {
  display: grid;
  min-height: 100vh;
  padding: 28px;
  background: transparent;
}

.login-stage {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(380px, 0.85fr);
  gap: 34px;
  align-items: stretch;
  width: min(1200px, 100%);
  margin: auto;
}

.login-copy {
  display: grid;
  align-content: center;
  gap: 22px;
  padding: 12px 10px;
}

.eyebrow {
  display: inline-flex;
  justify-self: start;
  padding: 7px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.76);
  color: #1453a3;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.login-copy h1 {
  max-width: 620px;
  margin: 0;
  color: #10263a;
  font-size: 48px;
  line-height: 1.08;
  letter-spacing: -0.03em;
}

.login-copy p {
  max-width: 640px;
  margin: 0;
  color: #5d7389;
  font-size: 16px;
  line-height: 1.95;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.feature-card {
  padding: 18px;
  border: 1px solid rgba(132, 160, 186, 0.16);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.84) 0%, rgba(247, 251, 255, 0.76) 100%);
  box-shadow: 0 18px 34px rgba(18, 38, 63, 0.06);
}

.feature-card span {
  display: block;
  color: #6e879f;
  font-size: 12px;
  font-weight: 700;
}

.feature-card strong {
  display: block;
  margin-top: 12px;
  color: #10263a;
  font-size: 20px;
}

.feature-card em {
  display: block;
  margin-top: 10px;
  color: #61788f;
  font-style: normal;
  line-height: 1.75;
}

.login-panel {
  align-self: center;
  padding: 32px 30px;
}

.panel-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 22px;
}

.panel-kicker {
  color: #7b8ea5;
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.panel-top h2 {
  margin: 8px 0 0;
  color: #10263a;
  font-size: 30px;
}

.panel-status {
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(28, 155, 132, 0.1);
  color: #1a8b6d;
  font-size: 12px;
  font-weight: 700;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
}

.login-tip {
  margin-top: 16px;
  color: #697f95;
  font-size: 13px;
  line-height: 1.75;
}

:deep(.login-panel .el-input__wrapper) {
  background: rgba(248, 251, 255, 0.92);
  box-shadow: 0 0 0 1px rgba(163, 183, 204, 0.22) inset !important;
}

:deep(.login-panel .el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 0 0 1px rgba(41, 111, 206, 0.42) inset !important;
}

@media (max-width: 1280px) {
  .login-stage {
    grid-template-columns: 1fr;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .login-copy h1 {
    font-size: 40px;
  }
}
</style>
