<template>
  <div class="layout-shell">
    <aside class="layout-sidebar">
      <div class="sidebar-surface">
        <div class="sidebar-glow sidebar-glow--top"></div>
        <div class="sidebar-glow sidebar-glow--bottom"></div>

        <div class="brand-block">
          <div class="brand-mark">
            <img src="/brand-mark.svg" alt="平台标识" class="brand-mark-image" />
          </div>
          <div class="brand-content">
            <div class="brand-title">心理咨询平台</div>
            <div class="brand-subtitle">服务预约与管理后台</div>
          </div>
        </div>

        <div class="nav-caption">导航菜单</div>

        <el-menu
          :default-active="route.path"
          class="side-menu"
          background-color="transparent"
          text-color="#dbe8f3"
          active-text-color="#ffffff"
          router
        >
          <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
            <div class="menu-item-badge">{{ item.short }}</div>
            <div class="menu-item-content">
              <span class="menu-item-title">{{ item.title }}</span>
              <span class="menu-item-desc">{{ item.desc }}</span>
            </div>
          </el-menu-item>
        </el-menu>
      </div>
    </aside>

    <div class="layout-main">
      <header class="layout-header page-card">
        <div>
          <div class="header-kicker">心理咨询服务预约平台</div>
          <div class="header-title">{{ route.meta.title || '后台管理' }}</div>
        </div>

        <div class="header-user">
          <div class="header-user-card">
            <div class="header-user-avatar">{{ userInitial }}</div>
            <div class="header-user-meta">
              <span class="user-name">{{ authStore.userInfo?.name || '未登录' }}</span>
              <span class="user-role">{{ roleText }}</span>
            </div>
            <div class="header-user-status">在线</div>
          </div>
          <el-button type="primary" plain @click="handleLogout">退出登录</el-button>
        </div>
      </header>

      <main class="layout-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { useAuthStore } from '@/stores/auth'
import { ROLE_MAP } from '@/utils/constant'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const menuItems = [
  { path: '/dashboard', title: '首页', desc: '业务总览与提醒', short: '01' },
  { path: '/counselors', title: '咨询师管理', desc: '维护咨询师资料', short: '02' },
  { path: '/appointments', title: '预约管理', desc: '处理预约流转', short: '03' },
  { path: '/appointments/create', title: '新增预约', desc: '发起新的预约申请', short: '04' },
  { path: '/schedules', title: '时间段管理', desc: '配置可预约服务资源', short: '05' },
  { path: '/consultation-records', title: '咨询记录管理', desc: '维护咨询服务记录', short: '06' },
  { path: '/feedbacks', title: '反馈评价管理', desc: '查看用户服务反馈', short: '07' },
  { path: '/reports', title: '统计报表', desc: '分析平台业务数据', short: '08' }
]

const roleText = computed(() => ROLE_MAP[authStore.userInfo?.role] || '未分配角色')

const userInitial = computed(() => {
  const name = authStore.userInfo?.name || '用'
  return name.slice(0, 1).toUpperCase()
})

function handleLogout() {
  authStore.clearLoginState()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.layout-shell {
  display: flex;
  min-height: 100vh;
}

.layout-sidebar {
  width: 280px;
  padding: 16px 14px;
}

.sidebar-surface {
  position: sticky;
  top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: calc(100vh - 32px);
  padding: 18px 14px 16px;
  overflow: hidden;
  border: 1px solid rgba(130, 171, 207, 0.14);
  border-radius: 30px;
  background:
    linear-gradient(180deg, rgba(13, 30, 48, 0.96) 0%, rgba(11, 25, 40, 0.96) 100%);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.05),
    0 20px 36px rgba(10, 22, 35, 0.18);
}

.sidebar-glow {
  position: absolute;
  border-radius: 999px;
  filter: blur(0);
  pointer-events: none;
}

.sidebar-glow--top {
  top: -60px;
  right: -30px;
  width: 180px;
  height: 180px;
  background: radial-gradient(circle, rgba(74, 144, 255, 0.3) 0%, transparent 72%);
}

