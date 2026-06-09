<template>
  <div class="appointment-create-page">
    <section class="page-card form-card">
      <div class="page-head">
        <div>
          <h3>新增预约</h3>
          <p>先选择咨询师，再选择一个当前可预约的时间段，最后提交预约信息。</p>
        </div>
        <el-button @click="router.push('/appointments')">返回列表</el-button>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="110px"
        class="appointment-form"
      >
        <el-form-item label="当前用户">
          <el-input :model-value="authStore.userInfo?.name || '-'" disabled />
        </el-form-item>

        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="选择咨询师" prop="counselorId">
          <el-select
            v-model="form.counselorId"
            placeholder="请选择咨询师"
            clearable
            filterable
            style="width: 100%"
            @change="handleCounselorChange"
          >
            <el-option
              v-for="item in counselorOptions"
              :key="item.id"
              :label="`${item.counselorName} / ${item.title}`"
              :value="item.id"
            >
              <div class="option-line">
                <span>{{ item.counselorName }}</span>
                <span>{{ item.title }}</span>
              </div>
              <div class="option-sub">{{ item.specialty }}</div>
            </el-option>
          </el-select>
        </el-form-item>

        <div v-if="counselorDetail" class="counselor-card">
          <div class="counselor-name">
            {{ counselorDetail.counselorName }}
            <span>{{ counselorDetail.title }}</span>
          </div>
          <div class="counselor-meta">擅长方向：{{ counselorDetail.specialty || '-' }}</div>
          <div class="counselor-meta">从业年限：{{ counselorDetail.yearsOfExperience || 0 }} 年</div>
          <div class="counselor-intro">{{ counselorDetail.introduction || '暂无简介' }}</div>
        </div>

        <el-form-item label="可预约时间段" prop="scheduleId">
          <el-radio-group v-model="form.scheduleId" class="schedule-group">
            <el-radio
              v-for="item in scheduleOptions"
              :key="item.id"
              :label="item.id"
              class="schedule-card"
            >
              <div class="schedule-date">{{ formatDate(item.scheduleDate) }}</div>
              <div class="schedule-time">{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</div>
              <div class="schedule-remark">{{ item.remark || '标准咨询时段' }}</div>
            </el-radio>
          </el-radio-group>

          <el-empty
            v-if="form.counselorId && !scheduleLoading && scheduleOptions.length === 0"
            description="当前咨询师暂无可预约时间段"
          />
          <el-skeleton v-if="scheduleLoading" animated :rows="4" />
        </el-form-item>

        <el-form-item label="预约备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="4"
            maxlength="200"
            show-word-limit
            placeholder="例如：近期工作压力较大，希望做情绪疏导"
          />
        </el-form-item>

        <el-form-item>
          <el-space>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">提交预约</el-button>
            <el-button @click="handleReset">重置表单</el-button>
          </el-space>
        </el-form-item>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { addAppointment } from '@/api/appointment'
import { getCounselorDetail, getCounselorOptions } from '@/api/counselor'
import { useAuthStore } from '@/stores/auth'
import { formatDate, formatTime } from '@/utils/format'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const counselorOptions = ref([])
const counselorDetail = ref(null)
const scheduleOptions = ref([])
const scheduleLoading = ref(false)
const submitting = ref(false)

const form = reactive({
  userId: authStore.userInfo?.id || null,
  counselorId: null,
  scheduleId: null,
  contactPhone: authStore.userInfo?.phone || '',
  remark: ''
})

const rules = {
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  counselorId: [{ required: true, message: '请选择咨询师', trigger: 'change' }],
  scheduleId: [{ required: true, message: '请选择可预约时间段', trigger: 'change' }]
}

async function loadCounselorOptions() {
  counselorOptions.value = await getCounselorOptions()
}

async function handleCounselorChange(counselorId) {
  form.scheduleId = null
  counselorDetail.value = null
  scheduleOptions.value = []

  if (!counselorId) {
    return
  }

  scheduleLoading.value = true
  try {
    const detail = await getCounselorDetail(counselorId)
    counselorDetail.value = detail
    scheduleOptions.value = detail.scheduleList || []
  } finally {
    scheduleLoading.value = false
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true

  try {
    form.userId = authStore.userInfo?.id || null
    await addAppointment(form)
    ElMessage.success('预约提交成功')
    router.push('/appointments')
  } finally {
    submitting.value = false
  }
}

function handleReset() {
  form.userId = authStore.userInfo?.id || null
  form.counselorId = null
  form.scheduleId = null
  form.contactPhone = authStore.userInfo?.phone || ''
  form.remark = ''
  counselorDetail.value = null
  scheduleOptions.value = []
  formRef.value?.clearValidate()
}

onMounted(() => {
  loadCounselorOptions()
})
</script>

<style scoped>
.appointment-create-page {
  display: grid;
}

.form-card {
  padding: 22px 24px;
}

.page-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 18px;
}

.page-head h3 {
  margin: 0 0 8px;
  font-size: 22px;
}

.page-head p {
  margin: 0;
  color: #64748b;
}

.appointment-form {
  max-width: 860px;
  margin-top: 20px;
}

.option-line {
  display: flex;
  justify-content: space-between;
}

.option-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}

.counselor-card {
  margin: 4px 0 22px 110px;
  padding: 18px;
  border: 1px solid #dbeafe;
  border-radius: 14px;
  background: linear-gradient(135deg, #f8fbff 0%, #f0f7ff 100%);
}

.counselor-name {
  font-size: 18px;
  font-weight: 700;
}

.counselor-name span {
  margin-left: 8px;
  color: #2563eb;
  font-size: 14px;
  font-weight: 500;
}

.counselor-meta {
  margin-top: 10px;
  color: #475569;
}

.counselor-intro {
  margin-top: 10px;
  line-height: 1.8;
  color: #334155;
}

.schedule-group {
  display: grid;
  width: 100%;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 14px;
}

.schedule-card {
  margin: 0;
  padding: 14px 16px;
  border: 1px solid #dbe3ef;
  border-radius: 12px;
  background: #fff;
}

:deep(.schedule-card .el-radio__input) {
  display: none;
}

:deep(.schedule-card .el-radio__label) {
  display: block;
  padding-left: 0;
}

:deep(.schedule-card.is-checked) {
  border-color: #2c7be5;
  background: #eff6ff;
}

.schedule-date {
  font-weight: 700;
}

.schedule-time {
  margin-top: 8px;
  color: #1453a3;
}

.schedule-remark {
  margin-top: 8px;
  font-size: 12px;
  color: #64748b;
}
</style>
