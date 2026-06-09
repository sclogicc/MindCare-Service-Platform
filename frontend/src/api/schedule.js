import request from '@/utils/request'

export function getSchedulePage(params) {
  return request({
    url: '/schedules',
    method: 'get',
    params
  })
}

export function getScheduleDetail(id) {
  return request({
    url: `/schedules/${id}`,
    method: 'get'
  })
}

export function addSchedule(data) {
  return request({
    url: '/schedules',
    method: 'post',
    data
  })
}

export function updateSchedule(data) {
  return request({
    url: '/schedules',
    method: 'put',
    data
  })
}

export function updateScheduleStatus(data) {
  return request({
    url: '/schedules/status',
    method: 'put',
    data
  })
}

export function deleteSchedule(id) {
  return request({
    url: `/schedules/${id}`,
    method: 'delete'
  })
}
