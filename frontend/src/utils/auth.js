const TOKEN_KEY = 'mindcare_token'
const USER_KEY = 'mindcare_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUserInfo() {
  const value = localStorage.getItem(USER_KEY)
  return value ? JSON.parse(value) : null
}

export function setUserInfo(userInfo) {
  localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
}

export function clearUserInfo() {
  localStorage.removeItem(USER_KEY)
}
