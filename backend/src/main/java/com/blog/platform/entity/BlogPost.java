package com.blog.platform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 博客文章
 * 用户记录和分享自己的博客文章
 */
@Data
@Entity
@Table(name = "blog_posts")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 文章标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 文章内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 文章作者 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** 封面图URL */
    @Column(length = 500)
    private String coverImage;

    /** 标签（逗号分隔） */
    @Column(length = 255)
    private String tags;

    /** 文章摘要 */
    @Column(length = 500)
    private String summary;

    /** 浏览量 */
    @Column(nullable = false)
    private Integer viewCount = 0;

    /** 点赞数 */
    @Column(nullable = false)
    private Integer likeCount = 0;

    /** 评论数 */
    @Column(nullable = false)
    private Integer commentCount = 0;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
