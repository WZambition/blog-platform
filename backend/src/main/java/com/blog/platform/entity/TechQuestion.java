package com.blog.platform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 技术问题
 * 以卡片形式展示，上方图片、下方问题描述
 * 状态：UNSOLVED(未解决) / VERIFYING(验证中) / SOLVED(已解决)
 */
@Data
@Entity
@Table(name = "tech_questions")
public class TechQuestion {

    public enum Status {
        UNSOLVED,   // 未解决
        VERIFYING,  // 验证中
        SOLVED      // 已解决
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 问题标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 问题描述 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** 问题配图URL */
    @Column(length = 500)
    private String imageUrl;

    /** 问题发起者 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** 问题状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.UNSOLVED;

    /** 标签（逗号分隔） */
    @Column(length = 255)
    private String tags;

    /** 方案数量 */
    @Column(nullable = false)
    private Integer solutionCount = 0;

    /** 浏览数 */
    @Column(nullable = false)
    private Integer viewCount = 0;

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
