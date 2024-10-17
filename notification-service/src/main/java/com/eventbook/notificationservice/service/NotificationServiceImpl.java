package com.eventbook.notificationservice.service;

import com.eventbook.notificationservice.domain.Notification;
import com.eventbook.notificationservice.domain.NotificationType;
import com.eventbook.notificationservice.infrastructure.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void createNotification(String userId, String message, NotificationType type) {
        Notification notification = new Notification(userId, message, type);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
