package com.blog.platform.controller;

import com.blog.platform.common.ApiResponse;
import com.blog.platform.dto.UserRequest;
import com.blog.platform.entity.User;
import com.blog.platform.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody UserRequest request) {
        User user = userService.register(request);
        return ApiResponse.success("注册成功", buildUserMap(user));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody UserRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        return ApiResponse.success("登录成功", buildUserMap(user));
    }

    /**
     * 游客登录（演示用，无需密码）
     */
    @PostMapping("/guest")
    public ApiResponse<Map<String, Object>> guestLogin(@RequestBody(required = false) UserRequest request) {
        String nickname = request != null ? request.getNickname() : null;
        User user = userService.getOrCreateGuest(nickname);
        return ApiResponse.success("游客登录成功", buildUserMap(user));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return ApiResponse.success(buildUserMap(user));
    }

    private Map<String, Object> buildUserMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("nickname", user.getNickname());
        map.put("avatar", user.getAvatar());
        map.put("bio", user.getBio());
        map.put("createdAt", user.getCreatedAt());
        return map;
    }
}
