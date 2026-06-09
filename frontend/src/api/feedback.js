import request from '@/utils/request'

export function addFeedback(data) {
  return request({
    url: '/feedbacks',
    method: 'post',
    data
  })
}

export function getFeedbackPage(params) {
  return request({
    url: '/feedbacks',
    method: 'get',
    params
  })
}

export function getFeedbackDetail(id) {
  return request({
    url: `/feedbacks/${id}`,
    method: 'get'
  })
}

export function getFeedbackByAppointmentId(appointmentId) {
  return request({
    url: `/feedbacks/appointment/${appointmentId}`,
    method: 'get'
  })
}

export function deleteFeedback(id) {
  return request({
    url: `/feedbacks/${id}`,
    method: 'delete'
  })
}
