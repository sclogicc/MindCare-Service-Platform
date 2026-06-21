<template>
  <div class="my-appointment-page">
    <section class="hero-card page-card">
      <div class="hero-main">
        <div class="hero-eyebrow">我的预约</div>
        <h2>{{ authStore.userInfo?.name || '用户' }}，欢迎回来</h2>
        <p class="hero-description">
          在这里可以查看您的所有预约记录、跟踪预约状态，并对已完成的咨询提交反馈评价。
        </p>
        <div class="hero-meta">
          <div class="hero-meta-item">
            <span>待确认</span>
            <strong>{{ countMap[1] || 0 }}</strong>
          </div>
          <div class="hero-meta-item">
            <span>已确认</span>
            <strong>{{ countMap[2] || 0 }}</strong>
          </div>
          <div class="hero-meta-item">
            <span>已完成</span>
            <strong>{{ countMap[3] || 0 }}</strong>
          </div>
          <div class="hero-meta-item">
            <span>已取消</span>
            <strong>{{ countMap[4] || 0 }}</strong>
          </div>
        </div>
      </div>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="router.push('/appointments/create')">
          发起新预约
        </el-button>
        <el-button plain size="large" @click="router.push('/feedbacks')">
          我的评价
        </el-button>
      </div>
    </section>

    <section class="page-card filter-card">
      <div class="filter-header">
        <div>
          <div class="filter-title">筛选条件</div>
          <div class="filter-desc">按预约单号、状态和日期范围筛选您的预约记录。</div>
        </div>
      </div>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="预约单号">
          <el-input v-model="queryForm.appointmentNo" placeholder="请输入预约单号" clearable />
        </el-form-item>

        <el-form-item label="预约状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="page-card table-card">
      <div class="table-header">
        <div>
          <h3>预约记录</h3>
          <p>共 {{ total }} 条预约记录，展示您的预约状态流转与服务历程。</p>
        </div>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="appointmentNo" label="预约单号" min-width="150" />
        <el-table-column prop="counselorName" label="咨询师" width="110" />
        <el-table-column label="预约时间" min-width="180">
          <template #default="{ row }">
            <div>
              <div>{{ formatDate(row.scheduleDate) }}</div>
              <div style="color: #64748b; font-size: 12px">
                {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="APPOINTMENT_STATUS_TYPE_MAP[row.status] || 'info'">
              {{ APPOINTMENT_STATUS_MAP[row.status] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button link type="primary" @click="handleView(row.id)">详情</el-button>
              <el-button
                v-if="row.status === 1 || row.status === 2"
                link
                type="danger"
                @click="handleCancel(row)"
              >
                取消
              </el-button>
              <el-button
                v-if="row.status === 3"
                link
                type="success"
                @click="handleGoFeedback(row)"
              >
                评价
              </el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          @size-change="fetchMyAppointments"
          @current-change="fetchMyAppointments"
        />
      </div>
    </section>

    <el-drawer v-model="detailVisible" title="预约详情" size="420px">
      <el-skeleton :loading="detailLoading" animated :rows="8">
        <template #default>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="预约单号">{{ detailData.appointmentNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="咨询师">{{ detailData.counselorName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="预约日期">{{ formatDate(detailData.scheduleDate) }}</el-descriptions-item>
            <el-descriptions-item label="预约时间段">
              {{ formatTime(detailData.startTime) }} - {{ formatTime(detailData.endTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="APPOINTMENT_STATUS_TYPE_MAP[detailData.status] || 'info'">
                {{ APPOINTMENT_STATUS_MAP[detailData.status] || '-' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detailData.contactPhone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="预约备注">{{ detailData.remark || '-' }}</el-descriptions-item>
            <el-descriptions-item label="取消原因">{{ detailData.cancelReason || '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDateTime(detailData.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </el-skeleton>
    </el-drawer>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { cancelAppointment, getAppointmentDetail, getAppointmentPage } from '@/api/appointment'
import { useAuthStore } from '@/stores/auth'
import { APPOINTMENT_STATUS_MAP, APPOINTMENT_STATUS_TYPE_MAP } from '@/utils/constant'
import { formatDate, formatDateTime, formatTime } from '@/utils/format'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref({})
const countMap = ref({ 1: 0, 2: 0, 3: 0, 4: 0 })

const queryForm = reactive({
  page: 1,
  pageSize: 10,
  appointmentNo: '',
  status: '',
  userId: authStore.userInfo?.id || null
})

const statusOptions = [
  { label: '待确认', value: 1 },
  { label: '已确认', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

async function fetchMyAppointments() {
  loading.value = true

  try {
    queryForm.userId = authStore.userInfo?.id || null
    const data = await getAppointmentPage(queryForm)
    tableData.value = data.rows || []
    total.value = data.total || 0

    // 并行拉取各状态计数用于概览卡片
    const [p1, p2, p3, p4] = await Promise.all([
      getAppointmentPage({ page: 1, pageSize: 1, status: 1, userId: queryForm.userId }),
      getAppointmentPage({ page: 1, pageSize: 1, status: 2, userId: queryForm.userId }),
      getAppointmentPage({ page: 1, pageSize: 1, status: 3, userId: queryForm.userId }),
      getAppointmentPage({ page: 1, pageSize: 1, status: 4, userId: queryForm.userId })
    ])
    countMap.value = {
      1: p1.total || 0,
      2: p2.total || 0,
      3: p3.total || 0,
      4: p4.total || 0
    }
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchMyAppointments()
}

function handleReset() {
  queryForm.page = 1
  queryForm.pageSize = 10
  queryForm.appointmentNo = ''
  queryForm.status = ''
  fetchMyAppointments()
}

async function handleView(id) {
  detailVisible.value = true
  detailLoading.value = true

  try {
    detailData.value = await getAppointmentDetail(id)
  } finally {
    detailLoading.value = false
  }
}

async function handleCancel(row) {
  const result = await ElMessageBox.prompt('请输入取消原因', '取消预约', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：临时有事，需要取消'
  })

  await cancelAppointment(row.id, {
    cancelReason: result.value
  })

  ElMessage.success('预约已取消')
  fetchMyAppointments()
}

function handleGoFeedback(row) {
  router.push({
    path: '/feedbacks',
    query: { appointmentId: row.id }
  })
}

fetchMyAppointments()
</script>

<style scoped>
.my-appointment-page {
  display: grid;
  gap: 18px;
}

.hero-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  padding: 28px;
  background:
    radial-gradient(circle at 100% 0%, rgba(55, 135, 235, 0.14), transparent 26%),
    radial-gradient(circle at 0% 100%, rgba(29, 155, 132, 0.1), transparent 22%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(246, 251, 255, 0.94) 100%);
}

.hero-main h2 {
  margin: 12px 0 8px;
  color: #10263a;
  font-size: 28px;
}

.hero-eyebrow {
  display: inline-flex;
  padding: 6px 14px;
  border-radius: 999px;
  background: #eaf4ff;
  color: #1453a3;
  font-size: 12px;
  font-weight: 700;
}

.hero-description {
  max-width: 560px;
  margin: 0;
  color: #5c7289;
  line-height: 1.8;
}

.hero-meta {
  display: flex;
  gap: 14px;
  margin-top: 20px;
}

.hero-meta-item {
  padding: 14px 20px;
  border: 1px solid rgba(138, 170, 202, 0.2);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.76);
  text-align: center;
}

.hero-meta-item span {
  display: block;
  color: #71859b;
  font-size: 12px;
  font-weight: 700;
}

.hero-meta-item strong {
  display: block;
  margin-top: 8px;
  color: #10263a;
  font-size: 22px;
}

.hero-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-shrink: 0;
}
</style>
