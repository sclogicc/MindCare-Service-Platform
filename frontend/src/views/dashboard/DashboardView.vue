<template>
  <div class="dashboard-page">
    <section class="hero-card page-card">
      <div class="hero-main">
        <div class="hero-eyebrow">首页总览</div>
        <h2>{{ greetingText }}，{{ authStore.userInfo?.name || '用户' }}</h2>
        <p class="hero-description">
          当前以“{{ roleText }}”身份查看{{ scopeText }}业务数据。首页聚合预约、咨询师与统计信息，便于快速掌握平台运行情况。
        </p>

        <div class="hero-meta-grid">
          <article v-for="item in heroMetaCards" :key="item.label" class="hero-meta-card">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </article>
        </div>
      </div>

      <div class="hero-actions">
        <div class="hero-actions-top">
          <el-button type="primary" @click="router.push('/appointments')">查看预约列表</el-button>
          <el-button plain @click="router.push('/appointments/create')">发起新预约</el-button>
          <el-button plain @click="router.push('/reports')">查看统计报表</el-button>
        </div>

        <div class="hero-note">
          <div class="hero-note-kicker">当前重点</div>
          <div class="hero-note-title">预约闭环与数据联动</div>
          <div class="hero-note-text">
            重点展示预约状态流转、时间段占用校验、咨询记录事务提交和统计报表联动效果。
          </div>
        </div>
      </div>
    </section>

    <section class="stats-grid">
      <article
        v-for="card in statCards"
        :key="card.key"
        class="page-card stat-card"
        :class="`stat-card--${card.tone}`"
      >
        <div class="stat-top">
          <div>
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-side">{{ card.sideText }}</div>
          </div>
          <div class="stat-mark" :class="`stat-mark--${card.tone}`">{{ card.mark }}</div>
        </div>
        <div class="stat-value">{{ card.value }}</div>
        <div class="stat-desc">{{ card.desc }}</div>
        <div class="stat-foot">{{ card.footText }}</div>
      </article>
    </section>

    <section class="content-grid">
      <section class="page-card dashboard-panel">
        <div class="panel-header">
          <div>
            <h3>最近预约</h3>
            <p>展示当前视角下最近创建的预约记录，便于快速进入业务处理。</p>
          </div>
          <el-button link type="primary" @click="router.push('/appointments')">查看全部</el-button>
        </div>

        <div class="panel-caption-row">
          <span class="panel-caption">当前范围：{{ scopeText }}数据</span>
          <span class="panel-caption">待确认 {{ pendingCount }} 单</span>
          <span class="panel-caption">已完成 {{ completedCount }} 单</span>
        </div>

        <el-table v-loading="loading" :data="recentAppointments" stripe>
          <el-table-column prop="appointmentNo" label="预约单号" min-width="150" />
          <el-table-column prop="userName" label="用户" min-width="100" />
          <el-table-column prop="counselorName" label="咨询师" min-width="120" />
          <el-table-column label="时间段" min-width="170">
            <template #default="{ row }">
              {{ formatDate(row.scheduleDate) }} {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="110">
            <template #default="{ row }">
              <el-tag :type="APPOINTMENT_STATUS_TYPE_MAP[row.status] || 'info'">
                {{ APPOINTMENT_STATUS_MAP[row.status] || '-' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="165">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </section>

      <section class="dashboard-side-column">
        <section class="page-card dashboard-panel side-panel">
          <div class="panel-header">
            <div>
              <h3>业务提示</h3>
              <p>基于当前统计结果，提炼需要优先关注的业务信息。</p>
            </div>
          </div>

          <div class="insight-list">
            <article v-for="item in insightCards" :key="item.title" class="insight-card">
              <div class="insight-head">
                <span class="insight-title">{{ item.title }}</span>
                <span class="insight-pulse"></span>
              </div>
              <div class="insight-main">{{ item.value }}</div>
              <div class="insight-sub">{{ item.desc }}</div>
            </article>
          </div>
        </section>

        <section class="page-card dashboard-panel trend-panel">
          <div class="panel-header compact-header">
            <div>
              <h3>状态分布</h3>
              <p>直接反映预约流转是否顺畅。</p>
            </div>
          </div>

          <div class="status-progress-list">
            <div v-for="item in statusProgressList" :key="item.label" class="status-progress-item">
              <div class="status-progress-top">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }} 单</strong>
              </div>
              <div class="status-progress-bar">
                <div class="status-progress-fill" :class="`status-progress-fill--${item.tone}`" :style="{ width: item.width }" />
              </div>
              <div class="status-progress-hint">{{ item.hint }}</div>
            </div>
          </div>
        </section>

        <section class="page-card dashboard-panel trend-panel">
          <div class="panel-header compact-header">
            <div>
              <h3>月度预约趋势</h3>
              <p>截取最近 6 个月数据，适合首页快速预览。</p>
            </div>
          </div>

          <div class="trend-list">
            <div v-for="item in monthlyPreview" :key="item.monthLabel" class="trend-item">
              <div class="trend-row">
                <span>{{ item.monthLabel }}</span>
                <strong>{{ item.value }}</strong>
              </div>
              <div class="trend-bar">
                <div class="trend-bar-fill" :style="{ width: item.width }" />
              </div>
            </div>
          </div>
        </section>
      </section>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getAppointmentPage } from '@/api/appointment'