.sidebar-glow--bottom {
  bottom: -80px;
  left: -40px;
  width: 180px;
  height: 180px;
  background: radial-gradient(circle, rgba(36, 179, 145, 0.18) 0%, transparent 76%);
}

.brand-block,
.nav-caption,
.side-menu {
  position: relative;
  z-index: 1;
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 2px;
  color: #f8fbff;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 48px;
  height: 48px;
  flex-shrink: 0;
}

.brand-mark-image {
  display: block;
  width: 48px;
  height: 48px;
  border-radius: 16px;
  box-shadow: 0 14px 28px rgba(28, 102, 199, 0.3);
}

.brand-content {
  min-width: 0;
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
}

.brand-subtitle {
  margin-top: 3px;
  color: #8cb3d3;
  font-size: 11px;
  letter-spacing: 0.03em;
}

.nav-caption {
  padding-left: 4px;
  color: #6f92b0;
  font-size: 11px;
  letter-spacing: 0.1em;
}

.side-menu {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 7px;
  min-height: 0;
  padding: 0;
  border-right: none;
}

.menu-item-badge {
  position: relative;
  z-index: 1;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  width: 30px;
  height: 30px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.04);
  color: #86abc8;
  font-size: 11px;
  font-weight: 700;
}

.menu-item-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  min-width: 0;
  margin-left: 10px;
}

.menu-item-title {
  font-size: 13px;
  font-weight: 700;
  line-height: 1.2;
}

.menu-item-desc {
  margin-top: 4px;
  color: #88aecd;
  font-size: 11px;
  line-height: 1.18;
}

.layout-main {
  flex: 1;
  min-width: 0;
  padding: 20px 22px 24px;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  border-color: rgba(140, 165, 190, 0.18);
}

.header-kicker {
  color: #7790a8;
  font-size: 12px;
  letter-spacing: 0.06em;
}

.header-title {
  margin-top: 7px;
  color: #10263a;
  font-size: 26px;
  font-weight: 800;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border: 1px solid rgba(132, 159, 188, 0.18);
  border-radius: 18px;
  background: linear-gradient(180deg, #f9fcff 0%, #eef5fb 100%);
}

.header-user-avatar {
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  border-radius: 14px;
  background: linear-gradient(135deg, #58abff 0%, #1f6ed4 100%);
  color: #ffffff;
  font-size: 14px;
  font-weight: 800;
}

.header-user-meta {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 700;
}

.user-role {
  margin-top: 3px;
  color: #64748b;
  font-size: 12px;
}

.header-user-status {
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(31, 155, 122, 0.1);
  color: #1a8a6c;
  font-size: 11px;
  font-weight: 700;
}

.layout-content {
  margin-top: 18px;
}

:deep(.side-menu .el-menu-item) {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 58px;
  margin: 0;
  padding: 10px 12px !important;
  overflow: hidden;
  border-radius: 18px;
  line-height: 1.2;
  white-space: normal;
}

:deep(.side-menu .el-menu-item::before) {
  content: "";
  position: absolute;
  inset: 0;
  border: 1px solid transparent;
  border-radius: 18px;
  background: transparent;
  transition: background 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

:deep(.side-menu .el-menu-item:hover::before) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.04);
}

:deep(.side-menu .el-menu-item.is-active::before) {
  border-color: rgba(255, 255, 255, 0.08);
  background: linear-gradient(135deg, rgba(62, 142, 248, 0.92) 0%, rgba(24, 94, 191, 0.96) 100%);
  box-shadow: 0 16px 26px rgba(18, 86, 179, 0.24);
}

:deep(.side-menu .el-menu-item.is-active .menu-item-badge) {
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
}

:deep(.side-menu .el-menu-item.is-active .menu-item-desc) {
  color: rgba(235, 245, 255, 0.88);
}

@media (max-width: 1360px) {
  .layout-sidebar {
    width: 258px;
  }

  .menu-item-desc,
  .brand-subtitle {
    display: none;
  }

  :deep(.side-menu .el-menu-item) {
    min-height: 52px;
  }
}
</style>
