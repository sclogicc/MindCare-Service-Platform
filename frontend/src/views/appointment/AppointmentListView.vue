<template>
  <div class="appointment-page">
    <section class="page-card filter-card">
      <div class="filter-header">
        <div>
          <div class="filter-title">筛选条件</div>
          <div class="filter-desc">按预约单号、状态和日期范围筛选预约记录。</div>
        </div>
      </div>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="预约单号">
          <el-input v-model="queryForm.appointmentNo" placeholder="请输入预约单号" clearable />
        </el-form-item>

        <el-form-item label="预约状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="日期范围">
          <el-date-picker
            v-model="queryDateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            range-separator="至"
            value-format="YYYY-MM-DD"
          />
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
          <h3>预约管理</h3>
          <p>支持预约分页查询、详情查看、状态确认、取消预约和咨询记录流转入口。</p>
        </div>
        <div class="table-header-actions">
          <el-button type="primary" @click="router.push('/appointments/create')">新增预约</el-button>
        </div>
      </div>

      <div class="table-summary">
        <span class="table-summary-item">当前共 {{ total }} 条记录</span>
        <span class="table-summary-item">状态筛选：{{ currentStatusText }}</span>
        <span class="table-summary-item">日期范围：{{ currentDateRangeText }}</span>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe class="standard-data-table">
        <el-table-column prop="appointmentNo" label="预约单号" min-width="136" show-overflow-tooltip />
        <el-table-column prop="userName" label="用户" width="92" show-overflow-tooltip />
        <el-table-column prop="counselorName" label="咨询师" width="104" show-overflow-tooltip />
        <el-table-column label="预约时间" min-width="170">
          <template #default="{ row }">
            <div class="compact-time-cell">
              <span>{{ formatDate(row.scheduleDate) }}</span>
              <span>{{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="96">
          <template #default="{ row }">
            <el-tag :type="APPOINTMENT_STATUS_TYPE_MAP[row.status] || 'info'">
              {{ APPOINTMENT_STATUS_MAP[row.status] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" width="124" show-overflow-tooltip />
        <el-table-column label="操作" width="168">
          <template #default="{ row }">
            <el-space wrap>
              <el-button link type="primary" @click="handleView(row.id)">详情</el-button>
              <el-button
                v-if="row.status === 1"
                link
                type="success"
                @click="handleUpdateStatus(row, 2)"
              >
                确认
              </el-button>
              <el-button
                v-if="row.status === 2"
                link
                type="warning"
                @click="handleFillRecord(row)"
              >
                记录
              </el-button>
              <el-button
                v-if="row.status === 3 && authStore.userInfo?.role === 3"
                link
                type="success"
                @click="handleGoFeedback(row)"
              >
                评价
              </el-button>
              <el-button
                v-if="row.status !== 3 && row.status !== 4"
                link
                type="danger"
                @click="handleCancel(row)"
              >
                取消
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
          @size-change="fetchAppointmentPage"
          @current-change="fetchAppointmentPage"
        />
      </div>
    </section>

    <el-drawer v-model="detailVisible" title="预约详情" size="420px">
      <el-skeleton :loading="detailLoading" animated :rows="8">
        <template #default>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="预约单号">{{ detailData.appointmentNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ detailData.userName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="咨询师">{{ detailData.counselorName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="预约日期">{{ formatDate(detailData.scheduleDate) }}</el-descriptions-item>
            <el-descriptions-item label="预约时间段">
              {{ formatTime(detailData.startTime) }} - {{ formatTime(detailData.endTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              {{ APPOINTMENT_STATUS_MAP[detailData.status] || '-' }}
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
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { cancelAppointment, getAppointmentDetail, getAppointmentPage, updateAppointmentStatus } from '@/api/appointment'
import { useAuthStore } from '@/stores/auth'
import { APPOINTMENT_STATUS_MAP, APPOINTMENT_STATUS_TYPE_MAP } from '@/utils/constant'
import { formatDate, formatDateTime, formatTime } from '@/utils/format'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const queryDateRange = ref([])
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref({})

const queryForm = reactive({
  page: 1,
  pageSize: 10,
  appointmentNo: '',
  status: '',
  beginDate: '',
  endDate: ''
})

const statusOptions = [
  { label: '待确认', value: 1 },
  { label: '已确认', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

const currentStatusText = computed(() => {
  if (!queryForm.status) {
    return '全部'
  }

  return APPOINTMENT_STATUS_MAP[queryForm.status] || '全部'
})

const currentDateRangeText = computed(() => {
  if (!queryForm.beginDate || !queryForm.endDate) {
    return '未限制'
  }

  return `${queryForm.beginDate} 至 ${queryForm.endDate}`
})

async function fetchAppointmentPage() {
  loading.value = true

  try {
    queryForm.beginDate = queryDateRange.value?.[0] || ''
    queryForm.endDate = queryDateRange.value?.[1] || ''

    const data = await getAppointmentPage(queryForm)
    tableData.value = data.rows || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchAppointmentPage()
}

function handleReset() {
  queryForm.page = 1
  queryForm.pageSize = 10
  queryForm.appointmentNo = ''
  queryForm.status = ''
  queryForm.beginDate = ''
  queryForm.endDate = ''
  queryDateRange.value = []
  fetchAppointmentPage()
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

async function handleUpdateStatus(row, status) {
  const actionText = '确认预约'
  await ElMessageBox.confirm(`确定要${actionText}吗？`, '操作确认', {
    type: 'warning'
  })

  await updateAppointmentStatus({
    id: row.id,
    status
  })

  ElMessage.success(`${actionText}成功`)
  fetchAppointmentPage()
}

function handleFillRecord(row) {
  router.push({
    path: '/consultation-records',
    query: {
      appointmentId: row.id
    }
  })
}

function handleGoFeedback(row) {
  router.push({
    path: '/feedbacks',
    query: {
      appointmentId: row.id
    }
  })
}

async function handleCancel(row) {
  const result = await ElMessageBox.prompt('请输入取消原因', '取消预约', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：临时有事，需要取消',
    inputValue: 'User canceled the appointment'
  })

  await cancelAppointment(row.id, {
    cancelReason: result.value
  })

  ElMessage.success('预约已取消')
  fetchAppointmentPage()
}

fetchAppointmentPage()
</script>

<style scoped>
.appointment-page {
  display: grid;
  gap: 18px;
}

.compact-time-cell {
  display: grid;
  gap: 2px;
}
</style>
