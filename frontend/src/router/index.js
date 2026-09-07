import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'ai-space',
        name: 'AiSpace',
        component: () => import('../views/AiSpace.vue'),
        meta: { title: 'AI观点星空' }
      },
      {
        path: 'opinion/:id',
        name: 'OpinionDetail',
        component: () => import('../views/OpinionDetail.vue'),
        meta: { title: '观点议题' }
      },
      {
        path: 'tech',
        name: 'Tech',
        component: () => import('../views/Tech.vue'),
        meta: { title: '技术讨论区' }
      },
      {
        path: 'question/:id',
        name: 'QuestionDetail',
        component: () => import('../views/QuestionDetail.vue'),
        meta: { title: '问题详情' }
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('../views/Notifications.vue'),
        meta: { title: '消息中心' }
      },
      {
        path: 'blog',
        name: 'Blog',
        component: () => import('../views/Blog.vue'),
        meta: { title: '博客文章' }
      },
      {
        path: 'blog/write',
        name: 'BlogWrite',
        component: () => import('../views/BlogEditor.vue'),
        meta: { title: '写文章' }
      },
      {
        path: 'blog/edit/:id',
        name: 'BlogEdit',
        component: () => import('../views/BlogEditor.vue'),
        meta: { title: '编辑文章' }
      },
      {
        path: 'blog/:id',
        name: 'BlogDetail',
        component: () => import('../views/BlogDetail.vue'),
        meta: { title: '文章详情' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 星语博客` : '星语博客'
  next()
})

export default router
