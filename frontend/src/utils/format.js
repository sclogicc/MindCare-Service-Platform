export function formatDate(dateValue) {
  if (!dateValue) return '-'
  return String(dateValue)
}

export function formatTime(timeValue) {
  if (!timeValue) return '-'
  return String(timeValue).slice(0, 5)
}

export function formatDateTime(dateTimeValue) {
  if (!dateTimeValue) return '-'
  return String(dateTimeValue).replace('T', ' ').slice(0, 19)
}
