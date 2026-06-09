import request from '@/utils/request'

export function getConsultationRecordPage(params) {
  return request({
    url: '/consultationRecords',
    method: 'get',
    params
  })
}

export function getConsultationRecordDetailByAppointmentId(appointmentId) {
  return request({
    url: `/consultationRecords/appointment/${appointmentId}`,
    method: 'get'
  })
}

export function completeAppointmentWithRecord(data) {
  return request({
    url: '/consultationRecords/complete',
    method: 'post',
    data
  })
}
