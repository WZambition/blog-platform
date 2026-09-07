import request from './request'

export const postApi = {
  // 发布博客文章
  create(data, authorId) {
    return request.post(`/posts?authorId=${authorId}`, data)
  },
  // 获取所有博客文章
  findAll() {
    return request.get('/posts')
  },
  // 获取某用户的博客文章
  findByAuthor(authorId) {
    return request.get(`/posts/author/${authorId}`)
  },
  // 获取博客文章详情
  getById(id) {
    return request.get(`/posts/${id}`)
  },
  // 更新博客文章
  update(id, data, authorId) {
    return request.put(`/posts/${id}?authorId=${authorId}`, data)
  },
  // 删除博客文章
  remove(id, authorId) {
    return request.delete(`/posts/${id}?authorId=${authorId}`)
  },
  // 点赞博客文章
  like(id) {
    return request.post(`/posts/${id}/like`)
  }
}
