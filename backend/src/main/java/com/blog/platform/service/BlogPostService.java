package com.blog.platform.service;

import com.blog.platform.dto.BlogPostRequest;
import com.blog.platform.entity.BlogPost;
import com.blog.platform.entity.User;
import com.blog.platform.repository.BlogPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final UserService userService;

    public BlogPostService(BlogPostRepository blogPostRepository, UserService userService) {
        this.blogPostRepository = blogPostRepository;
        this.userService = userService;
    }

    /**
     * 发布博客文章
     */
    public BlogPost create(BlogPostRequest request, Long authorId) {
        User author = userService.getById(authorId);
        BlogPost post = new BlogPost();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author);
        post.setCoverImage(request.getCoverImage());
        post.setTags(request.getTags());
        post.setSummary(request.getSummary());
        return blogPostRepository.save(post);
    }

    /**
     * 获取所有博客文章（按时间倒序）
     */
    public List<BlogPost> findAll() {
        return blogPostRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 获取某用户的博客文章
     */
    public List<BlogPost> findByAuthor(Long authorId) {
        return blogPostRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    /**
     * 获取博客文章详情（浏览量+1）
     */
    @Transactional
    public BlogPost getById(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        post.setViewCount(post.getViewCount() + 1);
        return blogPostRepository.save(post);
    }

    /**
     * 更新博客文章
     */
    @Transactional
    public BlogPost update(Long id, BlogPostRequest request, Long authorId) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException("只能编辑自己的文章");
        }
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getCoverImage() != null) {
            post.setCoverImage(request.getCoverImage());
        }
        if (request.getTags() != null) {
            post.setTags(request.getTags());
        }
        if (request.getSummary() != null) {
            post.setSummary(request.getSummary());
        }
        return blogPostRepository.save(post);
    }

    /**
     * 删除博客文章
     */
    @Transactional
    public void delete(Long id, Long authorId) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException("只能删除自己的文章");
        }
        blogPostRepository.delete(post);
    }

    /**
     * 点赞博客文章
     */
    @Transactional
    public BlogPost like(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        post.setLikeCount(post.getLikeCount() + 1);
        return blogPostRepository.save(post);
    }
}
