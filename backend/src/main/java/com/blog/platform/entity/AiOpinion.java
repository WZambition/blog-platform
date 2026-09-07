package com.blog.platform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI观点（星星）
 * 每个观点像一颗星星在夜空中闪烁，点击后进入可成为议题（讨论区）
 */
@Data
@Entity
@Table(name = "ai_opinions")
public class AiOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 观点标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 观点内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 观点作者 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** 星星颜色（用于星空展示） */
    @Column(length = 20)
    private String starColor;

    /** 星星大小（1-5） */
    @Column
    private Integer starSize;

    /** 星星在星空中的X坐标（0-100） */
    @Column
    private Double posX;

    /** 星星在星空中的Y坐标（0-100） */
    @Column
    private Double posY;

    /** 是否已转化为议题（讨论区） */
    @Column(nullable = false)
    private Boolean isTopic = false;

    /** 议题讨论数 */
    @Column(nullable = false)
    private Integer commentCount = 0;

    /** 点赞数 */
    @Column(nullable = false)
    private Integer likeCount = 0;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (starColor == null) {
            starColor = "#FFD700";
        }
        if (starSize == null) {
            starSize = 3;
        }
    }
}
