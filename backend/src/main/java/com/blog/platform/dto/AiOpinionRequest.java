package com.blog.platform.dto;

import lombok.Data;

/**
 * AI观点请求
 */
@Data
public class AiOpinionRequest {

    private String title;
    private String content;
    private String starColor;
    private Integer starSize;
    private Double posX;
    private Double posY;
}
