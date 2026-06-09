import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

export function getLoginUserInfo() {
  return request({
    url: '/login/info',
    method: 'get'
  })
}
