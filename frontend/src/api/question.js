import request from './request'

export const questionApi = {
  // 发布技术问题
  create(data, authorId) {
    return request.post(`/questions?authorId=${authorId}`, data)
  },
  // 获取所有技术问题
  findAll() {
    return request.get('/questions')
  },
  // 获取问题详情
  getById(id) {
    return request.get(`/questions/${id}`)
  },
  // 获取问题的所有方案
  getSolutions(id) {
    return request.get(`/questions/${id}/solutions`)
  },
  // 提交方案（别人回答）
  submitSolution(id, content, authorId) {
    return request.post(`/questions/${id}/solutions?authorId=${authorId}`, { content })
  },
  // 修改方案
  updateSolution(solutionId, content, userId) {
    return request.put(`/questions/solutions/${solutionId}?userId=${userId}`, { content })
  },
  // 采纳方案
  acceptSolution(solutionId, userId) {
    return request.post(`/questions/solutions/${solutionId}/accept?userId=${userId}`)
  },
  // 标记为未解决
  markUnsolved(id, userId) {
    return request.post(`/questions/${id}/unsolved?userId=${userId}`)
  },
  // 标记为已解决
  markSolved(id, userId) {
    return request.post(`/questions/${id}/solved?userId=${userId}`)
  }
}
