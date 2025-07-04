package com.frevi.notification.listener;

import com.frevi.notification.dto.MessageDto;
import com.frevi.notification.dto.UserDto;
import com.frevi.notification.service.email.EmailNotificationService;
import com.frevi.notification.service.push.PushNotificationService;
import com.frevi.notification.service.sms.SmsNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RecommendationListener {
    private PushNotificationService pushNotificationService;
    private EmailNotificationService emailNotificationService;
    private SmsNotificationService smsNotificationService;

    @KafkaListener(topics = "recommendation",
            groupId = "recommendation-group",
            containerFactory = "kafkaListenerContainerFactoryHabitSentEvent")
    public void recommendationListener(MessageDto messageDto) throws Exception {
        String message = messageDto.message();

        UserDto user = messageDto.user();

        List<String> deviceTokens = user.userFcmTokens();

        for (String deviceToken : deviceTokens) {
            pushNotificationService.sendNotification(deviceToken, "Recommendation for you", message);
        }

        emailNotificationService.sendOrderEmail(message, user.email());

        smsNotificationService.sendSmsNotification(user.phoneNumber(), message);
    }
}
