package com.frevi.notification.service.sms;

public interface SmsNotificationService {
    void sendSmsNotification(String to, String message);
}
