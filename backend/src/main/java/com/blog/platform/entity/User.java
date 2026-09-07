package com.blog.platform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名 */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 昵称 */
    @Column(nullable = false, length = 50)
    private String nickname;

    /** 密码（演示环境明文存储，生产请加密） */
    @Column(nullable = false)
    private String password;

    /** 头像URL */
    @Column(length = 255)
    private String avatar;

    /** 个人简介 */
    @Column(length = 500)
    private String bio;

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
