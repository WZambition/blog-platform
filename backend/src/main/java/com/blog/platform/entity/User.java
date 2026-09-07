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

    /** 用户名（邮箱或手机号） */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 昵称 */
    @Column(nullable = false, length = 50)
    private String nickname;

    /** 密码（BCrypt 加密存储） */
    @Column(nullable = false)
    private String password;

    /** 邮箱 */
    @Column(length = 100)
    private String email;

    /** 手机号 */
    @Column(length = 20)
    private String phone;

    /** 安全问题 */
    @Column(length = 200)
    private String securityQuestion;

    /** 安全问题答案 */
    @Column(length = 200)
    private String securityAnswer;

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
