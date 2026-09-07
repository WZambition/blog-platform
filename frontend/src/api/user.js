import request from './request'

export const userApi = {
  // 注册
  register(data) {
    return request.post('/users/register', data)
  },
  // 登录
  login(data) {
    return request.post('/users/login', data)
  },
  // 游客登录
  guestLogin(data) {
    return request.post('/users/guest', data)
  },
  // 获取用户信息
  getUser(id) {
    return request.get(`/users/${id}`)
  },
  // 获取未读消息数
  getUnreadCount(userId) {
    return request.get(`/notifications/user/${userId}/unread-count`)
  }
}
