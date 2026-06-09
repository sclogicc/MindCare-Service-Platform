<template>
  <div class="counselor-page">
    <section class="page-card filter-card">
      <div class="filter-header">
        <div>
          <div class="filter-title">筛选条件</div>
          <div class="filter-desc">按姓名、擅长方向和状态查询咨询师资料。</div>
        </div>
      </div>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="姓名">
          <el-input v-model="queryForm.counselorName" placeholder="请输入咨询师姓名" clearable />
        </el-form-item>

        <el-form-item label="擅长方向">
          <el-input v-model="queryForm.specialty" placeholder="请输入擅长方向" clearable />
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
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
          <h3>咨询师管理</h3>
          <p>支持分页查询、详情查看、业务信息编辑和启停状态切换，满足后台日常管理需求。</p>
        </div>
      </div>

      <div class="table-summary">
        <span class="table-summary-item">当前共 {{ total }} 位咨询师</span>
        <span class="table-summary-item">状态筛选：{{ currentStatusText }}</span>
        <span class="table-summary-item">擅长方向：{{ currentSpecialtyText }}</span>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="counselorName" label="姓名" min-width="120" />
        <el-table-column prop="phone" label="联系电话" min-width="130" />
        <el-table-column prop="specialty" label="擅长方向" min-width="180" />
        <el-table-column prop="title" label="职称" min-width="140" />
        <el-table-column prop="yearsOfExperience" label="从业年限" width="110" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="COUNSELOR_STATUS_TYPE_MAP[row.status] || 'info'">
              {{ COUNSELOR_STATUS_MAP[row.status] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="240">
          <template #default="{ row }">
            <el-space wrap>
              <el-button link type="primary" @click="handleView(row.id)">详情</el-button>
              <el-button link type="success" @click="handleEdit(row.id)">编辑</el-button>
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
          @size-change="fetchCounselorPage"
          @current-change="fetchCounselorPage"
        />
      </div>
    </section>

    <el-drawer v-model="detailVisible" title="咨询师详情" size="560px">
      <el-skeleton :loading="detailLoading" animated :rows="8">
        <template #default>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="姓名">{{ detailData.counselorName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detailData.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="擅长方向">{{ detailData.specialty || '-' }}</el-descriptions-item>
            <el-descriptions-item label="职称">{{ detailData.title || '-' }}</el-descriptions-item>
            <el-descriptions-item label="从业年限">{{ detailData.yearsOfExperience ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              {{ COUNSELOR_STATUS_MAP[detailData.status] || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="简介">{{ detailData.introduction || '-' }}</el-descriptions-item>
          </el-descriptions>

          <div class="schedule-section">
            <h4>可预约时间段</h4>
            <el-empty v-if="!detailData.scheduleList || detailData.scheduleList.length === 0" description="暂无时间段" />
            <div v-else class="schedule-list">
              <div v-for="item in detailData.scheduleList" :key="item.id" class="schedule-item">
                <strong>{{ formatDate(item.scheduleDate) }}</strong>
                <span>{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</span>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
    </el-drawer>

    <el-dialog v-model="dialogVisible" title="编辑咨询师" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="姓名">
          <el-input :model-value="form.counselorName" disabled />
        </el-form-item>

        <el-form-item label="联系电话">
          <el-input :model-value="form.phone" disabled />
        </el-form-item>

        <el-form-item label="擅长方向" prop="specialty">
          <el-input v-model="form.specialty" placeholder="请输入擅长方向" />
        </el-form-item>

        <el-form-item label="职称" prop="title">
          <el-input v-model="form.title" placeholder="请输入职称" />
        </el-form-item>

        <el-form-item label="从业年限" prop="yearsOfExperience">
          <el-input-number v-model="form.yearsOfExperience" :min="0" :max="50" style="width: 100%" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="简介">
          <el-input
            v-model="form.introduction"
            type="textarea"
            :rows="4"
            maxlength="400"
            show-word-limit
            placeholder="请输入咨询师简介"
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
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { getCounselorManageDetail, getCounselorPage, updateCounselor, updateCounselorStatus } from '@/api/counselor'
import { COUNSELOR_STATUS_MAP, COUNSELOR_STATUS_TYPE_MAP } from '@/utils/constant'
import { formatDate, formatDateTime, formatTime } from '@/utils/format'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref({})
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()

const queryForm = reactive({
  page: 1,
  pageSize: 10,
  counselorName: '',
  specialty: '',
  status: null
})

const form = reactive({
  id: null,
  counselorName: '',
  phone: '',
  specialty: '',
  title: '',
  yearsOfExperience: 0,
  introduction: '',
  status: 1,
  avatarFileId: null
})

const rules = {
  specialty: [{ required: true, message: '请输入擅长方向', trigger: 'blur' }],
  title: [{ required: true, message: '请输入职称', trigger: 'blur' }],
  yearsOfExperience: [{ required: true, message: '请输入从业年限', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const currentStatusText = computed(() => {
  if (queryForm.status === null) {
    return '全部'
  }

  return COUNSELOR_STATUS_MAP[queryForm.status] || '全部'
})

const currentSpecialtyText = computed(() => queryForm.specialty || '未限制')

async function fetchCounselorPage() {
  loading.value = true

  try {
    const data = await getCounselorPage(queryForm)
    tableData.value = data.rows || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchCounselorPage()
}

function handleReset() {
  queryForm.page = 1
  queryForm.pageSize = 10
  queryForm.counselorName = ''
  queryForm.specialty = ''
  queryForm.status = null
  fetchCounselorPage()
}

async function handleView(id) {
  detailVisible.value = true
  detailLoading.value = true

  try {
    detailData.value = await getCounselorManageDetail(id)
  } finally {
    detailLoading.value = false
  }
}

async function handleEdit(id) {
  const detail = await getCounselorManageDetail(id)
  form.id = detail.id
  form.counselorName = detail.counselorName
  form.phone = detail.phone
  form.specialty = detail.specialty
  form.title = detail.title
  form.yearsOfExperience = detail.yearsOfExperience
  form.introduction = detail.introduction
  form.status = detail.status
  form.avatarFileId = detail.avatarFileId
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true

  try {
    await updateCounselor(form)
    ElMessage.success('咨询师信息修改成功')
    dialogVisible.value = false
    fetchCounselorPage()
  } finally {
    submitting.value = false
  }
}

async function handleStatusChange(row, status) {
  const actionText = status === 1 ? '启用' : '停用'
  await ElMessageBox.confirm(`确定要${actionText}该咨询师吗？`, '操作确认', {
    type: 'warning'
  })

  await updateCounselorStatus({
    id: row.id,
    status
  })

  ElMessage.success(`${actionText}成功`)
  fetchCounselorPage()
}

fetchCounselorPage()
</script>

<style scoped>
.counselor-page {
  display: grid;
  gap: 18px;
}

.schedule-section {
  margin-top: 22px;
}

.schedule-section h4 {
  margin: 0 0 12px;
  font-size: 16px;
}

.schedule-list {
  display: grid;
  gap: 10px;
}

.schedule-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border: 1px solid #e6edf5;
  border-radius: 12px;
  background: #fbfdff;
}
</style>
