package com.frevi.notification.service.push;

public interface PushNotificationService {
    void sendNotification(String deviceToken, String title, String body) throws Exception;
}
