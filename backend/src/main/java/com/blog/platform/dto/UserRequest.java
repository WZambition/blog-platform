package com.blog.platform.dto;

import lombok.Data;

/**
 * 用户登录/注册请求
 */
@Data
public class UserRequest {

    private String username;
    private String nickname;
    private String password;
    private String avatar;
    private String bio;
}