import { getCounselorOptions } from '@/api/counselor'
import { getCounselorWorkloadReport, getMonthlyAppointmentReport } from '@/api/report'
import { useAuthStore } from '@/stores/auth'
import { APPOINTMENT_STATUS_MAP, APPOINTMENT_STATUS_TYPE_MAP, ROLE_MAP } from '@/utils/constant'
import { formatDate, formatDateTime, formatTime } from '@/utils/format'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const recentAppointments = ref([])
const workloadRanking = ref([])
const monthlyAppointments = ref([])
const counselorOptions = ref([])
const counselorId = ref(null)
const countMap = ref({
  1: 0,
  2: 0,
  3: 0,
  4: 0
})

const roleText = computed(() => ROLE_MAP[authStore.userInfo?.role] || '-')
const pendingCount = computed(() => countMap.value[1] || 0)
const confirmedCount = computed(() => countMap.value[2] || 0)
const completedCount = computed(() => countMap.value[3] || 0)
const canceledCount = computed(() => countMap.value[4] || 0)
const totalCount = computed(() => pendingCount.value + confirmedCount.value + completedCount.value + canceledCount.value)
const yearlyAppointmentTotal = computed(() => monthlyAppointments.value.reduce((sum, item) => sum + (item.value || 0), 0))
const topCounselorName = computed(() => workloadRanking.value[0]?.name || '暂无数据')
const topCounselorValue = computed(() => workloadRanking.value[0]?.value || 0)

const scopeText = computed(() => {
  if (authStore.userInfo?.role === 1) {
    return '全局'
  }

  return '我的'
})

const greetingText = computed(() => {
  const hour = new Date().getHours()

  if (hour < 12) {
    return '上午好'
  }

  if (hour < 18) {
    return '下午好'
  }

  return '晚上好'
})

const heroMetaCards = computed(() => [
  { label: '当前角色', value: roleText.value },
  { label: '数据范围', value: `${scopeText.value}视角` },
  { label: '年度预约', value: `${yearlyAppointmentTotal.value} 单` },
  { label: '系统状态', value: loading.value ? '同步中' : '已就绪' }
])

