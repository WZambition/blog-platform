package com.blog.platform.dto;

import lombok.Data;

/**
 * 博客文章请求
 */
@Data
public class BlogPostRequest {

    private String title;
    private String content;
    private String coverImage;
    private String tags;
    private String summary;
}
