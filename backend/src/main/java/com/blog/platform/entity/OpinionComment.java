package com.blog.platform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 观点讨论（议题下的评论）
 * 当一个观点被点击转化为议题后，其他人可在此发表讨论
 */
@Data
@Entity
@Table(name = "opinion_comments")
public class OpinionComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属观点 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opinion_id", nullable = false)
    private AiOpinion opinion;

    /** 评论作者 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** 评论内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