const statCards = computed(() => [
  {
    key: 'total',
    tone: 'slate',
    label: scopeText.value === '全局' ? '预约总量' : '我的预约总量',
    value: totalCount.value,
    desc: '来自当前范围内的预约统计结果。',
    sideText: `${yearlyAppointmentTotal.value} 单 / 年`,
    footText: `其中已取消 ${canceledCount.value} 单`,
    mark: 'ALL'
  },
  {
    key: 'pending',
    tone: 'amber',
    label: '待确认预约',
    value: pendingCount.value,
    desc: '最需要优先处理的预约状态。',
    sideText: buildRatioText(pendingCount.value),
    footText: '建议优先确认，避免用户等待过久',
    mark: 'P'
  },
  {
    key: 'confirmed',
    tone: 'blue',
    label: '已确认预约',
    value: confirmedCount.value,
    desc: '已锁定时间段，等待正式服务。',
    sideText: buildRatioText(confirmedCount.value),
    footText: '适合继续推进咨询记录填写流程',
    mark: 'OK'
  },
  {
    key: 'completed',
    tone: 'green',
    label: '已完成预约',
    value: completedCount.value,
    desc: '代表已形成完整服务闭环的数据。',
    sideText: buildRatioText(completedCount.value),
    footText: '可继续关联反馈评价和统计分析',
    mark: 'END'
  }
])

const insightCards = computed(() => [
  {
    title: '待确认积压',
    value: `${pendingCount.value} 单`,
    desc: pendingCount.value > 0 ? '建议管理员或咨询师优先处理这些预约。' : '当前没有待确认预约，流转状态比较顺畅。'
  },
  {
    title: '年度预约规模',
    value: `${yearlyAppointmentTotal.value} 单`,
    desc: '该数值来自月度预约统计接口汇总，可用于观察年度业务规模变化。'
  },
  {
    title: '工作量最高咨询师',
    value: topCounselorName.value,
    desc: `${topCounselorValue.value} 单预约，可用于衡量咨询师近期服务承接情况。`
  }
])

const statusProgressList = computed(() => [
  {
    label: '待确认',
    value: pendingCount.value,
    width: buildPercentWidth(pendingCount.value),
    tone: 'amber',
    hint: '等待咨询师或管理员确认'
  },
  {
    label: '已确认',
    value: confirmedCount.value,
    width: buildPercentWidth(confirmedCount.value),
    tone: 'blue',
    hint: '时间段已被占用，等待咨询完成'
  },
  {
    label: '已完成',
    value: completedCount.value,
    width: buildPercentWidth(completedCount.value),
    tone: 'green',
    hint: '可继续查看记录与反馈信息'
  },
  {
    label: '已取消',
    value: canceledCount.value,
    width: buildPercentWidth(canceledCount.value),
    tone: 'slate',
    hint: '取消后时间段会恢复可预约'
  }
])

const monthlyPreview = computed(() => {
  const previewList = monthlyAppointments.value.slice(-6)
  const maxValue = Math.max(...previewList.map((item) => item.value || 0), 0)

  return previewList.map((item) => ({
    ...item,
    width: maxValue > 0 ? `${Math.max(((item.value || 0) / maxValue) * 100, 10)}%` : '10%'
  }))
})

function buildRatioText(value) {
  if (!totalCount.value) {
    return '占比 0%'
  }

  return `占比 ${Math.round((value / totalCount.value) * 100)}%`
}

function buildPercentWidth(value) {
  if (!totalCount.value) {
    return '0%'
  }

  return `${Math.max((value / totalCount.value) * 100, value > 0 ? 8 : 0)}%`
}

function buildScopeQuery() {
  const query = {}

  if (authStore.userInfo?.role === 3) {
    query.userId = authStore.userInfo?.id
  }

  if (authStore.userInfo?.role === 2 && counselorId.value) {
    query.counselorId = counselorId.value
  }

  return query
}

async function resolveCounselorId() {
  if (authStore.userInfo?.role !== 2) {
    return
  }

  // 首页继续复用现有咨询师下拉接口，将登录账号映射为咨询师编号，避免额外新增接口。
  counselorOptions.value = await getCounselorOptions()
  const currentCounselor = counselorOptions.value.find((item) => item.userId === authStore.userInfo?.id)
  counselorId.value = currentCounselor?.id || null
}

