import request from '@/utils/request'

export function getAppointmentStatusReport(params) {
  return request({
    url: '/reports/appointmentStatus',
    method: 'get',
    params
  })
}

export function getCounselorWorkloadReport(params) {
  return request({
    url: '/reports/counselorWorkload',
    method: 'get',
    params
  })
}

export function getMonthlyAppointmentReport(params) {
  return request({
    url: '/reports/monthlyAppointments',
    method: 'get',
    params
  })
}

export function getFeedbackScoreReport(params) {
  return request({
    url: '/reports/feedbackScore',
    method: 'get',
    params
  })
}
