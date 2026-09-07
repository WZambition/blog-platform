import request from './request'

export const opinionApi = {
  // 发布观点（生成星星）
  create(data, authorId) {
    return request.post(`/opinions?authorId=${authorId}`, data)
  },
  // 获取所有观点
  findAll() {
    return request.get('/opinions')
  },
  // 获取观点详情
  getById(id) {
    return request.get(`/opinions/${id}`)
  },
  // 点击观点 -> 转化为议题
  convertToTopic(id) {
    return request.post(`/opinions/${id}/topic`)
  },
  // 点赞观点
  like(id) {
    return request.post(`/opinions/${id}/like`)
  },
  // 获取议题讨论列表
  getComments(id) {
    return request.get(`/opinions/${id}/comments`)
  },
  // 发表议题讨论
  addComment(id, content, authorId) {
    return request.post(`/opinions/${id}/comments?authorId=${authorId}`, { content })
  }
}
