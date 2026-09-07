package com.blog.platform.controller;

import com.blog.platform.common.ApiResponse;
import com.blog.platform.entity.Notification;
import com.blog.platform.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 获取用户消息列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Notification>> getByUser(@PathVariable Long userId) {
        return ApiResponse.success(notificationService.getByUser(userId));
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/user/{userId}/unread-count")
    public ApiResponse<Map<String, Object>> getUnreadCount(@PathVariable Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("count", notificationService.getUnreadCount(userId));
        return ApiResponse.success(map);
    }

    /**
     * 标记单条消息已读
     */
    @PostMapping("/{id}/read")
    public ApiResponse<Notification> markRead(@PathVariable Long id,
                                              @RequestParam Long userId) {
        return ApiResponse.success(notificationService.markRead(id, userId));
    }

    /**
     * 全部标记已读
     */
    @PostMapping("/user/{userId}/read-all")
    public ApiResponse<Void> markAllRead(@PathVariable Long userId) {
        notificationService.markAllRead(userId);
        return ApiResponse.success("全部已读", null);
    }
}
