package com.blog.platform.dto;

import lombok.Data;

/**
 * 技术问题请求
 */
@Data
public class TechQuestionRequest {

    private String title;
    private String description;
    private String imageUrl;
    private String tags;
}
