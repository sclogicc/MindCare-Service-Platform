<template>
  <div class="report-page">
    <section class="page-card report-hero">
      <div class="report-hero-main">
        <div class="report-eyebrow">统计中心</div>
        <h3>统计报表概览</h3>
        <p>集中展示预约状态分布、咨询师工作量、月度预约趋势和反馈评分情况，用于查看平台业务统计表现。</p>
      </div>

      <div class="report-hero-side">
        <div class="report-hero-card">
          <span>统计区间</span>
          <strong>{{ dateRangeText }}</strong>
        </div>
        <div class="report-hero-card">
          <span>统计年份</span>
          <strong>{{ reportYear }}</strong>
        </div>
      </div>
    </section>

    <section class="page-card filter-card report-filter-card">
      <div class="filter-header">
        <div>
          <div class="filter-title">报表条件</div>
          <div class="filter-desc">调整统计日期区间和年份后重新刷新报表。</div>
        </div>
      </div>

      <el-form :inline="true">
        <el-form-item label="统计日期">
          <el-date-picker
            v-model="reportDateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            range-separator="至"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="年份">
          <el-input-number v-model="reportYear" :min="2020" :max="2100" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="fetchReportData">刷新报表</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="report-summary-grid">
      <article v-for="item in summaryCards" :key="item.label" class="page-card report-summary-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <em>{{ item.desc }}</em>
      </article>
    </section>

    <div class="report-grid">
      <section class="page-card chart-card">
        <div class="chart-header">
          <div>
            <div class="chart-title">预约状态分布</div>
            <div class="chart-desc">反映预约流转结构与当前状态占比。</div>
          </div>
        </div>
        <div ref="statusChartRef" class="chart-box"></div>
      </section>

      <section class="page-card chart-card">
        <div class="chart-header">
          <div>
            <div class="chart-title">咨询师工作量</div>
            <div class="chart-desc">查看各咨询师承接预约数量。</div>
          </div>
        </div>
        <div ref="workloadChartRef" class="chart-box"></div>
      </section>

      <section class="page-card chart-card chart-wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">每月预约数量</div>
            <div class="chart-desc">按月份观察预约规模变化趋势。</div>
          </div>
        </div>
        <div ref="monthlyChartRef" class="chart-box"></div>
      </section>

      <section class="page-card chart-card chart-wide">
        <div class="chart-header">
          <div>
            <div class="chart-title">反馈评分统计</div>
            <div class="chart-desc">汇总不同评分段的评价数量。</div>
          </div>
        </div>
        <div ref="feedbackChartRef" class="chart-box"></div>
      </section>
    </div>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'

import {
  getAppointmentStatusReport,
  getCounselorWorkloadReport,
  getFeedbackScoreReport,
  getMonthlyAppointmentReport
} from '@/api/report'

const loading = ref(false)
const reportDateRange = ref([])
const reportYear = ref(new Date().getFullYear())
const statusDataList = ref([])
const workloadDataList = ref([])
const monthlyDataList = ref([])
const feedbackDataList = ref([])

const statusChartRef = ref()
const workloadChartRef = ref()
const monthlyChartRef = ref()
const feedbackChartRef = ref()

let statusChart
let workloadChart
let monthlyChart
let feedbackChart

const dateRangeText = computed(() => {
  if (!reportDateRange.value?.length) {
    return '默认全部时间'
  }

  return `${reportDateRange.value[0]} 至 ${reportDateRange.value[1]}`
})

const summaryCards = computed(() => {
  const totalAppointments = statusDataList.value.reduce((sum, item) => sum + (item.value || 0), 0)
  const topCounselor = workloadDataList.value[0]
  const yearlyAppointments = monthlyDataList.value.reduce((sum, item) => sum + (item.value || 0), 0)
  const totalFeedbacks = feedbackDataList.value.reduce((sum, item) => sum + (item.value || 0), 0)

  return [
    {
      label: '预约总量',
      value: `${totalAppointments} 单`,
      desc: '来自预约状态分布统计'
    },
    {
      label: '年度预约',
      value: `${yearlyAppointments} 单`,
      desc: `${reportYear.value} 年月度预约汇总`
    },
    {
      label: '最高工作量咨询师',
      value: topCounselor?.name || '暂无数据',
      desc: topCounselor ? `${topCounselor.value} 单预约` : '暂无可展示数据'
    },
    {
      label: '评价总数',
      value: `${totalFeedbacks} 条`,
      desc: '来源于反馈评分统计'
    }
  ]
})

