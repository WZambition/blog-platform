package com.blog.platform.service;

import com.blog.platform.dto.UserRequest;
import com.blog.platform.entity.User;
import com.blog.platform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 注册
     */
    public User register(UserRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname() == null || request.getNickname().isBlank()
                ? request.getUsername() : request.getNickname());
        user.setPassword(request.getPassword());
        user.setAvatar(request.getAvatar());
        user.setBio(request.getBio());
        return userRepository.save(user);
    }

    /**
     * 登录
     */
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return user;
    }

    /**
     * 获取用户
     */
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /**
     * 获取或创建游客用户（演示用，简化登录流程）
     */
    public User getOrCreateGuest(String nickname) {
        String baseName = nickname == null || nickname.isBlank() ? "游客" : nickname;
        String username = "guest_" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(username);
        user.setNickname(baseName);
        user.setPassword("guest");
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
