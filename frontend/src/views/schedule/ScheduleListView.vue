<template>
  <div class="schedule-page">
    <section class="page-card filter-card">
      <div class="filter-header">
        <div>
          <div class="filter-title">筛选条件</div>
          <div class="filter-desc">按咨询师、状态和日期筛选可预约时间段。</div>
        </div>
      </div>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="咨询师">
          <el-select
            v-model="queryForm.counselorId"
            placeholder="请选择咨询师"
            clearable
            filterable
            style="width: 220px"
          >
            <el-option
              v-for="item in counselorOptions"
              :key="item.id"
              :label="item.counselorName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="日期">
          <el-date-picker
            v-model="queryForm.scheduleDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择日期"
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
          <h3>时间段管理</h3>
          <p>维护咨询师可预约时间段，并在保存时校验同一咨询师同一天是否存在时间冲突。</p>
        </div>
        <div class="table-header-actions">
          <el-button type="primary" @click="handleAdd">新增时间段</el-button>
        </div>
      </div>

      <div class="table-summary">
        <span class="table-summary-item">当前共 {{ total }} 个时间段</span>
        <span class="table-summary-item">状态筛选：{{ currentStatusText }}</span>
        <span class="table-summary-item">日期筛选：{{ currentScheduleDateText }}</span>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="counselorName" label="咨询师" min-width="130" />
        <el-table-column label="日期" min-width="120">
          <template #default="{ row }">
            {{ formatDate(row.scheduleDate) }}
          </template>
        </el-table-column>
        <el-table-column label="时间段" min-width="140">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="SCHEDULE_STATUS_TYPE_MAP[row.status] || 'info'">
              {{ SCHEDULE_STATUS_MAP[row.status] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentCount" label="预约数" width="90" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="240">
          <template #default="{ row }">
            <el-space wrap>
              <el-button link type="primary" @click="handleEdit(row.id)">编辑</el-button>
              <el-button
                v-if="row.status === 1"
                link
                type="warning"
                @click="handleStatusChange(row, 0)"
              >
                停用
              </el-button>
              <el-button
                v-if="row.status === 0"
                link
                type="success"
                @click="handleStatusChange(row, 1)"
              >
                启用
              </el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
          @size-change="fetchSchedulePage"
          @current-change="fetchSchedulePage"
        />
      </div>
    </section>

    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑时间段' : '新增时间段'"
      width="620px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="咨询师" prop="counselorId">
          <el-select
            v-model="form.counselorId"
            placeholder="请选择咨询师"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in counselorOptions"
              :key="item.id"
              :label="`${item.counselorName} / ${item.title}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="预约日期" prop="scheduleDate">
          <el-date-picker
            v-model="form.scheduleDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择预约日期"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker
            v-model="form.startTime"
            value-format="HH:mm:ss"
            placeholder="请选择开始时间"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker
            v-model="form.endTime"
            value-format="HH:mm:ss"
            placeholder="请选择结束时间"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            maxlength="100"
            show-word-limit
            placeholder="例如：晚间咨询、情绪管理专场"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-space>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
        </el-space>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { getCounselorOptions } from '@/api/counselor'
import { addSchedule, deleteSchedule, getScheduleDetail, getSchedulePage, updateSchedule, updateScheduleStatus } from '@/api/schedule'
import { useAuthStore } from '@/stores/auth'
import { SCHEDULE_STATUS_MAP, SCHEDULE_STATUS_TYPE_MAP } from '@/utils/constant'
import { formatDate, formatDateTime, formatTime } from '@/utils/format'

const authStore = useAuthStore()
const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const counselorOptions = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const counselorIdByUser = ref(null)

const queryForm = reactive({
  page: 1,
  pageSize: 10,
  counselorId: null,
  status: null,
  scheduleDate: ''
})

const form = reactive({
  id: null,
  counselorId: null,
  scheduleDate: '',
  startTime: '',
  endTime: '',
  status: 1,
  remark: ''
})

const rules = {
  counselorId: [{ required: true, message: '请选择咨询师', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const currentStatusText = computed(() => {
  if (queryForm.status === null) {
    return '全部'
  }

  return SCHEDULE_STATUS_MAP[queryForm.status] || '全部'
})

const currentScheduleDateText = computed(() => queryForm.scheduleDate || '未限制')

async function loadCounselorOptions() {
  counselorOptions.value = await getCounselorOptions()

  // 咨询师登录后默认只看并维护自己的时间段，避免误操作其他咨询师的数据。
  if (authStore.userInfo?.role === 2) {
    const currentCounselor = counselorOptions.value.find((item) => item.userId === authStore.userInfo?.id)
    counselorIdByUser.value = currentCounselor?.id || null
    queryForm.counselorId = currentCounselor?.id || null
    form.counselorId = currentCounselor?.id || null
  }
}

async function fetchSchedulePage() {
  loading.value = true

  try {
    const data = await getSchedulePage(queryForm)
    tableData.value = data.rows || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchSchedulePage()
}

function handleReset() {
  queryForm.page = 1
  queryForm.pageSize = 10
  queryForm.counselorId = counselorIdByUser.value
  queryForm.status = null
  queryForm.scheduleDate = ''
  fetchSchedulePage()
}

function resetForm() {
  form.id = null
  form.counselorId = counselorIdByUser.value
  form.scheduleDate = ''
  form.startTime = ''
  form.endTime = ''
  form.status = 1
  form.remark = ''
}

function handleAdd() {
  resetForm()
  dialogVisible.value = true
}

async function handleEdit(id) {
  const detail = await getScheduleDetail(id)
  form.id = detail.id
  form.counselorId = detail.counselorId
  form.scheduleDate = detail.scheduleDate
  form.startTime = detail.startTime
  form.endTime = detail.endTime
  form.status = detail.status
  form.remark = detail.remark
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true

  try {
    if (form.id) {
      await updateSchedule(form)
      ElMessage.success('时间段修改成功')
    } else {
      await addSchedule(form)
      ElMessage.success('时间段新增成功')
    }

    dialogVisible.value = false
    fetchSchedulePage()
  } finally {
    submitting.value = false
  }
}

async function handleStatusChange(row, status) {
  const actionText = status === 1 ? '启用' : '停用'
  await ElMessageBox.confirm(`确定要${actionText}该时间段吗？`, '操作确认', {
    type: 'warning'
  })

  await updateScheduleStatus({
    id: row.id,
    status
  })

  ElMessage.success(`${actionText}成功`)
  fetchSchedulePage()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(
    '删除后该时间段将不可恢复；如果已经关联预约，系统会阻止删除。确定继续吗？',
    '删除确认',
    { type: 'warning' }
  )

  await deleteSchedule(row.id)
  ElMessage.success('删除成功')
  fetchSchedulePage()
}

onMounted(async () => {
  await loadCounselorOptions()
  fetchSchedulePage()
})
</script>

<style scoped>
.schedule-page {
  display: grid;
  gap: 18px;
}
</style>