async function fetchDashboardData() {
  loading.value = true

  try {
    await resolveCounselorId()
    const scopeQuery = buildScopeQuery()
    const currentYear = new Date().getFullYear()

    // 首页通过现有分页接口和报表接口组合数据，既能保证展示效果，也能保持后端结构简单清晰。
    const [
      pendingPage,
      confirmedPage,
      completedPage,
      canceledPage,
      recentPage,
      workloadData,
      monthlyData
    ] = await Promise.all([
      getAppointmentPage({ page: 1, pageSize: 1, status: 1, ...scopeQuery }),
      getAppointmentPage({ page: 1, pageSize: 1, status: 2, ...scopeQuery }),
      getAppointmentPage({ page: 1, pageSize: 1, status: 3, ...scopeQuery }),
      getAppointmentPage({ page: 1, pageSize: 1, status: 4, ...scopeQuery }),
      getAppointmentPage({ page: 1, pageSize: 5, ...scopeQuery }),
      getCounselorWorkloadReport({}),
      getMonthlyAppointmentReport({ year: currentYear })
    ])

    countMap.value = {
      1: pendingPage.total || 0,
      2: confirmedPage.total || 0,
      3: completedPage.total || 0,
      4: canceledPage.total || 0
    }
    recentAppointments.value = recentPage.rows || []
    workloadRanking.value = workloadData || []
    monthlyAppointments.value = monthlyData || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.dashboard-page {
  display: grid;
  gap: 20px;
}

.hero-card {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(320px, 0.85fr);
  gap: 22px;
  padding: 28px;
  overflow: hidden;
  background:
    radial-gradient(circle at 100% 0%, rgba(55, 135, 235, 0.18), transparent 26%),
    radial-gradient(circle at 0% 100%, rgba(29, 155, 132, 0.12), transparent 22%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(246, 251, 255, 0.94) 100%);
}

.hero-main h2 {
  margin: 16px 0 10px;
  color: #10263a;
  font-size: 34px;
  line-height: 1.08;
}

.hero-eyebrow {
  display: inline-flex;
  padding: 7px 14px;
  border-radius: 999px;
  background: #eaf4ff;
  color: #1453a3;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.hero-description {
  max-width: 760px;
  margin: 0;
  color: #5c7289;
  line-height: 1.9;
}

.hero-meta-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.hero-meta-card {
  padding: 15px 16px;
  border: 1px solid rgba(138, 170, 202, 0.2);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 12px 24px rgba(16, 38, 58, 0.05);
}

.hero-meta-card span {
  display: block;
  color: #71859b;
  font-size: 12px;
  font-weight: 700;
}

.hero-meta-card strong {
  display: block;
  margin-top: 10px;
  color: #10263a;
  font-size: 18px;
}

.hero-actions {
  display: grid;
  align-content: start;
  gap: 16px;
  padding-top: 6px;
}

.hero-actions-top {
  display: grid;
  gap: 12px;
}

.hero-note {
  padding: 18px;
  border: 1px solid rgba(132, 164, 198, 0.18);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(248, 251, 255, 0.96) 0%, rgba(236, 245, 255, 0.94) 100%);
}

.hero-note-kicker {
  color: #1a67c3;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.hero-note-title {
  margin-top: 10px;
  color: #10263a;
  font-size: 20px;
  font-weight: 800;
}

.hero-note-text {
  margin-top: 10px;
  color: #5b7087;
  line-height: 1.8;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.stat-card {
  position: relative;
  overflow: hidden;
  padding: 22px;
}

.stat-card::before {
  content: "";
  position: absolute;
  inset: 0 auto auto 0;
  width: 100%;
  height: 4px;
}

.stat-card--slate::before {
  background: linear-gradient(90deg, #334155 0%, #64748b 100%);
}

.stat-card--amber::before {
  background: linear-gradient(90deg, #d97706 0%, #f59e0b 100%);
}

.stat-card--blue::before {
  background: linear-gradient(90deg, #2563eb 0%, #60a5fa 100%);
}

.stat-card--green::before {
  background: linear-gradient(90deg, #0f9f6e 0%, #35c690 100%);
}

.stat-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.stat-label {
  color: #4f647b;
  font-size: 13px;
  font-weight: 700;
}

.stat-side {
  margin-top: 6px;
  color: #7890a7;
  font-size: 12px;
}

.stat-mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.08em;
}

.stat-mark--slate {
  background: rgba(71, 85, 105, 0.1);
  color: #475569;
}

.stat-mark--amber {
  background: rgba(245, 158, 11, 0.12);
  color: #b97708;
}

.stat-mark--blue {
  background: rgba(37, 99, 235, 0.1);
  color: #2563eb;
}

.stat-mark--green {
  background: rgba(16, 185, 129, 0.1);
  color: #0f9f6e;
}

.stat-value {
  margin-top: 18px;
  color: #10263a;
  font-size: 34px;
  font-weight: 800;
}

.stat-desc {
  margin-top: 10px;
  color: #5b7087;
  line-height: 1.75;
}

.stat-foot {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px dashed rgba(132, 157, 183, 0.26);
  color: #7b8ea5;
  font-size: 12px;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(340px, 0.95fr);
  gap: 18px;
  align-items: start;
}

.dashboard-panel {
  padding: 24px;
}

.dashboard-side-column {
  display: grid;
  gap: 18px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.panel-header h3 {
  margin: 0 0 8px;
  color: #10263a;
  font-size: 20px;
  font-weight: 800;
}

.panel-header p {
  margin: 0;
  color: #64748b;
  line-height: 1.75;
}

.compact-header {
  margin-bottom: 16px;
}

.panel-caption-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
}

.panel-caption {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid rgba(132, 170, 210, 0.2);
  border-radius: 999px;
  background: linear-gradient(180deg, #f9fcff 0%, #eef6ff 100%);
  color: #52687e;
  font-size: 12px;
  font-weight: 700;
}

.insight-list,
.status-progress-list,
.trend-list {
  display: grid;
  gap: 14px;
}

.insight-card {
  padding: 18px;
  border: 1px solid rgba(137, 164, 192, 0.18);
  border-radius: 18px;
  background: linear-gradient(180deg, #fbfdff 0%, #f5fbff 100%);
}

.insight-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.insight-title {
  color: #64748b;
  font-size: 13px;
  font-weight: 700;
}

.insight-pulse {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: linear-gradient(180deg, #2c7be5 0%, #1d9b84 100%);
  box-shadow: 0 0 0 6px rgba(44, 123, 229, 0.08);
}

.insight-main {
  margin-top: 12px;
  color: #10263a;
  font-size: 24px;
  font-weight: 800;
}

.insight-sub {
  margin-top: 8px;
  color: #64748b;
  line-height: 1.75;
}

.status-progress-item {
  display: grid;
  gap: 8px;
}

.status-progress-top,
.trend-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.status-progress-top span,
.trend-row span {
  color: #52687e;
  font-size: 13px;
  font-weight: 700;
}

.status-progress-top strong,
.trend-row strong {
  color: #10263a;
  font-size: 14px;
}

.status-progress-bar,
.trend-bar {
  height: 10px;
  border-radius: 999px;
  background: #edf3f8;
  overflow: hidden;
}

.status-progress-fill,
.trend-bar-fill {
  height: 100%;
  border-radius: 999px;
}

.status-progress-fill--amber {
  background: linear-gradient(90deg, #f59e0b 0%, #fbbf24 100%);
}

.status-progress-fill--blue {
  background: linear-gradient(90deg, #3b82f6 0%, #60a5fa 100%);
}

.status-progress-fill--green {
  background: linear-gradient(90deg, #10b981 0%, #34d399 100%);
}

.status-progress-fill--slate {
  background: linear-gradient(90deg, #64748b 0%, #94a3b8 100%);
}

.status-progress-hint {
  color: #7b8ea5;
  font-size: 12px;
}

.trend-item {
  padding: 14px 16px;
  border: 1px solid rgba(138, 164, 190, 0.18);
  border-radius: 16px;
  background: linear-gradient(180deg, #fbfdff 0%, #f7fbff 100%);
}

.trend-bar {
  margin-top: 10px;
}

.trend-bar-fill {
  background: linear-gradient(90deg, #2c7be5 0%, #72b4ff 100%);
}

@media (max-width: 1480px) {
  .hero-card,
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1200px) {
  .hero-meta-grid,
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
