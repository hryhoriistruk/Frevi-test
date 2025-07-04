package com.frevi.notification.service.sms;

import com.twilio.rest.chat.v1.service.channel.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService implements SmsNotificationService {
    @Value("${twilio.phone.number}")
    private String senderPhoneNumber;

    @Override
    public void sendSmsNotification(String to, String message) {
        Message.creator(
                to,
                senderPhoneNumber,
                message
        ).create();
    }
}
