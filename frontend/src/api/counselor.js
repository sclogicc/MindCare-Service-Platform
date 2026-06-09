import request from '@/utils/request'

export function getCounselorPage(params) {
  return request({
    url: '/counselors',
    method: 'get',
    params
  })
}

export function getCounselorOptions() {
  return request({
    url: '/counselors/options',
    method: 'get'
  })
}

export function getCounselorDetail(id) {
  return request({
    url: `/counselors/${id}`,
    method: 'get'
  })
}

export function getCounselorManageDetail(id) {
  return request({
    url: `/counselors/manage/${id}`,
    method: 'get'
  })
}

export function updateCounselor(data) {
  return request({
    url: '/counselors',
    method: 'put',
    data
  })
}

export function updateCounselorStatus(data) {
  return request({
    url: '/counselors/status',
    method: 'put',
    data
  })
}
