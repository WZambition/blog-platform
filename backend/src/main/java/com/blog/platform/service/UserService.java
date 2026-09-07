package com.blog.platform.service;

import com.blog.platform.dto.UserRequest;
import com.blog.platform.entity.User;
import com.blog.platform.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 注册（支持邮箱或手机号作为账号）
     */
    public User register(UserRequest request) {
        String account = request.getUsername();
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("请输入邮箱或手机号");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        account = account.trim();
        // 判断是邮箱还是手机号
        boolean isEmail = account.contains("@");
        if (isEmail) {
            if (userRepository.existsByEmail(account)) {
                throw new IllegalArgumentException("该邮箱已被注册");
            }
        } else {
            if (userRepository.existsByPhone(account)) {
                throw new IllegalArgumentException("该手机号已被注册");
            }
        }
        if (userRepository.existsByUsername(account)) {
            throw new IllegalArgumentException("该账号已被注册");
        }
        User user = new User();
        user.setUsername(account);
        user.setNickname(request.getNickname() == null || request.getNickname().isBlank()
                ? account : request.getNickname());
        // BCrypt 加密存储密码
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (isEmail) {
            user.setEmail(account);
        } else {
            user.setPhone(account);
        }
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(request.getSecurityAnswer());
        user.setAvatar(request.getAvatar());
        user.setBio(request.getBio());
        return userRepository.save(user);
    }

    /**
     * 登录（支持用户名/邮箱/手机号）
     */
    public User login(String account, String password) {
        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("请输入账号和密码");
        }
        account = account.trim();
        Optional<User> userOpt = findByAccount(account);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        User user = userOpt.get();
        String storedPwd = user.getPassword();
        // 兼容旧明文密码：若存储密码不是 BCrypt 格式（$2a$/$2b$/$2y$ 开头），按明文比较并自动升级为 BCrypt
        boolean isBcrypt = storedPwd != null && storedPwd.startsWith("$2");
        boolean matched = isBcrypt
                ? passwordEncoder.matches(password, storedPwd)
                : storedPwd != null && storedPwd.equals(password);
        if (!matched) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        // 旧明文密码自动升级为 BCrypt
        if (!isBcrypt) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }
        return user;
    }

    /**
     * 根据账号（用户名/邮箱/手机号）查找用户
     */
    private Optional<User> findByAccount(String account) {
        Optional<User> byUsername = userRepository.findByUsername(account);
        if (byUsername.isPresent()) {
            return byUsername;
        }
        Optional<User> byEmail = userRepository.findByEmail(account);
        if (byEmail.isPresent()) {
            return byEmail;
        }
        return userRepository.findByPhone(account);
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
        user.setPassword(passwordEncoder.encode("guest"));
        return userRepository.save(user);
    }

    /**
     * 忘记密码：根据账号（邮箱/手机号）和安全问题答案重置密码
     */
    public User resetPassword(String account, String securityAnswer, String newPassword) {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("请输入注册的邮箱或手机号");
        }
        if (securityAnswer == null || securityAnswer.isBlank()) {
            throw new IllegalArgumentException("请输入安全问题答案");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("请输入新密码");
        }
        account = account.trim();
        Optional<User> userOpt = findByAccount(account);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("该账号未注册");
        }
        User user = userOpt.get();
        if (user.getSecurityQuestion() == null || user.getSecurityAnswer() == null) {
            throw new IllegalArgumentException("该账号未设置安全问题，无法通过此方式找回密码");
        }
        if (!user.getSecurityAnswer().trim().equalsIgnoreCase(securityAnswer.trim())) {
            throw new IllegalArgumentException("安全问题答案错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
