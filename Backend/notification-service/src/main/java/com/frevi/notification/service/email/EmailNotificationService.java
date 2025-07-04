package com.frevi.notification.service.email;

public interface EmailNotificationService {
    void sendOrderEmail(String message, String to);
}
