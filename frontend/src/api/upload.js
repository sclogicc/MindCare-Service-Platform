import request from '@/utils/request'

export function uploadConsultationAttachment(file) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('businessType', 'consultation_attachment')

  return request({
    url: '/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