function getDateParams() {
  return {
    beginDate: reportDateRange.value?.[0] || '',
    endDate: reportDateRange.value?.[1] || ''
  }
}

async function fetchReportData() {
  loading.value = true

  try {
    const [statusData, workloadData, monthlyData, feedbackData] = await Promise.all([
      getAppointmentStatusReport(getDateParams()),
      getCounselorWorkloadReport(getDateParams()),
      getMonthlyAppointmentReport({ year: reportYear.value }),
      getFeedbackScoreReport(getDateParams())
    ])

    statusDataList.value = statusData || []
    workloadDataList.value = workloadData || []
    monthlyDataList.value = monthlyData || []
    feedbackDataList.value = feedbackData || []

    await nextTick()
    renderStatusChart(statusDataList.value)
    renderWorkloadChart(workloadDataList.value)
    renderMonthlyChart(monthlyDataList.value)
    renderFeedbackChart(feedbackDataList.value)
  } finally {
    loading.value = false
  }
}

function renderStatusChart(data) {
  statusChart ??= echarts.init(statusChartRef.value)
  statusChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 3
        },
        data
      }
    ]
  })
}

function renderWorkloadChart(data) {
  workloadChart ??= echarts.init(workloadChartRef.value)
  workloadChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.name),
      axisLabel: { interval: 0, rotate: 18 }
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        barWidth: 34,
        data: data.map((item) => item.value),
        itemStyle: {
          color: '#2c7be5',
          borderRadius: [8, 8, 0, 0]
        }
      }
    ]
  })
}

function renderMonthlyChart(data) {
  monthlyChart ??= echarts.init(monthlyChartRef.value)
  monthlyChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.monthLabel)
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'line',
        smooth: true,
        symbolSize: 10,
        data: data.map((item) => item.value),
        lineStyle: {
          width: 4,
          color: '#1ba784'
        },
        itemStyle: {
          color: '#1ba784'
        },
        areaStyle: {
          color: 'rgba(27, 167, 132, 0.12)'
        }
      }
    ]
  })
}

function renderFeedbackChart(data) {
  feedbackChart ??= echarts.init(feedbackChartRef.value)
  feedbackChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map((item) => item.name)
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        data: data.map((item) => item.value),
        itemStyle: {
          color: '#f59e0b',
          borderRadius: [8, 8, 0, 0]
        }
      }
    ]
  })
}

function handleResize() {
  statusChart?.resize()
  workloadChart?.resize()
  monthlyChart?.resize()
  feedbackChart?.resize()
}

onMounted(() => {
  fetchReportData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  statusChart?.dispose()
  workloadChart?.dispose()
  monthlyChart?.dispose()
  feedbackChart?.dispose()
})
</script>

<style scoped>
.report-page {
  display: grid;
  gap: 18px;
}

.report-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(260px, 0.8fr);
  gap: 18px;
  padding: 24px 26px;
  background:
    radial-gradient(circle at right top, rgba(44, 123, 229, 0.14), transparent 26%),
    linear-gradient(135deg, #ffffff 0%, #f7fbff 100%);
}

.report-eyebrow {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: #eaf4ff;
  color: #1453a3;
  font-size: 12px;
  font-weight: 700;
}

.report-hero h3 {
  margin: 14px 0 10px;
  color: #10263a;
  font-size: 28px;
}

.report-hero p {
  margin: 0;
  color: #64748b;
  line-height: 1.8;
}

.report-hero-side {
  display: grid;
  gap: 12px;
}

.report-hero-card,
.report-summary-card {
  padding: 16px 18px;
  border: 1px solid #e5edf6;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.84);
}

.report-hero-card span,
.report-summary-card span {
  display: block;
  color: #71859b;
  font-size: 12px;
}

.report-hero-card strong,
.report-summary-card strong {
  display: block;
  margin-top: 10px;
  color: #10263a;
  font-size: 18px;
}

.report-summary-card em {
  display: block;
  margin-top: 8px;
  color: #64748b;
  font-style: normal;
  line-height: 1.7;
}

.report-filter-card {
  padding-bottom: 2px;
}

.report-summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.report-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.chart-card {
  padding: 20px;
}

.chart-wide {
  grid-column: 1 / -1;
}

.chart-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.chart-title {
  color: #10263a;
  font-size: 18px;
  font-weight: 700;
}

.chart-desc {
  margin-top: 6px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.chart-box {
  width: 100%;
  height: 360px;
}

@media (max-width: 1480px) {
  .report-hero,
  .report-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1200px) {
  .report-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
