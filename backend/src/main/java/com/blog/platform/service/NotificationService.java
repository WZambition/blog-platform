package com.blog.platform.service;

import com.blog.platform.entity.Notification;
import com.blog.platform.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * 获取用户的消息列表
     */
    public List<Notification> getByUser(Long userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 获取未读消息数
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndReadFalse(userId);
    }

    /**
     * 标记消息已读
     */
    @Transactional
    public Notification markRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("消息不存在"));
        if (!notification.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该消息");
        }
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    /**
     * 全部标记已读
     */
    @Transactional
    public void markAllRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
        for (Notification n : notifications) {
            if (!n.getRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }
}
