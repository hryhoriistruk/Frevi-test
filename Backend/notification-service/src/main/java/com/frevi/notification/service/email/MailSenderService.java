package com.frevi.notification.service.email;

import lombok.AllArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class MailSenderService implements EmailNotificationService {
    private final MailSender mailSender;

    private static final String EMAIL_ADDRESS = "demenchuk1210m@gmail.com";
    private static final String EMAIL_SUBJECT = "We got an order from you";
    private static final BigDecimal TAX = BigDecimal.valueOf(0.05);

    private void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(EMAIL_ADDRESS);

        mailSender.send(message);
    }

    @Override
    public void sendOrderEmail(String message, String to) {
        sendSimpleEmail(to, message, EMAIL_SUBJECT);
    }
}
