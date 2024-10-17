package com.eventbook.notificationservice.service;

import com.eventbook.notificationservice.domain.Notification;
import com.eventbook.notificationservice.domain.NotificationType;

import java.util.List;

public interface NotificationService {
    void createNotification(String userId, String message, NotificationType type);
    List<Notification> getNotificationsForUser(String userId);

}
