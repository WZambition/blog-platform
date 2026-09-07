import { defineStore } from 'pinia'
import { userApi } from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('blog_user') || 'null'),
    unreadCount: 0
  }),
  getters: {
    isLoggedIn: (state) => !!state.user,
    userId: (state) => state.user?.id || null,
    nickname: (state) => state.user?.nickname || '游客'
  },
  actions: {
    setUser(user) {
      this.user = user
      localStorage.setItem('blog_user', JSON.stringify(user))
    },
    async login(username, password) {
      const res = await userApi.login({ username, password })
      this.setUser(res.data)
      return res
    },
    async register(form) {
      const res = await userApi.register(form)
      this.setUser(res.data)
      return res
    },
    async guestLogin(nickname) {
      const res = await userApi.guestLogin({ nickname })
      this.setUser(res.data)
      return res
    },
    logout() {
      this.user = null
      localStorage.removeItem('blog_user')
    },
    async fetchUnreadCount() {
      if (!this.user) return 0
      try {
        const res = await userApi.getUnreadCount(this.user.id)
        this.unreadCount = res.data.count
        return this.unreadCount
      } catch (e) {
        return 0
      }
    }
  }
})
