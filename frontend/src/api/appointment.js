import request from '@/utils/request'

export function addAppointment(data) {
  return request({
    url: '/appointments',
    method: 'post',
    data
  })
}

export function getAppointmentPage(params) {
  return request({
    url: '/appointments',
    method: 'get',
    params
  })
}

export function getAppointmentDetail(id) {
  return request({
    url: `/appointments/${id}`,
    method: 'get'
  })
}

export function cancelAppointment(id, data) {
  return request({
    url: `/appointments/cancel/${id}`,
    method: 'put',
    data
  })
}

export function updateAppointmentStatus(data) {
  return request({
    url: '/appointments/status',
    method: 'put',
    data
  })
}
