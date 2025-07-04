package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.MessageRequest;
import HireCraft.com.SpringBoot.dtos.response.MessageResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface MessageService {
    void sendMessageToBooking(MessageRequest request, UserDetails userDetails);
    List<MessageResponse> getConversation(Long bookingId);

    MessageResponse sendMessage(MessageRequest request, UserDetails userDetails);

    List<MessageResponse> getMessagesForBooking(Long bookingId);
}


