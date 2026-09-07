import request from './request'

export const notificationApi = {
  // 获取用户消息列表
  getByUser(userId) {
    return request.get(`/notifications/user/${userId}`)
  },
  // 获取未读消息数
  getUnreadCount(userId) {
    return request.get(`/notifications/user/${userId}/unread-count`)
  },
  // 标记单条消息已读
  markRead(id, userId) {
    return request.post(`/notifications/${id}/read?userId=${userId}`)
  },
  // 全部标记已读
  markAllRead(userId) {
    return request.post(`/notifications/user/${userId}/read-all`)
  }
}
