package com.blog.platform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息提醒
 * 当别人给问题发起者提交方案时，给问题发起者弹出消息提醒
 */
@Data
@Entity
@Table(name = "notifications")
public class Notification {

    public enum Type {
        NEW_SOLUTION,   // 收到新方案
        SOLUTION_ACCEPTED, // 方案被采纳
        NEW_COMMENT,    // 收到新评论
        SYSTEM          // 系统消息
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 接收者 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /** 触发者（谁发起的动作） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    /** 消息类型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Type type;

    /** 关联的技术问题（可选） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private TechQuestion question;

    /** 关联的观点（可选） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opinion_id")
    private AiOpinion opinion;

    /** 消息内容 */
    @Column(nullable = false, length = 500)
    private String content;

    /** 是否已读 */
    @Column(nullable = false)
    private Boolean read = false;

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
