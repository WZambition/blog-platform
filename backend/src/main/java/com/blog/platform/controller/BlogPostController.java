package com.blog.platform.controller;

import com.blog.platform.common.ApiResponse;
import com.blog.platform.dto.BlogPostRequest;
import com.blog.platform.entity.BlogPost;
import com.blog.platform.service.BlogPostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    /**
     * 发布博客文章
     */
    @PostMapping
    public ApiResponse<BlogPost> create(@RequestBody BlogPostRequest request,
                                        @RequestParam Long authorId) {
        BlogPost post = blogPostService.create(request, authorId);
        return ApiResponse.success("文章发布成功", post);
    }

    /**
     * 获取所有博客文章
     */
    @GetMapping
    public ApiResponse<List<BlogPost>> findAll() {
        return ApiResponse.success(blogPostService.findAll());
    }

    /**
     * 获取某用户的博客文章
     */
    @GetMapping("/author/{authorId}")
    public ApiResponse<List<BlogPost>> findByAuthor(@PathVariable Long authorId) {
        return ApiResponse.success(blogPostService.findByAuthor(authorId));
    }

    /**
     * 获取博客文章详情
     */
    @GetMapping("/{id}")
    public ApiResponse<BlogPost> getById(@PathVariable Long id) {
        return ApiResponse.success(blogPostService.getById(id));
    }

    /**
     * 更新博客文章
     */
    @PutMapping("/{id}")
    public ApiResponse<BlogPost> update(@PathVariable Long id,
                                        @RequestBody BlogPostRequest request,
                                        @RequestParam Long authorId) {
        BlogPost post = blogPostService.update(id, request, authorId);
        return ApiResponse.success("文章更新成功", post);
    }

    /**
     * 删除博客文章
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @RequestParam Long authorId) {
        blogPostService.delete(id, authorId);
        return ApiResponse.success("文章删除成功", null);
    }

    /**
     * 点赞博客文章
     */
    @PostMapping("/{id}/like")
    public ApiResponse<BlogPost> like(@PathVariable Long id) {
        return ApiResponse.success(blogPostService.like(id));
    }
}
