<template>
  <div class="record-page">
    <section class="page-card filter-card">
      <div class="filter-header">
        <div>
          <div class="filter-title">筛选条件</div>
          <div class="filter-desc">按预约单号、预约状态、记录状态和咨询师筛选咨询记录。</div>
        </div>
      </div>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="预约单号">
          <el-input v-model="queryForm.appointmentNo" placeholder="请输入预约单号" clearable />
        </el-form-item>

        <el-form-item label="预约状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="已确认" :value="2" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="记录状态">
          <el-select v-model="queryForm.hasRecord" placeholder="请选择记录状态" clearable style="width: 150px">
            <el-option label="待填写" :value="0" />
            <el-option label="已填写" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="authStore.userInfo?.role === 1" label="咨询师">
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

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="page-card table-card">
      <div class="table-header">
        <div>
          <h3>咨询记录管理</h3>
          <p>按预约闭环管理咨询记录，已确认预约可填写记录并完成预约，已完成预约可查看历史详情。</p>
        </div>
      </div>

      <div class="table-summary">
        <span class="table-summary-item">当前共 {{ total }} 条记录</span>
        <span class="table-summary-item">预约状态：{{ currentStatusText }}</span>
        <span class="table-summary-item">记录状态：{{ currentRecordStatusText }}</span>
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
        <el-table-column label="预约状态" width="96">
          <template #default="{ row }">
            <el-tag :type="APPOINTMENT_STATUS_TYPE_MAP[row.status] || 'info'">
              {{ APPOINTMENT_STATUS_MAP[row.status] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="记录状态" width="96">
          <template #default="{ row }">
            <el-tag :type="row.recordId ? 'success' : 'warning'">
              {{ row.recordId ? '已填写' : '待填写' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="summary" label="咨询摘要" min-width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="156">
          <template #default="{ row }">
            <el-space wrap>
              <el-button link type="primary" @click="handleView(row.appointmentId)">详情</el-button>
              <el-button
                v-if="canFillRecord(row)"
                link
                type="success"
                @click="handleOpenCompleteDialog(row.appointmentId)"
              >
                填写
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
          @size-change="fetchRecordPage"
          @current-change="fetchRecordPage"
        />
      </div>
    </section>

    <el-drawer v-model="detailVisible" title="咨询记录详情" size="520px">
      <el-skeleton :loading="detailLoading" animated :rows="8">
        <template #default>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="预约单号">{{ detailData.appointmentNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ detailData.userName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="咨询师">{{ detailData.counselorName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="预约时间">
              {{ formatDate(detailData.scheduleDate) }} {{ formatTime(detailData.startTime) }} - {{ formatTime(detailData.endTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="预约状态">
              {{ APPOINTMENT_STATUS_MAP[detailData.status] || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="咨询摘要">{{ detailData.summary || '-' }}</el-descriptions-item>
            <el-descriptions-item label="后续建议">{{ detailData.suggestion || '-' }}</el-descriptions-item>
            <el-descriptions-item label="附件">
              <template v-if="detailData.attachmentFileUrl">
                <a :href="detailData.attachmentFileUrl" target="_blank" rel="noreferrer">
                  {{ detailData.attachmentOriginalName || '查看附件' }}
                </a>
              </template>
              <template v-else>-</template>
            </el-descriptions-item>
            <el-descriptions-item label="记录时间">{{ formatDateTime(detailData.recordCreateTime) }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </el-skeleton>
    </el-drawer>

    <el-dialog v-model="completeVisible" title="填写咨询记录并完成预约" width="700px" destroy-on-close>
      <el-alert
        type="info"
        :closable="false"
        show-icon
        title="提交后系统会在同一事务中写入咨询记录，并同步把预约状态修改为已完成。"
      />

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="complete-form">
        <el-form-item label="预约单号">
          <el-input :model-value="form.appointmentNo" disabled />
        </el-form-item>

        <el-form-item label="用户">
          <el-input :model-value="form.userName" disabled />
        </el-form-item>

        <el-form-item label="咨询师">
          <el-input :model-value="form.counselorName" disabled />
        </el-form-item>

        <el-form-item label="咨询摘要" prop="summary">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            placeholder="请填写本次咨询的核心内容摘要"
          />
        </el-form-item>

        <el-form-item label="后续建议" prop="suggestion">
          <el-input
            v-model="form.suggestion"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            placeholder="例如：建议持续记录情绪变化，下周进行二次咨询"
          />
        </el-form-item>

        <el-form-item label="咨询附件">
          <el-upload
            :show-file-list="false"
            :http-request="handleUploadRequest"
            accept=".png,.jpg,.jpeg,.pdf,.doc,.docx"
          >
            <el-button :loading="uploading">上传附件</el-button>
          </el-upload>

          <div class="upload-tip">附件为可选项。如果 OSS 还未配置完成，可以先不上传。</div>

          <div v-if="form.attachmentFileId" class="attachment-card">
            <div>{{ form.attachmentOriginalName }}</div>
            <el-space>
              <a :href="form.attachmentFileUrl" target="_blank" rel="noreferrer">查看</a>
              <el-button link type="danger" @click="clearAttachment">移除</el-button>
            </el-space>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-space>
          <el-button @click="completeVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmitRecord">提交并完成预约</el-button>
        </el-space>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

import { completeAppointmentWithRecord, getConsultationRecordDetailByAppointmentId, getConsultationRecordPage } from '@/api/consultationRecord'
import { getCounselorOptions } from '@/api/counselor'
import { uploadConsultationAttachment } from '@/api/upload'
import { useAuthStore } from '@/stores/auth'
import { APPOINTMENT_STATUS_MAP, APPOINTMENT_STATUS_TYPE_MAP } from '@/utils/constant'
import { formatDate, formatDateTime, formatTime } from '@/utils/format'

const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const counselorOptions = ref([])
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref({})
const completeVisible = ref(false)
const formRef = ref()
const uploading = ref(false)
const submitting = ref(false)
const counselorIdByUser = ref(null)

const queryForm = reactive({
  page: 1,
  pageSize: 10,
  appointmentNo: '',
  counselorId: null,
  status: null,
  hasRecord: null,
  userId: null
})

const form = reactive({
  appointmentId: null,
  appointmentNo: '',
  userName: '',
  counselorName: '',
  counselorId: null,
  summary: '',
  suggestion: '',
  attachmentFileId: null,
  attachmentOriginalName: '',
  attachmentFileUrl: ''
})

const rules = {
  summary: [{ required: true, message: '请填写咨询摘要', trigger: 'blur' }]
}

const currentStatusText = computed(() => {
  if (queryForm.status === null) {
    return '全部'
  }

  return APPOINTMENT_STATUS_MAP[queryForm.status] || '全部'
})

const currentRecordStatusText = computed(() => {
  if (queryForm.hasRecord === null) {
    return '全部'
  }

  return queryForm.hasRecord === 1 ? '已填写' : '待填写'
})

function applyRoleScope() {
  if (authStore.userInfo?.role === 3) {
    queryForm.userId = authStore.userInfo?.id
  }

  if (authStore.userInfo?.role === 2) {
    queryForm.counselorId = counselorIdByUser.value
  }
}

async function loadCounselorOptions() {
  counselorOptions.value = await getCounselorOptions()

  if (authStore.userInfo?.role === 2) {
    const currentCounselor = counselorOptions.value.find((item) => item.userId === authStore.userInfo?.id)
    counselorIdByUser.value = currentCounselor?.id || null
  }
}

async function fetchRecordPage() {
  loading.value = true

  try {
    applyRoleScope()
    const data = await getConsultationRecordPage(queryForm)
    tableData.value = data.rows || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchRecordPage()
}

function handleReset() {
  queryForm.page = 1
  queryForm.pageSize = 10
  queryForm.appointmentNo = ''
  queryForm.status = null
  queryForm.hasRecord = null
  queryForm.userId = authStore.userInfo?.role === 3 ? authStore.userInfo?.id : null
  queryForm.counselorId = authStore.userInfo?.role === 2 ? counselorIdByUser.value : null
  fetchRecordPage()
}

function canFillRecord(row) {
  return authStore.userInfo?.role !== 3 && row.status === 2 && !row.recordId
}

async function handleView(appointmentId) {
  detailVisible.value = true
  detailLoading.value = true

  try {
    detailData.value = await getConsultationRecordDetailByAppointmentId(appointmentId)
  } finally {
    detailLoading.value = false
  }
}

async function handleOpenCompleteDialog(appointmentId) {
  const detail = await getConsultationRecordDetailByAppointmentId(appointmentId)
  form.appointmentId = detail.appointmentId
  form.appointmentNo = detail.appointmentNo
  form.userName = detail.userName
  form.counselorName = detail.counselorName
  form.counselorId = detail.counselorId
  form.summary = detail.summary || ''
  form.suggestion = detail.suggestion || ''
  form.attachmentFileId = detail.attachmentFileId || null
  form.attachmentOriginalName = detail.attachmentOriginalName || ''
  form.attachmentFileUrl = detail.attachmentFileUrl || ''
  completeVisible.value = true
}

async function handleUploadRequest(options) {
  uploading.value = true

  try {
    const result = await uploadConsultationAttachment(options.file)
    form.attachmentFileId = result.id
    form.attachmentOriginalName = result.originalName
    form.attachmentFileUrl = result.fileUrl
    ElMessage.success('附件上传成功')
    options.onSuccess?.(result)
  } finally {
    uploading.value = false
  }
}

function clearAttachment() {
  form.attachmentFileId = null
  form.attachmentOriginalName = ''
  form.attachmentFileUrl = ''
}

async function handleSubmitRecord() {
  await formRef.value.validate()
  submitting.value = true

  try {
    await completeAppointmentWithRecord({
      appointmentId: form.appointmentId,
      counselorId: form.counselorId,
      summary: form.summary,
      suggestion: form.suggestion,
      attachmentFileId: form.attachmentFileId
    })

    ElMessage.success('咨询记录提交成功，预约已完成')
    completeVisible.value = false
    fetchRecordPage()
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadCounselorOptions()
  handleReset()

  const appointmentId = route.query.appointmentId
  if (appointmentId) {
    handleOpenCompleteDialog(Number(appointmentId))
  }
})
</script>

<style scoped>
.record-page {
  display: grid;
  gap: 18px;
}

.compact-time-cell {
  display: grid;
  gap: 2px;
}

.complete-form {
  margin-top: 18px;
}

.upload-tip {
  margin-top: 10px;
  color: #64748b;
  font-size: 12px;
}

.attachment-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
  padding: 12px 14px;
  border: 1px solid #dbeafe;
  border-radius: 12px;
  background: #f8fbff;
}
</style>
