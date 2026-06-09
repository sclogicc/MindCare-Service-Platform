<template>
  <div class="feedback-page">
    <section class="page-card filter-card">
      <div class="filter-header">
        <div>
          <div class="filter-title">筛选条件</div>
          <div class="filter-desc">按预约单号、反馈状态、评分、匿名情况和咨询师筛选评价记录。</div>
        </div>
      </div>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="预约单号">
          <el-input v-model="queryForm.appointmentNo" placeholder="请输入预约单号" clearable />
        </el-form-item>

        <el-form-item label="反馈状态">
          <el-select v-model="queryForm.hasFeedback" placeholder="请选择反馈状态" clearable style="width: 150px">
            <el-option label="待评价" :value="0" />
            <el-option label="已评价" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item label="评分">
          <el-select v-model="queryForm.score" placeholder="请选择评分" clearable style="width: 140px">
            <el-option v-for="score in [5, 4, 3, 2, 1]" :key="score" :label="`${score}分`" :value="score" />
          </el-select>
        </el-form-item>

        <el-form-item label="匿名">
          <el-select v-model="queryForm.isAnonymous" placeholder="请选择" clearable style="width: 140px">
            <el-option label="实名" :value="0" />
            <el-option label="匿名" :value="1" />
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
          <h3>反馈评价管理</h3>
          <p>围绕已完成预约管理用户评价。普通用户可补充反馈，管理员可查看并删除不合规评价。</p>
        </div>
      </div>

      <div class="table-summary">
        <span class="table-summary-item">当前共 {{ total }} 条记录</span>
        <span class="table-summary-item">反馈状态：{{ currentFeedbackStatusText }}</span>
        <span class="table-summary-item">评分筛选：{{ currentScoreText }}</span>
        <span class="table-summary-item">匿名方式：{{ currentAnonymousText }}</span>
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
        <el-table-column label="反馈状态" width="96">
          <template #default="{ row }">
            <el-tag :type="row.feedbackId ? 'success' : 'warning'">
              {{ row.feedbackId ? '已评价' : '待评价' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="76">
          <template #default="{ row }">
            {{ row.score ? `${row.score}分` : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="匿名" width="72">
          <template #default="{ row }">
            {{ row.isAnonymous === 1 ? '是' : row.feedbackId ? '否' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="164">
          <template #default="{ row }">
            <el-space wrap>
              <el-button link type="primary" @click="handleView(row)">详情</el-button>
              <el-button
                v-if="canEvaluate(row)"
                link
                type="success"
                @click="handleOpenDialog(row.appointmentId)"
              >
                评价
              </el-button>
              <el-button
                v-if="authStore.userInfo?.role === 1 && row.feedbackId"
                link
                type="danger"
                @click="handleDelete(row.feedbackId)"
              >
                删除
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
          @size-change="fetchFeedbackPage"
          @current-change="fetchFeedbackPage"
        />
      </div>
    </section>

    <el-drawer v-model="detailVisible" title="反馈详情" size="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="预约单号">{{ detailData.appointmentNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ detailData.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="咨询师">{{ detailData.counselorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">
          {{ formatDate(detailData.scheduleDate) }} {{ formatTime(detailData.startTime) }} - {{ formatTime(detailData.endTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="评分">
          {{ detailData.score ? `${detailData.score}分` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="匿名">
          {{ detailData.isAnonymous === 1 ? '是' : detailData.feedbackId ? '否' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="评价内容">{{ detailData.content || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价时间">{{ formatDateTime(detailData.feedbackCreateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <el-dialog v-model="dialogVisible" title="提交反馈评价" width="660px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="预约单号">
          <el-input :model-value="form.appointmentNo" disabled />
        </el-form-item>

        <el-form-item label="咨询师">
          <el-input :model-value="form.counselorName" disabled />
        </el-form-item>

        <el-form-item label="评分" prop="score">
          <el-rate v-model="form.score" :max="5" show-score />
        </el-form-item>

        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            placeholder="请填写你对本次咨询服务的真实反馈"
          />
        </el-form-item>

        <el-form-item label="是否匿名">
          <el-switch
            v-model="anonymousSwitch"
            inline-prompt
            active-text="匿名"
            inactive-text="实名"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-space>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交评价</el-button>
        </el-space>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { addFeedback, deleteFeedback, getFeedbackByAppointmentId, getFeedbackPage } from '@/api/feedback'
import { getCounselorOptions } from '@/api/counselor'
import { useAuthStore } from '@/stores/auth'
import { formatDate, formatDateTime, formatTime } from '@/utils/format'

const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const counselorOptions = ref([])
const counselorIdByUser = ref(null)
const detailVisible = ref(false)
const detailData = ref({})
const dialogVisible = ref(false)
const formRef = ref()
const submitting = ref(false)

const queryForm = reactive({
  page: 1,
  pageSize: 10,
  appointmentNo: '',
  userId: null,
  counselorId: null,
  score: null,
  isAnonymous: null,
  hasFeedback: null
})

const form = reactive({
  appointmentId: null,
  appointmentNo: '',
  userId: null,
  userName: '',
  counselorId: null,
  counselorName: '',
  score: 5,
  content: '',
  isAnonymous: 0
})

const anonymousSwitch = computed({
  get: () => form.isAnonymous === 1,
  set: (value) => {
    form.isAnonymous = value ? 1 : 0
  }
})

const rules = {
  score: [{ required: true, message: '请选择评分', trigger: 'change' }],
  content: [{ required: true, message: '请填写评价内容', trigger: 'blur' }]
}

const currentFeedbackStatusText = computed(() => {
  if (queryForm.hasFeedback === null) {
    return '全部'
  }

  return queryForm.hasFeedback === 1 ? '已评价' : '待评价'
})

const currentScoreText = computed(() => (queryForm.score ? `${queryForm.score}分` : '全部'))

const currentAnonymousText = computed(() => {
  if (queryForm.isAnonymous === null) {
    return '全部'
  }

  return queryForm.isAnonymous === 1 ? '匿名' : '实名'
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

async function fetchFeedbackPage() {
  loading.value = true

  try {
    applyRoleScope()
    const data = await getFeedbackPage(queryForm)
    tableData.value = data.rows || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchFeedbackPage()
}

function handleReset() {
  queryForm.page = 1
  queryForm.pageSize = 10
  queryForm.appointmentNo = ''
  queryForm.score = null
  queryForm.isAnonymous = null
  queryForm.hasFeedback = null
  queryForm.userId = authStore.userInfo?.role === 3 ? authStore.userInfo?.id : null
  queryForm.counselorId = authStore.userInfo?.role === 2 ? counselorIdByUser.value : null
  fetchFeedbackPage()
}

function canEvaluate(row) {
  return authStore.userInfo?.role === 3 && !row.feedbackId
}

function handleView(row) {
  detailData.value = row
  detailVisible.value = true
}

async function handleOpenDialog(appointmentId) {
  const detail = await getFeedbackByAppointmentId(appointmentId)
  form.appointmentId = detail.appointmentId
  form.appointmentNo = detail.appointmentNo
  form.userId = authStore.userInfo?.id
  form.userName = detail.userName
  form.counselorId = detail.counselorId
  form.counselorName = detail.counselorName
  form.score = detail.score || 5
  form.content = detail.content || ''
  form.isAnonymous = detail.isAnonymous || 0
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true

  try {
    await addFeedback({
      appointmentId: form.appointmentId,
      userId: form.userId,
      counselorId: form.counselorId,
      score: form.score,
      content: form.content,
      isAnonymous: form.isAnonymous
    })

    ElMessage.success('反馈提交成功')
    dialogVisible.value = false
    fetchFeedbackPage()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定要删除该评价吗？删除后会影响反馈统计结果。', '删除确认', {
    type: 'warning'
  })

  await deleteFeedback(id)
  ElMessage.success('删除成功')
  fetchFeedbackPage()
}

onMounted(async () => {
  await loadCounselorOptions()
  handleReset()

  const appointmentId = route.query.appointmentId
  if (appointmentId) {
    handleOpenDialog(Number(appointmentId))
  }
})
</script>

<style scoped>
.feedback-page {
  display: grid;
  gap: 18px;
}

.compact-time-cell {
  display: grid;
  gap: 2px;
}
</style>
